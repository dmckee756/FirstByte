package dam95.android.uk.firstbyte.gui.mainactivity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import dam95.android.uk.firstbyte.api.util.ConvertImageURL
import dam95.android.uk.firstbyte.databinding.ImageSliderBinding
import dam95.android.uk.firstbyte.model.PCBuild

/**
 * @author David Mckee
 * @Version 1.0
 * Recycler list adapter for the current Recommend PC Build.
 * Responsible for converting Component Image URL's into viewable image views and creating a gallery
 * that the user can scroll through (or the app's auto scroll) on the home screen, in a ViewPager view for each Recommended Build.
 */
class ImageSliderAdapter(
    private val context: Context?,
    private val urlImageList: MutableList<String?>,
    private val listener: RecommendedBuildRecyclerList.OnItemClickListener,
    private val pcBuild: PCBuild
) : RecyclerView.Adapter<ImageSliderAdapter.ViewHolder>() {

    /**
     * Bind each image view in this recommended pc build "gallery" with the components URL.
     * If any onclick events happen to the image views in the PC's ViewPager adapter, It will send the event to the Home Fragment.
     */
    inner class ViewHolder(
        itemView: View,
        private val sliderImageView: ImageView
    ) : RecyclerView.ViewHolder(itemView) {

        init {
            sliderImageView.setOnClickListener {
                listener.onBuildButtonClick(pcBuild)
            }
        }

        /**
         * Convert the supplied PC Part Image URLs into images and assign them to the image view using Picasso.
         */
        fun onBindDataSet(pcPartURL: String?) {
            pcPartURL?.let { url ->
                ConvertImageURL.convertURLtoImage(
                    url, sliderImageView
                )
            }
        }
    }

    /**
     * Return size of the data set.
     */
    override fun getItemCount(): Int = urlImageList.size

    /**
     * Initialize the horizontal image gallery that displays some of the Recommended PC parts images.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val sliderViewBinding =
            ImageSliderBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(
            sliderViewBinding.imageSliderView,
            sliderViewBinding.displayRecommendBuildPCPartImage
        )
    }

    /**
     * Call the inner view holder class and bind each PC Part image to the current image view item.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindDataSet(urlImageList[position])
    }
}