package dam95.android.uk.firstbyte.model.components

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 *
 */
@Entity(tableName = "savedHeatsink")
data class Heatsink(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("component_type")
    override var type: String,
    @SerializedName("image_link")
    override var imageLink: String,

    @SerializedName("heatsink_name")
    override var name: String,
    var fan_slots: Int,
    var amd_socket_min: String?,
    var amd_socket_max: String?,
    var intel_socket_min: String?,
    var intel_socket_max: String?,
    var heatsink_dimensions: String,

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
     * Bundles all variables of a Heatsink into a list and returns it to the caller.
     */
    override fun getDetails(): List<*> {
        return listOf(
            name, type,
            imageLink, rrpPrice,
            amazonPrice, amazonLink,
            scanPrice, scanLink, deletable,
            name, fan_slots,
            amd_socket_min, amd_socket_max,
            intel_socket_min, intel_socket_max,
            heatsink_dimensions
        )
    }

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
        fan_slots = database_Read[10] as Int
        amd_socket_min = database_Read[11] as String?
        amd_socket_max = database_Read[12] as String?
        intel_socket_min = database_Read[13] as String?
        intel_socket_max = database_Read[14] as String?
        heatsink_dimensions = database_Read[database_Read.lastIndex] as String
    }
}