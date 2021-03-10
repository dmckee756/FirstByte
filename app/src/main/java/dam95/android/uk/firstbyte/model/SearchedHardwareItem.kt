package dam95.android.uk.firstbyte.model

import com.google.gson.annotations.SerializedName

/**
 * The retrieved data from the Node API for the HardwareList fragment recycler list
 */
data class SearchedHardwareItem(
    @SerializedName("component_name")
    var name: String,
    @SerializedName("component_type")
    val category: String,
    @SerializedName("image_link")
    val image_link: String,
    @SerializedName("rrp_price")
    val rrpPrice: Double
) {

    /**
     *
     */
    fun rrpPriceToCurrency(): String = "£${String.format("%.2f", rrpPrice)}"
}