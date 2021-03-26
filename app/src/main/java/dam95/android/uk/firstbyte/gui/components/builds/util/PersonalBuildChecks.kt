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
private const val PSU_SLOT = 2
private const val MOTHERBOARD_SLOT = 3
private const val CASE_SLOT = 5

//Hardcoded way of dealing with ram slots
private const val RAM_SLOT_START = 6
private const val RAM_SLOT_END = 7
private const val RAM = "ram"

//Hardcoded way of dealing with storage slots
private const val STORAGE_SLOT_START = 8
private const val STORAGE_SLOT_END = 10
private const val STORAGE = "storage"

private const val NVME = "M.2 NVMe"

object PersonalBuildChecks {

    /**
     * Display extra detail under a pc part/component in the PC's details.
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
     *
     */
    private fun searchLocalSlots(
        componentsList: List<Pair<Component?, String>>,
        slotStart: Int,
        slotEnd: Int,
        desiredType: String
    ): Int {
        for (index in slotStart..slotEnd) {
            if (componentsList[index].first != null &&
                componentsList[index].second.toLowerCase(Locale.ROOT) == desiredType
            ) {
                return View.GONE
            }
        }
        return View.VISIBLE
    }

    /**
     *
     */
    private fun isRequiredIconDisplayed(component: Component?): Int {
        return if (component == null) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    /**
     *
     */
    fun checkCompatibility(
        position: Int,
        componentsList: List<Pair<Component?, String>>
    ): Int {
        when (componentsList[position].first?.type?.toUpperCase(Locale.ROOT)) {
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
     *
     */
    private fun cpuCompatibility(cpu: Cpu?, motherboard: Motherboard?): Int =
        if (cpu?.cpu_socket == motherboard?.processor_socket) View.GONE else View.VISIBLE

    /**
     *
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
     *
     */
    private fun ramCompatibility(ram: Ram?, motherboard: Motherboard?): Int =
        if (ram?.ram_ddr.equals(
                motherboard?.ddr_sdram,
                ignoreCase = true
            )
        ) View.GONE else View.VISIBLE

    /**
     *
     */
    private fun storageCompatibility(storage: Storage?, motherboard: Motherboard?): Int =
        if ((storage?.storage_type.equals(NVME, ignoreCase = true) && motherboard?.hasNvmeSupport == 1)
            || !(storage?.storage_type.equals(NVME, ignoreCase = true))
        ) View.GONE else View.VISIBLE

    /**
     *
     */
    private fun casesCompatibility(case: Case?, motherboard: Motherboard?): Int {
        val motherboardTypes = mapOf<String, Int>(
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
}