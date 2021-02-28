package dam95.android.uk.firstbyte.api

import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso
import dam95.android.uk.firstbyte.model.SearchedHardwareItem

/**
 *
 */
object ConvertImageURL {

    /**
     *
     */
    fun convertURLtoImage(hardwareComponent: SearchedHardwareItem, imageView: ImageView) {
        Log.i("IMAGE_NAME", hardwareComponent.name)
        Log.i("IMAGE_URL", hardwareComponent.image_link)
        //
        Picasso.get().load(hardwareComponent.image_link).into(imageView)
    }
}