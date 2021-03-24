package dam95.android.uk.firstbyte.gui.mainactivity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dam95.android.uk.firstbyte.databinding.DisplayRecommendedBuildRightBinding
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess

class RecommendedBuildRecyclerList (
    private val context: Context?,
    private val fbHardware: FirstByteDBAccess,
    private val listener: OnItemClickListener,
) : RecyclerView.Adapter<RecommendedBuildRecyclerList.ViewHolder>() {

    private var recommendedTier = emptyList<String>()

    /**
     *
     */
    inner class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {


        /**
         *
         */
        fun bindDataSet() {

        }

        /**
         *
         */
        override fun onClick(view: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {

            }
        }

    }

    /**
     *
     */
    interface OnItemClickListener {
        fun onBuildButtonClick()
        fun onLeftImageClick()
        fun onRightImageClick()
    }

    /**
     *
     */
    fun setDataList(listOfTiers: List<String>) {
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
        /*
            val recommendedBuildLeftBinding = DisplayRecommendedBuildLeftBinding.inflate(LayoutInflater.from(context), parent, false)
        */

        val recommendedBuildRightBinding =
            DisplayRecommendedBuildRightBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(
        recommendedBuildRightBinding.recommendedPCLayout
        )
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }
}