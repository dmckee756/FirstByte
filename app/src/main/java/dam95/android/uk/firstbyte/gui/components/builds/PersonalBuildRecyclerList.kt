package dam95.android.uk.firstbyte.gui.components.builds

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dam95.android.uk.firstbyte.api.util.ConvertImageURL
import dam95.android.uk.firstbyte.databinding.DisplayPcDetailsBinding
import dam95.android.uk.firstbyte.datasource.ComponentDBAccess
import dam95.android.uk.firstbyte.gui.components.builds.util.PersonalBuildChecks
import dam95.android.uk.firstbyte.model.components.Component
import dam95.android.uk.firstbyte.model.util.HumanReadableUtils
import java.util.*

class PersonalBuildRecyclerList(
    private val context: Context?,
    private val fbHardwareDb: ComponentDBAccess,
    private val listener: OnItemListener,
) : RecyclerView.Adapter<PersonalBuildRecyclerList.ViewHolder>() {

    private var pcDetails = emptyList<Pair<Component?, String>>()
    private var totalPrice: Double = 0.00

    /**
     *
     */
    inner class ViewHolder(
        itemView: View,
        private val title: TextView,
        private val addButton: Button,
        private val name: TextView,
        private val priceOrAddInfo: TextView,
        private val otherDetail: TextView,
        private val imageView: ImageView,
        private val removeComponentBtn: Button,
        private val notCompatibleBtn: Button,
        private val partRequiredBtn: Button
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        init {
            addButton.setOnClickListener(this)
            removeComponentBtn.setOnClickListener(this)
            notCompatibleBtn.setOnClickListener(this)
            partRequiredBtn.setOnClickListener(this)
        }

        /**
         *
         */
        fun bindDataSet(component: Component?, type: String) {
            //change to inform user what type of hardware they are adding
            title.text = type
            component?.let { part ->
                totalPrice += component.rrpPrice
                name.visibility = View.VISIBLE
                name.text = part.name

                priceOrAddInfo.text = HumanReadableUtils.rrpPriceToCurrency(part.rrpPrice)
                otherDetail.text = PersonalBuildChecks.otherDetail(part)

                ConvertImageURL.convertURLtoImage(
                    part.imageLink,
                    imageView
                )

                partRequiredBtn.visibility = View.GONE
                removeComponentBtn.visibility = View.VISIBLE
                notCompatibleBtn.visibility = PersonalBuildChecks.checkCompatibility(part)
            } ?: addHardwareSetup()
        }

        /**
         *
         */
        private fun addHardwareSetup() {

        }

        /**
         *
         */
        override fun onClick(view: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                when (view?.id) {
                    //
                    addButton.id -> listener.onAddButtonClick(pcDetails[adapterPosition].second)
                    //
                    removeComponentBtn.id -> listener.removePCPart(
                        pcDetails[adapterPosition].second.toLowerCase(
                            Locale.ROOT
                        ), adapterPosition
                    )
                }
            }
        }
    }

    /**
     *
     */
    interface OnItemListener {
        fun onAddButtonClick(addCategory: String)
        fun removePCPart(category: String, position: Int)
        fun updateTotalPrice(totalPrice: Double)
    }

    /**
     *
     */
    fun setDataList(updatedPCDetails: List<Pair<Component?, String>>) {
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
        val displayPcDetailsBinding =
            DisplayPcDetailsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(
            displayPcDetailsBinding.hardwarePCComponentLayout,
            displayPcDetailsBinding.componentType,
            displayPcDetailsBinding.componentDetailBtn,
            displayPcDetailsBinding.componentName,
            displayPcDetailsBinding.componentPriceOrAddPart,
            displayPcDetailsBinding.componentOtherDetail,
            displayPcDetailsBinding.componentImageOrAddIcon,
            displayPcDetailsBinding.removeFromPC,
            displayPcDetailsBinding.componentCompatible,
            displayPcDetailsBinding.requiredPCpart
        )
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataSet(pcDetails[position].first, pcDetails[position].second)
        if (position == pcDetails.lastIndex) listener.updateTotalPrice(totalPrice)
    }
}