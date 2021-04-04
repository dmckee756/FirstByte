package dam95.android.uk.firstbyte.gui.components.builds.util

import android.content.Context
import android.view.View
import android.widget.TextView
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.model.components.*
import dam95.android.uk.firstbyte.model.util.ComponentsEnum
import dam95.android.uk.firstbyte.model.util.HumanReadableUtils
import java.util.*

const val CPU_SLOT = 1
private const val GPU_SLOT = 0
private const val MOTHERBOARD_SLOT = 3

//Hardcoded way of dealing with ram slots
const val RAM_SLOT_START = 6
const val RAM_SLOT_END = 7
private const val RAM = "ram"

//Hardcoded way of dealing with storage slots
const val STORAGE_SLOT_START = 8
const val STORAGE_SLOT_END = 10
private const val STORAGE = "storage"
private const val NVME = "M.2 NVMe"
private const val FAN_SLOT_START = 11

/**
 * @author David Mckee
 * @Version 1.0
 * This class handles with all of the PC Parts compatibility checks and what PC Parts are required in a PC Build.
 * It also retrieves slots relative positions if it the component is RAM, STORAGE or FAN. When one of these components are deleted,
 * it will these sub lists down a slot, where applicable.
 */
class PersonalBuildChecks(var fanSlotEnd: Int = 0) {


    /**
     * This checks which components can display the additional information
     * underneath the components Rrp Price.
     */
    fun otherDetail(
        context: Context,
        _component: Component,
        otherDetail: TextView
    ) {
        val component: Component
        when (_component.type.toUpperCase(Locale.ROOT)) {
            //DISPLAY CPU DETAIL
            ComponentsEnum.CPU.toString() -> {
                component = _component as Cpu
                //Display the CPU's socket on the pc details
                otherDetail.text =
                    context.resources.getString(
                        R.string.displayProcessorSocket,
                        component.cpu_socket
                    )
                otherDetail.visibility = View.VISIBLE
                return
            }
            //DISPLAY RAM DETAIL
            ComponentsEnum.RAM.toString() -> {
                component = _component as Ram
                //Display the RAM Size in GB's on the pc details
                otherDetail.text = context.resources.getString(
                    R.string.ramDisplayMemorySize, component.memory_size_gb
                )
                otherDetail.visibility = View.VISIBLE
                return
            }
            //DISPLAY STORAGE DETAIL
            ComponentsEnum.STORAGE.toString() -> {
                component = _component as Storage
                //Display if the storage is external or not on the pc details
                otherDetail.text = context.resources.getString(
                    R.string.storageDisplayExternal,
                    HumanReadableUtils.tinyIntHumanReadable(component.isExternalStorage)
                )
                otherDetail.visibility = View.VISIBLE
                return
            }
            //DISPLAY MOTHERBOARD DETAIL
            ComponentsEnum.MOTHERBOARD.toString() -> {
                component = _component as Motherboard
                //Display the Motherboard's Cpu socket on the pc details
                otherDetail.text = context.resources.getString(
                    R.string.displayProcessorSocket,
                    component.processor_socket
                )
                otherDetail.visibility = View.VISIBLE
                return
            }
            //DISPLAY CASE DETAIL
            ComponentsEnum.CASES.toString() -> {
                component = _component as Case
                //Display the Cases' maximum Motherboard on the pc details
                otherDetail.text = context.resources.getString(
                    R.string.caseDisplayMotherboard,
                    component.case_motherboard
                )
                otherDetail.visibility = View.VISIBLE
                return
            }
            //DISPLAY FAN DETAIL
            ComponentsEnum.FAN.toString() -> {
                component = _component as Fan
                //Display the Fan size on the pc details
                otherDetail.text =
                    context.resources.getString(R.string.fanDisplayFanSize, component.fan_size_mm)
                otherDetail.visibility = View.VISIBLE
                return
            }
        }
    }

