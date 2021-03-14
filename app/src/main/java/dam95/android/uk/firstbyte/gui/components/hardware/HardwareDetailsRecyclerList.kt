package dam95.android.uk.firstbyte.gui.components.hardware

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dam95.android.uk.firstbyte.databinding.DisplayHardwareDetailsBinding

class HardwareDetailsRecyclerList(
    private val context: Context?
) : RecyclerView.Adapter<HardwareDetailsRecyclerList.ViewHolder>() {

    private var hardwareDetails = emptyList<String>()

    /**
     *
     */
    inner class ViewHolder(
        itemView: View,
        private val detailText: TextView
    ) : RecyclerView.ViewHolder(itemView) {

        /**
         *
         */
        fun bindDataSet(detail: String) {
        detailText.text = detail
        }
    }

    /**
     *
     */
    fun setDataList(newComponentDetails: List<String>) {
        hardwareDetails = newComponentDetails
        notifyDataSetChanged()
    }


    /**
     *
     */
    override fun getItemCount(): Int = hardwareDetails.size

    /**
     *
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val displayHardwareDetail =
            DisplayHardwareDetailsBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(
            displayHardwareDetail.detailLayout,
            displayHardwareDetail.textDetail
        )
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataSet(hardwareDetails[position])
    }
}