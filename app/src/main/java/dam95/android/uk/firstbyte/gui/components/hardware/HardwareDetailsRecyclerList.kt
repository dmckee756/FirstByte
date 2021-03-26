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

class HardwareDetailsRecyclerList(
    private val context: Context?,
    private val listener: OnItemListener
) : RecyclerView.Adapter<HardwareDetailsRecyclerList.ViewHolder>() {

    private var hardwareDetails = emptyList<String>()

    /**
     *
     */
    inner class ViewHolder(
        itemView: View,
        private val parent: ViewGroup,
        private val clickableButton: Button,
        private val detailText: TextView,
        private val descriptionLayout: ConstraintLayout
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        /**
         *
         */
        fun bindDataSet(detail: String) {
            detailText.text = detail
            correctIcon(detail)

        }

        /**
         * Determine the correct icon to load depending on what detail is being displayed
         */
        private fun correctIcon(detail: String) {
            return when (true) {
                (detail.indexOf("Amazon Price") != -1) -> webLink(clickableButton)
                (detail.indexOf("Scan.co.uk Price") != -1) -> webLink(clickableButton)
                (detail.indexOf("RRP Price") != -1) -> bulletPoint(clickableButton)
                (detail.indexOf("Dimension") != -1) -> bulletPoint(clickableButton)
                else -> allowDetailDisplay(clickableButton)
            }
        }

        private fun webLink(clickableButton: Button) {
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

        private fun bulletPoint(clickableButton: Button) {
            clickableButton.setBackgroundResource(R.drawable.ic_blank_bulletpoint)
            clickableButton.isClickable = false
        }

        private fun allowDetailDisplay(clickableButton: Button) {
            clickableButton.setOnClickListener {
                if (descriptionLayout.visibility == View.GONE) {
                    descriptionLayout.visibility = View.VISIBLE
                    clickableButton.setBackgroundResource(R.drawable.ic_dropup)
                } else {
                    descriptionLayout.visibility = View.GONE
                    clickableButton.setBackgroundResource(R.drawable.ic_dropdown)
                }
                TransitionManager.beginDelayedTransition(parent, AutoTransition())
            }
        }

        /**
         *
         */
        override fun onClick(view: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                when (view?.id) {
                  clickableButton.id -> listener.onLinkClicked(hardwareDetails[adapterPosition])
                }
            }
        }
    }

    /**
     *
     */
    interface OnItemListener {
        fun onLinkClicked(clickedLink: String)
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
            parent,
            displayHardwareDetail.expandDisplayButtonID,
            displayHardwareDetail.textDetail,
            displayHardwareDetail.expandableDisplayViewID
        )
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataSet(hardwareDetails[position])
    }
}