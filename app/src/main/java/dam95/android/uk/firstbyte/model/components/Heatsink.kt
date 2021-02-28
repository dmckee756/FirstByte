package dam95.android.uk.firstbyte.model.components

import com.google.gson.annotations.SerializedName

/**
 *
 */
object Heatsink : Component() {
    @SerializedName("socket")
    private lateinit var cpuSocket: String

    @SerializedName("fan_slots")
    private var fanSlots: Int = INT_DEFAULT

    @SerializedName("amd_socket_min")
    private var amdSocketMin: String? = null

    @SerializedName("amd_socket_max")
    private var amdSocketMax: String? = null

    @SerializedName("intel_socket_min")
    private var intelSocketMin: String? = null

    @SerializedName("intel_socket_max")
    private var intelSocketMax: String? = null

    @SerializedName("heatsink_dimensions")
    private lateinit var dimensions: String
}