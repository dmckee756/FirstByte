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

class ImageSliderAdapter(
    private val context: Context?,
    private val urlImageList: MutableList<String?>,
    private val listener: RecommendedBuildRecyclerList.OnItemClickListener,
    private val pcBuild: PCBuild
) : RecyclerView.Adapter<ImageSliderAdapter.ViewHolder>() {

    inner class ViewHolder(
        itemView: View,
        private val sliderImageView: ImageView
    ) : RecyclerView.ViewHolder(itemView) {

        init {
            sliderImageView.setOnClickListener{
                listener.onBuildButtonClick(pcBuild)
            }
        }

        fun onBindDataSet(pcPartURL: String?) {
            pcPartURL?.let { url ->
                ConvertImageURL.convertURLtoImage(
                    url, sliderImageView
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val sliderViewBinding =
            ImageSliderBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(
            sliderViewBinding.imageSliderView,
            sliderViewBinding.displayRecommendBuildPCPartImage
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindDataSet(urlImageList[position])
    }

    override fun getItemCount(): Int = urlImageList.size

}