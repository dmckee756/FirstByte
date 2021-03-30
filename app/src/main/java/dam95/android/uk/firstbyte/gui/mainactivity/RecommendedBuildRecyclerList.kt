package dam95.android.uk.firstbyte.gui.mainactivity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
    private var tierPosition: Int = 1

    /**
     *
     */
    inner class ViewHolder(
        itemView: View,
        private val tierTitle: TextView,
        private val tierPrice: TextView,
        private val tierDescription: TextView,
        private val tierButton: Button
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {


        /**
         *
         */
        fun bindDataSet(pcAndDetails: Pair<PCBuild, String>) {
            tierTitle.text = pcAndDetails.second
            tierPrice.text = context!!.resources.getString(R.string.recommendedTierPrice, "£", pcAndDetails.first.pcPrice)
            if (tierPosition % 2 == 0){
                itemView.setBackgroundColor(context.resources.getColor(R.color.secondaryColorDark))
                tierTitle.setTextColor(context.resources.getColor(R.color.textColorLight))
                tierPrice.setTextColor(context.resources.getColor(R.color.textColorLight))
                tierDescription.setTextColor(context.resources.getColor(R.color.textColorLight))
            }
        }

        /**
         *
         */
        override fun onClick(view: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                when (view?.id){
                    this.tierButton.id -> listener.onBuildButtonClick(recommendedTier[adapterPosition].first)
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
        val tierButton: Button

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
            tierButton = homeLeftBinding.imageBtnLeft
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
            tierButton = homeRightBinding.imageBtnRight
        }

        return ViewHolder(
            cardView,
            tierTitle,
            tierPrice,
            tierDescription,
            tierButton
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