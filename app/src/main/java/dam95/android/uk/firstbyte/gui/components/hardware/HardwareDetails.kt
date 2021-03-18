package dam95.android.uk.firstbyte.gui.components.hardware

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.api.util.ConvertImageURL
import dam95.android.uk.firstbyte.api.ApiRepository
import dam95.android.uk.firstbyte.api.ApiViewModel
import dam95.android.uk.firstbyte.databinding.FragmentHardwareDetailsBinding
import dam95.android.uk.firstbyte.datasource.ComponentDBAccess
import dam95.android.uk.firstbyte.gui.components.search.HardwareList
import dam95.android.uk.firstbyte.model.components.Component
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 *
 */
private const val NAME_KEY = "NAME"
private const val CATEGORY_KEY = "CATEGORY"
private const val LOCAL_OR_NETWORK_KEY = "LOADING_METHOD"
private const val COMPONENT_INDEX = 0

class HardwareDetails : Fragment() {

    private lateinit var hardwareDetailsBinding: FragmentHardwareDetailsBinding
    private lateinit var componentsComponentDB: ComponentDBAccess
    private lateinit var hardwareDetailsListAdapter: HardwareDetailsRecyclerList

    private var isLoadingFromServer: Boolean? = null
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

        if (componentName != null && componentType != null) {
            //Load FB_Hardware_Android Instance
            componentsComponentDB = context?.let { ComponentDBAccess.dbInstance(it) }!!

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
                    ConvertImageURL.convertURLtoImage(
                        res.body()!![COMPONENT_INDEX].imageLink,
                        hardwareDetailsBinding.componentImage
                    )
                    setUpButtons(res.body()!![COMPONENT_INDEX])
                    //
                } else {
                    Log.e("NULL_COMPONENT", "Error, loaded hardware component is empty.")
                }
                //
            } else {
                Log.e("HARDWARE_RES_FAIL", "FAILED LOAD")
            }
        })
    }

    /**
     *
     */
    private fun loadSavedHardware(name: String, type: String) {
        coroutineScope.launch {
            val component: Component =
                componentsComponentDB.getHardware(name, type)

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

        //If the component cannot be delete or the is offline without cached hardware data, do not setup the buttons. TODO REMOVE '!' from !component.deletable when that problem is fixed
        //THIS CHECK NEEDS WORK, IT DOESN'T MAKE THE BUTTONS DISAPPEAR, BUT IT STOPS THE LISTENERS TO USERS CAN'T ADD NON EXISTENT DATA... SO OK FOR NOW I GUESS
        if (component.name != "" || component.name != "null") {
            //setup up the adding and removing buttons...
            val addHardware = hardwareDetailsBinding.addHardwareBtn
            val removeHardware = hardwareDetailsBinding.removeHardwareBtn
            //Initialise variable for database hardware removal, look at OnDestroy() for more information
            componentName = component.name

            setButtonText(addHardware, R.string.addHardware, component.type)
            setButtonText(removeHardware, R.string.removeHardware, component.type)

            //If the loaded component is already stored in the Component Database...
            //...then do not allow the user to click on addHardware
            coroutineScope.launch {
                if (componentsComponentDB.hardwareExists(component.name) > 0) {
                    setClickable(noInteractionBtn = addHardware, hasInteractionBtn = removeHardware)
                    //...then do not allow the user to click on removeHardware...
                } else {
                    setClickable(noInteractionBtn = removeHardware, hasInteractionBtn = addHardware)
                }
            }

            addHardware.setOnClickListener {
                //Add the component to the database, make this button un-clickable
                //and allow the user to click on the remove hardware button
                if (isLoadingFromServer == true) {
                    coroutineScope.launch {
                        componentsComponentDB.insertHardware(component)
                    }
                } else offlineRemoveHardwareOnDestroy = false
                setClickable(noInteractionBtn = addHardware, hasInteractionBtn = removeHardware)
            }
            removeHardware.setOnClickListener {
                //Remove the component from the database, make this button un-clickable
                //and allow the user to click on the add hardware button
                if (isLoadingFromServer == true) {
                    coroutineScope.launch {
                        componentsComponentDB.removeHardware(component.name)
                    }
                } else offlineRemoveHardwareOnDestroy = true
                setClickable(noInteractionBtn = removeHardware, hasInteractionBtn = addHardware)
            }
            setUpRecyclerList(component)
            //... If the component can't be deleted, then get rid of the buttons.
        } else {
            Log.i("DOES_IT_REACH?", "HELLO????") //Bug with it not reaching this code
            hardwareDetailsBinding.addHardwareBtn.visibility = View.GONE
            hardwareDetailsBinding.removeHardwareBtn.visibility = View.GONE
            setUpRecyclerList(component)
        }
    }

    /**
     *
     */
    private fun setUpRecyclerList(component: Component){

        //Display the component's name at the top of the screen
        hardwareDetailsBinding.componentNameDisplay.text = component.name

        val displayHardwareDetails = hardwareDetailsBinding.detailsRecyclerList
        displayHardwareDetails.layoutManager = LinearLayoutManager(this.context)
        hardwareDetailsListAdapter = HardwareDetailsRecyclerList(context)

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

    /**
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        if (isLoadingFromServer!!) componentsComponentDB.closeDatabase()
        //If this class is loaded from SavedSearchComponents,
        //only remove this component from the database when the fragment is destroyed
        if (offlineRemoveHardwareOnDestroy) {
            coroutineScope.launch {
                componentName?.let {
                    componentsComponentDB.removeHardware(it)
                }
            }
        }
    }
}
