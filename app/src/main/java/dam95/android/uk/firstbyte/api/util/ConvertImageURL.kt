package dam95.android.uk.firstbyte.api.util

import android.util.Log
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

        Log.i("IMAGE_URL", imageLink)
        //
        Picasso.get().load(imageLink).into(imageView)
    }
}