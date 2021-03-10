package dam95.android.uk.firstbyte.gui.components.hardware

import dam95.android.uk.firstbyte.databinding.FragmentHardwareDetailsBinding
import dam95.android.uk.firstbyte.model.components.*
import java.util.*

/**
 * This class is only to correctly display the appropriate component,
 * along with the relevant information. This will display both saved and online hardware details.
 */
class DisplayCorrectHardware {

    fun loadCorrectHardware(component: Component, hardwareBinding: FragmentHardwareDetailsBinding){
        when (component.type.toUpperCase(Locale.ROOT)) {
            ComponentsEnum.GPU.toString() -> displayGpu(component as Gpu, hardwareBinding)
            ComponentsEnum.CPU.toString() -> displayCpu(component as Cpu, hardwareBinding)
            ComponentsEnum.RAM.toString() -> displayRam(component as Ram, hardwareBinding)
            ComponentsEnum.PSU.toString() -> displayPsu(component as Psu, hardwareBinding)
             ComponentsEnum.STORAGE.toString() -> displayStorage(component as Storage, hardwareBinding)
             ComponentsEnum.MOTHERBOARD.toString() -> displayMotherboard(component as Motherboard, hardwareBinding)
             ComponentsEnum.CASES.toString() -> displayCases(component as Case, hardwareBinding)
             ComponentsEnum.HEATSINK.toString() -> displayHeatsink(component as Heatsink, hardwareBinding)
             ComponentsEnum.FAN.toString() -> displayFan(component as Fan, hardwareBinding)
        }
    }

    private fun displayGpu(gpu: Gpu, hardwareBinding: FragmentHardwareDetailsBinding){
        hardwareBinding.tempDisplayHardwareSpecs.text = gpu.toString()
    }

    private fun displayCpu(cpu: Cpu, hardwareBinding: FragmentHardwareDetailsBinding){
        hardwareBinding.tempDisplayHardwareSpecs.text = cpu.name
    }

    private fun displayRam(ram: Ram, hardwareBinding: FragmentHardwareDetailsBinding){
        hardwareBinding.tempDisplayHardwareSpecs.text = ram.name
    }

    private fun displayPsu(psu: Psu, hardwareBinding: FragmentHardwareDetailsBinding){
        hardwareBinding.tempDisplayHardwareSpecs.text = psu.name
    }

    private fun displayStorage(storage: Storage, hardwareBinding: FragmentHardwareDetailsBinding){
        hardwareBinding.tempDisplayHardwareSpecs.text = storage.name
    }

    private fun displayMotherboard(motherboard: Motherboard, hardwareBinding: FragmentHardwareDetailsBinding){
        hardwareBinding.tempDisplayHardwareSpecs.text = motherboard.name
    }

    private fun displayCases(case: Case, hardwareBinding: FragmentHardwareDetailsBinding){
        hardwareBinding.tempDisplayHardwareSpecs.text = case.name
    }

    private fun displayHeatsink(heatsink: Heatsink, hardwareBinding: FragmentHardwareDetailsBinding){
        hardwareBinding.tempDisplayHardwareSpecs.text = heatsink.name
    }

    private fun displayFan(fan: Fan, hardwareBinding: FragmentHardwareDetailsBinding){
        hardwareBinding.tempDisplayHardwareSpecs.text = fan.name
    }
}