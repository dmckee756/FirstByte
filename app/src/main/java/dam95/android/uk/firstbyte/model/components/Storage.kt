package dam95.android.uk.firstbyte.model.components

import com.google.gson.annotations.SerializedName

/**
 *
 */
object Storage : Component() {
    @SerializedName("storage_type")
    private lateinit var storageType: String
    @SerializedName("external_storage")
    private var externalStorage: Boolean = false
    @SerializedName("storage_capacity_gb")
    private var storageCapacityGB: Int = INT_DEFAULT
    @SerializedName("storage_speed_mbps")
    private var storageSpeedMBps: Int = INT_DEFAULT
}