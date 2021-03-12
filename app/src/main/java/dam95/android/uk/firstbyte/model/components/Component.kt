package dam95.android.uk.firstbyte.model.components

/**
 *
 */
interface Component {
    var type: String
    var imageLink: String
    var name: String
    var rrpPrice: Double
    var amazonPrice: Double?
    var amazonLink: String?
    var scanPrice: Double?
    var scanLink: String?
    var deletable: Boolean

    /**
     * Bundles all variables of a Component into a list and returns it to the caller.
     * Name must be repeated twice, first at the start of the list and
     * again when the relational table in the AndroidFB_Hardware/Components database
     * has started being read/written to/from.
     */
    fun getDetails(): List<*>

    /**
     *
     */
    fun setDetails(database_Read: List<*>)
}
