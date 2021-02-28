package dam95.android.uk.firstbyte.model.components

import com.google.gson.annotations.SerializedName

/**
 *
 */
object Case : Component() {
    @SerializedName("case_fan_slots")
    private var fanSlots: Int = INT_DEFAULT

    @SerializedName("case_fan_sizes_mm")
    private var fanSizesMM: Int = INT_DEFAULT

    @SerializedName("case_motherboard")
    private lateinit var motherboardSize: String

    @SerializedName("case_dimensions")
    private lateinit var dimensions: String
}