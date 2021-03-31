package dam95.android.uk.firstbyte.gui.components.compare.util

import dam95.android.uk.firstbyte.model.components.Component
import dam95.android.uk.firstbyte.model.components.Gpu

@Suppress("UNCHECKED_CAST")
object CompareGPU {

    fun compareClockSpeed(_componentList: List<Component?>):List<Float>{
        val gpuClockSpeed = mutableListOf<Float>()
        val componentList = _componentList as List<Gpu?>


        for (index in _componentList.indices) {
            componentList[index]?.core_speed_mhz?.toFloat()?.let {
                gpuClockSpeed.add(it) }
        }
        return gpuClockSpeed
    }

    fun compareGpuMemorySpeed(_componentList: List<Component?>):List<Float>{
        val gpuMemorySpeed = mutableListOf<Float>()
        val componentList = _componentList as List<Gpu?>

        for (index in _componentList.indices) {
            componentList[index]?.memory_speed_mhz?.toFloat()?.let {
                gpuMemorySpeed.add(it) }
        }
        return gpuMemorySpeed
    }

    fun compareGpuMemorySize(_componentList: List<Component?>):List<Float>{
        val gpuMemorySize = mutableListOf<Float>()
        val componentList = _componentList as List<Gpu?>

        for (index in _componentList.indices) {
            componentList[index]?.memory_size_gb?.toFloat()?.let {
                gpuMemorySize.add(it) }
        }
        return gpuMemorySize
    }

    fun compareGpuWattage(_componentList: List<Component?>): List<Float>{
        val gpuWattage = mutableListOf<Float>()
        val componentList = _componentList as List<Gpu?>


        for (index in _componentList.indices) {
            componentList[index]?.wattage?.toFloat()?.let {
                gpuWattage.add(it)
            }
        }
        return gpuWattage
    }

}