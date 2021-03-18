package dam95.android.uk.firstbyte.model.components


import android.content.Context
import com.google.gson.annotations.SerializedName
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.model.util.HumanReadableUtils

/**
 *
 */
data class Cpu(
    @SerializedName("component_type")
    override var type: String,
    @SerializedName("image_link")
    override var imageLink: String,

    @SerializedName("cpu_name")
    override var name: String,
    var core_speed_ghz: Double,
    var core_count: Int,
    @SerializedName("multi_threading")
    var isMultiThreaded: Int,
    var cpu_socket: String,
    var cpu_wattage: Int,
    @SerializedName("default_heatsink")
    var hasHeatsink: Int,

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
     * Bundles all variables of a Cpu into a list and returns it to the caller.
     */
    override fun getDetails(): List<*> {
        return listOf(
            name, type,
            imageLink, rrpPrice,
            amazonPrice, amazonLink,
            scanPrice, scanLink, deletable,
            core_speed_ghz, core_count,
            isMultiThreaded, cpu_socket,
            cpu_wattage, hasHeatsink
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
            context.resources.getString(R.string.cpuDisplayCoreSpeed, core_speed_ghz),
            context.resources.getString(R.string.cpuDisplayCoreCount, core_count),
            context.resources.getString(
                R.string.cpuDisplayMultiThreaded,
                HumanReadableUtils.tinyIntHumanReadable(isMultiThreaded)
            ),
            context.resources.getString(R.string.displayProcessorSocket, cpu_socket),
            context.resources.getString(R.string.displayWattage, cpu_wattage),
            context.resources.getString(
                R.string.cpuDisplayHasHeatsink,
                HumanReadableUtils.tinyIntHumanReadable(hasHeatsink)
            )
        )
        return super.getDetailsForDisplay(context, details)
    }

    override fun setAllDetails(allDetails: List<Any?>) {
        core_speed_ghz = allDetails[allDetails.lastIndex - 5] as Double
        core_count = allDetails[allDetails.lastIndex - 4] as Int
        isMultiThreaded = allDetails[allDetails.lastIndex - 3] as Int
        cpu_socket = allDetails[allDetails.lastIndex - 2] as String
        cpu_wattage = allDetails[allDetails.lastIndex - 1] as Int
        hasHeatsink = allDetails[allDetails.lastIndex] as Int
        super.setAllDetails(allDetails)
    }
}