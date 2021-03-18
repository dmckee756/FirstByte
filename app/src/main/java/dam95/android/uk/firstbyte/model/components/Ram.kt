package dam95.android.uk.firstbyte.model.components

import android.content.Context
import com.google.gson.annotations.SerializedName
import dam95.android.uk.firstbyte.R

/**
 *
 */
data class Ram(
    @SerializedName("component_type")
    override var type: String,
    @SerializedName("image_link")
    override var imageLink: String,

    @SerializedName("ram_name")
    override var name: String,
    @SerializedName("ram_memory_speed_mhz")
    var memory_speed_mhz: Int,
    @SerializedName("ram_memory_size_gb")
    var memory_size_gb: Int,
    var ram_ddr: String,
    var num_of_sticks: Int,

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
     * Bundles all variables of Ram into a list and returns it to the caller.
     */
    override fun getDetails(): List<*> {
        return listOf(
            name, type,
            imageLink, rrpPrice,
            amazonPrice, amazonLink,
            scanPrice, scanLink, deletable,
            memory_speed_mhz, memory_size_gb,
            ram_ddr, num_of_sticks
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
            context.resources.getString(R.string.ramDisplayMemorySize, memory_size_gb),
            context.resources.getString(R.string.ramDisplayMemorySpeed, memory_speed_mhz),
            context.resources.getString(R.string.displayRamDDR, ram_ddr),
            context.resources.getString(R.string.ramDisplayNumOfSticks, num_of_sticks)
        )
        return super.getDetailsForDisplay(context, details)
    }

    override fun setAllDetails(allDetails: List<Any?>) {
        memory_speed_mhz = allDetails[allDetails.lastIndex - 3] as Int
        memory_size_gb = allDetails[allDetails.lastIndex - 2] as Int
        ram_ddr = allDetails[allDetails.lastIndex - 1] as String
        num_of_sticks = allDetails[allDetails.lastIndex] as Int
        super.setAllDetails(allDetails)
    }
}