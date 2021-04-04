package dam95.android.uk.firstbyte.model

import com.google.gson.annotations.SerializedName

/**
 * @author David Mckee
 * @Version 1.0
 * The retrieved data from the Node API or the app's database for
 * display in the HardwareList fragment.
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
) {}