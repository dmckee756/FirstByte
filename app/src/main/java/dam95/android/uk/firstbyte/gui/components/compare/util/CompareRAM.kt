package dam95.android.uk.firstbyte.gui.components.compare.util

import dam95.android.uk.firstbyte.model.components.Component
import dam95.android.uk.firstbyte.model.components.Ram

/**
 * @author David Mckee
 * @Version 1.0
 * Holds a function that retrieves all of the currently compared components.
 * This is used for retrieving RAM values and then returning it back the CompareHardware fragment.
 */
@Suppress("UNCHECKED_CAST")
object CompareRAM {

    /**
     * Returns a list of each Ram components Memory speeds.
     * @param _componentList All currently compared RAM
     */
    fun compareRamMemorySpeed(_componentList: List<Component?>):List<Float>{
        val ramMemorySpeed = mutableListOf<Float>()
        val componentList = _componentList as List<Ram?>

        //Iterate through and retrieve the rams desired value
        for (index in _componentList.indices){
            componentList[index]?.memory_speed_mhz?.toFloat()?.let { ramMemorySpeed.add(it) }
        }
        return ramMemorySpeed
    }

    /**
     * Returns a list of each Ram components Memory sizes.
     * @param _componentList All currently compared RAM
     */
    fun compareRamMemorySize(_componentList: List<Component?>):List<Float>{
        val ramMemorySize = mutableListOf<Float>()
        val componentList = _componentList as List<Ram?>

        //Iterate through and retrieve the rams desired value
        for (index in _componentList.indices){
            componentList[index]?.memory_size_gb?.toFloat()?.let { ramMemorySize.add(it) }
        }
        return ramMemorySize
    }
}