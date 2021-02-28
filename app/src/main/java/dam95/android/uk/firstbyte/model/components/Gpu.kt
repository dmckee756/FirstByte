package dam95.android.uk.firstbyte.model.components

import com.google.gson.annotations.SerializedName

/**
 *
 */
object Gpu : Component() {
    @SerializedName("core_speed_mhz")
    private var coreSpeedMhz: Int = INT_DEFAULT
    @SerializedName("memory_size_gb")
    private var memorySizeGb: Int = INT_DEFAULT
    @SerializedName("memory_speed_mhz")
    private var memorySpeedMhz: Int = INT_DEFAULT
    @SerializedName("wattage")
    private var wattage: Int = INT_DEFAULT
    @SerializedName("dimensions")
    private lateinit var dimensions: String
}