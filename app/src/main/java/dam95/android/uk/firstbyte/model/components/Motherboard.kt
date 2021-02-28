package dam95.android.uk.firstbyte.model.components

import com.google.gson.annotations.SerializedName

/**
 *
 */
object Motherboard : Component() {
    @SerializedName("board_type")
    private lateinit var boardType: String

    @SerializedName("board_dimensions")
    private lateinit var dimensions: String

    @SerializedName("processor_socket")
    private lateinit var cpuSocket: String

    @SerializedName("ddr_sdram")
    private lateinit var ddrSDRAM: String

    @SerializedName("usb3+")
    private var usb3Plus: Boolean = false //Default Value

    @SerializedName("wifi")
    private var wifi: Boolean = false //Default Value

    @SerializedName("pci-e")
    private var pci_e: Double = DOUBLE_DEFAULT

    @SerializedName("nvme_support")
    private var nvmeSupport: Boolean = false //Default Value
}