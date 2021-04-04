package dam95.android.uk.firstbyte.gui.components.compare.util

import dam95.android.uk.firstbyte.model.components.Component
import dam95.android.uk.firstbyte.model.components.Gpu

/**
 * @author David Mckee
 * @Version 1.0
 * Holds a function that retrieves all of the currently compared components.
 * This is used for retrieving GPU values and then returning it back the CompareHardware fragment.
 */
@Suppress("UNCHECKED_CAST")
object CompareGPU {

    /**
     * Returns a list of each Gpu components clock speeds.
     * @param _componentList All currently compared GPU's
     */
    fun compareClockSpeed(_componentList: List<Component?>):List<Float>{
        val gpuClockSpeed = mutableListOf<Float>()
        val componentList = _componentList as List<Gpu?>

        //Iterate through and retrieve the gpu's desired value
        for (index in _componentList.indices) {
            componentList[index]?.core_speed_mhz?.toFloat()?.let {
                gpuClockSpeed.add(it) }
        }
        return gpuClockSpeed
    }

    /**
     * Returns a list of each Gpu components Memory speeds.
     * @param _componentList All currently compared GPU's
     */
    fun compareGpuMemorySpeed(_componentList: List<Component?>):List<Float>{
        val gpuMemorySpeed = mutableListOf<Float>()
        val componentList = _componentList as List<Gpu?>

        //Iterate through and retrieve the gpu's desired value
        for (index in _componentList.indices) {
            componentList[index]?.memory_speed_mhz?.toFloat()?.let {
                gpuMemorySpeed.add(it) }
        }
        return gpuMemorySpeed
    }

    /**
     * Returns a list of each Gpu components Memory Sizes.
     * @param _componentList All currently compared GPU's
     */
    fun compareGpuMemorySize(_componentList: List<Component?>):List<Float>{
        val gpuMemorySize = mutableListOf<Float>()
        val componentList = _componentList as List<Gpu?>

        //Iterate through and retrieve the gpu's desired value
        for (index in _componentList.indices) {
            componentList[index]?.memory_size_gb?.toFloat()?.let {
                gpuMemorySize.add(it) }
        }
        return gpuMemorySize
    }

    /**
     * Returns a list of each Gpu components wattage usage.
     * @param _componentList All currently compared GPU's
     */
    fun compareGpuWattage(_componentList: List<Component?>): List<Float>{
        val gpuWattage = mutableListOf<Float>()
        val componentList = _componentList as List<Gpu?>

        //Iterate through and retrieve the gpu's desired value
        for (index in _componentList.indices) {
            componentList[index]?.wattage?.toFloat()?.let {
                gpuWattage.add(it)
            }
        }
        return gpuWattage
    }

}