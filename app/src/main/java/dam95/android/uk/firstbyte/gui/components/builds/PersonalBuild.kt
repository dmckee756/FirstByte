package dam95.android.uk.firstbyte.gui.components.builds

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.api.util.ConvertImageURL
import dam95.android.uk.firstbyte.databinding.FragmentPersonalBuildBinding
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import dam95.android.uk.firstbyte.gui.components.search.CATEGORY_KEY
import dam95.android.uk.firstbyte.gui.components.search.LOCAL_OR_NETWORK_KEY
import dam95.android.uk.firstbyte.gui.components.search.NAME_KEY
import dam95.android.uk.firstbyte.gui.components.search.PC_ID
import dam95.android.uk.firstbyte.gui.mainactivity.READ_ONLY_PC
import dam95.android.uk.firstbyte.model.PCBuild
import dam95.android.uk.firstbyte.model.components.*
import dam95.android.uk.firstbyte.model.util.ComponentsEnum
import dam95.android.uk.firstbyte.model.util.HumanReadableUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*

const val SELECTED_PC = "SELECTED_PC"
private const val NUM_OF_RAM = 1
private const val NUM_OF_STORAGE = 2
const val NOT_FROM_SEARCH = "FROM_PC"

/**
 * @author David Mckee
 * @Version 1.0
 * This class loads all components of a PC Build and allows the user to see all PC Parts,
 * and edit the build by adding/removing PC Parts in a dynamic fashion. Displays some hardware incompatibilities,
 * if a part if required for the PC to be functional and updates PC values when necessary.
 *
 * It also allows users to inspect Read only recommended build, which they cannot edit. But instead
 * they can saved the recommended build for editing (creates a new instance of the recommended build).
 */
class PersonalBuild : Fragment(), PersonalBuildRecyclerList.OnItemListener {

    private lateinit var personalBuildBinding: FragmentPersonalBuildBinding
    private lateinit var fbHardwareDb: FirstByteDBAccess
    private lateinit var personalBuildListAdapter: PersonalBuildRecyclerList
    private lateinit var personalPC: MutableLiveData<PCBuild>
    private var readOnlyPC = false

    /**
     * Load a MutableLiveData instance of the passed in PCBuild,
     * load all of the component's in the PCBuild into their objects (with all of their details)
     * and pass all of these components to the recycler list for display and editing.
     * Display the PC with it's correct name and the PC's image (Image of it's Computer Case).
     *
     * If this is a read only computer, reset the alpha value of the edit Recommended Build icon.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val loadedPc = arguments?.getParcelable(SELECTED_PC) as PCBuild?
        readOnlyPC = arguments?.getBoolean(READ_ONLY_PC)!!
        //If there is no loadedPC from arguments, then skip the initialisation
        if (loadedPc != null) {

            setHasOptionsMenu(true)
            fbHardwareDb = FirstByteDBAccess(requireContext(), Dispatchers.Default)

            //Load the arguments pc from the database as a MutableLiveData variable.
            //This allows the features of live data updating etc.
            personalPC = loadedPc.pcID?.let { fbHardwareDb.retrievePC(it) }!!
            personalBuildBinding = FragmentPersonalBuildBinding.inflate(inflater, container, false)

            //Allow the loaded PC build to be edited with live data updating.
            personalPC.observe(viewLifecycleOwner) {

                //Load all of the PC's components
                val pcParts = getPCBuildContents(it)

                //Display the correct pc name
                personalBuildBinding.pcNameDisplay.text = personalPC.value?.pcName
                //If this is a writable PC, allow the user to change the name of the PC.
                if (!readOnlyPC) {
                    //Edit PC name button listener
                    personalBuildBinding.changePCName.setOnClickListener {
                        dialogBox()
                    }
                } else {
                    //If this is a read only recommended PC, don't allow the user to change the PC's name and reset
                    //the icon for saving a recommended build.
                    val saveIcon = ResourcesCompat.getDrawable(
                        requireContext().resources,
                        R.drawable.ic_add_recommended_pc,
                        null
                    )
                    saveIcon!!.alpha = 255
                    personalBuildBinding.changePCName.visibility = View.GONE
                }
                //Set up the recycler list with loaded PC and allow it to be edited.
                setUpPCDisplay(pcParts)
            }
        }
        return personalBuildBinding.root
    }

    /**
     * Load all of the components that is a part of the PC from the database and put their Component objects it a
     * paired list, with the first slot being the Component object and the second slot being an identifier on what type of
     * component inhabits this slot of the list.
     */
    private fun getPCBuildContents(loadedPC: PCBuild): List<Pair<Component?, String>> {
        val pcParts: MutableList<Pair<Component?, String>> = mutableListOf()
        val pcSingularParts: List<Pair<String?, String>> = loadedPC.pcPartsSearchConfig()

        var numberOfFans = 0

        //Get's all information from each computer part.
        //Not all component information will be displayed, but it allows easier tweaking to what info will be displayed.
        for (i in pcSingularParts.indices) {
            //If there is a component in the current slot of the computer, then load it,
            //otherwise just add an empty slot for users to add it in their pc build

            pcSingularParts[i].first?.let {
                pcParts.add(
                    Pair(
                        fbHardwareDb.retrieveHardware(it, pcSingularParts[i].second),
                        pcSingularParts[i].second.capitalize(Locale.ROOT)
                    )
                )
            } ?: pcParts.add(Pair(null, pcSingularParts[i].second.capitalize(Locale.ROOT)))
            //If this component is either a Case or a fan,
            //add the number of fan slots that the computer can hold for dynamic number of fans.
            //If a heatsink or case is removed, it will take away that amount of fans.
            if (pcParts[i].first is Case) {
                val temp = pcParts[i].first as Case
                numberOfFans += temp.case_fan_slots

                ConvertImageURL.convertURLtoImage(
                    temp.imageLink,
                    personalBuildBinding.caseImage
                )
                personalBuildBinding.caseImage.visibility = View.VISIBLE
            } else if (pcParts[i].first is Heatsink) {
                val temp = pcParts[i].first as Heatsink
                numberOfFans += temp.fan_slots
            }

        }
        getRelationalParts(pcParts, loadedPC, numberOfFans)
        return pcParts
    }

