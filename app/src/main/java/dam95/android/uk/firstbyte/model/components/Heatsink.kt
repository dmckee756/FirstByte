package dam95.android.uk.firstbyte.model.components

import android.content.Context
import com.google.gson.annotations.SerializedName
import dam95.android.uk.firstbyte.R

/**
 * @author David Mckee
 * @Version 1.0
 * Heatsink object designed to hold all hardware specifications of display on fragments, saving to the app's database
 * and data retrieval from both the API and app's database.
 * Is a child object of Component.
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
     * This is primarily used when dealing with loading/saving components into the app's database.
     * @return list of all Heatsink variables and values.
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
     * Important to keep this in the same order as the constructor,
     * When the Heatsink is loaded from the database, assign all of it's details to this Heatsink object first, then call the
     * parent components setAllDetails method to assign the rest of it's values.
     * @param allDetails All loaded details from the app's database.
     */
    override fun setAllDetails(allDetails: List<Any?>) {
        fan_slots = allDetails[allDetails.lastIndex - 5] as Int
        amd_socket_min = allDetails[allDetails.lastIndex - 4] as String?
        amd_socket_max = allDetails[allDetails.lastIndex - 3] as String?
        intel_socket_min = allDetails[allDetails.lastIndex - 2] as String?
        intel_socket_max = allDetails[allDetails.lastIndex - 1] as String?
        heatsink_dimensions = allDetails[allDetails.lastIndex]  as String
        super.setAllDetails(allDetails)
    }

    /**
     * Put the Heatsink values into a human readable sentences for display in the hardware details fragment.
     * @param context used to find the XML String resource that the values are put into.
     * @param childDetails details of this Heatsink object
     */
    override fun getDetailsForDisplay(
        context: Context,
        childDetails: MutableList<String>?
    ): List<String>? {
        val details = mutableListOf<String>()

        details.add(context.resources.getString(R.string.displayFanSlots, fan_slots))
        amd_socket_min?.let{ details.add(context.resources.getString(R.string.heatsinkDisplayAMDSocketMin, it)) }
        amd_socket_max?.let { details.add(context.resources.getString(R.string.heatsinkDisplayAMDSocketMax, it)) }
        intel_socket_min?.let { details.add(context.resources.getString(R.string.heatsinkDisplayINTELSocketMin, it)) }
        intel_socket_max?.let { details.add(context.resources.getString(R.string.heatsinkDisplayINTELSocketMax, it)) }
        details.add(context.resources.getString(R.string.displayDimensions, heatsink_dimensions))

        return super.getDetailsForDisplay(context, details)
    }
}