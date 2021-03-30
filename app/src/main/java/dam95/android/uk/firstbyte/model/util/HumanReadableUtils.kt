package dam95.android.uk.firstbyte.model.util

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

/**
 *
 */
private const val YES = "Yes"
private const val NO = "No"
private const val ZERO = 0

object HumanReadableUtils {

    class MyBarChartFormatting : ValueFormatter() {

        lateinit var formatFloat: (Float) -> String

        fun formatCurrency(value: Float): String {
            val mFormat = DecimalFormat("£###,##0.00")
            return mFormat.format(value)
        }

        fun formatMhz(value: Float): String {
            val mFormat = DecimalFormat("###,###,##0MHz")
            return mFormat.format(value)
        }

        fun formatWattage(value: Float): String {
            val mFormat = DecimalFormat("###,##0 Watts")
            return mFormat.format(value)
        }

        fun formatGhz(value: Float): String {
            val mFormat = DecimalFormat("##0.00GHz")
            return mFormat.format(value)
        }

        fun formatCoreCount(value: Float): String {
            val mFormat = DecimalFormat("##0 Cores")
            return mFormat.format(value)
        }

        fun formatGB(value: Float): String {
            val mFormat = DecimalFormat("#,##0GB")
            return mFormat.format(value)
        }

        override fun getFormattedValue(value: Float): String {
            return formatFloat(value)
        }

    }

    /**
     *
     */
    fun tinyIntHumanReadable(tinyInt: Int): String = if (tinyInt == ZERO) NO else YES

    /**
     *
     */
    fun rrpPriceToCurrency(rrpPrice: Double): String = "£${String.format("%.2f", rrpPrice)}"
}