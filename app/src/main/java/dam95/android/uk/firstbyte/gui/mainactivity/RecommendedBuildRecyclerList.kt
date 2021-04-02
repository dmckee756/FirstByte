package dam95.android.uk.firstbyte.gui.mainactivity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.DisplayRecommendedBuildLeftBinding
import dam95.android.uk.firstbyte.databinding.DisplayRecommendedBuildRightBinding
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import dam95.android.uk.firstbyte.model.PCBuild

class RecommendedBuildRecyclerList(
    private val context: Context?,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RecommendedBuildRecyclerList.ViewHolder>() {

    private var recommendedTier = emptyList<Pair<PCBuild, String>>()
    private lateinit var viewPager2: ViewPager2
    private var tierPosition: Int = 1

    /**
     *
     */
    inner class ViewHolder(
        itemView: View,
        private val tierTitle: TextView,
        private val tierPrice: TextView,
        private val tierDescription: TextView
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {


        /**
         *
         */
        fun bindDataSet(pcAndDetails: Pair<PCBuild, String>) {
            tierTitle.text = pcAndDetails.second
            tierPrice.text = context!!.resources.getString(R.string.recommendedTierPrice, "£", pcAndDetails.first.pcPrice)
            if (tierPosition % 2 == 0){
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.secondaryColorDark))
                tierTitle.setTextColor(ContextCompat.getColor(context, R.color.textColorLight))
                tierPrice.setTextColor(ContextCompat.getColor(context, R.color.textColorLight))
                tierDescription.setTextColor(ContextCompat.getColor(context, R.color.textColorLight))
            }
        }

        /**
         *
         */
        override fun onClick(view: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                when (view?.id){

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
    fun setDataList(listOfTiers: List<Pair<PCBuild,String>>) {
        recommendedTier = listOfTiers
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
        }

        return ViewHolder(
            cardView,
            tierTitle,
            tierPrice,
            tierDescription
        )
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataSet(recommendedTier[position])
        tierPosition++
        //Reset tierPosition to 1
        if(position == recommendedTier.size) tierPosition = 1
    }

}