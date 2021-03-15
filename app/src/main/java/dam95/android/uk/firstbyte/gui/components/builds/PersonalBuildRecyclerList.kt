package dam95.android.uk.firstbyte.gui.components.builds

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dam95.android.uk.firstbyte.databinding.DisplayPcDetailsBinding
import dam95.android.uk.firstbyte.datasource.ComponentDBAccess
import dam95.android.uk.firstbyte.model.components.Component
import dam95.android.uk.firstbyte.model.pcbuilds.PCBuild

class PersonalBuildRecyclerList (
    private val context: Context?,
    private val fb_Hardware_DB: ComponentDBAccess,
    private val listener: OnItemClickListener,
) : RecyclerView.Adapter<PersonalBuildRecyclerList.ViewHolder>() {

    private var pcDetails = emptyList<Component?>()

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
        fun bindDataSet(pcBuild: PCBuild?) {

        }

        /**
         *
         */
        override fun onClick(view: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                when (view?.id) {

                }
            }
        }
    }

    /**
     *
     */
    interface OnItemClickListener {
        fun onButtonClick()
    }

    /**
     *
     */
    fun setDataList(updatedPCDetails: List<Component?>) {
        pcDetails = updatedPCDetails
        notifyDataSetChanged()
    }

    /**
     *
     */
    override fun getItemCount(): Int = pcDetails.size

    /**
     *
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val displayPcDetailsBinding = DisplayPcDetailsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(
            displayPcDetailsBinding.hardwarePCComponentLayout
        )
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }
}