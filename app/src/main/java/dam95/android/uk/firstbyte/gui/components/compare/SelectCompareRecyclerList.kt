package dam95.android.uk.firstbyte.gui.components.compare

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.*
import dam95.android.uk.firstbyte.model.util.ComponentsEnum
import java.io.IOException
import java.util.*

/**
 * @author David Mckee
 * @Version 1.0
 * Recycler list adapter sets up the display that allows the user to compare components of the same type of category.
 * Display the image, what values an be compared and a button to allow navigation to HardwareCompare.
 */
class SelectCompareRecyclerList(
    private val context: Context?,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<SelectCompareRecyclerList.ViewHolder>() {

    private var comparisonSelection = emptyList<String>()
    private var tierPosition: Int = 1

    /**
     * Bind each part of the compared category selection to each display item of this recycler list.
     */
    inner class ViewHolder(
        itemView: View,
        private val compareTitle: TextView,
        private val compareDescription: TextView,
        private val imageView: ImageView,
        private val goCompareBtn: Button
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        init {
            goCompareBtn.setOnClickListener(this)
        }

        /**
         * Loads the current category that is being created and applies the correct information
         * of what component category is being compared and what values can be compared.
         */
        @Throws(IOException::class)
        fun bindDataSet(componentType: String) {
            //Dynamically load the hardware images in drawable from assets folder.
            try {
                val inputStream =
                    context!!.assets.open("compare_${componentType.toLowerCase(Locale.ROOT)}.png")
                //Convert loaded image into drawable...
                val image = Drawable.createFromStream(inputStream, null)
                //Assign the drawable to image view if it exists
                image?.let { imageView.setImageDrawable(it) }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            compareTitle.text =
                context!!.resources?.getString(R.string.hardwareCompareTitle, componentType)
            goCompareBtn.text =
                context.resources?.getString(R.string.compareCategory, componentType)

            //Set the description of what values can be compared depending on the current category.
            when (componentType) {
                ComponentsEnum.GPU.toString() -> {
                    compareDescription.text =
                        context.resources.getString(R.string.gpuCompareDetails)
                }
                ComponentsEnum.CPU.toString() -> {
                    compareDescription.text =
                        context.resources.getString(R.string.cpuCompareDetails)
                }
                ComponentsEnum.RAM.toString() -> {
                    compareDescription.text =
                        context.resources.getString(R.string.ramCompareDetails)
                    //Take of the 's off of "COMPARE RAM'S", because it looks weird.
                    goCompareBtn.text = goCompareBtn.text.dropLast(2)

                }
            }
        }

        /**
         * Send onClick event to the SelectCompare fragment.
         */
        override fun onClick(view: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                when (view?.id) {
                    this.goCompareBtn.id -> listener.onCompareBtnClick(
                        comparisonSelection[adapterPosition].toLowerCase(
                            Locale.ROOT
                        )
                    )
                }
            }
        }

    }

    /**
     * Methods that call back to the SelectCompare fragment.
     */
    interface OnItemClickListener {
        fun onCompareBtnClick(chosenCategory: String)
    }

    /**
     * Assigns the 3 category compare options for this recycler list adapter to display.
     */
    fun setDataList(selectionList: List<String>) {
        comparisonSelection = selectionList
        notifyDataSetChanged()
    }

    /**
     * Return size of the data set.
     */
    override fun getItemCount(): Int = comparisonSelection.size

    /**
     * Initialize the layout/views that will display the compared category selection.
     * Every odd numbered display will have it's image to the left, and description to the right.
     * Every even numbered display will have it's image to the right, and description to the left.
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val cardView: View
        val compareTitle: TextView
        val compareDescription: TextView
        val compareImage: ImageView
        val goCompareButton: Button

        if (tierPosition % 2 == 1) {
            // If the current displayed item position is odd
            // then load the left layout display.
            val selectCompareLeftBinding = DisplayChooseCompareLeftBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            cardView = selectCompareLeftBinding.compareSelectionLayoutLeft
            compareTitle = selectCompareLeftBinding.hardwareToCompareLeft
            compareDescription = selectCompareLeftBinding.compareDescriptionLeft
            compareImage = selectCompareLeftBinding.componentCompareImageLeft
            goCompareButton = selectCompareLeftBinding.enterComparisonBtnLeft
        } else {
            // If the current displayed item position is even
            // then load the right layout display.
            val selectCompareRightBinding = DisplayChooseCompareRightBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            cardView = selectCompareRightBinding.compareSelectionLayoutRight
            compareTitle = selectCompareRightBinding.hardwareToCompareRight
            compareDescription = selectCompareRightBinding.compareDescriptionRight
            compareImage = selectCompareRightBinding.componentCompareImageRight
            goCompareButton = selectCompareRightBinding.enterComparisonBtnRight
        }

        return ViewHolder(
            cardView,
            compareTitle,
            compareDescription,
            compareImage,
            goCompareButton
        )
    }

    /**
     * Call the inner view holder class and bind the current compared categories image, details and navigation button.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataSet(comparisonSelection[position])
        tierPosition++
        if (position == comparisonSelection.size) tierPosition = 1
    }
}