package dam95.android.uk.firstbyte.model.util

/**
 *
 */
private const val YES = "Yes"
private const val NO = "No"
private const val ZERO = 0
object HumanReadableUtils {

    /**
     *
     */
    fun tinyIntHumanReadable(tinyInt: Int): String = if (tinyInt == ZERO) NO else YES

    /**
     *
     */
    fun rrpPriceToCurrency(rrpPrice: Double): String = "£${String.format("%.2f", rrpPrice)}"
}