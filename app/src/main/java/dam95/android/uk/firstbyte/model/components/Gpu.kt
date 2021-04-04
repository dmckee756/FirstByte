package dam95.android.uk.firstbyte.model.components

import android.content.Context
import com.google.gson.annotations.SerializedName
import dam95.android.uk.firstbyte.R

/**
 * @author David Mckee
 * @Version 1.0
 * Graphics Card object designed to hold all hardware specifications of display on fragments, saving to the app's database
 * and data retrieval from both the API and app's database.
 * Is a child object of Component.
 */
data class Gpu(
    @SerializedName("component_type")
    override var type: String,
    @SerializedName("image_link")
    override var imageLink: String,

    @SerializedName("gpu_name")
    override var name: String,
    var core_speed_mhz: Int,
    var memory_size_gb: Int,
    var memory_speed_mhz: Int,
    var wattage: Int,
    var dimensions: String,

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
     * Bundles all variables of a Gpu into a list and returns it to the caller.
     * This is primarily used when dealing with loading/saving components into the app's database.
     * @return list of all Gpu variables and values.
     */
    override fun getDetails(): List<*> {
        return listOf(
            name, type,
            imageLink, rrpPrice,
            amazonPrice, amazonLink,
            scanPrice, scanLink, deletable,
            core_speed_mhz, memory_size_gb,
            memory_speed_mhz, wattage, dimensions
        )
    }

    /**
     * Important to keep this in the same order as the constructor,
     * When the Gpu is loaded from the database, assign all of it's details to this Gpu object first, then call the
     * parent components setAllDetails method to assign the rest of it's values.
     * @param allDetails All loaded details from the app's database.
     */
    override fun setAllDetails(allDetails: List<Any?>) {
        core_speed_mhz = allDetails[allDetails.lastIndex - 5] as Int
        core_speed_mhz = allDetails[allDetails.lastIndex - 4] as Int
        memory_size_gb = allDetails[allDetails.lastIndex - 3] as Int
        memory_speed_mhz = allDetails[allDetails.lastIndex - 2] as Int
        wattage = allDetails[allDetails.lastIndex - 1]  as Int
        dimensions = allDetails[allDetails.lastIndex] as String
        super.setAllDetails(allDetails)
    }

    /**
     * Put the Gpu values into a human readable sentences for display in the hardware details fragment.
     * @param context used to find the XML String resource that the values are put into.
     * @param childDetails details of this Gpu object
     */
    override fun getDetailsForDisplay(
        context: Context,
        childDetails: MutableList<String>?
    ): List<String>? {
        val details = mutableListOf(
            context.resources.getString(R.string.gpuDisplayCoreSpeed, core_speed_mhz),
            context.resources.getString(R.string.gpuDisplayVirtualMemorySize, memory_size_gb),
            context.resources.getString(R.string.gpuDisplayVirtualMemorySpeed, memory_speed_mhz),
            context.resources.getString(R.string.displayWattage, wattage),
            context.resources.getString(R.string.displayDimensions, dimensions)
        )
        return super.getDetailsForDisplay(context, details)
    }
}