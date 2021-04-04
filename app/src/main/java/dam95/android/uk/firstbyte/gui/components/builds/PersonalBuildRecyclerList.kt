package dam95.android.uk.firstbyte.gui.components.builds

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.api.util.ConvertImageURL
import dam95.android.uk.firstbyte.databinding.DisplayPcDetailsBinding
import dam95.android.uk.firstbyte.gui.components.builds.util.PersonalBuildChecks
import dam95.android.uk.firstbyte.model.components.Component
import dam95.android.uk.firstbyte.model.util.HumanReadableUtils
import java.util.*

/**
 * @author David Mckee
 * @Version 1.0
 * Recycler list adapter used to load all components in current Personal PC Build.
 * The fan slots in a PC are dynamically added and removed depending on the fan slots in a PC's Case and Heatsink.
 * Supplies the user with information if the PC has incompatible parts, or requires parts to be added to the PC.
 */
class PersonalBuildRecyclerList(
    private val context: Context?,
    private val listener: OnItemListener
) : RecyclerView.Adapter<PersonalBuildRecyclerList.ViewHolder>() {

    private var pcDetails = mutableListOf<Pair<Component?, String>>()
    private var firstLoad: Boolean = true
    private var totalPrice: Double = 0.00
    private var isPCCompleted: Boolean = true
    private var recommendedPC: Boolean = false
    private val pcCheck = PersonalBuildChecks(pcDetails.size-1)

    /**
     * Bind each PC Part slot's information accordingly.
     * If the slot is occupied then display it's name, rrp price and an additional detail if applicable.
     * Allow the user to navigate to the hardware details of the PC Part when it's image is clicked.
     *
     * If the slot is empty (null), then inform the user a slot can be added to the PC. Some slots require a PC to be functional/complete.
     * If a slot's component is incompatible with another slot, inform the user.
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
            imageView.setOnClickListener(this)
        }

        /**
         *
         */
        fun bindDataSet(component: Component?, type: String) {
            //change to inform user what type of hardware they are adding
            title.text = type
            component?.let { part ->

                //Put the pc part's rrp price towards the pc's total price
                if (firstLoad) totalPrice += component.rrpPrice

                //Make pc part's name visible
                name.visibility = View.VISIBLE
                name.text = part.name

                //Make the pc part's price visible
                priceOrAddInfo.text = HumanReadableUtils.rrpPriceToCurrency(part.rrpPrice)
                //Display the additional detail if the pc part allows it.
                pcCheck.otherDetail(context!!, part, otherDetail)

                //Convert the PC Parts image URL into a viewable image.
                imageView.background = null
                ConvertImageURL.convertURLtoImage(
                    part.imageLink,
                    imageView
                )

                //Allow a user to see the component's details if they click on the image view.
                imageView.isClickable = true
                //If this is a recommended read only pc, then set up a read only format.
                if (recommendedPC){
                    readOnlySetup()
                } else {
                    //Otherwise...
                        //Don't allow adding component functionality to this slot
                    addButton.isClickable = false

                    //Remove the part require button.
                    partRequiredBtn.visibility = View.GONE
                    //Allow the user to remove the component
                    removeComponentBtn.visibility = View.VISIBLE
                    //Determine if the component is compatible with all other related components
                    //and display the incompatible icon if there is an incompatibility in the PC
                    notCompatibleBtn.visibility =
                        pcCheck.checkCompatibility(adapterPosition, pcDetails)
                }
            } ?: addHardwareSetup(type)

            //If there are no incompatible hardware, no missing required parts and the total price is under the user's budget then the computer is completed.
            //and price is under budget
            if ((partRequiredBtn.visibility == View.GONE && notCompatibleBtn.visibility == View.GONE) && isPCCompleted) {
                listener.pcCompleted(true)
                return
            }
            isPCCompleted = false
            if (adapterPosition == pcDetails.lastIndex) listener.pcCompleted(false)
        }

        /**
         * If the current slot is empty, then set up an empty slot that allows the user to add a component to the PC and this slot.
         * If a PC Part is required, this will display the button indicating to the user.
         */
        private fun addHardwareSetup(type: String) {
            //Allow functionality to add hardware and disable functionality to see a components hardware... because there is no component.
            addButton.isClickable = true
            imageView.isClickable = false
            //Assign the add component icon
            imageView.setImageResource(R.drawable.ic_add_component)

            //Hide unnecessary text views
            name.visibility = View.GONE
            otherDetail.visibility = View.GONE
            removeComponentBtn.visibility = View.GONE
            notCompatibleBtn.visibility = View.GONE

            //Determine if the current slot is required in the PC, display the icon if it is. E.g. GPU etc.
            partRequiredBtn.visibility =
                pcCheck.partRequired(adapterPosition, pcDetails)

            //Add instructions for user to add the component to PC.
            priceOrAddInfo.text = context?.getString(R.string.addPartToBuild, type)

            //If this is a recommended read only pc, then set up a read only format.
            if (isPCCompleted){
                readOnlySetup()
                priceOrAddInfo.text = context!!.resources.getString(R.string.saveToEditPCTest)
            }
        }

        /**
         * For Recommended builds, do not allow the user to remove or add components
         * and disable all compatibility and hardware requires buttons.
         * The Recommended PC's are all compatible and have all the required parts.
         * But no need to have them potentially showing up.
         */
        private fun readOnlySetup(){
            addButton.isClickable = false
            partRequiredBtn.visibility = View.GONE
            removeComponentBtn.visibility = View.GONE
            notCompatibleBtn.visibility = View.GONE
        }

        /**
         * Onclick listener actions that interact with the Personal Build class.
         * @param view the clicked on displayed details view
         */
        override fun onClick(view: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                when (view?.id) {
                    //Navigate to saved hardware list to add a component to the pc
                    addButton.id -> listener.onAddButtonClick(pcDetails[adapterPosition].second)
                    //Remove the component from the current pc
                    removeComponentBtn.id -> pcDetails[adapterPosition].first?.let {
                        //If the Component is Ram, Storage or a Fan, then get it's relative position.
                        val relativePosition: Int = pcCheck.getRelativePosition(
                            adapterPosition,
                            pcDetails[adapterPosition].first!!.type
                        )
                        listener.removePCPart(it, adapterPosition, relativePosition)
                    }
                    //Component has a compatibility issue toast.
                    notCompatibleBtn.id -> makeAToast("This component is not compatible with another.")
                    //Component required toast.
                    partRequiredBtn.id -> makeAToast("This component is required.")
                    //Navigate to component hardware details if clicked
                    imageView.id -> isImageClickable(imageView, adapterPosition)
                }
            }
        }
    }

    /**
     * Method interfaces that call back to the PersonalBuild fragment providing...
     * ...information or a command, depending on the selected event or update.
     */
    interface OnItemListener {
        fun onAddButtonClick(addCategory: String)
        fun removePCPart(component: Component, position: Int, relativePosition: Int)
        fun updateTotalPrice(totalPrice: Double)
        fun goToHardware(componentName: String, componentType: String)
        fun pcCompleted(isCompleted: Boolean)
    }

    /**
     * Assigns the all pc slots, either filled with a component or empty with a null,
     * to a list data set that will be used in this recycler list.
     * Also adds in a boolean value to inform this adapter if the loading PC is ReadOnly or not.
     */
    fun setDataList(updatedPCDetails: List<Pair<Component?, String>>, readOnlyPC: Boolean) {
        pcDetails = updatedPCDetails.toMutableList()
        recommendedPC = readOnlyPC
        notifyDataSetChanged()
    }

    /**
     * Return size of the PC data set.
     */
    override fun getItemCount(): Int = pcDetails.size

    /**
     * Initialize all of the views that is used for each component slot in a Personal PC Build.
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
     * Call the inner view holder class and bind all of the current components information to the displays views,
     * or bind all of the empty slot values to the displays views.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        pcDetails = pcCheck.doSlotsNeedMoved(pcDetails, position)
        holder.bindDataSet(pcDetails[position].first, pcDetails[position].second)
        if (position == pcDetails.lastIndex && firstLoad) {
            pcCheck.fanSlotEnd = pcDetails.size-1
            //Only update the price when this is loaded for the first time... but this doesn't apply...
            //...to reloads after adding hardware or price change when removing hardware)
            firstLoad = false
            listener.updateTotalPrice(totalPrice)
            listener.pcCompleted(isPCCompleted)
        }
    }

    /**
     * When a component is removed from the PC, free up the component's slot in the data set list used in this recycler list.
     * Remove the component's Rrp Price from the PC's total price and update it.
     * Notify that the data set changed.
     */
    fun removeDetail(position: Int, type: String) {
        pcDetails[position].first?.let {
            //Update the total price of the PC
            totalPrice -= it.rrpPrice
            listener.updateTotalPrice(totalPrice)
            Log.i("BEFORE_REPLACE", it.name)
        }
        //Free up the occupied slot
        pcDetails[position] = Pair(null, type.capitalize(Locale.ROOT))
        Log.i("PC_LIST_POS", "$position")
        //Notify the adapter that the data set changed.
        notifyItemChanged(position)
        notifyDataSetChanged()
    }

    /**
     * When a heatsink or case is removed from the PC, trim off the excess
     * fan slots that are not available to the PC and update the PC Price if any fans were trimmed.
     */
    fun removeFans(numberOfFans: Int) {
        val size = (itemCount - 1)
        val trimmedSize = (size - numberOfFans) + 1
        //Trim away the excess fan slots from this data set list.
        for (i in size downTo trimmedSize) {
            pcDetails[i].first?.let {
                totalPrice -= it.rrpPrice
            }
            pcDetails.removeLast()
        }
        //Notify the adapter that the data set changed.
        notifyDataSetChanged()
        //Assign the new position of the last fan slot in this pc build.
        pcCheck.fanSlotEnd = pcDetails.size-1
        //Update the total price of the PC
        listener.updateTotalPrice(totalPrice)
    }

    /**
     * If the incompatible or part required buttons were clicked, then display a toast message to the user.
     * @param displayedTest The text informing the user what the icon means (Part Incompatible, or Part Required)
     */
    private fun makeAToast(displayedTest: String) {
        Toast.makeText(
            context,
            displayedTest,
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * If the imageView (component image) is clickable,
     * then navigate to that's component's hardware details screen
     * @param imageView the component's image
     * @param adapterPosition the current position in the recycler list
     */
    private fun isImageClickable(imageView: ImageView, adapterPosition: Int) {
        if (imageView.isClickable) {
            //if the component is not null, then navigate to hardware details.
            pcDetails[adapterPosition].first?.let {
                listener.goToHardware(it.name, it.type)
            }
        }
    }
}