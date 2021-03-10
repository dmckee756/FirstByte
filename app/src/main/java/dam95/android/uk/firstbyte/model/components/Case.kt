package dam95.android.uk.firstbyte.model.components

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 *
 */
@Entity(tableName = "savedCase")
data class Case(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("component_type")
    var type: String,
    @SerializedName("image_link")
    var imageLink: String,

    var case_name: String,
    var case_fan_slots: Int,
    var case_fan_sizes_mm: Int,
    var case_motherboard: String,
    var case_dimensions: String,

    @SerializedName("rrp_price")
    var rrpPrice: Double,
    @SerializedName("amazon_price")
    var amazonPrice: Double?,
    @SerializedName("amazon_link")
    var amazonLink: String?,
    @SerializedName("scan_price")
    var scanPrice: Double?,
    @SerializedName("scan_link")
    var scanLink: String?,
    var deletable: Boolean = true
): Component() {}