    /**
     * Determine which pc components are required for the computer to be "completed"
     * These slots will have the "PC Part Required" Button/icon displayed, informing the user it needs to be added
     * to have a functional PC.
     */
    fun partRequired(currentPosition: Int, componentsList: List<Pair<Component?, String>>): Int {
        when (componentsList[currentPosition].second.toUpperCase(Locale.ROOT)) {
            //GPU is required
            ComponentsEnum.GPU.toString() -> return isRequiredIconDisplayed(componentsList[currentPosition].first)
            //CPU is required
            ComponentsEnum.CPU.toString() -> return isRequiredIconDisplayed(componentsList[currentPosition].first)
            //PSU is required
            ComponentsEnum.PSU.toString() -> return isRequiredIconDisplayed(componentsList[currentPosition].first)
            //1 Ram slot is required
            ComponentsEnum.RAM.toString() -> return searchLocalSlots(
                componentsList,
                RAM_SLOT_START,
                RAM_SLOT_END,
                RAM
            )
            //1 storage slot is required
            ComponentsEnum.STORAGE.toString() -> return searchLocalSlots(
                componentsList,
                STORAGE_SLOT_START,
                STORAGE_SLOT_END,
                STORAGE
            )
            //Motherboard is required
            ComponentsEnum.MOTHERBOARD.toString() -> return isRequiredIconDisplayed(componentsList[currentPosition].first)
            //Case is required
            ComponentsEnum.CASES.toString() -> return isRequiredIconDisplayed(componentsList[currentPosition].first)
            //If Cpu requires Heatsink, then Heatsink is required
            ComponentsEnum.HEATSINK.toString() -> {
                val cpu = componentsList[CPU_SLOT].first as Cpu?
                return if (cpu == null || cpu.hasHeatsink != 0) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }
        }
        return View.GONE
    }

    /**
     * Since RAM and STORAGE is required for a PC to work, this method checks if at least one slot has Ram or Storage in it.
     * If it does, then the other slots are not required for the PC to be functional.
     * To clarify, this only applies to the components of the same category.
     *
     * Having an occupied Ram slot will not get rid of the "PC Part required" button/icon in storage slots, and vice versa.
     */
    private fun searchLocalSlots(
        componentsList: List<Pair<Component?, String>>,
        slotStart: Int,
        slotEnd: Int,
        desiredType: String
    ): Int {
        //For each slot in this "Sublist"
        for (index in slotStart..slotEnd) {
            //If at least one slot is not empty, AKA is occupied, then all slots of this "Sublist"
            //Will have their "PC Part required" button hidden/removed
            if (componentsList[index].first != null &&
                componentsList[index].second.toLowerCase(Locale.ROOT) == desiredType
            ) {
                return View.GONE
            }
        }
        return View.VISIBLE
    }

