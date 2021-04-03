package dam95.android.uk.firstbyte.gui.components.builds

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
import kotlinx.coroutines.Dispatchers
import java.util.*

const val SELECTED_PC = "SELECTED_PC"
private const val NUM_OF_RAM = 1
private const val NUM_OF_STORAGE = 2
const val NOT_FROM_SEARCH = "FROM_PC"

class PersonalBuild : Fragment(), PersonalBuildRecyclerList.OnItemListener {

    private lateinit var personalBuildBinding: FragmentPersonalBuildBinding
    private lateinit var fbHardwareDb: FirstByteDBAccess
    private lateinit var personalBuildListAdapter: PersonalBuildRecyclerList
    private lateinit var personalPC: MutableLiveData<PCBuild>
    private var readOnlyPC = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val loadedPc = arguments?.getParcelable(SELECTED_PC) as PCBuild?
        readOnlyPC = arguments?.getBoolean(READ_ONLY_PC)!!
        //If there is no loadedPC from arguments, then skip the initialisation
        if (loadedPc != null) {
            setHasOptionsMenu(true)
            fbHardwareDb = FirstByteDBAccess(requireContext(), Dispatchers.Main)

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
                if (!readOnlyPC) {
                    //Edit PC name button listener
                    personalBuildBinding.changePCName.setOnClickListener {
                        dialogBox()
                    }
                } else {
                    val saveIcon = ResourcesCompat.getDrawable(requireContext().resources, R.drawable.ic_add_recommended_pc, null)
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
     *
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

    private fun getRelationalParts(
        pcParts: MutableList<Pair<Component?, String>>,
        loadedPC: PCBuild,
        numberOfFans: Int
    ) {
        //Load Ram
        pcParts.addAll(fbHardwareDb.retrievePCComponents(
                loadedPC.pcID!!, ComponentsEnum.RAM.toString()
                    .toLowerCase(Locale.ROOT),loadedPC.ramList, NUM_OF_RAM))
        loadedPC.ramList = attachedToPC(pcParts, NUM_OF_RAM)

        //Load Storage
        pcParts.addAll(fbHardwareDb.retrievePCComponents(
            loadedPC.pcID!!, ComponentsEnum.STORAGE.toString()
                .toLowerCase(Locale.ROOT), loadedPC.storageList, NUM_OF_STORAGE))
        loadedPC.storageList = attachedToPC(pcParts, NUM_OF_STORAGE)

        //Load Fans
        pcParts.addAll(fbHardwareDb.retrievePCComponents(
                loadedPC.pcID!!, ComponentsEnum.FAN.toString()
                    .toLowerCase(Locale.ROOT), loadedPC.fanList, numberOfFans - 1))
        loadedPC.fanList = attachedToPC(pcParts, numberOfFans - 1)
    }

    /**
     *
     */
    private fun attachedToPC(pcParts: List<Pair<Component?, String>>, slots: Int): List<String?> {
        val tempList = mutableListOf<String?>()
        for (i in slots downTo 0) {
            pcParts[pcParts.lastIndex - i].first?.let { tempList.add(it.name) }
                ?: tempList.add(null)
        }
        return tempList
    }

    /**
     *
     */
    private fun setUpPCDisplay(pcParts: List<Pair<Component?, String>>) {
        //
        val displayDetails = personalBuildBinding.pcDetailsRecyclerList
        //
        displayDetails.layoutManager = LinearLayoutManager(this.context)
        personalBuildListAdapter = PersonalBuildRecyclerList(context, this)

        personalBuildListAdapter.setDataList(pcParts, readOnlyPC)
        displayDetails.adapter = personalBuildListAdapter
    }

    /**
     *
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
     *
     */
    private fun removeExtraFans(category: String, component: Component) {
        when (category.toUpperCase(Locale.ROOT)) {
            //If the pc part was the computer case...
            ComponentsEnum.CASES.toString() -> removeCaseFans(component as Case)
            //If the pc part was the heatsink...
            ComponentsEnum.HEATSINK.toString() -> removeHeatsinkFans(component as Heatsink)
        }
    }

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

    private fun removeHeatsinkFans(component: Heatsink){
        //remove fans from recycler list first and update the price...
        personalBuildListAdapter.removeFans(component.fan_slots)
        //then remove the "overflowed" fan slots
        fbHardwareDb.trimFanList("fan", personalPC.value!!.pcID!!, component.fan_slots)
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

                if (userInput?.text.toString().isBlank()){
                    //If the user changes the pc to a blank name, then set the default name
                    personalPC.value?.pcName = "New-PC"
                } else{
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
     *
     */
    override fun onAddButtonClick(addCategory: String) {

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
     *
     */
    override fun goToHardware(componentName: String, componentType: String) {
        //
        val nameBundle = bundleOf(
            NAME_KEY to componentName,
            CATEGORY_KEY to componentType,
            LOCAL_OR_NETWORK_KEY to false,
            NOT_FROM_SEARCH to true
        )

        //
        val navController =
            activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
        navController?.navigate(
            R.id.action_personalBuild_to_hardwareDetails_fragmentID,
            nameBundle
        )
    }

    /**
     *
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


    override fun pcCompleted(isCompleted: Boolean) {
        personalPC.value!!.isPcCompleted = isCompleted
    }

    /**
     *
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        if (readOnlyPC){
            inflater.inflate(R.menu.readonlypc_toolbar_items, menu)
        } else {
            inflater.inflate(R.menu.pc_build_toolbar_items, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     *
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (readOnlyPC){
            readOnlyPCToolbars(item)
        } else {
            writeablePCToolbars(item)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun readOnlyPCToolbars(item: MenuItem){
        when (item.itemId){
            R.id.editRecommendedPCID -> {
                personalPC.value!!.deletable = true
                val result = fbHardwareDb.createPC(personalPC.value!!)
                item.isEnabled = false
                item.icon.alpha = 155
                if (result >= 0){
                    Toast.makeText(context, "Recommended PC Saved.", Toast.LENGTH_SHORT).show()
                } else {
                    personalPC.value!!.deletable = false
                    Toast.makeText(context, "Cannot save PC, at max limit.", Toast.LENGTH_SHORT).show()
                }
            }
            // Display a tip to the user
            R.id.tipsID -> Toast.makeText(context, "Tip Displayed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun writeablePCToolbars(item: MenuItem){
        //Otherwise execute toolbar button command.
        when (item.itemId) {
            // Delete the PC and back out to the pc list
            R.id.deleteID -> personalPC.observe(viewLifecycleOwner) {
                it.pcID?.let { id -> fbHardwareDb.deletePC(id) }
                requireActivity().onBackPressed()
            }
            // Display a tip to the user
            R.id.tipsID -> Toast.makeText(context, "Tip Displayed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        
        if (this::fbHardwareDb.isInitialized) fbHardwareDb.pcUpdateCompletedValue(personalPC.value!!)
        super.onPause()
    }

    override fun onDestroy() {
        if (this::fbHardwareDb.isInitialized) fbHardwareDb.pcUpdateCompletedValue(personalPC.value!!)
        super.onDestroy()
    }
}