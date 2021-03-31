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

class SelectCompareRecyclerList(
    private val context: Context?,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<SelectCompareRecyclerList.ViewHolder>() {

    private var comparisonSelection = emptyList<String>()
    private var tierPosition: Int = 1

    /**
     *
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
         *
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

            when (componentType) {
                ComponentsEnum.GPU.toString() -> {
                    compareDescription.text = context.resources.getString(R.string.gpuCompareDetails)
                }
                ComponentsEnum.CPU.toString() -> {
                    compareDescription.text = context.resources.getString(R.string.cpuCompareDetails)
                }
                ComponentsEnum.RAM.toString() -> {
                    compareDescription.text = context.resources.getString(R.string.ramCompareDetails)
                    //Take of the 's off of "COMPARE RAM'S", because it looks weird.
                    goCompareBtn.text = goCompareBtn.text.dropLast(2)

                }
            }
        }

        /**
         *
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
     *
     */
    interface OnItemClickListener {
        fun onCompareBtnClick(chosenCategory: String)
    }

    /**
     *
     */
    fun setDataList(selectionList: List<String>) {
        comparisonSelection = selectionList
        notifyDataSetChanged()
    }

    /**
     *
     */
    override fun getItemCount(): Int = comparisonSelection.size

    /**
     *
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataSet(comparisonSelection[position])
        tierPosition++
        if (position == comparisonSelection.size) tierPosition = 1
    }
}