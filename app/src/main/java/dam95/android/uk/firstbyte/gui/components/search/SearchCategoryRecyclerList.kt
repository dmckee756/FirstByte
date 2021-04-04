package dam95.android.uk.firstbyte.gui.components.search

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.DisplaySearchBinding
import dam95.android.uk.firstbyte.model.components.*
import dam95.android.uk.firstbyte.model.util.ComponentsEnum
import java.util.*

/**
 * @author David Mckee
 * @Version 1.0
 * Recycler list adapter that displays each component category the user can choose from.
 * When the user clicked on one of the categories, in then loads into HardwareList with only components of the
 * selected category being shown.
 */
class SearchCategoryRecyclerList(
    private val context: Context?,
    private val listener: OnItemClickListener,
    private val online: Boolean
) : RecyclerView.Adapter<SearchCategoryRecyclerList.ViewHolder>() {

    private var categories: List<Pair<String, String>> = listOf()

    /**
     * Bind each part of the category view with the correct details, images and allow the user to navigate to
     * HardwareList fragment with the correctly selected category and information to determine if they want to find saved components
     * from the app's database, or the online API.
     */
    inner class ViewHolder(
        itemView: View,
        private val categoryBtn: Button,
        private val searchText: TextView,
        private val imageCategory: ImageView
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        init {
            categoryBtn.setOnClickListener(this)
        }

        /**
         * Bind each category view with the correct human readable text and images.
         * If it is the first view in this recycler list, apply a style to make it look nice.
         * Blue for online search.
         * Green for app database search.
         */
        fun bindDataSet(category: Pair<String, String>) {
            //Dynamically load the category images in drawable from assets folder.
            try {
                val inputStream =
                    context!!.assets.open("img_${category.second.toLowerCase(Locale.ROOT)}_search.png")
                //Convert loaded image into drawable...
                val image = Drawable.createFromStream(inputStream, null)
                //Assign the drawable to image view if it exists
                image?.let { imageCategory.setImageDrawable(it) }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            //Assign human readable text of the current category.
            searchText.text = categories[adapterPosition].first
            //Assign the correct colour to the actual first card of the recycler list, because it looks nice.
            if ( searchText.text == categories[0].first) {
                if (online) {
                    categoryBtn.setBackgroundResource(R.drawable.object_on_search_all)
                } else {
                    categoryBtn.setBackgroundResource(R.drawable.object_saved_search_all)
                }
            }
        }

        /**
         * Send onClick event to the SearchCategory fragment that is currently utilising this adapter.
         */
        override fun onClick(category: View?) {

            if (adapterPosition != RecyclerView.NO_POSITION) {
                when (category?.id) {
                    categoryBtn.id -> listener.onButtonClick(categories[adapterPosition].second)
                }
            }

        }

    }

    /**
     * Methods that call back to the SearchCategory fragment that is currently utilising this adapter.
     */
    interface OnItemClickListener {
        fun onButtonClick(chosenCategory: String)
    }

    /**
     * Assigns the data set that will be used in this recycler list.
     */
    fun setDataList(categoryList: List<Pair<String, String>>) {
        categories = categoryList
        notifyDataSetChanged()
    }

    /**
     * Return size of the data set.
     */
    override fun getItemCount(): Int = categories.size

    /**
     * Initialize the layout/views that will display the category, it's text and image.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val displaySearchBinding =
            DisplaySearchBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(
            displaySearchBinding.categoryCard,
            displaySearchBinding.hardwareListFragmentID,
            displaySearchBinding.categorySearchTxt,
            displaySearchBinding.categoryImage
        )
    }

    /**
     * Call the inner view holder class and bind category selection details to the current recycler list display item.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bindDataSet(categories[position])
    }
}