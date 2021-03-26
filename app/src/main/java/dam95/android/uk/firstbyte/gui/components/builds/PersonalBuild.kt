package dam95.android.uk.firstbyte.gui.components.builds

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.api.util.ConvertImageURL
import dam95.android.uk.firstbyte.databinding.FragmentPersonalBuildBinding
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import dam95.android.uk.firstbyte.gui.components.search.CATEGORY_KEY
import dam95.android.uk.firstbyte.gui.components.search.LOCAL_OR_NETWORK_KEY
import dam95.android.uk.firstbyte.gui.components.search.NAME_KEY
import dam95.android.uk.firstbyte.gui.components.search.PC_ID
import dam95.android.uk.firstbyte.model.PCBuild
import dam95.android.uk.firstbyte.model.components.*
import dam95.android.uk.firstbyte.model.util.ComponentsEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.*

const val SELECTED_PC = "SELECTED_PC"
private const val NUM_OF_RAM = 1
private const val NUM_OF_STORAGE = 2
const val FROM_PC = "FROM_PC"

class PersonalBuild : Fragment(), PersonalBuildRecyclerList.OnItemListener {

    private lateinit var personalBuildBinding: FragmentPersonalBuildBinding
    private lateinit var fbHardwareDb: FirstByteDBAccess
    private lateinit var personalBuildListAdapter: PersonalBuildRecyclerList
    private lateinit var personalPC: MutableLiveData<PCBuild>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val loadedPc = arguments?.getParcelable(SELECTED_PC) as PCBuild?
        if (loadedPc != null) {
            setHasOptionsMenu(true)
            fbHardwareDb = FirstByteDBAccess(requireContext(), Dispatchers.Main)

            personalPC = loadedPc.pcID?.let { fbHardwareDb.retrievePC(it) }!!
            personalBuildBinding = FragmentPersonalBuildBinding.inflate(inflater, container, false)

            personalPC.observe(viewLifecycleOwner) {
                val pcParts = getPCBuildContents(it)

                setUpPCDisplay(pcParts)
            }
        }
        return personalBuildBinding.root
    }

    /**
     *
     */
    private fun getPCBuildContents(personalPC: PCBuild): List<Pair<Component?, String>> {
        val pcParts: MutableList<Pair<Component?, String>> = mutableListOf()
        val pcSingularParts: List<Pair<String?, String>> = personalPC.pcPartsSearchConfig()

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

        //Load Ram
        pcParts.addAll(
            fbHardwareDb.retrievePCComponents(
                personalPC.pcID!!, ComponentsEnum.RAM.toString()
                    .toLowerCase(Locale.ROOT), personalPC.ramList, NUM_OF_RAM
            )
        )
        personalPC.ramList = attachedToPC(pcParts, NUM_OF_RAM)

        //Load Storage
        pcParts.addAll(
            fbHardwareDb.retrievePCComponents(
                personalPC.pcID!!, ComponentsEnum.STORAGE.toString()
                    .toLowerCase(Locale.ROOT), personalPC.storageList, NUM_OF_STORAGE
            )
        )
        personalPC.storageList = attachedToPC(pcParts, NUM_OF_STORAGE)

        //Load Fans
        pcParts.addAll(
            fbHardwareDb.retrievePCComponents(
                personalPC.pcID!!, ComponentsEnum.FAN.toString()
                    .toLowerCase(Locale.ROOT), personalPC.fanList, numberOfFans - 1
            )
        )
        personalPC.fanList = attachedToPC(pcParts, numberOfFans - 1)
        return pcParts
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

        personalBuildListAdapter.setDataList(pcParts)
        displayDetails.adapter = personalBuildListAdapter
    }

    /**
     *
     */
    override fun removePCPart(component: Component, position: Int) {
        val category = component.type

        //Check to determine if the pc part we want to remove is [RAM, STORAGE, FAN]
        if (category.toUpperCase(Locale.ROOT) == ComponentsEnum.RAM.toString()
            || category.toUpperCase(Locale.ROOT) == ComponentsEnum.STORAGE.toString()
            || category.toUpperCase(Locale.ROOT) == ComponentsEnum.FAN.toString()
        ) {
            //Remove the relational database pc part
            personalPC.value?.pcID?.let {
                fbHardwareDb.removeRelationalPCPart(
                    category.toLowerCase(Locale.ROOT), component.name,
                    it
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
            ComponentsEnum.CASES.toString() -> {
                //remove the personal build image at the top of the screen
                personalBuildBinding.caseImage.background = null
                personalBuildBinding.caseImage.visibility = View.GONE
                val tempComponent = component as Case
                //and remove the "overflowed" fan slots
                fbHardwareDb.trimFanList("fan", personalPC.value!!.pcID!!, tempComponent.case_fan_slots)
                personalBuildListAdapter.removeFans(tempComponent.case_fan_slots)
            }
            //If the pc part was the heatsink...
            ComponentsEnum.HEATSINK.toString() -> {
                val tempComponent = component as Heatsink
                //remove the "overflowed" fan slots
                fbHardwareDb.trimFanList("fan", personalPC.value!!.pcID!!, tempComponent.fan_slots)
                personalBuildListAdapter.removeFans(tempComponent.fan_slots)
            }
        }
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
            FROM_PC to true
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
        personalBuildBinding.pcTotalPrice.text = resources.getString(R.string.totalPrice, "£", personalPC.value!!.pcPrice)
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
        inflater.inflate(R.menu.pc_build_toolbar_items, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     *
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        fbHardwareDb.pcUpdateCompletedValue(personalPC.value!!)
        super.onPause()
    }

    override fun onDestroy() {
        fbHardwareDb.pcUpdateCompletedValue(personalPC.value!!)
        super.onDestroy()
    }
}