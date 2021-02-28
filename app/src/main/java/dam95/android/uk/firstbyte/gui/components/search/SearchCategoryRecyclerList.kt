package dam95.android.uk.firstbyte.gui.components.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.DisplaySearchBinding

/**
 *
 */
class SearchCategoryRecyclerList(
    private val context: Context?,
    private val listener: OnItemClickListener,
    private val categories: ArrayList<Pair<String, String>>
) : RecyclerView.Adapter<SearchCategoryRecyclerList.ViewHolder>() {

    private lateinit var displaySearchBinding: DisplaySearchBinding
    private var firstCard = true

    /**
     *
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val categoryBtn: Button = displaySearchBinding.categoryBtn

            init {
                if (firstCard) {
                    categoryBtn.setBackgroundResource(R.drawable.object_on_search_all)
                    firstCard = false
                }
                categoryBtn.setOnClickListener(this)
            }

        /**
         *
         */
        override fun onClick(category: View?) {

            if (adapterPosition != RecyclerView.NO_POSITION){
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
    override fun getItemCount(): Int = categories.size

    /**
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        displaySearchBinding =
            DisplaySearchBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(
            displaySearchBinding.categoryCard,
        )
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        displaySearchBinding.categorySearchTxt.text = categories[position].first
        //implement image
    }
}