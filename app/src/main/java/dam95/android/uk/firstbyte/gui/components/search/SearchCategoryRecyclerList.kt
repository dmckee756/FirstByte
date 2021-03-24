package dam95.android.uk.firstbyte.gui.components.search

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.DisplaySearchBinding
import dam95.android.uk.firstbyte.model.components.*
import dam95.android.uk.firstbyte.model.util.ComponentsEnum
import java.util.*
import kotlin.collections.ArrayList

/**
 *
 */
class SearchCategoryRecyclerList(
    private val context: Context?,
    private val listener: OnItemClickListener,
    private val categories: ArrayList<Pair<String, String>>,
    private val online: Boolean
) : RecyclerView.Adapter<SearchCategoryRecyclerList.ViewHolder>() {

    private lateinit var displaySearchBinding: DisplaySearchBinding

    /**
     *
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val categoryBtn: Button = displaySearchBinding.hardwareListFragmentID

        init {
            categoryBtn.setOnClickListener(this)
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
    override fun getItemCount(): Int = categories.size

    /**
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        displaySearchBinding =
            DisplaySearchBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(
            displaySearchBinding.categoryCard
        )
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        displaySearchBinding.categoryImage.background =
            loadCorrectImage(categories[position].second)
        displaySearchBinding.categorySearchTxt.text = categories[position].first

        //Assign the correct colour to the actual first card of the recycler list, because it looks nice.
        if (displaySearchBinding.categorySearchTxt.text == categories[0].first) {
            if (online) {
                displaySearchBinding.hardwareListFragmentID.setBackgroundResource(R.drawable.object_on_search_all)
            } else {
                displaySearchBinding.hardwareListFragmentID.setBackgroundResource(R.drawable.object_saved_search_all)
            }
        }

    }

    /**
     *
     */
    private fun loadCorrectImage(category: String): Drawable? {
        val drawableID: Int = when (category.toUpperCase(Locale.ROOT)) {
            ComponentsEnum.GPU.toString() -> R.drawable.img_gpu_search
            ComponentsEnum.CPU.toString() -> R.drawable.img_cpu_search
            ComponentsEnum.RAM.toString() -> R.drawable.img_ram_search
            ComponentsEnum.PSU.toString() -> R.drawable.img_psu_search
            ComponentsEnum.STORAGE.toString() -> R.drawable.img_storage_search
            ComponentsEnum.MOTHERBOARD.toString() -> R.drawable.img_motherboard_search
            ComponentsEnum.CASES.toString() -> R.drawable.img_case_search
            ComponentsEnum.HEATSINK.toString() -> R.drawable.img_heatsink_search
            ComponentsEnum.MOTHERBOARD.toString() -> R.drawable.img_fan_search
            else -> R.drawable.img_search_all
        }
        return context?.let { AppCompatResources.getDrawable(it, drawableID) }
    }
}