package dam95.android.uk.firstbyte.model.components

import com.google.gson.annotations.SerializedName


/**
 *
 */
object Psu : Component() {
    @SerializedName("psu_wattage")
    private var wattage: Int = INT_DEFAULT
    @SerializedName("rating")
    private lateinit var rating: String
    @SerializedName("modular")
    private var modular: Boolean = false
}