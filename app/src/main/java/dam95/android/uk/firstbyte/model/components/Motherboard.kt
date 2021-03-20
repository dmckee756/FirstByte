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
    @SerializedName("usb3")
    var hasUsb3Plus: Int,
    @SerializedName("wifi")
    var hasWifi: Int,
    @SerializedName("pci_e")
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
            board_type, board_dimensions,
            processor_socket, ddr_sdram,
            hasUsb3Plus, hasWifi,
            pci_e, hasNvmeSupport
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
            context.resources.getString(R.string.motherboardDisplayBoardSize, board_type),
            context.resources.getString(R.string.displayProcessorSocket, processor_socket),
            context.resources.getString(R.string.displayRamDDR, ddr_sdram),
            context.resources.getString(
                R.string.motherboardDisplayHaveWifi,
                HumanReadableUtils.tinyIntHumanReadable(hasWifi)
            ),
            context.resources.getString(
                R.string.motherboardDisplayHasUSB3,
                HumanReadableUtils.tinyIntHumanReadable(hasUsb3Plus)
            ),
            context.resources.getString(R.string.motherboardDisplayPCIE, pci_e),
            context.resources.getString(
                R.string.motherboardDisplayNVME,
                HumanReadableUtils.tinyIntHumanReadable(hasNvmeSupport)
            ),
            context.resources.getString(R.string.displayDimensions, board_dimensions)
        )
        return super.getDetailsForDisplay(context, details)
    }

    override fun setAllDetails(allDetails: List<Any?>) {
        board_type = allDetails[allDetails.lastIndex - 7] as String
        board_dimensions = allDetails[allDetails.lastIndex - 6] as String
        processor_socket = allDetails[allDetails.lastIndex - 5]  as String
        ddr_sdram = allDetails[allDetails.lastIndex - 4]  as String
        hasUsb3Plus = allDetails[allDetails.lastIndex - 3] as Int
        hasWifi = allDetails[allDetails.lastIndex - 2] as Int
        pci_e = allDetails[allDetails.lastIndex - 1] as Double
        hasNvmeSupport = allDetails[allDetails.lastIndex] as Int
        super.setAllDetails(allDetails)
    }
}