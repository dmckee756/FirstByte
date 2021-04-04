package dam95.android.uk.firstbyte.gui.components.compare.util

import dam95.android.uk.firstbyte.model.components.Component

/**
 * @author David Mckee
 * @Version 1.0
 * Holds a function that retrieves all of the currently compared components.
 * This is used for retrieving the shared Rrp Price value and then returning it back the CompareHardware fragment.
 */
object CompareGeneric {

    /**
     * Returns a list of each components Rrp Price.
     * @param componentList All currently compared components.
     */
    fun compareRRPPrice(componentList: List<Component?>): List<Float>{
        val componentPrices = mutableListOf<Float>()

        //Iterate through and retrieve the components Rrp Price
        for (index in componentList.indices) {
            componentList[index]?.rrpPrice?.toFloat()?.let {
                componentPrices.add(it)}
        }
        return componentPrices
    }
}