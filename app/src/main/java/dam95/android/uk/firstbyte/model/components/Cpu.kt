package dam95.android.uk.firstbyte.model.components


import android.content.Context
import com.google.gson.annotations.SerializedName
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.model.util.HumanReadableUtils

/**
 * @author David Mckee
 * @Version 1.0
 * Processor object designed to hold all hardware specifications of display on fragments, saving to the app's database
 * and data retrieval from both the API and app's database.
 * Is a child object of Component.
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
     * This is primarily used when dealing with loading/saving components into the app's database.
     * @return list of all Cpu variables and values.
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
     * Important to keep this in the same order as the constructor,
     * When the Cpu is loaded from the database, assign all of it's details to this Cpu object first, then call the
     * parent components setAllDetails method to assign the rest of it's values.
     * @param allDetails All loaded details from the app's database.
     */
    override fun setAllDetails(allDetails: List<Any?>) {
        core_speed_ghz = allDetails[allDetails.lastIndex - 5] as Double
        core_count = allDetails[allDetails.lastIndex - 4] as Int
        isMultiThreaded = allDetails[allDetails.lastIndex - 3] as Int
        cpu_socket = allDetails[allDetails.lastIndex - 2] as String
        cpu_wattage = allDetails[allDetails.lastIndex - 1] as Int
        hasHeatsink = allDetails[allDetails.lastIndex] as Int
        super.setAllDetails(allDetails)
    }

    /**
     * Put the Cpu values into a human readable sentences for display in the hardware details fragment.
     * @param context used to find the XML String resource that the values are put into.
     * @param childDetails details of this Cpu object
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
}