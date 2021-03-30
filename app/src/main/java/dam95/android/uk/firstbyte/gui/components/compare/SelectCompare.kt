package dam95.android.uk.firstbyte.gui.components.compare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.RecyclerListBinding
import dam95.android.uk.firstbyte.gui.components.search.CATEGORY_KEY
import dam95.android.uk.firstbyte.gui.components.search.LOCAL_OR_NETWORK_KEY
import dam95.android.uk.firstbyte.gui.components.search.PC_ID
import dam95.android.uk.firstbyte.model.util.ComponentsEnum
import java.util.*


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

    private fun setUpCompareSelectionList() {
        //
        val displayCompareSelection = recyclerListBinding.recyclerList
        //
        displayCompareSelection.layoutManager = LinearLayoutManager(this.context)
        compareSelectionList = SelectCompareRecyclerList(context, this)

        val pcTiers = listOf("CPU", "GPU", "RAM")
        compareSelectionList.setDataList(pcTiers)
        displayCompareSelection.adapter = compareSelectionList
    }

    private fun findSetValues(type: String): ArrayList<String> {
        val arrayList = arrayListOf<String>()
        arrayList.add("Prices")
        when (type.toUpperCase(Locale.ROOT)) {
            ComponentsEnum.CPU.toString() -> {
                arrayList.add("Core Speed")
                arrayList.add("Core Count")
                arrayList.add("Wattage")
            }
            ComponentsEnum.GPU.toString() -> {
                arrayList.add("Clock Speed")
                arrayList.add("Memory Size")
                arrayList.add("Memory Speed")
                arrayList.add("Wattage")
            }
            ComponentsEnum.RAM.toString() -> {
                arrayList.add("Memory Speed")
                arrayList.add("Memory Size")
            }
        }
        return arrayList
    }

    override fun onCompareBtnClick(chosenCategory: String) {

        val categoryBundle =
            bundleOf(
                COMPARE to chosenCategory.toLowerCase(Locale.ROOT),
                COMPARED_VALUES_LIST to findSetValues(chosenCategory)
            )

        //
        val navController = activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
        navController?.navigate(
            R.id.action_compare_fragmentID_to_compareHardware_fragmentID,
            categoryBundle
        )
    }
}