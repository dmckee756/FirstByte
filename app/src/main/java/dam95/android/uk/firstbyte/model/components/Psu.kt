package dam95.android.uk.firstbyte.model.components

import android.content.Context
import com.google.gson.annotations.SerializedName
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.model.util.HumanReadableUtils

/**
 *
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
     */
    override fun getDetails(): List<*> {
        return listOf(
            name, type,
            imageLink, rrpPrice,
            amazonPrice, amazonLink,
            scanPrice, scanLink, deletable,
            name,
            psu_wattage, rating,
            isModular
        )
    }

    /**
     *
     */
    override fun getDetailsForDisplay(context: Context, childDetails: MutableList<String>?): List<String>? {
        val details = mutableListOf(
            context.resources.getString(R.string.psuDisplayWattageProduces, psu_wattage),
            context.resources.getString(R.string.psuDisplayRating, rating),
            context.resources.getString(R.string.psuDisplayIsModular, HumanReadableUtils.tinyIntHumanReadable(isModular))
        )
        return super.getDetailsForDisplay(context, details)
    }

    /**
     *
     */
    override fun setDetails(database_Read: List<*>){
        name = database_Read[0] as String
        type = database_Read[1] as String
        imageLink = database_Read[2] as String
        rrpPrice = database_Read[3] as Double
        amazonPrice = database_Read[4] as Double?
        amazonLink = database_Read[5] as String?
        scanPrice = database_Read[6] as Double?
        scanLink = database_Read[7] as String?
        deletable = database_Read[8] as Boolean
        //Skip 9, it contains a duplicate name. If this gets turned into a loop then just assign to to name again.
        psu_wattage = database_Read[10] as Int
        rating = database_Read[11] as String
        isModular = database_Read[12] as Int
        //The data retrieval from SQLite doesn't actually convert it to boolean, so it must be done here
        deletable = database_Read[database_Read.lastIndex] == 1
    }
}