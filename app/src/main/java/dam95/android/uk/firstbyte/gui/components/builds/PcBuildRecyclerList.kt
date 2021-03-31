package dam95.android.uk.firstbyte.gui.components.builds

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
import dam95.android.uk.firstbyte.databinding.DisplayPclistBinding
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import dam95.android.uk.firstbyte.model.PCBuild
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PcBuildRecyclerList(
    private val context: Context?,
    private val fb_Hardware_DB: FirstByteDBAccess,
    private val listener: OnItemClickListener,
) : RecyclerView.Adapter<PcBuildRecyclerList.ViewHolder>() {

    private var pcList = emptyList<PCBuild?>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    /**
     *
     */
    inner class ViewHolder(
        itemView: View,
        private val pcBtn: Button,
        private val pcName: TextView,
        private val pcPriceOrCreation: TextView,
        private val completeOrIncomplete: TextView,
        private val pcCaseImageOrAdd: ImageView
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        init {
            pcBtn.setOnClickListener(this)
        }

        /**
         *
         */
        fun bindDataSet(pcBuild: PCBuild?) {
            //List the pc build list as being able to create a new pc
            if (pcBuild == null) {
                //Hide unnecessary text views
                pcName.visibility = View.GONE
                completeOrIncomplete.visibility = View.GONE
                pcBtn.setBackgroundResource(R.drawable.object_add_pc)

                //Set up the display with instructions to create a new PC
                pcPriceOrCreation.text = context?.resources?.getString(R.string.createPC)
                //Get the add "+" icon from drawable resources

                pcCaseImageOrAdd.setImageResource(R.drawable.ic_create_pc)
            } else {
                //Load the correct pc in the list
                //Load a colorful background from drawable resources

                pcName.visibility = View.VISIBLE
                completeOrIncomplete.visibility = View.VISIBLE
                pcBtn.setBackgroundResource(R.drawable.object_pc_display_item)

                //Setup correct information for pc selection display
                pcName.text = pcBuild.pcName

                pcPriceOrCreation.text =
                    context?.resources?.getString(R.string.totalPrice, "£", pcBuild.pcPrice)
                val pcStatus =
                    if (pcBuild.isPcCompleted) context?.resources?.getString(R.string.buildComplete) else context?.resources?.getString(
                        R.string.buildIncomplete
                    )

                completeOrIncomplete.text = pcStatus

                //If the pc has a case assigned to it, then find the image link.
                pcBuild.caseName?.let {
                    coroutineScope.launch {
                        val imageLink = fb_Hardware_DB.retrieveImageURL(it)
                        //If the case image link returns a URL then have picasso load it...
                        ConvertImageURL.convertURLtoImage(imageLink!!, pcCaseImageOrAdd)
                    }
                }
                //At the end of display information, remove the add symbol from the display.
                pcCaseImageOrAdd.background = null
            }
        }

        /**
         *
         */
        override fun onClick(view: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                when (view?.id) {
                    R.id.pcListBtn -> {
                        if (pcName.visibility == View.GONE) {
                            //If Pc doesn't exist
                            val newPC = PCBuild()
                            newPC.pcID = fb_Hardware_DB.createPC(newPC)
                            listener.onButtonClick(newPC)
                        } else {
                            //Create and save a new PC and enter into it
                            pcList[adapterPosition]?.let { listener.onButtonClick(it) }
                        }
                    }
                }
            }
        }
    }

    /**
     *
     */
    interface OnItemClickListener {
        fun onButtonClick(pcBuild: PCBuild)
    }

    /**
     *
     */
    fun setDataList(updatedPCList: List<PCBuild?>) {
        pcList = updatedPCList
        notifyDataSetChanged()
    }

    /**
     *
     */
    override fun getItemCount(): Int = pcList.size

    /**
     *
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val displayPCListBinding =
            DisplayPclistBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(
            displayPCListBinding.pcListCard,
            displayPCListBinding.pcListBtn,
            displayPCListBinding.personalBuildName,
            displayPCListBinding.pcPriceOrCreation,
            displayPCListBinding.completeOrIncomplete,
            displayPCListBinding.pcCaseImageOrAdd
        )
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataSet(pcList[position])
    }
}