    /**
     * Handle the loading of all components that is a part of the PC, but is in one of the many-to-many
     * relational tables [RAM, STORAGE, FAN].
     */
    private fun getRelationalParts(
        pcParts: MutableList<Pair<Component?, String>>,
        loadedPC: PCBuild,
        numberOfFans: Int
    ) {
        //Load Ram
        pcParts.addAll(
            fbHardwareDb.retrievePCComponents(
                loadedPC.pcID!!, ComponentsEnum.RAM.toString()
                    .toLowerCase(Locale.ROOT), loadedPC.ramList, NUM_OF_RAM
            )
        )
        loadedPC.ramList = attachedToPC(pcParts, NUM_OF_RAM)

        //Load Storage
        pcParts.addAll(
            fbHardwareDb.retrievePCComponents(
                loadedPC.pcID!!, ComponentsEnum.STORAGE.toString()
                    .toLowerCase(Locale.ROOT), loadedPC.storageList, NUM_OF_STORAGE
            )
        )
        loadedPC.storageList = attachedToPC(pcParts, NUM_OF_STORAGE)

        //Load Fans
        pcParts.addAll(
            fbHardwareDb.retrievePCComponents(
                loadedPC.pcID!!, ComponentsEnum.FAN.toString()
                    .toLowerCase(Locale.ROOT), loadedPC.fanList, numberOfFans - 1
            )
        )
        loadedPC.fanList = attachedToPC(pcParts, numberOfFans - 1)
    }

    /**
     * A loop method used to load the components that is a part of the PC, but is in one of the many-to-many
     * relational tables [RAM, STORAGE, FAN].
     */
    private fun attachedToPC(pcParts: List<Pair<Component?, String>>, slots: Int): List<String?> {
        val tempList = mutableListOf<String?>()
        //Attach the multi-slot components to the list of PC Parts.
        for (i in slots downTo 0) {
            pcParts[pcParts.lastIndex - i].first?.let { tempList.add(it.name) }
                ?: tempList.add(null)
        }
        return tempList
    }

    /**
     * Sets up the PersonalBuildRecyclerList with all of the PC Parts slots, but occupied and empty.
     * Allows the user to successfully edit the build and allows a dynamic list.
     * @param pcParts all current PC parts and empty pc slots that's a part of the PC.
     */
    private fun setUpPCDisplay(pcParts: List<Pair<Component?, String>>) {
        //Finds and initialises the correct recycler list for this fragment.
        val displayDetails = personalBuildBinding.pcDetailsRecyclerList
        displayDetails.layoutManager = LinearLayoutManager(this.context)
        personalBuildListAdapter = PersonalBuildRecyclerList(context, this)
        //Assigns all PC Part slots into the recycler list adapter
        personalBuildListAdapter.setDataList(pcParts, readOnlyPC)
        displayDetails.adapter = personalBuildListAdapter
    }

