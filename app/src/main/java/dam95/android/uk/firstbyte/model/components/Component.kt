package dam95.android.uk.firstbyte.model.components

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
}