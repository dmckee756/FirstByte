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
 *
 */
class SearchCategoryRecyclerList(
    private val context: Context?,
    private val listener: OnItemClickListener,
    private val online: Boolean
) : RecyclerView.Adapter<SearchCategoryRecyclerList.ViewHolder>() {

    private var categories: List<Pair<String, String>> = listOf()

    /**
     *
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
         *
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
     *
     */
    interface OnItemClickListener {
        fun onButtonClick(chosenCategory: String)
    }

    /**
     *
     */
    fun setDataList(categoryList: List<Pair<String, String>>) {
        categories = categoryList
        notifyDataSetChanged()
    }

    /**
     *
     */
    override fun getItemCount(): Int = categories.size

    /**
     *
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
     *
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bindDataSet(categories[position])
    }
}