package dam95.android.uk.firstbyte.gui.components.search

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import dam95.android.uk.firstbyte.api.ConvertImageURL
import dam95.android.uk.firstbyte.databinding.DisplayHardwarelistBinding
import dam95.android.uk.firstbyte.model.SearchedHardwareItem

/**
 *
 */
class HardwareListRecyclerList(
    private val context: Context?,
    private val listener: OnItemClickListener,
    private val hardwareListFull: List<SearchedHardwareItem>
) : RecyclerView.Adapter<HardwareListRecyclerList.ViewHolder>() {

    private lateinit var hardwareListBinding: DisplayHardwarelistBinding

    /**
     *
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val hardwareBtn: Button = hardwareListBinding.hardwareBtn
        val addHardwareBtn: Button = hardwareListBinding.addHardwareBtn

        init {
            hardwareBtn.setOnClickListener(this)
            /*
            if hardware is saved{
            addHardwareBtn == not visible
            } else {
             */
            addHardwareBtn.setOnClickListener(this)

        }

        /**
         *
         */
        override fun onClick(view: View?)
        {
            if (adapterPosition != RecyclerView.NO_POSITION){
                when (view?.id) {
                    hardwareBtn.id -> listener.onHardwareClick(hardwareListFull[adapterPosition].name, hardwareListFull[adapterPosition].category)
                }
            }
        }


    }

    /**
     *
     */
    interface OnItemClickListener {
        fun onHardwareClick(componentName: String, componentType: String)
    }

    /**
     *
     */
    override fun getItemCount() = hardwareListFull.size

    /**
     *
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        hardwareListBinding = DisplayHardwarelistBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(
            hardwareListBinding.hardwareListCard
        )
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Log.i("LIST_HARDWARE_NAME", hardwareListFull[position].name)
        Log.i("LIST_HARDWARE_LINK", hardwareListFull[position].image_link)
        ConvertImageURL.convertURLtoImage(hardwareListFull[position], hardwareListBinding.hardwareSearchImage)
        hardwareListBinding.hardwareSearchName.text = hardwareListFull[position].name
        hardwareListBinding.hardwareSearchPrice.text = hardwareListFull[position].rrpPriceToCurrency()
    }


}