    /**
     * Method to make sure that the required PC Part slot is empty.
     */
    private fun isRequiredIconDisplayed(component: Component?): Int {
        return if (component == null) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    /**
     * This checks the current compatibilities between PC Parts in the PC Build.
     * If the PC Part is incompatible, both Parts will display the incompatible button...
     * Excluding the motherboard slot, because doing that would be a large method for one small gain.
     *
     * However currently when the user clicks on the incompatible button,
     * it doesn't tell them which component's are incompatible with what, and why.
     */
    fun checkCompatibility(
        position: Int,
        componentsList: List<Pair<Component?, String>>
    ): Int {
        when (componentsList[position].second.toUpperCase(Locale.ROOT)) {
            //Compare CPU and Motherboard sockets
            ComponentsEnum.CPU.toString() ->
                return cpuCompatibility(
                    componentsList[position].first as Cpu?,
                    componentsList[MOTHERBOARD_SLOT].first as Motherboard?
                )

            //Compare PSU wattage with CPU, GPU wattage
            ComponentsEnum.PSU.toString() ->
                return psuCompatibility(
                    componentsList[position].first as Psu?,
                    componentsList[GPU_SLOT].first as Gpu?,
                    componentsList[CPU_SLOT].first as Cpu?
                )

            //Compare Ram and Motherboard DDR
            ComponentsEnum.RAM.toString() -> return ramCompatibility(
                componentsList[position].first as Ram?,
                componentsList[MOTHERBOARD_SLOT].first as Motherboard?
            )

            //Compare storage NVME with Motherboard NVME support
            ComponentsEnum.STORAGE.toString() -> return storageCompatibility(
                componentsList[position].first as Storage?,
                componentsList[MOTHERBOARD_SLOT].first as Motherboard?
            )
            //Compare Case motherboard and Motherboard type
            ComponentsEnum.CASES.toString() ->
                return casesCompatibility(
                    componentsList[position].first as Case?,
                    componentsList[MOTHERBOARD_SLOT].first as Motherboard?
                )

        }
        return View.GONE
    }

    /**
     * Check the CPU's compatibility with the motherboards processor socket.
     */
    private fun cpuCompatibility(cpu: Cpu?, motherboard: Motherboard?): Int =
        if (cpu?.cpu_socket == motherboard?.processor_socket) View.GONE else View.VISIBLE

    /**
     * Check if the power supply has enough wattage to run the Gpu and Cpu.
     */
    private fun psuCompatibility(psu: Psu?, gpu: Gpu?, cpu: Cpu?): Int {
        var wattage = 0

        gpu?.let {
            wattage += it.wattage
        }
        cpu?.let {
            wattage += it.cpu_wattage
        }

        if (psu != null) {
            if (psu.psu_wattage > wattage) {
                return View.GONE
            }
        }
        return View.VISIBLE
    }

    /**
     * Check if the Ram's SDDRAM is the same version as the Motherboards.
     * E.g. DDR4 Ram can only go in Motherboard DDR4 Ram Slots.
     */
    private fun ramCompatibility(ram: Ram?, motherboard: Motherboard?): Int =
        if (ram?.ram_ddr.equals(
                motherboard?.ddr_sdram,
                ignoreCase = true
            )
        ) View.GONE else View.VISIBLE

    /**
     * If the storage component is a M.2 NVME Drive, and the motherboard doesn't support M.2's,
     * then display that it is incompatible.
     */
    private fun storageCompatibility(storage: Storage?, motherboard: Motherboard?): Int =
        if ((storage?.storage_type.equals(
                NVME,
                ignoreCase = true
            ) && motherboard?.hasNvmeSupport == 1)
            || !(storage?.storage_type.equals(NVME, ignoreCase = true))
        ) View.GONE else View.VISIBLE

    /**
     * Check if the current case can fit the motherboard.
     * An ATX Sized motherboard cannot fit into a Case meant for a Micro-ATX.
     */
    private fun casesCompatibility(case: Case?, motherboard: Motherboard?): Int {
        val motherboardTypes = mapOf(
            "ATX" to 5,
            "Micro-ATX" to 4,
            "µATX" to 3,
            "Mini-ATX" to 2,
            "Nano-ITX" to 1,
            "Pico-ITX" to 0
        )
        if (case != null && motherboard != null) {
            //Translates motherboard types/sizes into number values for comparison.
            // The bigger the value, the larger the motherboard.
            val caseBoardID = motherboardTypes[case.case_motherboard]
            val motherboardID = motherboardTypes[motherboard.board_type]
            //If the case is bigger than the case, or is of equal size, then they are compatible.
            if (motherboardID!! <= caseBoardID!!) {
                return View.GONE
            }
        }
        return View.VISIBLE
    }

    /**
     * Get the relative position of a RAM, STORAGE Or FAN slot.
     * I consider these the sub lists in the PC Part list.
     *
     * This is typically used when removing a component.
     * If there is no relational position, return -1 so that the Database Handler knows it's a single slot PC Part.
     */
    fun getRelativePosition(position: Int, type: String): Int {
        when (type.toUpperCase(Locale.ROOT)) {
            ComponentsEnum.RAM.toString() -> return convertDeletedSlotPosition(
                position,
                RAM_SLOT_START,
                RAM_SLOT_END
            )
            ComponentsEnum.STORAGE.toString() -> return convertDeletedSlotPosition(
                position,
                STORAGE_SLOT_START,
                STORAGE_SLOT_END
            )
            ComponentsEnum.FAN.toString() -> return convertDeletedSlotPosition(
                position,
                FAN_SLOT_START,
                fanSlotEnd
            )
        }
        return -1
    }

    /**
     * If the required relational slot is found in the sublist, return it to "getRelativePosition"
     */
    private fun convertDeletedSlotPosition(position: Int, minimumSlot: Int, maximumSlot: Int): Int {
        for ((slotFound, index) in (minimumSlot until maximumSlot).withIndex()) {
            if (index == position) return slotFound
        }
        return 0
    }

    /**
     * Check if the altered sublist needs to fill a gap and do a small iteration sort.
     */
    fun doSlotsNeedMoved(
        pcDetails: MutableList<Pair<Component?, String>>,
        pos: Int
    ): MutableList<Pair<Component?, String>> {
        if (pos == pcDetails.lastIndex) return pcDetails
        when (pcDetails[pos].second.toUpperCase(Locale.ROOT)) {
            ComponentsEnum.RAM.toString() -> return moveSlots(pos, pcDetails)
            ComponentsEnum.STORAGE.toString() -> return moveSlots(pos, pcDetails)
            ComponentsEnum.FAN.toString() -> return moveSlots(pos, pcDetails)
        }
        return pcDetails
    }

    /**
     * Performs a small iteration sorting method for moving empty slots to the end of a sublist, and occupied
     * relational components [RAM, STORAGE, FAN] closer to the front to fill in any empty gaps.
     */
    private fun moveSlots(
        pos: Int,
        pcDetails: MutableList<Pair<Component?, String>>
    ): MutableList<Pair<Component?, String>> {
        if (pcDetails[pos].second == pcDetails[pos + 1].second && (pcDetails[pos].first == null && pcDetails[pos + 1].first != null)) {
            //Move the next slot into the current removed slot
            pcDetails[pos] =
                Pair(pcDetails[pos + 1].first, pcDetails[pos + 1].second.capitalize(Locale.ROOT))

            //Make the next slot empty
            pcDetails[pos + 1] = Pair(null, pcDetails[pos + 1].second.capitalize(Locale.ROOT))
        }
        return pcDetails
    }
}