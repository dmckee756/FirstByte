package dam95.android.uk.firstbyte.gui.components.search

import android.content.Context
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
    private var hardwareListFullSorted = listOf<SearchedHardwareItem>()
    private var hardwareListUsed = listOf<SearchedHardwareItem>()
    private var listIsSorted = false
    private var listIsFiltered = false

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

    /**
     *
     */
    override fun getItemCount(): Int = hardwareListUsed.size

    fun getFullSortedList(): List<SearchedHardwareItem> = hardwareListFullSorted

    /**
     *
     */
    fun setDataList(newList: List<SearchedHardwareItem>) {
        hardwareListFull = newList
        hardwareListFullSorted = newList
        hardwareListUsed = newList
        notifyDataSetChanged()
    }

    fun loadPreviousSearchSession(
        previousList: List<SearchedHardwareItem>,
        previousSortedList: List<SearchedHardwareItem>,
        previousUsedList: List<SearchedHardwareItem>,
        wasListSorted: Boolean,
        wasListFiltered: Boolean
    ) {
        hardwareListFull = previousList
        hardwareListFullSorted = previousSortedList
        hardwareListUsed = previousUsedList
        listIsSorted = wasListSorted
        listIsFiltered = wasListFiltered
        notifyDataSetChanged()
    }

    fun setUsedDataList(newList: List<SearchedHardwareItem>) {
        hardwareListUsed = newList
        notifyDataSetChanged()
    }

    fun sortDataSet(buttonID: Int) {

        listIsSorted = true
        //If the list is currently being filtered by price, sort using the "hardwareListUsed" filtered list
        //Otherwise use the unaltered full hardware list
        val currentList = if (listIsSorted) hardwareListUsed else hardwareListFullSorted

        val sortedList: List<SearchedHardwareItem> = when (buttonID) {
            R.id.alphabeticalAscendingID -> {
                hardwareListFullSorted =
                    hardwareListFullSorted.sortedBy { it.name.capitalize(Locale.ROOT) }
                currentList.sortedBy { it.name.capitalize(Locale.ROOT) }
            }
            R.id.alphabeticalDescendingID -> {
                hardwareListFullSorted =
                    hardwareListFullSorted.sortedByDescending { it.name.capitalize(Locale.ROOT) }
                currentList.sortedByDescending { it.name.capitalize(Locale.ROOT) }
            }
            R.id.priceAscendingID -> {
                hardwareListFullSorted = hardwareListFullSorted.sortedBy { it.rrpPrice }
                currentList.sortedBy { it.rrpPrice }
            }
            R.id.priceDescendingID -> {
                hardwareListFullSorted = hardwareListFullSorted.sortedByDescending { it.rrpPrice }
                currentList.sortedByDescending { it.rrpPrice }
            }
            else -> currentList
        }

        //If the list is currently not filtered by price, update the displayed list with the unaltered full hardware list
        //Otherwise update the displayed list with the sorted filtered list
        hardwareListUsed = sortedList
        notifyDataSetChanged()
    }

    fun filterByPrice(minPrice: Float, maxPrice: Float) {
        val filteredList = mutableListOf<SearchedHardwareItem>()
        //loop add if greater or equal to minPrice and less or equal than maxPrice
        for (index in hardwareListFullSorted.indices) {
            if (hardwareListFullSorted[index].rrpPrice in minPrice..maxPrice) {
                filteredList.add(hardwareListFullSorted[index])
            }
        }

        listIsFiltered = true
        hardwareListUsed = filteredList
        notifyDataSetChanged()
    }

    fun resetFilter() {
        listIsSorted = false
        listIsFiltered = false
        hardwareListUsed = hardwareListFull
        hardwareListFullSorted = hardwareListFull
        notifyDataSetChanged()
    }


}