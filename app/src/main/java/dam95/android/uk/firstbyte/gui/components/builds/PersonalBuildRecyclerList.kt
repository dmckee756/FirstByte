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
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.api.util.ConvertImageURL
import dam95.android.uk.firstbyte.databinding.DisplayPcDetailsBinding
import dam95.android.uk.firstbyte.gui.components.builds.util.PersonalBuildChecks
import dam95.android.uk.firstbyte.model.components.Component
import dam95.android.uk.firstbyte.model.util.HumanReadableUtils
import java.util.*

class PersonalBuildRecyclerList(
    private val context: Context?,
    private val listener: OnItemListener
) : RecyclerView.Adapter<PersonalBuildRecyclerList.ViewHolder>() {

    private var pcDetails = mutableListOf<Pair<Component?, String>>()
    private var firstLoad: Boolean = true
    private var totalPrice: Double = 0.00
    private var isPCCompleted: Boolean = true
    private val pcCheck = PersonalBuildChecks(pcDetails.size-1, this)

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

                if (firstLoad) totalPrice += component.rrpPrice

                name.visibility = View.VISIBLE
                name.text = part.name

                priceOrAddInfo.text = HumanReadableUtils.rrpPriceToCurrency(part.rrpPrice)
                pcCheck.otherDetail(context!!, part, otherDetail)

                imageView.background = null
                ConvertImageURL.convertURLtoImage(
                    part.imageLink,
                    imageView
                )
                addButton.isClickable = false
                imageView.isClickable = true

                partRequiredBtn.visibility = View.GONE
                removeComponentBtn.visibility = View.VISIBLE
                notCompatibleBtn.visibility =
                    pcCheck.checkCompatibility(adapterPosition, pcDetails)

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
         *
         */
        private fun addHardwareSetup(type: String) {
            addButton.isClickable = true
            imageView.isClickable = false
            imageView.setImageResource(R.drawable.ic_add)

            name.visibility = View.GONE

            otherDetail.visibility = View.GONE
            partRequiredBtn.visibility =
                pcCheck.partRequired(adapterPosition, pcDetails)
            removeComponentBtn.visibility = View.GONE
            notCompatibleBtn.visibility = View.GONE
            priceOrAddInfo.text = context?.getString(R.string.addPartToBuild, type)
        }

        /**
         * Onclick listener actions that interact with the Personal Build class.
         * This method determines how the user interacts with the pc build screen.
         * @param view the clicked on displayed details view
         */
        override fun onClick(view: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                when (view?.id) {
                    //Navigate to saved hardware list to add a component to the pc
                    addButton.id -> listener.onAddButtonClick(pcDetails[adapterPosition].second)
                    //Remove the component from the current pc
                    removeComponentBtn.id -> pcDetails[adapterPosition].first?.let {
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
     *
     */
    fun setDataList(updatedPCDetails: List<Pair<Component?, String>>) {
        pcDetails = updatedPCDetails.toMutableList()
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
     *
     */
    fun removeDetail(position: Int, type: String) {
        pcDetails[position].first?.let {
            totalPrice -= it.rrpPrice
            listener.updateTotalPrice(totalPrice)
            Log.i("BEFORE_REPLACE", it.name)
        }
        pcDetails[position] = Pair(null, type.capitalize(Locale.ROOT))
        Log.i("PC_LIST_POS", "$position")
        notifyItemChanged(position)
        notifyDataSetChanged()
    }

    /**
     *
     */
    fun removeFans(numberOfFans: Int) {
        val size = (itemCount - 1)
        val trimmedSize = (size - numberOfFans) + 1
        for (i in size downTo trimmedSize) {
            pcDetails[i].first?.let {
                totalPrice -= it.rrpPrice
            }
            pcDetails.removeLast()
        }
        notifyDataSetChanged()
        pcCheck.fanSlotEnd = pcDetails.size-1
        listener.updateTotalPrice(totalPrice)
    }

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