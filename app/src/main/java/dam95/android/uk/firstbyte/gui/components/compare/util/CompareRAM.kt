package dam95.android.uk.firstbyte.gui.components.compare.util

import dam95.android.uk.firstbyte.model.components.Component
import dam95.android.uk.firstbyte.model.components.Ram

object CompareRAM {

    fun compareRamMemorySpeed(_componentList: List<Component?>):List<Float>{
        val ramMemorySpeed = mutableListOf<Float>()
        val componentList: List<Ram?> = _componentList as List<Ram?>

        for (index in _componentList.indices){
            componentList[index]?.memory_speed_mhz?.toFloat()?.let { ramMemorySpeed.add(it) }
        }
        return ramMemorySpeed
    }

    fun compareRamMemorySize(_componentList: List<Component?>):List<Float>{
        val ramMemorySize = mutableListOf<Float>()
        val componentList: List<Ram?> = _componentList as List<Ram?>

        for (index in _componentList.indices){
            componentList[index]?.memory_size_gb?.toFloat()?.let { ramMemorySize.add(it) }
        }
        return ramMemorySize
    }
}