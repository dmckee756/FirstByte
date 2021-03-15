package dam95.android.uk.firstbyte.gui.components.compare

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dam95.android.uk.firstbyte.databinding.DisplayChooseCompareHardwareBinding

class SelectCompareRecyclerList (
    private val context: Context?,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<SelectCompareRecyclerList.ViewHolder>() {

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
        fun onCompareBtnClick()
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
            DisplayChooseCompareHardwareBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(
            displayChooseCompareHardwareBinding.compareSelectionLayout
        )
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }
}