package dam95.android.uk.firstbyte.model.components

import android.content.Context
import com.google.gson.annotations.SerializedName
import dam95.android.uk.firstbyte.R

/**
 *
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
     *
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

    override fun setAllDetails(allDetails: List<Any?>) {
        core_speed_mhz = allDetails[allDetails.lastIndex - 5] as Int
        core_speed_mhz = allDetails[allDetails.lastIndex - 4] as Int
        memory_size_gb = allDetails[allDetails.lastIndex - 3] as Int
        memory_speed_mhz = allDetails[allDetails.lastIndex - 2] as Int
        wattage = allDetails[allDetails.lastIndex - 1]  as Int
        dimensions = allDetails[allDetails.lastIndex] as String
        super.setAllDetails(allDetails)
    }

}