package dam95.android.uk.firstbyte.gui.components.compare

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CompareHardwareRecyclerList(
    private val context: Context?,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<CompareHardwareRecyclerList.ViewHolder>() {

    private var comparisonSelection = emptyList<String>()

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
        val displayChooseCompareHardwareBinding =


            return ViewHolder(
                parent
            )
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }
}