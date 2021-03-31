package dam95.android.uk.firstbyte.api.util

import android.widget.ImageView
import com.squareup.picasso.Picasso

/**
 *
 */
object ConvertImageURL {

    /**
     *
     */
    fun convertURLtoImage(imageLink: String, imageView: ImageView) {

        //
        Picasso.get().load(imageLink).into(imageView)
    }
}