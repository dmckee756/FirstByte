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
    fun booleanHumanReadable(boolean: Boolean): String = if (boolean) YES else NO

    /**
     *
     */
    fun tinyIntHumanReadable(tinyInt: Int): String = if (tinyInt == ZERO) NO else YES
}