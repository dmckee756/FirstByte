package dam95.android.uk.firstbyte.model.components

import android.content.Context
import com.google.gson.annotations.SerializedName
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.model.util.HumanReadableUtils

/**
 * @author David Mckee
 * @Version 1.0
 * Power Supply object designed to hold all hardware specifications of display on fragments, saving to the app's database
 * and data retrieval from both the API and app's database.
 * Is a child object of Component.
 */
data class Psu(
    @SerializedName("component_type")
    override var type: String,
    @SerializedName("image_link")
    override var imageLink: String,

    @SerializedName("psu_name")
    override var name: String,
    var psu_wattage: Int,
    var rating: String,
    @SerializedName("modular")
    var isModular: Int,

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
): Component {

    /**
     * Bundles all variables of a Power Supply into a list and returns it to the caller.
     * This is primarily used when dealing with loading/saving components into the app's database.
     * @return list of all Psu variables and values.
     */
    override fun getDetails(): List<*> {
        return listOf(
            name, type,
            imageLink, rrpPrice,
            amazonPrice, amazonLink,
            scanPrice, scanLink, deletable,
            psu_wattage, rating,
            isModular
        )
    }

    /**
     * Important to keep this in the same order as the constructor,
     * When the Psu is loaded from the database, assign all of it's details to this Psu object first, then call the
     * parent components setAllDetails method to assign the rest of it's values.
     * @param allDetails All loaded details from the app's database.
     */
    override fun setAllDetails(allDetails: List<Any?>) {
        psu_wattage = allDetails[allDetails.lastIndex - 2] as Int
        rating = allDetails[allDetails.lastIndex - 1] as String
        isModular = allDetails[allDetails.lastIndex] as Int
        super.setAllDetails(allDetails)
    }

    /**
     * Put the Psu values into a human readable sentences for display in the hardware details fragment.
     * @param context used to find the XML String resource that the values are put into.
     * @param childDetails details of this Psu object
     */
    override fun getDetailsForDisplay(context: Context, childDetails: MutableList<String>?): List<String>? {
        val details = mutableListOf(
            context.resources.getString(R.string.psuDisplayWattageProduces, psu_wattage),
            context.resources.getString(R.string.psuDisplayRating, rating),
            context.resources.getString(R.string.psuDisplayIsModular, HumanReadableUtils.tinyIntHumanReadable(isModular))
        )
        return super.getDetailsForDisplay(context, details)
    }
}