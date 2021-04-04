package dam95.android.uk.firstbyte.model.components

import android.content.Context
import com.google.gson.annotations.SerializedName
import dam95.android.uk.firstbyte.R

/**
 * @author David Mckee
 * @Version 1.0
 * Ram object designed to hold all hardware specifications of display on fragments, saving to the app's database
 * and data retrieval from both the API and app's database.
 * Is a child object of Component.
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
     * This is primarily used when dealing with loading/saving components into the app's database.
     * @return list of all Ram variables and values.
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
     * Important to keep this in the same order as the constructor,
     * When the Ram is loaded from the database, assign all of it's details to this Ram object first, then call the
     * parent components setAllDetails method to assign the rest of it's values.
     * @param allDetails All loaded details from the app's database.
     */
    override fun setAllDetails(allDetails: List<Any?>) {
        memory_speed_mhz = allDetails[allDetails.lastIndex - 3] as Int
        memory_size_gb = allDetails[allDetails.lastIndex - 2] as Int
        ram_ddr = allDetails[allDetails.lastIndex - 1] as String
        num_of_sticks = allDetails[allDetails.lastIndex] as Int
        super.setAllDetails(allDetails)
    }

    /**
     * Put the Ram values into a human readable sentences for display in the hardware details fragment.
     * @param context used to find the XML String resource that the values are put into.
     * @param childDetails details of this Ram object
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
}