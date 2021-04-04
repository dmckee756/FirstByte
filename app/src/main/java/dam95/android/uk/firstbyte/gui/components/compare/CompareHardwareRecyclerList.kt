package dam95.android.uk.firstbyte.gui.components.compare

import android.content.Context
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

/**
 * @author David Mckee
 * @Version 1.0
 * Recycler list adapter that shows 5 component slots the user can utilise to compare components of the same type against another.
 * The list adapter takes nulls and turns them into empty slots, allowing users to place components into the spots.
 * Used to correctly identify components with the bar charts Legend.
 */
class CompareHardwareRecyclerList(
    private val context: Context?,
    private val comparedType: String,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<CompareHardwareRecyclerList.ViewHolder>() {

    private var comparisonSelection = mutableListOf<Component?>()

    /**
     * Bind each part of the compare component slot with the information to correctly identify the component when it's value is on the bar chart.
     * Allow the user to add and remove components and have each slot display the correct information depending if the slot is occupied or not.
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
         * If the current slot has a component, then set up the slot with the details needed to identify the component
         * and allow the user to remove it from the slot. If the slot is null, then instead call "addComponentSetup()" below.
         */
        fun bindDataSet(component: Component?) {
            //Get rid of the add icon for the image view and replace it with the components image.
            componentCompareImageOrAddIcon.background = null
            component?.let {
                ConvertImageURL.convertURLtoImage(
                    component.imageLink,
                    componentCompareImageOrAddIcon
                )
                //Allow the user to remove the component from being compared.
                removeComparedComponent.visibility = View.VISIBLE
                //Text indicating slot name, lines up with the bar char's legend.
                slotText.visibility = View.VISIBLE
                slotText.text = context!!.resources.getString(R.string.compareSlot, adapterPosition+1)
                //Display the components name.
                componentNameOrAddCompare.text = component.name
                //Take away the functionality to add a component to this slot, as it's already occupied.
                addComponentCompareBtn.isClickable = false
            } ?: addComponentSetup()
        }

        /**
         * If the current slot is null, the set up the slot informing the user they can add a component
         * that will be compared and allow the button to execute this functionality.
         */
        private fun addComponentSetup() {
            //Hide unnecessary details for an empty slot.
            slotText.visibility = View.GONE
            removeComparedComponent.visibility = View.GONE
            //Assign the add component Icon to the image view.
            componentCompareImageOrAddIcon.setImageResource(R.drawable.ic_add_component)
            //Add instruction and functionality to add a component.
            componentNameOrAddCompare.text = context!!.resources.getString(R.string.addComponentToCompare)
            addComponentCompareBtn.isClickable = true
        }

        /**
         * Send onClick event to the CompareHardware fragment.
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
     * Methods that call back to the CompareHardware fragment.
     */
    interface OnItemClickListener {
        fun addComponent(componentType: String)
        fun removeComponent(component : Component, position: Int)
    }

    /**
     * Assigns the compared components data set that will be used in this recycler list.
     * Can include nulls for "empty" slots.
     */
    fun setDataList(selectionList: MutableList<Component?>) {
        comparisonSelection = selectionList
        notifyDataSetChanged()
    }

    /**
     * Return size of the data set.
     */
    override fun getItemCount(): Int = comparisonSelection.size

    /**
     * Initialize the layout/views that will display the compared component, it's text and image.
     * Or display an empty slot that informs the user they can add a component to compare.
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
     * Call the inner view holder class and bind the current compared component's name and image, or set up a blank slot that can be added to.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataSet(comparisonSelection[position])
    }
}