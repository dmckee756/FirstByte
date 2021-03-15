package dam95.android.uk.firstbyte.gui.components.compare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.databinding.RecyclerListBinding


class SelectCompare : Fragment(), SelectCompareRecyclerList.OnItemClickListener {

    private lateinit var recyclerListBinding: RecyclerListBinding
    private lateinit var compareSelectionList: SelectCompareRecyclerList

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recyclerListBinding = RecyclerListBinding.inflate(inflater, container, false)
        setUpCompareSelectionList()
        return recyclerListBinding.root
    }

    private fun setUpCompareSelectionList(){
        //
        val displayCompareSelection = recyclerListBinding.recyclerList
        //
        displayCompareSelection.layoutManager = LinearLayoutManager(this.context)
        compareSelectionList = SelectCompareRecyclerList(context, this)

        val pcTiers = listOf("Processor", "Graphics Card", "RAM")
        compareSelectionList.setDataList(pcTiers)
        displayCompareSelection.adapter = compareSelectionList
    }

    override fun onCompareBtnClick() {
        TODO("Not yet implemented")
    }
}