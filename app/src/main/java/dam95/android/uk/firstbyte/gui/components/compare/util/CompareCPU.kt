package dam95.android.uk.firstbyte.gui.components.compare.util

import dam95.android.uk.firstbyte.model.components.Component
import dam95.android.uk.firstbyte.model.components.Cpu

object CompareCPU {


    fun compareCoreSpeed(_componentList: List<Component?>): List<Float>{
        val cpuCoreSpeed = mutableListOf<Float>()
        val componentList: List<Cpu?> = _componentList as List<Cpu?>

        for (index in _componentList.indices) {
            componentList[index]?.core_speed_ghz?.toFloat()?.let {
                cpuCoreSpeed.add(it) }
        }
        return cpuCoreSpeed
    }

    fun compareCoreCount(_componentList: List<Component?>): List<Float>{
        val cpuCores = mutableListOf<Float>()
        val componentList: List<Cpu?> = _componentList as List<Cpu?>

        for (index in _componentList.indices) {
            componentList[index]?.core_count?.toFloat()?.let {
                cpuCores.add(it) }
        }
        return cpuCores
    }

    fun compareCpuWattage(_componentList: List<Component?>): List<Float>{
        val cpuWattage = mutableListOf<Float>()
        val componentList: List<Cpu?> = _componentList as List<Cpu?>

        for (index in _componentList.indices) {
            componentList[index]?.cpu_wattage?.toFloat()?.let {
                cpuWattage.add(it)}
        }
        return cpuWattage
    }
}