    /**
     * Navigates to the HardwareList fragment for the user to select what component they want to add to this PC Build
     * Will only see components of the same category type that are saved to the app's database.
     * @param addCategory The category of components that will only be displayed in the HardwareList
     */
    override fun onAddButtonClick(addCategory: String) {
        //Finds the action that allows navigation from the PersonalBuild fragment to the HardwareListFragment,
        //with a bundle of information indicating the category we want to see displayed,
        //that it's from the components fragment and indicating we only want to see components that are saved to the app's database.
        val addToPcBundle = bundleOf(
            CATEGORY_KEY to addCategory,
            LOCAL_OR_NETWORK_KEY to false,
            PC_ID to personalPC.value?.pcID
        )
        val navController =
            activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
        navController?.navigate(
            R.id.action_personalBuild_fragmentID_to_hardwareList_fragmentID,
            addToPcBundle
        )
    }

    /**
     * When the user removes a component from the PC, determine if it's a multi-slot component such as a RAM, STORAGE OR FAN.
     * If it is, then remove it from the PC Builds many-to-many relational table in the database.
     * If the removed component was a Heatsink or Case, then trim off the excess amount of fans on the PC.
     * If it was any other slot, free up the slot in the PC Builds table.
     *
     * Once the component is removed from the PC's in the database, update the recycler list that the slot is now unoccupied
     * and update the PC's total price, if the PC is still complete or incomplete (Part was required).
     */
    override fun removePCPart(component: Component, position: Int, relativePosition: Int) {
        val category = component.type

        //Check to determine if the pc part we want to remove is [RAM, STORAGE, FAN]
        if (category.toUpperCase(Locale.ROOT) == ComponentsEnum.RAM.toString()
            || category.toUpperCase(Locale.ROOT) == ComponentsEnum.STORAGE.toString()
            || category.toUpperCase(Locale.ROOT) == ComponentsEnum.FAN.toString()
        ) {
            //Remove the relational database pc part
            personalPC.value?.pcID?.let {
                fbHardwareDb.removeRelationalPCPart(
                    category.toLowerCase(Locale.ROOT), it, relativePosition
                )
            }
        } else {
            removeExtraFans(category, component)
            //Remove the pc part within the pcbuilds table
            fbHardwareDb.removePCPart(category.toLowerCase(Locale.ROOT), personalPC.value!!.pcID!!)
        }
        //After removing the pc part in the correct table, update the recycler list by freeing up the slot in the correct position.
        personalBuildListAdapter.removeDetail(position, category)
    }

    /**
     * Update the recycler list that Fan slots have been removed from the database if the component was
     * a heatsink or a case.
     */
    private fun removeExtraFans(category: String, component: Component) {
        when (category.toUpperCase(Locale.ROOT)) {
            //If the pc part was the computer case...
            ComponentsEnum.CASES.toString() -> removeCaseFans(component as Case)
            //If the pc part was the heatsink...
            ComponentsEnum.HEATSINK.toString() -> removeHeatsinkFans(component as Heatsink)
        }
    }

    /**
     * When the case is removed from the PC...
     * Trim off fan slots that were added because of the case fan slots from the database and inform the recycler list.
     */
    private fun removeCaseFans(component: Case) {
        //remove the personal build image at the top of the screen
        personalBuildBinding.caseImage.background = null
        personalBuildBinding.caseImage.visibility = View.GONE
        //remove fans from recycler list first and update the price...
        personalBuildListAdapter.removeFans(component.case_fan_slots)
        //and then remove the "overflowed" fan slots
        fbHardwareDb.trimFanList(
            "fan",
            personalPC.value!!.pcID!!,
            component.case_fan_slots
        )
    }

    /**
     * Updates the total price of the PC when the PC Build is edited.
     * E.g. PC Part is added/removed.
     */
    override fun updateTotalPrice(totalPrice: Double) {
        personalPC.value!!.pcPrice = totalPrice
        //Sometimes removing all hardware puts the price into a minutely small negative number,
        //which is a ticking time bomb. Therefore if the total price is below 0.00, enforce total price to equal 0.00.
        if (totalPrice < 0.00) personalPC.value!!.pcPrice = 0.00
        personalBuildBinding.pcTotalPrice.text =
            resources.getString(R.string.totalPrice, "£", personalPC.value!!.pcPrice)
        fbHardwareDb.updatePCPrice(personalPC.value!!.pcPrice, personalPC.value!!.pcID!!)
    }


