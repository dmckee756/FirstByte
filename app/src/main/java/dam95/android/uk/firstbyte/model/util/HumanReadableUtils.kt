package dam95.android.uk.firstbyte.model.util

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

private const val YES = "Yes"
private const val NO = "No"
private const val ZERO = 0

/**
 * @author David Mckee
 * @Version 1.0
 * This converts raw values into human readable formats for display.
 * Methods within MyBarChartFormatting class deals with BarChar compare formatting.
 * Methods below the class deals with component pricing formatting.
 */
object HumanReadableUtils {


    /**
     * @author David Mckee
     * @Version 1.0
     * Allows raw numbers to be formatted into the corresponding values for easier human readability on
     * the comparison bar chart.
     */
    class MyBarChartFormatting : ValueFormatter() {

        //This variable points to a specific number formatter method below,
        //depending on the value being compared in the bar chart.
        lateinit var formatFloat: (Float) -> String

        /**
         * Formats the bar chart values into 2 decimal place GBP (£) currency.
         * @param value
         * @return Formatted GBP currency value
         */
        fun formatCurrency(value: Float): String {
            val mFormat = DecimalFormat("£###,##0.00")
            return mFormat.format(value)
        }

        /**
         * Formats the bar chart values into component MHz Speeds, used in Memory speed.
         * @param value
         * @return Formatted GBP currency value
         */
        fun formatMhz(value: Float): String {
            val mFormat = DecimalFormat("###,###,##0MHz")
            return mFormat.format(value)
        }

        /**
         * Formats the bar chart values into component wattage produced.
         * @param value
         * @return Formatted GBP currency value
         */
        fun formatWattage(value: Float): String {
            val mFormat = DecimalFormat("###,##0 Watts")
            return mFormat.format(value)
        }

        /**
         * Formats the bar chart values into component GHz speeds, used in CPU speed.
         * @param value
         * @return Formatted GBP currency value
         */
        fun formatGhz(value: Float): String {
            val mFormat = DecimalFormat("##0.00GHz")
            return mFormat.format(value)
        }

        /**
         * Formats the bar chart values into number of CPU cores.
         * @param value
         * @return Formatted GBP currency value
         */
        fun formatCoreCount(value: Float): String {
            val mFormat = DecimalFormat("##0 Cores")
            return mFormat.format(value)
        }

        /**
         * Formats the bar chart values into memory size in GigaBytes.
         * @param value
         * @return Formatted GBP currency value
         */
        fun formatGB(value: Float): String {
            val mFormat = DecimalFormat("#,##0GB")
            return mFormat.format(value)
        }

        /**
         * Gets the values from the bar chart and formats them depending on the format method that is currently being pointed to.
         * @param value
         * @return Formatted GBP currency value
         */
        override fun getFormattedValue(value: Float): String {
            return formatFloat(value)
        }

    }

    /**
     * Puts integers that are TinyInts from the database into a human readable format for display.
     * @return Yes Or No String
     */
    fun tinyIntHumanReadable(tinyInt: Int): String = if (tinyInt == ZERO) NO else YES

    /**
     * Puts double values that are used to store currency pricing into human readable format.
     * In this case it is hard coded for GBP (£).
     * @return A currency format for displayed hardware components dealing with prices.
     */
    fun rrpPriceToCurrency(rrpPrice: Double): String = "£${String.format("%.2f", rrpPrice)}"
}