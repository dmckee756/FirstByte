package dam95.android.uk.firstbyte.model.components

import android.content.Context
import com.google.gson.annotations.SerializedName
import dam95.android.uk.firstbyte.R


/**
 *
 */
data class Fan(
    @SerializedName("component_type")
    override var type: String,
    @SerializedName("image_link")
    override var imageLink: String,

    @SerializedName("fan_name")
    override var name: String,
    var fan_size_mm: Int,
    var fan_rpm: Int,

    @SerializedName("rrp_price")
    override var rrpPrice: Double,
    @SerializedName("amazon_price")
    override var amazonPrice: Double?,
    @SerializedName("amazon_link")
    override var amazonLink: String?,
    @SerializedName("scan_price")
    override var scanPrice: Double?,
    @SerializedName("scan_link")
    override var scanLink: String?,
    override var deletable: Boolean = true
) : Component {

    /**
     * Bundles all variables of a Fan into a list and returns it to the caller.
     */
    override fun getDetails(): List<*> {
        return listOf(
            name, type,
            imageLink, rrpPrice,
            amazonPrice, amazonLink,
            scanPrice, scanLink, deletable,
            fan_size_mm, fan_rpm
        )
    }

    /**
     *
     */
    override fun getDetailsForDisplay(
        context: Context,
        childDetails: MutableList<String>?
    ): List<String>? {
        val details = mutableListOf(
            context.resources.getString(R.string.fanDisplayFanSize, fan_size_mm),
            context.resources.getString(R.string.fanDisplayFanSpeed, fan_rpm)
        )
        return super.getDetailsForDisplay(context, details)
    }

    /**
     *
     */
    override fun setAllDetails(allDetails: List<Any?>) {
        fan_size_mm = allDetails[allDetails.lastIndex - 1] as Int
        fan_rpm = allDetails[allDetails.lastIndex] as Int
        super.setAllDetails(allDetails)
    }
}