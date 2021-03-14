package dam95.android.uk.firstbyte.model.components

import android.content.Context
import com.google.gson.annotations.SerializedName
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.model.util.HumanReadableUtils

/**
 *
 */
data class Motherboard(
    @SerializedName("component_type")
    override var type: String,
    @SerializedName("image_link")
    override var imageLink: String,

    @SerializedName("board_name")
    override var name: String,
    var board_type: String,
    var board_dimensions: String,
    var processor_socket: String,
    var ddr_sdram: String,
    @SerializedName("usb3+")
    var hasUsb3Plus: Int,
    @SerializedName("wifi")
    var hasWifi: Int,
    @SerializedName("pci-e")
    var pci_e: Double,
    @SerializedName("nvme_support")
    var hasNvmeSupport: Int,

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
     * Bundles all variables of a Motherboard into a list and returns it to the caller.
     */
    override fun getDetails(): List<*> {
        return listOf(
            name, type,
            imageLink, rrpPrice,
            amazonPrice, amazonLink,
            scanPrice, scanLink, deletable,
            name,
            board_type, board_dimensions,
            processor_socket, ddr_sdram,
            hasUsb3Plus, hasWifi,
            pci_e, hasNvmeSupport
        )
    }

    /**
     *
     */
    override fun getDetailsForDisplay(context: Context, childDetails: MutableList<String>?): List<String>? {
        val details = mutableListOf(
            context.resources.getString(R.string.motherboardDisplayBoardSize, board_type),
            context.resources.getString(R.string.displayProcessorSocket, processor_socket),
            context.resources.getString(R.string.displayRamDDR, ddr_sdram),
            context.resources.getString(R.string.motherboardDisplayHaveWifi, HumanReadableUtils.tinyIntHumanReadable(hasWifi)),
            context.resources.getString(R.string.motherboardDisplayHasUSB3, HumanReadableUtils.tinyIntHumanReadable(hasUsb3Plus)),
            context.resources.getString(R.string.motherboardDisplayPCIE, pci_e),
            context.resources.getString(R.string.motherboardDisplayNVME, HumanReadableUtils.tinyIntHumanReadable(hasNvmeSupport)),
            context.resources.getString(R.string.displayDimensions, board_dimensions)
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
        board_type = database_Read[10] as String
        board_dimensions = database_Read[11] as String
        processor_socket = database_Read[12] as String
        ddr_sdram = database_Read[13] as String
        hasUsb3Plus = database_Read[14] as Int
        hasWifi = database_Read[15] as Int
        pci_e = database_Read[16] as Double
        hasNvmeSupport = database_Read[17] as Int
        //The data retrieval from SQLite doesn't actually convert it to boolean, so it must be done here
        deletable = database_Read[database_Read.lastIndex] == 1
    }
}