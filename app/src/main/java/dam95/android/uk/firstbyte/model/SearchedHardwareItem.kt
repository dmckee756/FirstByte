package dam95.android.uk.firstbyte.model

import com.google.gson.annotations.SerializedName

/**
 * The retrieved data from the Node API for the HardwareList fragment recycler list
 */
data class SearchedHardwareItem(
    @SerializedName("component_name")
    var name: String,
    @SerializedName("component_type")
    var category: String,
    @SerializedName("image_link")
    var image_link: String,
    @SerializedName("rrp_price")
    var rrpPrice: Double
) {
}