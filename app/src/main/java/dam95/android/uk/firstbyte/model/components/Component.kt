package dam95.android.uk.firstbyte.model.components

/* Values that must not be null and must have their value changed...
 * ...when it is loaded by from the API, or in app schema
 */
val DOUBLE_DEFAULT = 0.0
val INT_DEFAULT = 0

/**
 *
 */
abstract class Component {
    private lateinit var name: String
    private lateinit var type: String
    private lateinit var imageLink: String
    private var rrpPrice: Double = DOUBLE_DEFAULT
    private var amazonPrice: Double? = null
    private var amazonLink: String? = null
    private var scanPrice: Double? = null
    private var scanLink: String? = null
}