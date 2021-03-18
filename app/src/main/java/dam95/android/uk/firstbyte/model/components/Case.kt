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
            case_fan_slots, case_fan_sizes_mm,
            case_motherboard, case_dimensions
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
    override fun setAllDetails(allDetails: List<Any?>) {
        case_fan_slots = allDetails[allDetails.lastIndex - 3] as Int
        case_fan_sizes_mm = allDetails[allDetails.lastIndex - 2] as Int
        case_motherboard = allDetails[allDetails.lastIndex - 1] as String
        case_dimensions = allDetails[allDetails.lastIndex] as String
        super.setAllDetails(allDetails)
    }
}