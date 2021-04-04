package dam95.android.uk.firstbyte.gui.components.hardware

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.api.util.ConvertImageURL
import dam95.android.uk.firstbyte.api.ApiRepository
import dam95.android.uk.firstbyte.api.ApiViewModel
import dam95.android.uk.firstbyte.databinding.FragmentHardwareDetailsBinding
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import dam95.android.uk.firstbyte.gui.components.builds.NOT_FROM_SEARCH
import dam95.android.uk.firstbyte.model.components.Component
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

private const val NAME_KEY = "NAME"
private const val CATEGORY_KEY = "CATEGORY"
private const val LOCAL_OR_NETWORK_KEY = "LOADING_METHOD"
private const val COMPONENT_INDEX = 0

/**
 * @author David Mckee
 * @Version 1.0
 * Loads and displays all details of any selected component. Allows users to save and remove a component to/from the database.
 * Users can click on detail bullet point buttons that give a short description on what each specification of a component means.
 * At the bottom, if a component has a Amazon WebLink or a Scan.co.uk WebLink, clicking on the basket icon will take the user to the Web page.
 *
 * This class loads components through the online API, or through any saved components on the app's database.
 * It utilises a recycler adapter to allow a dynamic list of specifications for different components.
 */
class HardwareDetails : Fragment(), HardwareDetailsRecyclerList.OnItemListener {

    private lateinit var hardwareDetailsBinding: FragmentHardwareDetailsBinding
    private lateinit var fbHardwareDb: FirstByteDBAccess
    private lateinit var hardwareDetailsListAdapter: HardwareDetailsRecyclerList
    private lateinit var component: Component

    private var isLoadingFromServer: Boolean? = null
    private var isLoadingFromPC: Boolean = false
    private var offlineRemoveHardwareOnDestroy = false
    private var componentName: String? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    /**
     * Determines if the Component was loaded from the database or from the online API,
     * then chooses it's initialisation and loading path accordingly.
     *
     * It will either Stream in the hardware details from the API and converge when the fragment sets up the add/remove buttons...
     * or will load the component from the database and converge when the fragment sets up the add/remove buttons.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hardwareDetailsBinding = FragmentHardwareDetailsBinding.inflate(inflater, container, false)
        //All arguments that could be loaded from different fragments
        val componentName = arguments?.getString(NAME_KEY)
        val componentType = arguments?.getString(CATEGORY_KEY)
        isLoadingFromServer = arguments?.getBoolean(LOCAL_OR_NETWORK_KEY)
        isLoadingFromPC = arguments?.getBoolean(NOT_FROM_SEARCH) == true

        if (componentName != null && componentType != null) {
            setHasOptionsMenu(true)
            //Load FB_Hardware_Android Instance
            fbHardwareDb =
                context?.let { FirstByteDBAccess.dbInstance(it, Dispatchers.Main) }!!

            Log.i("SEARCH_CATEGORY", componentName)
            //Determine if offline load from FB_Hardware_Android Database or the online server.
            when (isLoadingFromServer) {
                true -> {
                    streamInHardware(componentName, componentType)
                }
                false -> {
                    loadSavedHardware(componentName, componentType)
                }
                else -> {
                    Log.e("NULL_CONNECTION?", "Error, Connection is showing null. How?")
                }
            }
        }
        // Inflate the layout for this fragment
        return hardwareDetailsBinding.root
    }

    /**
     * Communicates with First Byte's API for the selected component's specifications and pricing for display.
     * @param name Name of selected component that will be loaded.
     * @param type Category of selected component that will be loaded.
     */
    private fun streamInHardware(name: String, type: String) {
        val apiRepository = ApiRepository(requireContext())
        val apiViewModel = ApiViewModel(apiRepository)

        //Ask the API to return all the details of the selected component.
        apiViewModel.getHardware(name, type)
        apiViewModel.apiHardwareResponse.observe(viewLifecycleOwner, { res ->
            if (res.isSuccessful) {
                //Because the API must give the response in a list, the singular component is always at index 0.
                if (res.body()?.get(COMPONENT_INDEX) != null) {
                    //Because deletable is not on the API or Server database,
                    // retrofit makes it by default not deletable, therefore we must explicitly make it deletable
                    res.body()!![0].deletable = true
                    component = res.body()!![0]
                    ConvertImageURL.convertURLtoImage(
                        res.body()!![COMPONENT_INDEX].imageLink,
                        hardwareDetailsBinding.componentImage
                    )
                    //Set up the add/remove buttons
                    setUpButtons(res.body()!![COMPONENT_INDEX])
                    //If index 0 is null, then the API responded with an empty value, don't continue with the hardwareList setup.
                } else {
                    Log.e("NULL_COMPONENT", "Error, loaded hardware component is empty.")
                    return@observe
                }
                //If the API response was unsuccessful, then don't continue with the hardwareList setup.
            } else {
                Log.e("HARDWARE_RES_FAIL", "FAILED LOAD")
                return@observe
            }
        })
    }

