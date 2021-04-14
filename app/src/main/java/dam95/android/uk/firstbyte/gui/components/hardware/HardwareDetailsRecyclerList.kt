package dam95.android.uk.firstbyte.gui.components.hardware

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.DisplayHardwareDetailsBinding

/**
 * @author David Mckee
 * @Version 1.0
 * This recycler adapter allows a dynamic approach to displaying all component specifications/details
 * of any type of component that exists in this project.
 * Each specification is converted to a human readable format and allows the user to navigate to a components
 * Amazon or Scan.co.uk Web Page, if there is a URL assigned to the component from these websites.
 */
class HardwareDetailsRecyclerList(
    private val context: Context?,
    private val listener: OnItemListener
) : RecyclerView.Adapter<HardwareDetailsRecyclerList.ViewHolder>() {

    private var hardwareDetails = emptyList<Pair<String, String?>>()

    /**
     * Bind each component specification information in a dynamic approach, correctly assigning the icons depending
     * if the specification has expanded details to describe what it means.
     */
    inner class ViewHolder(
        itemView: View,
        private val parent: ViewGroup,
        private val clickableButton: Button,
        private val detailText: TextView,
        private val specDescription: TextView
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        /**
         * Assign the correct human readable specification to the text view and assign the correct bullet point icon.
         */
        fun bindDataSet(detail: Pair<String, String?>) {
            detailText.text = detail.first
            //If applicable, apply the specification's description.
            detail.second?.let { description -> specDescription.text = description }
            correctIcon(detail)
        }

        /**
         * Determine the correct icon to load depending on what detail is being displayed.
         * Basket for Web Link navigation.
         * Bullet Points for non expandable views (When detail's second pair value is null).
         */
        private fun correctIcon(detail: Pair<String, String?>) {
            return when (true) {
                (detail.first.indexOf("Amazon Price") != -1) -> webLink()
                (detail.first.indexOf("Scan.co.uk Price") != -1) -> webLink()
                else -> {
                    if (detail.second != null) {
                        allowDetailDisplay()
                    } else {
                        bulletPoint()
                    }
                }
            }
        }

        /**
         * Assign the WebLink icons to the correct component details and set up the button click listener that takes
         * the user to the Web Client.
         */
        private fun webLink() {
            clickableButton.setBackgroundResource(R.drawable.ic_baseline_shopping_basket_24)
            // 50 Width, 40 Height
            val pixelsToDp = context?.resources?.displayMetrics?.density
            val layoutParams = ConstraintLayout.LayoutParams(
                (60 * pixelsToDp!!).toInt(),
                (55 * pixelsToDp).toInt()
            )
            clickableButton.layoutParams = layoutParams
            clickableButton.setOnClickListener(this)
        }

        /**
         * Setup the blank bullet point and make sure that the button does nothing when clicked.
         */
        private fun bulletPoint() {
            clickableButton.setBackgroundResource(R.drawable.ic_blank_bulletpoint)
            clickableButton.isClickable = false
        }

        /**
         * Set up the expandable view bullet points, where it will expand and collapse further details about
         * the component's specification.
         */
        private fun allowDetailDisplay() {
            clickableButton.setOnClickListener {
                if (specDescription.visibility == View.GONE) {
                    specDescription.visibility = View.VISIBLE
                    clickableButton.setBackgroundResource(R.drawable.ic_dropup)
                } else {
                    specDescription.visibility = View.GONE
                    clickableButton.setBackgroundResource(R.drawable.ic_dropdown)
                }
                TransitionManager.beginDelayedTransition(parent, AutoTransition())
            }
        }

        /**
         * Send onClick event to the HardwareDetails fragment.
         */
        override fun onClick(view: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                when (view?.id) {
                  clickableButton.id -> listener.onLinkClicked(hardwareDetails[adapterPosition].first)
                }
            }
        }
    }

    /**
     * Methods that call back to the HardwareDetails fragment.
     */
    interface OnItemListener {
        fun onLinkClicked(clickedLink: String)
    }

    /**
     * Assigns the data set that will be used in this recycler list.
     */
    fun setDataList(newComponentDetails: List<Pair<String, String?>>) {
        hardwareDetails = newComponentDetails
        notifyDataSetChanged()
    }

    /**
     * Return size of the data set.
     */
    override fun getItemCount(): Int = hardwareDetails.size

    /**
     * Initialize the layout/views that will display the component's current specification.
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val displayHardwareDetail =
            DisplayHardwareDetailsBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(
            displayHardwareDetail.detailLayout,
            parent,
            displayHardwareDetail.expandDisplayButtonID,
            displayHardwareDetail.textDetail,
            displayHardwareDetail.specificationDescriptionID
        )
    }

    /**
     * Call the inner view holder class and bind the details of the currently displayed Component specification.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataSet(hardwareDetails[position])
    }
}