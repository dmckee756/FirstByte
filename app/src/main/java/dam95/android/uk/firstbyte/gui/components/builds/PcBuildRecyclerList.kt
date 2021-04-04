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

/**
 * @author David Mckee
 * @Version 1.0
 * Recycler list adapter displays all writeable saved PC Builds currently in the app's database that the user created.
 * It allows empty slots in the list, so that the user can create a pc when these slots are clicked.
 * Each slot will display the PC's name, Total Price, if the PC is completed and the PC's image, with is the Image of the PC's Computer Case.
 */
class PcBuildRecyclerList(
    private val context: Context?,
    private val fb_Hardware_DB: FirstByteDBAccess,
    private val listener: OnItemClickListener,
) : RecyclerView.Adapter<PcBuildRecyclerList.ViewHolder>() {

    private var pcList = emptyList<PCBuild?>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    /**
     * Bind each PC Builds display details to all occupied slots in the recycler list.
     * Set up the empty slots with information informing the user that clicking on these slots will create a new PC Build.
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
         * If the slot is empty, display the empty drawable background, assign the icon for creating a new PC with
         * instructions to the user that clicking on the button will create a new PC.
         *
         * If the slot is occupied with a created PC, display it's Name, Total Price, If it is Completed and it's Computer case Image.
         * Change it's background to further indicate the slot is occupied and for additional color on the screen.
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
         * Send onClick event to the FragmentPCBuildList fragment.
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
     * Methods that call back to the FragmentPCBuildList fragment.
     */
    interface OnItemClickListener {
        fun onButtonClick(pcBuild: PCBuild)
    }

    /**
     * Assigns the Personal PC List data set that will be used in this recycler list.
     * Can include nulls for "empty" slots.
     */
    fun setDataList(updatedPCList: List<PCBuild?>) {
        pcList = updatedPCList
        notifyDataSetChanged()
    }

    /**
     * Return size of the data set.
     */
    override fun getItemCount(): Int = pcList.size

    /**
     * Initialize the layout/views that will display PC details is the current display slot is occupied,
     * or display an view indicated the user can create a new PC build.
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
     * Call the inner view holder class and bind the current PC's display details, or the details to create a new PC.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataSet(pcList[position])
    }
}