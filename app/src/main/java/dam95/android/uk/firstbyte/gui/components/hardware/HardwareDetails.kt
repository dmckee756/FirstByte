package dam95.android.uk.firstbyte.gui.components.hardware

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
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

/**
 *
 */
private const val NAME_KEY = "NAME"
private const val CATEGORY_KEY = "CATEGORY"
private const val LOCAL_OR_NETWORK_KEY = "LOADING_METHOD"
private const val COMPONENT_INDEX = 0

class HardwareDetails : Fragment(), HardwareDetailsRecyclerList.OnItemListener {

    private lateinit var hardwareDetailsBinding: FragmentHardwareDetailsBinding
    private lateinit var componentsFirstByteDB: FirstByteDBAccess
    private lateinit var hardwareDetailsListAdapter: HardwareDetailsRecyclerList
    private lateinit var component: Component

    private var isLoadingFromServer: Boolean? = null
    private var isLoadingFromPC: Boolean = false
    private var offlineRemoveHardwareOnDestroy = false
    private var componentName: String? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hardwareDetailsBinding = FragmentHardwareDetailsBinding.inflate(inflater, container, false)
        val componentName = arguments?.getString(NAME_KEY)
        val componentType = arguments?.getString(CATEGORY_KEY)
        isLoadingFromServer = arguments?.getBoolean(LOCAL_OR_NETWORK_KEY)
        isLoadingFromPC = arguments?.getBoolean(NOT_FROM_SEARCH) == true

        if (componentName != null && componentType != null) {
            setHasOptionsMenu(true)
            //Load FB_Hardware_Android Instance
            componentsFirstByteDB =
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.hardware_related_toolbar_items, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     *
     */
    private fun streamInHardware(name: String, type: String) {
        val apiRepository = ApiRepository(requireContext())
        val apiViewModel = ApiViewModel(apiRepository)

        apiViewModel.getHardware(name, type)
        apiViewModel.apiHardwareResponse.observe(viewLifecycleOwner, { res ->
            //
            if (res.isSuccessful) {
                //
                if (res.body()?.get(COMPONENT_INDEX) != null) {
                    //Because deletable is not on the API or Server database,
                    // retrofit makes it by default not deletable, therefore we must explicitly make it deletable
                    res.body()!![0].deletable = true
                    component = res.body()!![0]
                    ConvertImageURL.convertURLtoImage(
                        res.body()!![COMPONENT_INDEX].imageLink,
                        hardwareDetailsBinding.componentImage
                    )
                    setUpButtons(res.body()!![COMPONENT_INDEX])
                    //
                } else {
                    Log.e("NULL_COMPONENT", "Error, loaded hardware component is empty.")
                    return@observe
                }
                //
            } else {
                Log.e("HARDWARE_RES_FAIL", "FAILED LOAD")
                return@observe
            }
        })
    }

    /**
     *
     */
    private fun loadSavedHardware(name: String, type: String) {
        coroutineScope.launch {
            component =
                componentsFirstByteDB.retrieveHardware(name, type)

            ConvertImageURL.convertURLtoImage(
                component.imageLink,
                hardwareDetailsBinding.componentImage
            )
            setUpButtons(component)
        }
    }

    /**
     * Setup up the buttons that allow the user to both add and remove a component to/from
     * the Component MySQLite database.
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
                if (componentsFirstByteDB.hardwareExists(component.name) > 0) {
                    setClickable(noInteractionBtn = addHardware, hasInteractionBtn = removeHardware)
                    //...then do not allow the user to click on removeHardware...
                } else {
                    setClickable(noInteractionBtn = removeHardware, hasInteractionBtn = addHardware)
                }

            buttonListeners(addHardware, removeHardware, component)
            setUpRecyclerList(component)
            //... If the component can't be deleted, then get rid of the buttons.
        } else {
            hardwareDetailsBinding.addHardwareBtn.visibility = View.GONE
            hardwareDetailsBinding.removeHardwareBtn.visibility = View.GONE
            setUpRecyclerList(component)
        }
    }

    private fun buttonListeners(addHardware: Button, removeHardware: Button, component: Component) {
        addHardware.setOnClickListener {
            //Add the component to the database, make this button un-clickable
            //and allow the user to click on the remove hardware button
            if (isLoadingFromServer == true) {
                coroutineScope.launch {
                    componentsFirstByteDB.insertHardware(component)
                }
            } else offlineRemoveHardwareOnDestroy = false
            Toast.makeText(context, "Component Saved", Toast.LENGTH_SHORT).show()
            setClickable(noInteractionBtn = addHardware, hasInteractionBtn = removeHardware)
        }
        removeHardware.setOnClickListener {
            //Remove the component from the database, make this button un-clickable
            //and allow the user to click on the add hardware button
            if (isLoadingFromServer == true) {
                coroutineScope.launch {
                    componentsFirstByteDB.removeHardware(component.name)
                }
            } else offlineRemoveHardwareOnDestroy = true
            Toast.makeText(context, "Component Removed", Toast.LENGTH_SHORT).show()
            setClickable(noInteractionBtn = removeHardware, hasInteractionBtn = addHardware)
        }
    }

    /**
     *
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
     * e.g. Add button text = "Add GPU". Remove button text = "Remove GPU"
     */
    private fun setButtonText(button: Button, stringID: Int, type: String) {
        button.text = context?.resources?.getString(
            stringID,
            type.toUpperCase(Locale.ROOT)
        )
    }

    /**
     * Only allows user interaction with one button at a time in the hardware display screen.
     */
    private fun setClickable(noInteractionBtn: Button, hasInteractionBtn: Button) {
        noInteractionBtn.isEnabled = false
        hasInteractionBtn.isEnabled = true
    }

    override fun onResume() {
        super.onResume()
        //If the hardware details is resumed from an online search,
        //make this false so that the component doesn't get removed
        //when it loads the standard button setup for offline hardware.
        offlineRemoveHardwareOnDestroy = false
    }

    /**
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        //If this class is loaded from SavedSearchComponents,
        //only remove this component from the database when the fragment is destroyed
        if (offlineRemoveHardwareOnDestroy) {
            runBlocking {
                componentName?.let {
                    componentsFirstByteDB.removeHardware(it)
                }
            }
        }
    }

    /**
     *
     */
    override fun onLinkClicked(clickedLink: String) {
        val navController =
            activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
        val linkBundle = Bundle()
        //
        if (clickedLink.indexOf("Amazon Price") != -1) {
            linkBundle.putString(URL_LINK, component.amazonLink)
            //
        } else if (clickedLink.indexOf("Scan.co.uk Price") != -1) {
            linkBundle.putString(URL_LINK, component.scanLink)
        }
        //
        navController?.navigate(
            R.id.action_hardwareDetails_fragmentID_to_webViewConnection_fragmentID,
            linkBundle
        )
    }
}