    /**
     * Communicates with First Byte's App SQLite database to retrieve the saved components specifications and pricing for display.
     * @param name Name of selected component that will be loaded.
     * @param type Category of selected component that will be loaded.
     */
    private fun loadSavedHardware(name: String, type: String) {
        coroutineScope.launch {
            //Retrieve the components details from the database
            component =
                fbHardwareDb.retrieveHardware(name, type)

            //Convert the component's Image URL into an image, in an image view.
            ConvertImageURL.convertURLtoImage(
                component.imageLink,
                hardwareDetailsBinding.componentImage
            )
            //Set up the add/remove buttons
            setUpButtons(component)
        }
    }

    /**
     * Setup up the buttons that allow the user to both add and remove a component to/from the Component MySQLite database.
     * @param component The built object of the loaded component, doesn't matter if it came from the App's database or the Project's API.
     */
    private fun setUpButtons(component: Component) {

        //If the component cannot be delete or the is offline without cached hardware data, do not setup the buttons.
        if ((component.name != "" || component.name != "null") && !isLoadingFromPC && component.deletable) {
            //setup up the adding and removing buttons...
            val addHardware = hardwareDetailsBinding.addHardwareBtn
            val removeHardware = hardwareDetailsBinding.removeHardwareBtn
            //Initialise variable for database hardware removal, look at OnDestroy() for more information
            componentName = component.name

            setButtonText(addHardware, R.string.addHardware, component.type)
            setButtonText(removeHardware, R.string.removeHardware, component.type)

            //If component is in database, only allow remove button click, otherwise add button click
            if (fbHardwareDb.hardwareExists(component.name) > 0) {
                setClickable(noInteractionBtn = addHardware, hasInteractionBtn = removeHardware)
                //...then do not allow the user to click on removeHardware...
            } else {
                setClickable(noInteractionBtn = removeHardware, hasInteractionBtn = addHardware)
            }

            //Set on click listeners to buttons
            buttonListeners(addHardware, removeHardware, component)
            setUpRecyclerList(component)
            //... If the component can't be deleted, then get rid of the buttons.
        } else {
            hardwareDetailsBinding.addHardwareBtn.visibility = View.GONE
            hardwareDetailsBinding.removeHardwareBtn.visibility = View.GONE
            setUpRecyclerList(component)
        }
    }

    /**
     * Assigns on click listeners to both the save and remove component buttons in hardware details.
     * @param addHardware Button used to insert/save components to the App's database.
     * @param removeHardware Button used to remove/delete components to the App's database.
     * @param component The loaded Component, used for correctly inserting or removing the component to/from the App's database.
     */
    private fun buttonListeners(addHardware: Button, removeHardware: Button, component: Component) {
        //Add the component to the database, make this button un-clickable
        addHardware.setOnClickListener {
            //and allow the user to click on the remove hardware button
            if (isLoadingFromServer == true) {
                coroutineScope.launch {
                    fbHardwareDb.insertHardware(component)
                }
            } else offlineRemoveHardwareOnDestroy = false
            Toast.makeText(context, "Component Saved", Toast.LENGTH_SHORT).show()
            setClickable(noInteractionBtn = addHardware, hasInteractionBtn = removeHardware)
        }

        //Remove the component from the database, make this button un-clickable
        removeHardware.setOnClickListener {
            //and allow the user to click on the add hardware button
            if (isLoadingFromServer == true) {
                coroutineScope.launch {
                    fbHardwareDb.removeHardware(component.name)
                }
            } else offlineRemoveHardwareOnDestroy = true
            Toast.makeText(context, "Component Removed", Toast.LENGTH_SHORT).show()
            setClickable(noInteractionBtn = removeHardware, hasInteractionBtn = addHardware)
        }
    }

