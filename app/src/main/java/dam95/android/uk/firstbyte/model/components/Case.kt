package dam95.android.uk.firstbyte.model.components

import android.content.Context
import com.google.gson.annotations.SerializedName
import dam95.android.uk.firstbyte.R

/**
 *
 */
data class Case(
    @SerializedName("component_type")
    override var type: String,
    @SerializedName("image_link")
    override var imageLink: String,

    @SerializedName("case_name")
    override var name: String,
    var case_fan_slots: Int,
    var case_fan_sizes_mm: Int,
    var case_motherboard: String,
    var case_dimensions: String,

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
     * Bundles all variables of a Case into a list and returns it to the caller.
     */
    override fun getDetails(): List<*> {
        return listOf(
            name, type,
            imageLink, rrpPrice,
            amazonPrice, amazonLink,
            scanPrice, scanLink, deletable,
            name,
            case_fan_slots, case_fan_sizes_mm,
            case_motherboard, case_dimensions
        )
    }

    /**
     *
     */
    override fun getDetailsForDisplay(context: Context, childDetails: MutableList<String>?): List<String>? {
        val details = mutableListOf(
            context.resources.getString(R.string.caseDisplayMotherboard, case_motherboard),
            context.resources.getString(R.string.displayFanSlots, case_fan_slots),
            context.resources.getString(R.string.caseDisplayFanSizes, case_fan_sizes_mm),
            context.resources.getString(R.string.displayDimensions, case_dimensions)
        )
        return super.getDetailsForDisplay(context, details)
    }

    /**
     *
     */
    override fun setDetails(database_Read: List<*>) {
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
        case_fan_slots = database_Read[10] as Int
        case_fan_sizes_mm = database_Read[11] as Int
        case_motherboard = database_Read[12] as String
        case_dimensions = database_Read[13] as String
        //The data retrieval from SQLite doesn't actually convert it to boolean, so it must be done here
        deletable = database_Read[database_Read.lastIndex] == 1
    }
}