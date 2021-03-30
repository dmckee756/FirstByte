package dam95.android.uk.firstbyte.gui.components.compare

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.api.util.ConvertImageURL
import dam95.android.uk.firstbyte.databinding.DisplayComparedComponentBinding
import dam95.android.uk.firstbyte.model.components.Component
import java.util.*

class CompareHardwareRecyclerList(
    private val context: Context?,
    private val comparedType: String,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<CompareHardwareRecyclerList.ViewHolder>() {

    private var comparisonSelection = mutableListOf<Component?>()

    /**
     *
     */
    inner class ViewHolder(
        itemView: View,
        private val slotText: TextView,
        private val addComponentCompareBtn: Button,
        private val componentCompareImageOrAddIcon: ImageView,
        private val componentNameOrAddCompare: TextView,
        private val removeComparedComponent: Button
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        init {
            addComponentCompareBtn.setOnClickListener(this)
            removeComparedComponent.setOnClickListener(this)
        }

        /**
         *
         */
        fun bindDataSet(component: Component?) {

            component?.let {
                ConvertImageURL.convertURLtoImage(
                    component.imageLink,
                    componentCompareImageOrAddIcon
                )
                slotText.visibility = View.VISIBLE
                slotText.text = context!!.resources.getString(R.string.compareSlot, adapterPosition+1)
                componentNameOrAddCompare.text = component.name
                removeComparedComponent.visibility = View.VISIBLE
                componentCompareImageOrAddIcon.background = null
                addComponentCompareBtn.isClickable = false
            } ?: addComponentSetup()
        }

        private fun addComponentSetup() {
            slotText.visibility = View.GONE
            componentCompareImageOrAddIcon.setImageResource(R.drawable.ic_add)
            componentNameOrAddCompare.text = context!!.resources.getString(R.string.addComponentToCompare)
            removeComparedComponent.visibility = View.GONE
            addComponentCompareBtn.isClickable = true
        }

        /**
         *
         */
        override fun onClick(view: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                when (view?.id) {
                    this.addComponentCompareBtn.id -> listener.addComponent(comparedType)
                    this.removeComparedComponent.id -> comparisonSelection[adapterPosition]?.let {
                        listener.removeComponent(it, adapterPosition)
                    }
                }
            }
        }
    }

    /**
     *
     */
    interface OnItemClickListener {
        fun addComponent(componentType: String)
        fun removeComponent(component : Component, position: Int)
    }

    /**
     *
     */
    fun setDataList(selectionList: MutableList<Component?>) {
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
        val displayComparedComponentBinding =
            DisplayComparedComponentBinding.inflate(LayoutInflater.from(context), parent, false)


        return ViewHolder(
            displayComparedComponentBinding.hardwarePCComponentLayout,
            displayComparedComponentBinding.compareSlot,
            displayComparedComponentBinding.addComponentCompareBtn,
            displayComparedComponentBinding.componentCompareImageOrAddIcon,
            displayComparedComponentBinding.componentNameOrAddCompare,
            displayComparedComponentBinding.removeComparedComponent
        )
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataSet(comparisonSelection[position])
    }
}