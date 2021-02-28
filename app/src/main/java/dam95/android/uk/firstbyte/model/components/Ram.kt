package dam95.android.uk.firstbyte.model.components

import com.google.gson.annotations.SerializedName

/**
 *
 */
object Ram : Component() {
    @SerializedName("memory_speed_mhz")
    private var memorySpeedMhz: Int = INT_DEFAULT
    @SerializedName("memory_size_gb")
    private var memorySizeGb: Int = INT_DEFAULT
    @SerializedName("ram_ddr")
    private lateinit var ramDDR: String
    @SerializedName("num_of_sticks")
    private var numOfSticks: Int = INT_DEFAULT
}