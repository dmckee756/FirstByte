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
 * @author David Mckee
 * @Version 1.0
 * Recycler list adapter that displays each component display details the user can choose from.
 * When the user clicked on one of the components, it can display the user the components specifications, or add the component to
 * a PC Build or a compared list.
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
     * Bind each view with the details of each retrieved Component SearchedHardwareItem list.
     * With the correct details and it's images.
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
         * Bind each component display details, component name, rrp price and image, to this view.
         * Blue Underline for online search.
         * Green Underline for app database search.
         */
        fun bindDataSet(displayedComponent: SearchedHardwareItem) {
            //If the user is searching from the API, apply a blue line at the bottom of this view.
            //If the user is searching from the App's database, apply a green line at the bottom of this view.
            //These colors represent the loading method.
            if (isLoadingFromServer) {
                hardwareBtn.setBackgroundResource(R.drawable.object_online_display)
            } else {
                hardwareBtn.setBackgroundResource(R.drawable.object_offline_display)
            }

            //Assign the displayed component information to the current view.
            ConvertImageURL.convertURLtoImage(
                displayedComponent.image_link,
                componentImage
            )
            nameText.text = displayedComponent.name
            priceText.text = HumanReadableUtils.rrpPriceToCurrency(displayedComponent.rrpPrice)
        }

        /**
         * Send onClick event to the HardwareList fragment.
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
     * Methods that call back to the HardwareList fragment.
     */
    interface OnItemClickListener {
        fun onHardwareClick(componentName: String, componentType: String)
    }

    /**
     * Return size of the data set.
     */
    override fun getItemCount(): Int = hardwareListUsed.size

    /**
     * Retrieves the Current sorted full list of components,
     * utilised when the user is searching through the list with search view queries.
     */
    fun getFullSortedList(): List<SearchedHardwareItem> = hardwareListFullSorted

    /**
     * Assigns the data set that will be used in this recycler list.
     * HardwareListFull is the original, in case the user resets all filters.
     * HardwareListFullSorted is a full version of the list, but with sorting applied.
     * HardwareListUsed is a version of the list that may have sorting and/or filtering applied.
     */
    fun setDataList(newList: List<SearchedHardwareItem>) {
        hardwareListFull = newList
        hardwareListFullSorted = newList
        hardwareListUsed = newList
        notifyDataSetChanged()
    }

    /**
     * Updates the HardwareListUsed whenever sorting, searching and/or filtering has been applied to the fragment/list.
     */
    fun setUsedDataList(newList: List<SearchedHardwareItem>) {
        hardwareListUsed = newList
        notifyDataSetChanged()
    }

    /**
     * Initialize the layout/views that will display the component display item (SearchedHardwareItem).
     * The Component's Name, RrpPrice and Image.
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
     * Call the inner view holder class and bind each component display details to the current recycler list display item.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataSet(hardwareListUsed[position])
    }

    /**
     * Sorts the currently used displayed component list in of these orders:
     * Alphabetical Ascending order,
     * Alphabetical Descending order,
     * Price Tag Low-High order,
     * Price Tag High-Low order.
     */
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

    /**
     * Only displays components in the recycler list that are between the users selected price filters.
     */
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

    /**
     * Resets all sorting and filters that are currently applied to this recycler list.
     */
    fun resetFilter() {
        listIsSorted = false
        listIsFiltered = false
        hardwareListUsed = hardwareListFull
        hardwareListFullSorted = hardwareListFull
        notifyDataSetChanged()
    }
}