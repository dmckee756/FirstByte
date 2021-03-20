package dam95.android.uk.firstbyte.gui.components.builds

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.api.util.ConvertImageURL
import dam95.android.uk.firstbyte.databinding.FragmentPersonalBuildBinding
import dam95.android.uk.firstbyte.datasource.ComponentDBAccess
import dam95.android.uk.firstbyte.gui.components.search.CATEGORY_KEY
import dam95.android.uk.firstbyte.gui.components.search.LOCAL_OR_NETWORK_KEY
import dam95.android.uk.firstbyte.gui.components.search.PC_ID
import dam95.android.uk.firstbyte.model.PCBuild
import dam95.android.uk.firstbyte.model.components.*
import dam95.android.uk.firstbyte.model.util.ComponentsEnum
import java.util.*

const val SELECTED_PC = "SELECTED_PC"
private const val NUM_OF_RAM = 1
private const val NUM_OF_STORAGE = 2

class PersonalBuild : Fragment(), PersonalBuildRecyclerList.OnItemListener {

    private lateinit var personalBuildBinding: FragmentPersonalBuildBinding
    private lateinit var fbHardwareDb: ComponentDBAccess
    private lateinit var personalBuildListAdapter: PersonalBuildRecyclerList
    private lateinit var personalPC: MutableLiveData<PCBuild>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val loadedPc = arguments?.getParcelable(SELECTED_PC) as PCBuild?

        if (loadedPc != null) {
            fbHardwareDb = ComponentDBAccess(requireContext())

            personalPC = loadedPc.pcID?.let { fbHardwareDb.retrievePC(it) }!!
            personalBuildBinding = FragmentPersonalBuildBinding.inflate(inflater, container, false)

            personalPC.observe(viewLifecycleOwner) {
                val pcParts = getPCBuildContents(it)
                setUpButtons()

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
        personalBuildListAdapter = PersonalBuildRecyclerList(context, fbHardwareDb, this)

        personalBuildListAdapter.setDataList(pcParts)
        displayDetails.adapter = personalBuildListAdapter
    }

    private fun setUpButtons() {
        personalBuildBinding.deletePcBtn.setOnClickListener {
            personalPC.observe(viewLifecycleOwner) {
                it.pcID?.let { id -> fbHardwareDb.deletePC(id) }
                requireActivity().onBackPressed()
            }
        }
    }

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

    override fun removePCPart(component: Component, position: Int) {
        val category = component.type

        //Check to determine if the pc part we want to remove is [RAM, STORAGE, FAN]
        if (category.toUpperCase(Locale.ROOT) == ComponentsEnum.RAM.toString()
            || category.toUpperCase(Locale.ROOT) == ComponentsEnum.STORAGE.toString()
            || category.toUpperCase(Locale.ROOT) == ComponentsEnum.FAN.toString()
        ) {
            //Remove the relational database pc part
            fbHardwareDb.removePCPart(
                category.toLowerCase(Locale.ROOT), component.name,
                personalPC.value?.pcID
            )
        } else {

            when (category.toUpperCase(Locale.ROOT)) {
                ComponentsEnum.CASES.toString() -> {
                    //If the pc part was the computer case...
                    //remove the personal build image at the top of the screen
                    //and remove the "overflowed" fan slots
                    personalBuildBinding.caseImage.background = null
                    personalBuildBinding.caseImage.visibility = View.GONE
                    val tempComponent = component as Case
                    personalPC.value?.pcID?.let {
                        fbHardwareDb.trimFanList(
                            "fan",
                            it,
                            tempComponent.case_fan_slots
                        )
                        personalBuildListAdapter.removeFans(tempComponent.case_fan_slots)
                    }
                }
                ComponentsEnum.HEATSINK.toString() -> {
                    //If the pc part was the heatsink...
                    //remove the "overflowed" fan slots
                    val tempComponent = component as Heatsink
                    personalPC.value?.pcID?.let {
                        fbHardwareDb.trimFanList(
                            "fan",
                            it,
                            tempComponent.fan_slots
                        )
                        personalBuildListAdapter.removeFans(tempComponent.fan_slots)
                    }
                }
            }
            //Remove the pc part within the pcbuilds table
            fbHardwareDb.removePCPart(category.toLowerCase(Locale.ROOT), null, null)
        }
        //After removing the pc part in the correct table, update the recycler list by freeing up the slot in the correct position.
        personalBuildListAdapter.removeDetail(position, category)
    }

    override fun updateTotalPrice(totalPrice: Double) {
        personalBuildBinding.pcTotalPrice.text =
            resources.getString(R.string.totalPrice, "£", totalPrice)
        personalPC.value?.pcID?.let { fbHardwareDb.updatePCPrice(totalPrice, it) }
    }
}