package dam95.android.uk.firstbyte.gui.mainactivity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import dam95.android.uk.firstbyte.databinding.ImageSliderBinding

class ImageSliderAdapter(
    private val viewPager2: ViewPager2,
    private val context: Context?,
    private val listener: RecommendedBuildRecyclerList.OnItemClickListener
) : RecyclerView.Adapter<ImageSliderAdapter.ViewHolder>() {

    private var urlImageList = listOf<String>()

    inner class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        fun onBindDataSet(){

        }


        override fun onClick(v: View?) {
            TODO("Not yet implemented")
        }
    }

    interface OnItemClickListener {

    }

    fun setImageUrlList(urlList: List<String>){
        urlImageList = urlList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val sliderViewBinding = ImageSliderBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(
            sliderViewBinding.imageSliderView
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindDataSet()
    }

    override fun getItemCount(): Int = urlImageList.size
}