    /**
     * Update the value indicating the PC is completed/functional.
     */
    override fun pcCompleted(isCompleted: Boolean) {
        personalPC.value!!.isPcCompleted = isCompleted
    }

    /**
     * When the heatsink is removed from the PC...
     * Trim off fan slots that were added because of the heatsink fan slots from the database and inform the recycler list.
     */
    private fun removeHeatsinkFans(component: Heatsink) {
        //remove fans from recycler list first and update the price...
        personalBuildListAdapter.removeFans(component.fan_slots)
        //then remove the "overflowed" fan slots
        fbHardwareDb.trimFanList("fan", personalPC.value!!.pcID!!, component.fan_slots)
    }

    /**
     * Navigates to the HardwareDetails fragment so we can see the PC Parts specifications/details.
     * @param componentName The name of the component that we are searching for/loading it's information.
     * @param componentType The component's category type, used to find what table we are loading the information from in the database.
     */
    override fun goToHardware(componentName: String, componentType: String) {
        //Finds the action that allows navigation from the PersonalBuild fragment to the HardwareDetails Fragment,
        //with a bundle of information indicating that the component details will be loaded from the app's database,
        //that it's from the PCBuild fragment (can't be added or removed to/from database)
        //and the name of the component we to see.
        val nameBundle = bundleOf(
            NAME_KEY to componentName,
            CATEGORY_KEY to componentType,
            LOCAL_OR_NETWORK_KEY to false,
            NOT_FROM_SEARCH to true
        )
        val navController =
            activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
        navController?.navigate(
            R.id.action_personalBuild_to_hardwareDetails_fragmentID,
            nameBundle
        )
    }

    /**
     * Allow the user to change the name of the PC in both it's display and in the database.
     * This will also update the name in the PC Build recycler list.
     */
    private fun dialogBox() {
        //Retrieve the alert box's xml layout
        val inflater: LayoutInflater? = activity?.layoutInflater
        val alertBoxLayout = inflater?.inflate(R.layout.alert_box_layout, null)
        val userInput = alertBoxLayout?.findViewById<EditText>(R.id.changePCNameEditText)

        val alertBuilder =
            AlertDialog.Builder(ContextThemeWrapper(activity, R.style.myAlertDialogTheme))

        //Setup a dialog box that allows the user to change the PC's Name
        alertBuilder.setTitle(activity?.resources?.getString(R.string.enterNewPCName))
            .setCancelable(false)
            .setPositiveButton(activity?.getString(R.string.ok_button)) { _, _ ->

                if (userInput?.text.toString().isBlank()) {
                    //If the user changes the pc to a blank name, then set the default name
                    personalPC.value?.pcName = "New-PC"
                } else {
                    //Change the PC Name to the user's inputted text...
                    personalPC.value?.pcName = userInput?.text.toString()
                }
                //Update the PC's display name...
                personalBuildBinding.pcNameDisplay.text = personalPC.value!!.pcName
                //Update the PC's name in the database.
                personalPC.value?.pcName?.let {
                    fbHardwareDb.changePCName(
                        personalPC.value?.pcID!!,
                        it
                    )
                }
            }
            .setNegativeButton(activity?.resources?.getString(R.string.cancel_button)) { dialog, _ ->
                // Don't change the PC name and dismiss the alert
                dialog.dismiss()
            }.setView(alertBoxLayout)
        //Show the alert box
        val alert = alertBuilder.create()
        alert.show()
    }

