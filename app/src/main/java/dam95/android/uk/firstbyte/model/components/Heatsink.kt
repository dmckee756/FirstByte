package dam95.android.uk.firstbyte.model.components

import android.content.Context
import com.google.gson.annotations.SerializedName
import dam95.android.uk.firstbyte.R

/**
 *
 */
data class Heatsink(
    @SerializedName("component_type")
    override var type: String,
    @SerializedName("image_link")
    override var imageLink: String,

    @SerializedName("heatsink_name")
    override var name: String,
    var fan_slots: Int,
    var amd_socket_min: String?,
    var amd_socket_max: String?,
    var intel_socket_min: String?,
    var intel_socket_max: String?,
    var heatsink_dimensions: String,

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
     * Bundles all variables of a Heatsink into a list and returns it to the caller.
     */
    override fun getDetails(): List<*> {
        return listOf(
            name, type,
            imageLink, rrpPrice,
            amazonPrice, amazonLink,
            scanPrice, scanLink, deletable,
            fan_slots,
            amd_socket_min, amd_socket_max,
            intel_socket_min, intel_socket_max,
            heatsink_dimensions
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
            context.resources.getString(R.string.displayFanSlots, fan_slots),
            context.resources.getString(
                R.string.heatsinkDisplayAMDSocketMin,
                amd_socket_min
            ), //CHECK IF NULL
            context.resources.getString(
                R.string.heatsinkDisplayAMDSocketMax,
                amd_socket_max
            ), //CHECK IF NULL
            context.resources.getString(
                R.string.heatsinkDisplayINTELSocketMin,
                intel_socket_min
            ), //CHECK IF NULL
            context.resources.getString(
                R.string.heatsinkDisplayINTELSocketMax,
                intel_socket_max
            ), //CHECK IF NULL
            context.resources.getString(R.string.displayDimensions, heatsink_dimensions)
        )
        return super.getDetailsForDisplay(context, details)
    }

    override fun setAllDetails(allDetails: List<Any?>) {
        fan_slots = allDetails[allDetails.lastIndex - 5] as Int
        amd_socket_min = allDetails[allDetails.lastIndex - 4] as String?
        amd_socket_max = allDetails[allDetails.lastIndex - 3] as String?
        intel_socket_min = allDetails[allDetails.lastIndex - 2] as String?
        intel_socket_max = allDetails[allDetails.lastIndex - 1] as String?
        heatsink_dimensions = allDetails[allDetails.lastIndex]  as String
        super.setAllDetails(allDetails)
    }

}