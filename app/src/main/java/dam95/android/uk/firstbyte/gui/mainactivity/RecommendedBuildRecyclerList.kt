package dam95.android.uk.firstbyte.gui.mainactivity

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.DisplayRecommendedBuildLeftBinding
import dam95.android.uk.firstbyte.databinding.DisplayRecommendedBuildRightBinding
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import dam95.android.uk.firstbyte.model.PCBuild
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.*

class RecommendedBuildRecyclerList(
    private val context: Context?,
    private val listener: OnItemClickListener,
    private val fbHardwareDB: FirstByteDBAccess
) : RecyclerView.Adapter<RecommendedBuildRecyclerList.ViewHolder>() {

    private var recommendedTier = emptyList<PCBuild>()
    private var tierPosition: Int = 1

    /**
     *
     */
    inner class ViewHolder(
        itemView: View,
        private val tierTitle: TextView,
        private val tierPrice: TextView,
        private val tierDescription: TextView,
        private val viewPager2: ViewPager2,

        ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        /**
         *
         */
        fun bindDataSet(pcBuild: PCBuild) {
            tierTitle.text = pcBuild.pcName
            tierPrice.text =
                context!!.resources.getString(R.string.recommendedTierPrice, "£", pcBuild.pcPrice)
            val urlList = mutableListOf(
                fbHardwareDB.retrieveImageURL(pcBuild.caseName!!),
                fbHardwareDB.retrieveImageURL(pcBuild.gpuName!!),
                fbHardwareDB.retrieveImageURL(pcBuild.cpuName!!),
                fbHardwareDB.retrieveImageURL(pcBuild.psuName!!),
                fbHardwareDB.retrieveImageURL(pcBuild.motherboardName!!)
            )

            pcBuild.heatsinkName?.let { heatsink -> urlList.add(fbHardwareDB.retrieveImageURL(heatsink)) }


            //Do a small time delay, meaning there's some time difference between image scrolling in each display
            runBlocking {
                delay(50)
            }

            //
            viewPager2.adapter = ImageSliderAdapter(context, urlList, listener, pcBuild)


            val moveImages = Runnable {
                //Reset the image list position to the start, simulating a loop
                if (viewPager2.currentItem == urlList.lastIndex) viewPager2.currentItem = 0
                //Move position of current displayed image on recommended build
                //Due to the developers that did ViewPager2, controlling the scroll speed is very difficult.
                //I've used a Fake drag value of view's width divided by 1.5. Why 1.5? Because it worked, it moved and it wouldn't skip images.
                //But that worried me greatly. The size of this view shouldn't change on each phone, only it's background will.
                viewPager2.beginFakeDrag()
                viewPager2.fakeDragBy(-(viewPager2.width/1.5).toFloat())
                viewPager2.endFakeDrag()
            }
            //Schedule for this viewPager2 display to change the displayed image every 4 seconds
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    viewPager2.post(moveImages)
                }
            }, 4000, 4000)

            //For every even numbered position in this recycler adapter, change the background colors
            if (tierPosition % 2 == 0) {
                itemView.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.secondaryColorDark
                    )
                )
                tierTitle.setTextColor(ContextCompat.getColor(context, R.color.textColorLight))
                tierPrice.setTextColor(ContextCompat.getColor(context, R.color.textColorLight))
                tierDescription.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.textColorLight
                    )
                )
            }
        }

        /**
         *
         */
        override fun onClick(view: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                when (view?.id) {
                    viewPager2.id -> listener.onBuildButtonClick(recommendedTier[adapterPosition])
                }
            }
        }
    }

    /**
     *
     */
    interface OnItemClickListener {
        fun onBuildButtonClick(recommendedPC: PCBuild)
    }

    /**
     *
     */
    fun setDataList(recommendedBuilds: List<PCBuild>) {
        recommendedTier = recommendedBuilds
        notifyDataSetChanged()
    }

    /**
     *
     */
    override fun getItemCount(): Int = recommendedTier.size

    /**
     *
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val cardView: View
        val tierTitle: TextView
        val tierPrice: TextView
        val tierDescription: TextView
        val viewPager2: ViewPager2

        if (tierPosition == 1 || tierPosition == 2) {
            val homeLeftBinding = DisplayRecommendedBuildLeftBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            cardView = homeLeftBinding.recommendedPCLayoutLeft
            tierTitle = homeLeftBinding.recommendedTierTitleLeft
            tierPrice = homeLeftBinding.recommendedTierPriceLeft
            tierDescription = homeLeftBinding.recommendedTierDescriptionLeft
            viewPager2 = homeLeftBinding.imageSliderView
        } else {
            val homeRightBinding = DisplayRecommendedBuildRightBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            cardView = homeRightBinding.recommendedPCLayoutRight
            tierTitle = homeRightBinding.recommendedTierTitleRight
            tierPrice = homeRightBinding.recommendedTierPriceRight
            tierDescription = homeRightBinding.recommendedTierDescriptionRight
            viewPager2 = homeRightBinding.imageSliderView
        }

        return ViewHolder(
            cardView,
            tierTitle,
            tierPrice,
            tierDescription,
            viewPager2
        )
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindDataSet(recommendedTier[position])
        tierPosition++
        //Reset tierPosition to 1
        if (position == recommendedTier.size) tierPosition = 1
    }

}