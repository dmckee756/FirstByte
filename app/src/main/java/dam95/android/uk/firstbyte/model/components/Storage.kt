package dam95.android.uk.firstbyte.model.components

import android.content.Context
import com.google.gson.annotations.SerializedName
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.model.util.HumanReadableUtils

/**
 * @author David Mckee
 * @Version 1.0
 * Storage object designed to hold all hardware specifications of display on fragments, saving to the app's database
 * and data retrieval from both the API and app's database.
 * Is a child object of Component.
 */
data class Storage(
    @SerializedName("component_type")
    override var type: String,
    @SerializedName("image_link")
    override var imageLink: String,

    @SerializedName("storage_name")
    override var name: String,
    var storage_type: String,
    @SerializedName("external_storage")
    var isExternalStorage: Int,
    var storage_capacity_gb: Int,
    var storage_speed_mbps: Int,

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
     * Bundles all variables of Storage into a list and returns it to the caller.
     * This is primarily used when dealing with loading/saving components into the app's database.
     * @return list of all storage variables and values.
     */
    override fun getDetails(): List<*> {
        return listOf(
            name, type,
            imageLink, rrpPrice,
            amazonPrice, amazonLink,
            scanPrice, scanLink, deletable,
            storage_type, isExternalStorage,
            storage_capacity_gb, storage_speed_mbps
        )
    }

    /**
     * Important to keep this in the same order as the constructor,
     * When the Storage is loaded from the database, assign all of it's details to this Storage object first, then call the
     * parent components setAllDetails method to assign the rest of it's values.
     * @param allDetails All loaded details from the app's database.
     */
    override fun setAllDetails(allDetails: List<Any?>) {
        storage_type = allDetails[allDetails.lastIndex - 3] as String
        isExternalStorage = allDetails[allDetails.lastIndex - 2] as Int
        storage_capacity_gb = allDetails[allDetails.lastIndex - 1] as Int
        storage_speed_mbps = allDetails[allDetails.lastIndex] as Int
        super.setAllDetails(allDetails)
    }

    /**
     * Put the Storage values into a human readable sentences for display in the hardware details fragment.
     * @param context used to find the XML String resource that the values are put into.
     * @param childDetails details of this Storage object
     */
    override fun getDetailsForDisplay(
        context: Context,
        childDetails: MutableList<String>?
    ): List<String>? {
        val details = mutableListOf(
            context.resources.getString(R.string.storageDisplayCapacity, storage_capacity_gb),
            context.resources.getString(R.string.storageDisplayType, storage_type),
            context.resources.getString(R.string.storageDisplaySpeed, storage_speed_mbps),
            context.resources.getString(
                R.string.storageDisplayExternal,
                HumanReadableUtils.tinyIntHumanReadable(isExternalStorage)
            )
        )
        return super.getDetailsForDisplay(context, details)
    }
}
