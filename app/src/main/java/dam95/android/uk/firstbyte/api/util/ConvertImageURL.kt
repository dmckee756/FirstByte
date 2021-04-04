package dam95.android.uk.firstbyte.api.util

import android.widget.ImageView
import com.squareup.picasso.Picasso

/**
 * @Version 1.0
 * @author David Mckee
 * This singleton object's function is to convert component/pc part Image url's into images.
 * It will load these pictures into XML ImageViews. It utilises Picasso's libraries for automatic conversion...
 * ...and automatic caching of loaded images.
 */
object ConvertImageURL {

    /**
     * Converts the component image url/web link into an image and loads it into the passed in ImageView
     * @param imageLink The WebLink/URL that will be loaded into an image.
     * @param imageView The passed through ImageView that will hold the loaded image.
     */
    fun convertURLtoImage(imageLink: String, imageView: ImageView) = Picasso.get().load(imageLink).into(imageView)

}