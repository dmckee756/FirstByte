package dam95.android.uk.firstbyte.gui.components.compare.util

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import dam95.android.uk.firstbyte.model.components.Component
import dam95.android.uk.firstbyte.model.util.ComponentsEnum
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KFunction2

/**
 * @author David Mckee
 * @Version 1.0
 * Holds the spinner that is used to decide what value is to be compared in the HardwareCompare fragment.
 */
object CompareValueSpinner {
    private var initialClick = true

    /**
     * Setup spinner that allows users to compare component values on the BarChart.
     */
    fun initializeSpinner(
        callbackFunction: KFunction2<(List<Component?>) -> List<Float>, String, Unit>,
        listOfComparedValue: ArrayList<String>,
        categoryType: String,
        compareSpinner: Spinner,
        context: Context
    ) {
        //Value Spinner
        listOfComparedValue.setUpValueSpinner(compareSpinner, categoryType, context, callbackFunction)
    }

    /**
     * Stores what values the user can compared in the bar chart.
     * It retrieves the function that HardwareCompare must call to retrieve all of the currently compared components values.
     * @param valueSpinner the spinner component
     */
    private fun ArrayList<String>.setUpValueSpinner(
        valueSpinner: Spinner,
        categoryType: String,
        context: Context,
        callbackFunction: KFunction2<(List<Component?>) -> List<Float>, String, Unit>
    ) {
        //
        val valueSelection =
            ArrayAdapter<String>(context, android.R.layout.simple_spinner_item)
        valueSelection.addAll(this)

        valueSelection.setDropDownViewResource(android.R.layout.simple_list_item_1)
        valueSpinner.adapter = valueSelection

        valueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            /**
             * Ignore
             */
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Ignore
            }

            /**
             * Handles the spinners on click when the user changes value that is being compared in HardwareCompare.
             * It retrieves the function that HardwareCompare must call to retrieve all of the currently compared components values.
             */
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                //Skip the initial click when a spinner is set up.
                if (initialClick){
                    initialClick = false
                    return
                }

                //Get the selected item in the spinner.
                val newValueName: String = valueSelection.getItem(position).toString()
                //If this is the price value, then assign the price function to be sent back to the CompareHardware fragment.
                val newValueFunction = if (position == 0) {
                    CompareGeneric::compareRRPPrice
                    //Otherwise find correct value to compare and return the correct fragment.
                } else {
                     when (categoryType.toUpperCase(Locale.ROOT)) {
                        ComponentsEnum.CPU.toString() -> changeProcessorValue(position)
                        ComponentsEnum.GPU.toString() -> changeGraphicsCardValue(position)
                        ComponentsEnum.RAM.toString() -> changeRamValue(position)
                        else -> CompareGeneric::compareRRPPrice
                    }
                }
                //Sends back information on what values to load from the components.
                callbackFunction(newValueFunction, newValueName)
            }
        }
    }

    /**
     * The spinner gets a function that retrieves all of the currently compared components new compared CPU values
     * and then returns it back the CompareHardware fragment. Uses CompareCPU and CompareGeneric.
     * "Core Speed" = Position 1
     * "Core Count" = Position 2
     * "Wattage" = Position 3
     */
    private fun changeProcessorValue(position: Int): (List<Component?>) -> List<Float> {
        val coreSpeed = 1
        val coreCount = 2
        val wattage = 3

        return when (position) {
            coreSpeed -> CompareCPU::compareCoreSpeed
            coreCount -> CompareCPU::compareCoreCount
            wattage -> CompareCPU::compareCpuWattage
            else -> CompareGeneric::compareRRPPrice
        }
    }

    /**
     * The spinner gets a function that retrieves all of the currently compared components new compared GPU values
     * and then returns it back the CompareHardware fragment. Uses CompareGPU and CompareGeneric.
     * "Clock Speed" = Position 1
     * "Memory Size" = Position 2
     * "Memory Speed" = Position 3
     * "Wattage" = Position 4
     */
    private fun changeGraphicsCardValue(position: Int): (List<Component?>) -> List<Float> {
        val clockSpeed = 1
        val memorySize = 2
        val memorySpeed = 3
        val wattage = 4

        return when (position) {
            clockSpeed -> CompareGPU::compareClockSpeed
            memorySize -> CompareGPU::compareGpuMemorySize
            memorySpeed -> CompareGPU::compareGpuMemorySpeed
            wattage -> CompareGPU::compareGpuWattage
            else -> CompareGeneric::compareRRPPrice
        }
    }

    /**
     * The spinner gets a function that retrieves all of the currently compared components new compared RAM values
     * and then returns it back the CompareHardware fragment. Uses CompareRAM and CompareGeneric.
     * "Memory Speed" = Position 1
     * "Memory Size" = Position 2
     */
    private fun changeRamValue(position: Int): (List<Component?>) -> List<Float> {
        val memorySpeed = 1
        val memorySize = 2

        return when (position) {
            memorySpeed -> CompareRAM::compareRamMemorySpeed
            memorySize -> CompareRAM::compareRamMemorySize
            else -> CompareGeneric::compareRRPPrice
        }
    }

}