package dam95.android.uk.firstbyte.model

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.renderscript.ScriptGroup
import android.widget.ImageView
import com.google.gson.annotations.SerializedName
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.DisplayHardwarelistBinding

/**
 * The retrieved data from the Node API for the HardwareList fragment recycler list
 */
data class SearchedHardwareItem(
    @SerializedName("component_name")
    var name: String,
    @SerializedName("component_type")
    val category: String,
    @SerializedName("image_link")
    val image_link: String,
    @SerializedName("rrp_price")
    val rrpPrice: Double
) {
    //Use Picasso to load link into an image

    /**
     *
     */
    fun rrpPriceToCurrency(): String = "£$rrpPrice"
}