    /**
     * When a component is removed from the database, check if it is in the any compared tables or PC builds in the database.
     * If it is, then remove the component from either the compared list, PC build (or both) accordingly and updating all affect data.
     * @param component The component the app is going to remove.
     */
    private fun pcAndCompareListChecks(component: Component) {

        //Check if component is in compared list, if it is, then remove it from it's compared table first.
        if (fbHardwareDb.checkIfComponentIsInComparedTable(component.name) > 0) {
            fbHardwareDb.removeComparedComponent(component.name)
        }
        //Check if component is in any writable PC Builds, if it is, then remove it any writable PC Builds
        if (fbHardwareDb.checkIfComponentIsInAnyPC(component.name, component.type) > 0) {
            fbHardwareDb.removeComponentFromAllPCs(
                component.name,
                component.type,
                component.rrpPrice
            )
        }
    }

    /**
     * Setup the recycler list that display the component's details.
     * @param component the loaded component being displayed
     */
    private fun setUpRecyclerList(component: Component) {

        //Display the component's name at the top of the screen
        hardwareDetailsBinding.componentNameDisplay.text = component.name

        val displayHardwareDetails = hardwareDetailsBinding.detailsRecyclerList
        displayHardwareDetails.layoutManager = LinearLayoutManager(this.context)
        hardwareDetailsListAdapter = HardwareDetailsRecyclerList(context, this)

        val details: List<String> = context?.let { component.getDetailsForDisplay(it, null) }!!

        hardwareDetailsListAdapter.setDataList(details)
        displayHardwareDetails.adapter = hardwareDetailsListAdapter

    }

    /**
     * Add text to the "add hardware" button depending on component's category.
     * e.g. Add button text = "Add Gpu". Remove button text = "Remove Gpu"
     */
    private fun setButtonText(button: Button, stringID: Int, type: String) {
        button.text = context?.resources?.getString(
            stringID,
            type.capitalize(Locale.ROOT)
        )
    }

    /**
     * Only allows user interaction with one button at a time in the hardware display screen.
     */
    private fun setClickable(noInteractionBtn: Button, hasInteractionBtn: Button) {
        noInteractionBtn.isEnabled = false
        hasInteractionBtn.isEnabled = true
    }

    /**
     * Creates the default app bar menu
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.hardware_related_toolbar_items, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * If the hardware details is resumed from an online search,
     * make this false so that the component doesn't get removed
     * when it loads the standard button setup for offline hardware.
     */
    override fun onResume() {
        super.onResume()
        offlineRemoveHardwareOnDestroy = false
    }

    /**
     * If this fragment was used to load a component from the App's database
     * and the user wants the component to be removed, only remove the component on the fragments exit.
     */
    override fun onDestroy() {
        super.onDestroy()
        //If this class is loaded from SavedSearchComponents,
        //only remove this component from the database when the fragment is destroyed
        if (offlineRemoveHardwareOnDestroy) {
            pcAndCompareListChecks(component)
            runBlocking {
                componentName?.let {
                    fbHardwareDb.removeHardware(it)
                }
            }
        }
    }

    /**
     * Open the component's Amazon Or Scan.co.uk web page using a web view fragment.
     * @param clickedLink The selected Web URL
     */
    override fun onLinkClicked(clickedLink: String) {
        val navController =
            activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
        val linkBundle = Bundle()
        //Amazon URL
        if (clickedLink.indexOf("Amazon Price") != -1) {
            linkBundle.putString(URL_LINK, component.amazonLink)
        //Scan.co.uk URL
        } else if (clickedLink.indexOf("Scan.co.uk Price") != -1) {
            linkBundle.putString(URL_LINK, component.scanLink)
        }
        //Finds the action that allows navigation from this fragment to the web view,
        //with a bundle of the URL.
        navController?.navigate(
            R.id.action_hardwareDetails_fragmentID_to_webViewConnection_fragmentID,
            linkBundle
        )
    }
}
