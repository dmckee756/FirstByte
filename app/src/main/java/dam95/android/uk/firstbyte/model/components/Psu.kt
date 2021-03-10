package dam95.android.uk.firstbyte.model.components

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


/**
 *
 */
@Entity(tableName = "savedPsu")
data class Psu(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("component_type")
    var type: String,
    @SerializedName("image_link")
    var imageLink: String,

    var psu_name: String,
    var psu_wattage: Int,
    var rating: String,
    @SerializedName("modular")
    var isModular: Byte,

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