package dam95.android.uk.firstbyte.model.components

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 *
 */
@Entity(tableName = "savedMotherboard")
data class Motherboard(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("component_type")
    var type: String,
    @SerializedName("image_link")
    var imageLink: String,

    var board_name: String,
    var board_type: String,
    var board_dimensions: String,
    var processor_socket: String,
    var ddr_sdram: String,
    @SerializedName("usb3+")
    val hasUsb3Plus: Byte,
    @SerializedName("wifi")
    var hasWifi: Byte,
    @SerializedName("pci-e")
    var pci_e: Double,
    @SerializedName("nvme_support")
    val hasNvmeSupport: Byte,

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