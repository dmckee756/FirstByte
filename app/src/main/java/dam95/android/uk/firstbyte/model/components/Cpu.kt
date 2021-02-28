package dam95.android.uk.firstbyte.model.components

import com.google.gson.annotations.SerializedName

/**
 *
 */
object Cpu : Component() {
    @SerializedName("core_speed_ghz")
    private var coreSpeedGhz: Double = DOUBLE_DEFAULT
    @SerializedName("core_count")
    private var coreCount: Int = INT_DEFAULT
    @SerializedName("multi_threading")
    private var boolean: Boolean = false
    @SerializedName("cpu_socket")
    private lateinit var cpuSocket: String
    @SerializedName("cpu_wattage")
    private var wattage: Int = INT_DEFAULT
    @SerializedName("default_heatsink")
    private var defaultHeatsink: Double = DOUBLE_DEFAULT
}