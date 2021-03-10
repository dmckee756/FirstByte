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
    var isMultiThreaded: Byte,
    var cpu_socket: String,
    var cpu_wattage: Int,
    @SerializedName("default_heatsink")
    var hasHeatsink: Byte,

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
): Component {}