package dam95.android.uk.firstbyte.model.components

import com.google.gson.annotations.SerializedName


/**
 *
 */
object Fan : Component(){
    @SerializedName("fan_size_mm")
    private var fanSizeMM: Int = INT_DEFAULT
    @SerializedName("fan_rpm")
    private var fanRPM: Int = INT_DEFAULT
}