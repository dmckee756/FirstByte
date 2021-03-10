package dam95.android.uk.firstbyte.model.components

open class Component {
    lateinit var component_type: String
    lateinit var image_Link: String
    lateinit var name: String
    var rrp_Price: Double = 0.0
    var amazon_Price: Double? = null
    var amazon_Link: String? = null
    var scan_Price: Double? = null
    var scan_Link: String? = null
    var idDeletable: Boolean = false
}