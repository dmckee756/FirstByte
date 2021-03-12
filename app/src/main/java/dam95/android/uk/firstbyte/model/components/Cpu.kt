package dam95.android.uk.firstbyte.model.components

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 *
 */
@Entity(tableName = "savedCpu")
data class Cpu(
    @PrimaryKey(autoGenerate = false)
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
     */
    override fun getDetails(): List<*> {
        return listOf(
            name, type,
            imageLink, rrpPrice,
            amazonPrice, amazonLink,
            scanPrice, scanLink, deletable,
            name,
            core_speed_ghz, core_count,
            isMultiThreaded, cpu_socket,
            cpu_wattage, hasHeatsink
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
        core_speed_ghz = database_Read[10] as Double
        core_count = database_Read[11] as Int
        isMultiThreaded = database_Read[12] as Int
        cpu_socket = database_Read[13] as String
        cpu_wattage = database_Read[14] as Int
        hasHeatsink = database_Read[database_Read.lastIndex] as Int
    }
}