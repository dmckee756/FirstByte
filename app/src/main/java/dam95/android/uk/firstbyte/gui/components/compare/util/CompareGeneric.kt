package dam95.android.uk.firstbyte.gui.components.compare.util

import dam95.android.uk.firstbyte.model.components.Component

object CompareGeneric {

    fun compareRRPPrice(componentList: List<Component?>): List<Float>{
        val componentPrices = mutableListOf<Float>()

        for (index in componentList.indices) {
            componentList[index]?.rrpPrice?.toFloat()?.let {
                componentPrices.add(it)}
        }
        return componentPrices
    }
}