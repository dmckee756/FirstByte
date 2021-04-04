package dam95.android.uk.firstbyte.gui.components.compare.util

import dam95.android.uk.firstbyte.model.components.Component
import dam95.android.uk.firstbyte.model.components.Cpu

/**
 * @author David Mckee
 * @Version 1.0
 * Holds a function that retrieves all of the currently compared components.
 * This is used for retrieving CPU values and then returning it back the CompareHardware fragment.
 */
@Suppress("UNCHECKED_CAST")
object CompareCPU {

    /**
     * Returns a list of each Cpu components core speeds.
     * @param _componentList All currently compared CPU's
     */
    fun compareCoreSpeed(_componentList: List<Component?>): List<Float>{
        val cpuCoreSpeed = mutableListOf<Float>()
        val componentList =  _componentList as List<Cpu?>

        //Iterate through and retrieve the cpu's desired value
        for (index in _componentList.indices) {
            componentList[index]?.core_speed_ghz?.toFloat()?.let {
                cpuCoreSpeed.add(it) }
        }
        return cpuCoreSpeed
    }

    /**
     * Returns a list of each Cpu components core counts.
     * @param _componentList All currently compared CPU's
     */
    fun compareCoreCount(_componentList: List<Component?>): List<Float>{
        val cpuCores = mutableListOf<Float>()
        val componentList = _componentList as List<Cpu?>

        //Iterate through and retrieve the cpu's desired value
        for (index in _componentList.indices) {
            componentList[index]?.core_count?.toFloat()?.let {
                cpuCores.add(it) }
        }
        return cpuCores
    }

    /**
     * Returns a list of each Cpu components wattage usage.
     * @param _componentList All currently compared CPU's
     */
    fun compareCpuWattage(_componentList: List<Component?>): List<Float>{
        val cpuWattage = mutableListOf<Float>()
        val componentList = _componentList as List<Cpu?>

        //Iterate through and retrieve the cpu's desired value
        for (index in _componentList.indices) {
            componentList[index]?.cpu_wattage?.toFloat()?.let {
                cpuWattage.add(it)}
        }
        return cpuWattage
    }
}