    /**
     * If the loaded PC is writable, the it applies an appbar menu that allows the user to delete the PC Build,
     * display tips and emailing the PC's details.
     *
     * If the loaded PC is read only, then it can be edited/saved with a Heart Icon, display tips and be emailed.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        if (readOnlyPC) {
            inflater.inflate(R.menu.readonlypc_toolbar_items, menu)
        } else {
            inflater.inflate(R.menu.pc_build_toolbar_items, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Correctly handle the menu item that was clicked by the user.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.emailID -> emailPC()
            R.id.deleteID -> areYouSureAlert()
            R.id.editRecommendedPCID -> editRecommendedPC(item)
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Gets all of the parts in a PC and sends it over to an email app, allowing the user
     * to share the names of components in their PC.
     */
    private fun emailPC() {
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            //Launch an send email intent
            val emailIntent = Intent(Intent.ACTION_SEND)
            //Indicate this is a mail to intent
            emailIntent.data = Uri.parse("mailto:")
            //Assign the subject's name
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My Designed PC")

            //Create a string variable that will hold the emails text.
            var emailText =
                "The name of my PC: ${personalPC.value!!.pcName}\n" +
                        "\nTotal Price of my PC: ${HumanReadableUtils.rrpPriceToCurrency(personalPC.value!!.pcPrice)}\n"

            val pcParts = personalBuildListAdapter.getDataList()
            //Put in all of the names of each PC Part in the PCBuild, into the email.
            for (index in pcParts.indices) {
                //Skip empty slots.
                if (pcParts[index].first != null) {
                    //Add component to the PC Build email.
                    emailText += "\n${pcParts[index].second}: ${pcParts[index].first!!.name}\n"
                }
            }

            //Bundle the PC Build's components into the email intent.
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailText)
            try {
                //Attempt to start this intent and send the contents of the email intent.
                startActivity(Intent.createChooser(emailIntent, "Choose Email App"))
            } catch (exception: Exception) {
                //Catch the exception and print the error to the user.
                exception.printStackTrace()
                Toast.makeText(requireContext(), "Error: $exception", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Handle menu edit Recommend PC item click, saves a new (separate) instance of the Recommended PC
     * into the database and allows the user to edit it.
     */
    private fun editRecommendedPC(item: MenuItem) {

        //If the heart icon was clicked, try adding the PC Build to the database for the user to edit.
        personalPC.value!!.deletable = true
        //Attempt to duplicate the PC as a writable Build
        val result = fbHardwareDb.createPC(personalPC.value!!)
        //Re-check this current Recommended PC as being read only, just in case.
        //It was altered changed in the database.
        personalPC.value!!.deletable = false
        //Disable the button and grey it out.
        item.isEnabled = false
        item.icon.alpha = 155

        if (result >= 0) {
            //If the build was succesfully added, then inform the user it was added to the database.
            Toast.makeText(context, "Recommended PC Saved.", Toast.LENGTH_SHORT).show()
        } else {
            //If the PC couldn't be added due to there being too many slots, inform the user.
            Toast.makeText(context, "Cannot save PC, at max limit.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    /**
     * Alert box to give the user a final chance to determine if they want to delete their PC.
     * Just in case they tried to click on the icon menu.
     */
    private fun areYouSureAlert() {

        val alertBuilder =
            AlertDialog.Builder(ContextThemeWrapper(activity, R.style.myAlertDialogTheme))

        //Setup a dialog box that allows the user to back out of their choice to delete this PC.
        alertBuilder.setTitle(activity?.resources?.getString(R.string.alertDeletePC))
            .setCancelable(false)
            .setPositiveButton(activity?.getString(R.string.yesButton)) { _, _ ->
                //Delete PC
                deletePC()
            }
            .setNegativeButton(activity?.resources?.getString(R.string.noButton)) { dialog, _ ->
                // Don't delete PC
                dialog.dismiss()
            }
        //Show the alert box
        val alert = alertBuilder.create()
        alert.show()
    }

    /**
     * Handle menu delete PC item click, which removes the pc from the database and backs the user out
     * to the pc build list.
     */
    private fun deletePC() {
        //Otherwise execute toolbar button command.
        // Delete the PC and back out to the pc list
        personalPC.observe(viewLifecycleOwner) {
            it.pcID?.let { id -> fbHardwareDb.deletePC(id) }
            requireActivity().onBackPressed()
        }
    }


    /**
     * If the database is initialized, update the database that this PC is either complete or incomplete when this fragment is paused.
     */
    override fun onPause() {
        if (this::fbHardwareDb.isInitialized) fbHardwareDb.pcUpdateCompletedValue(personalPC.value!!)
        super.onPause()
    }

    /**
     * If the database is initialized, update the database that this PC is either complete or incomplete when this fragment is destroyed.
     */
    override fun onDestroy() {
        if (this::fbHardwareDb.isInitialized) fbHardwareDb.pcUpdateCompletedValue(personalPC.value!!)
        super.onDestroy()
    }
}