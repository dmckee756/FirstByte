package dam95.android.uk.firstbyte.gui.components.search

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.api.util.ConvertImageURL
import dam95.android.uk.firstbyte.databinding.DisplayHardwarelistBinding
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.util.HumanReadableUtils
import java.util.*

/**
 *
 */
class HardwareListRecyclerList(
    private val context: Context?,
    private val listener: OnItemClickListener,
    private val isLoadingFromServer: Boolean,
) : RecyclerView.Adapter<HardwareListRecyclerList.ViewHolder>() {

    private var hardwareListFull = listOf<SearchedHardwareItem>()
    private var hardwareListUsed = listOf<SearchedHardwareItem>()


    /**
     *
     */
    inner class ViewHolder(
        itemView: View,
        private val nameText: TextView,
        private val priceText: TextView,
        private val componentImage: ImageView,
        private val hardwareBtn: Button
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        init {
            hardwareBtn.setOnClickListener(this)
        }

        /**
         *
         */
        fun bindDataSet(displayedComponent: SearchedHardwareItem) {
            if (isLoadingFromServer) {
                hardwareBtn.setBackgroundResource(R.drawable.object_online_display)
            } else {
                hardwareBtn.setBackgroundResource(R.drawable.object_offline_display)
            }

            ConvertImageURL.convertURLtoImage(
                displayedComponent.image_link,
                componentImage
            )
            nameText.text = displayedComponent.name
            priceText.text = HumanReadableUtils.rrpPriceToCurrency(displayedComponent.rrpPrice)
        }

        /**
         *
         */
        override fun onClick(view: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                when (view?.id) {
                    hardwareBtn.id -> listener.onHardwareClick(
                        hardwareListUsed[adapterPosition].name,
                        hardwareListUsed[adapterPosition].category
                    )
                }
            }
        }

    }

    /**
     *
     */
    interface OnItemClickListener {
        fun onHardwareClick(componentName: String, componentType: String)
    }

    /**
     *
     */
    fun setDataList(newList: List<SearchedHardwareItem>) {
        hardwareListFull = newList
        hardwareListUsed = newList
        notifyDataSetChanged()
    }

    /**
     *
     */
    override fun getItemCount(): Int = hardwareListFull.size

    /**
     *
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val hardwareListBinding =
            DisplayHardwarelistBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(
            hardwareListBinding.hardwareListCard,
            hardwareListBinding.hardwareSearchName,
            hardwareListBinding.hardwareSearchPrice,
            hardwareListBinding.hardwareSearchImage,
            hardwareListBinding.hardwareBtn
        )
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataSet(hardwareListUsed[position])
    }

    fun sortDataSet(buttonID: Int) {
        val sortedList: List<SearchedHardwareItem> = when (buttonID) {
            R.id.alphabeticalAscendingID -> hardwareListUsed.sortedBy { it.name.capitalize(Locale.ROOT)}
            R.id.alphabeticalDescendingID -> hardwareListUsed.sortedByDescending { it.name.capitalize(Locale.ROOT)}
            R.id.priceAscendingID -> hardwareListUsed.sortedBy { it.rrpPrice.toString() }
            R.id.priceDescendingID -> hardwareListUsed.sortedByDescending { it.rrpPrice.toString() }
            else -> hardwareListFull
        }
        hardwareListUsed = sortedList
        notifyDataSetChanged()
    }
}