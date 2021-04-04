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

/**
 * @author David Mckee
 * @Version 1.0
 * Allows the user to select from 3 different component categories.
 * Displays information on what values a component in each category can compare.
 */
class SelectCompare : Fragment(), SelectCompareRecyclerList.OnItemClickListener {

    private lateinit var recyclerListBinding: RecyclerListBinding
    private lateinit var compareSelectionList: SelectCompareRecyclerList

    /**
     * Re-uses the recycler list layout to allow the user to select what type of component they want to compare.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recyclerListBinding = RecyclerListBinding.inflate(inflater, container, false)
        setUpCompareSelectionList()
        return recyclerListBinding.root
    }

    /**
     * Sets up the 3 categories that allow components to be compared to another as a recycler list: [CPU, GPU, RAM]
     */
    private fun setUpCompareSelectionList() {
        //Finds and initialises the correct recycler list for this fragment.
        val displayCompareSelection = recyclerListBinding.recyclerList
        displayCompareSelection.layoutManager = LinearLayoutManager(this.context)
        compareSelectionList = SelectCompareRecyclerList(context, this)
        //Assigns the PC list into the recycler list adapter
        val pcTiers = listOf("CPU", "GPU", "RAM")
        compareSelectionList.setDataList(pcTiers)
        displayCompareSelection.adapter = compareSelectionList
    }

    /**
     * Return a list of the correct values that can be compared depending on the selected category.
     * This list will then be bundled, before beings transported into the CompareHardware fragment.
     */
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

    /**
     * When the selects what category of component they want to compare values with,
     * bundle the correct information/instructions and navigated to the CompareHardware fragment.
     * @param chosenCategory The category of components the user will compare.
     */
    override fun onCompareBtnClick(chosenCategory: String) {
        //Finds the action that allows navigation from the select compare page to the compare hardware Fragment,
        //with a bundle of the selected category's values that component can compare and informing the class which component type is being compared.
        val categoryBundle =
            bundleOf(
                COMPARE to chosenCategory.toLowerCase(Locale.ROOT),
                COMPARED_VALUES_LIST to findSetValues(chosenCategory)
            )

        val navController = activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
        navController?.navigate(
            R.id.action_compare_fragmentID_to_compareHardware_fragmentID,
            categoryBundle
        )
    }
}