package dam95.android.uk.firstbyte.gui.mainactivity

import android.content.Context
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

/**
 * @author David Mckee
 * @Version 1.0
 * Recycler list adapter for the home fragment, responsible for creating the views
 * for displaying each recommended pc and adding the necessary
 * contents that are displayed on the home screen.
 */
class RecommendedBuildRecyclerList(
    private val context: Context?,
    private val listener: OnItemClickListener,
    private val fbHardwareDB: FirstByteDBAccess
) : RecyclerView.Adapter<RecommendedBuildRecyclerList.ViewHolder>() {

    private var recommendedTier = emptyList<Pair<PCBuild, String>>()
    private var tierPosition: Int = 1

    /**
     * Bind each part of the recommended pc builds for displaying.
     * This will repeat until all 4 recommended pc builds have their views correctly assigned,
     * if any onclick events happen to the image views in the PC's ViewPager adapter, It will send the event to the Home Fragment.
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
         * Bind each view with the Recommended PC Builds displayed details. E.g. The tier name, pc price, images and description.
         * Also sets a timer for the ViewPager2 adapter to scroll to the next PC Part image in it's horizontal recycler list.
         */
        fun bindDataSet(recommendedPC: Pair<PCBuild, String>) {
            tierTitle.text = recommendedPC.first.pcName
            tierPrice.text =
                context!!.resources.getString(R.string.recommendedTierPrice, "£", recommendedPC.first.pcPrice)
            tierDescription.text = recommendedPC.second
            val urlList = mutableListOf(
                fbHardwareDB.retrieveImageURL(recommendedPC.first.caseName!!),
                fbHardwareDB.retrieveImageURL(recommendedPC.first.gpuName!!),
                fbHardwareDB.retrieveImageURL(recommendedPC.first.cpuName!!),
                fbHardwareDB.retrieveImageURL(recommendedPC.first.psuName!!),
                fbHardwareDB.retrieveImageURL(recommendedPC.first.motherboardName!!)
            )

            recommendedPC.first.heatsinkName?.let { heatsink -> urlList.add(fbHardwareDB.retrieveImageURL(heatsink)) }
            //Do a small time delay, meaning there's some time difference between image scrolling in each display
            runBlocking {
                delay(50)
            }

            //Initialise the ViewPager2 recycler list adapter, to allow PC Part images to be shown like a slideshow gallery, in a horizontal orientation
            viewPager2.adapter = ImageSliderAdapter(context, urlList, listener, recommendedPC.first)


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
            //Schedule for this viewPager2 display to change the displayed image every 4 seconds.
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    viewPager2.post(moveImages)
                }
            }, 4000, 4000)

            //For every even numbered position in this recycler adapter, change the background colors.
            if (tierPosition % 2 == 0) {
                itemView.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.secondaryColorDark
                    )
                )
                //Set up the display text colors for the darker background displays.
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
         * Send onClick event to the home fragment
         */
        override fun onClick(view: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                when (view?.id) {
                    viewPager2.id -> listener.onBuildButtonClick(recommendedTier[adapterPosition].first)
                }
            }
        }
    }

    /**
     * Methods that call back to the home fragment when the assigned view is clicked.
     */
    interface OnItemClickListener {
        fun onBuildButtonClick(recommendedPC: PCBuild)
    }

    /**
     * Assigns the data set that will be used in this recycler list.
     */
    fun setDataList(recommendedBuilds: List<Pair<PCBuild, String>>) {
        recommendedTier = recommendedBuilds
        notifyDataSetChanged()
    }

    /**
     * Return size of the data set.
     */
    override fun getItemCount(): Int = recommendedTier.size

    /**
     * Initialize the layout/views that will display the recommended PC and send the information to the inner view holder class.
     * Utilises 2 different layouts, so that the home screen has some variation.
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
            //For the first two displays of this recycler list, use the left layout view for the home page
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
            //For the next two displays of this recycler list, use the right layout view for the home page
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
     * Call the inner view holder class and bind pc details to the current recycler list display item.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindDataSet(recommendedTier[position])
        tierPosition++
        //Reset tierPosition to 1
        if (position == recommendedTier.size) tierPosition = 1
    }

}