package dam95.android.uk.firstbyte.gui.components.compare

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.FragmentCompareHardwareBinding
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import dam95.android.uk.firstbyte.gui.components.builds.NOT_FROM_SEARCH
import dam95.android.uk.firstbyte.gui.components.compare.util.CompareGeneric
import dam95.android.uk.firstbyte.gui.components.compare.util.CompareValueSpinner
import dam95.android.uk.firstbyte.gui.components.search.CATEGORY_KEY
import dam95.android.uk.firstbyte.gui.components.search.LOCAL_OR_NETWORK_KEY
import dam95.android.uk.firstbyte.model.components.Component
import dam95.android.uk.firstbyte.model.util.HumanReadableUtils
import kotlinx.coroutines.Dispatchers
import java.util.*


const val COMPARE = "COMPARE_LIST"
const val FINISH_ID = "_COMPARE_LIST"
const val FROM_COMPARE = "FROM_COMPARE"
const val COMPARED_VALUES_LIST = "COMPARED_VALUES_LIST"

class CompareHardware : Fragment(), CompareHardwareRecyclerList.OnItemClickListener {

    private lateinit var compareHardwareBinding: FragmentCompareHardwareBinding
    private lateinit var compareHardwareListAdapter: CompareHardwareRecyclerList
    private lateinit var fbHardwareDb: FirstByteDBAccess
    private lateinit var mutableLiveComponentList: MutableLiveData<MutableList<Component?>>

    private lateinit var compareBarChart: BarChart
    private lateinit var barDataSet: BarDataSet

    //Pair<The compared component value, the Category type>
    private lateinit var currentComparison: Pair<String, String>

    private lateinit var listOfComparedValue: ArrayList<String>
    private lateinit var currentComparedValueFunction: (List<Component?>) -> List<Float>
    private val myBarChartFormatting = HumanReadableUtils.MyBarChartFormatting()

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val categoryType = arguments?.getString(COMPARE)
        listOfComparedValue =
            arguments?.getStringArrayList(COMPARED_VALUES_LIST) as ArrayList<String>

        compareHardwareBinding =
            FragmentCompareHardwareBinding.inflate(inflater, container, false)

        if (categoryType != null) {
            val comparedTableID = "${categoryType.toUpperCase(Locale.ROOT)}$FINISH_ID"
            //Override previous toolbar menu
            setHasOptionsMenu(true)
            //Initialise the database connection
            fbHardwareDb =
                context?.let { FirstByteDBAccess.dbInstance(it, Dispatchers.Main) }!!

            initialiseComponents(categoryType, comparedTableID)
            //Initialise the BarChart with the initial values
            compareBarChart = compareHardwareBinding.compareBarChart
            updateBarChart()

            mutableLiveComponentList.observe(viewLifecycleOwner) { comparedList ->
                //Set up the recycler list with loaded components
                setUpRecyclerList(comparedList, categoryType)
            }
        }
        return compareHardwareBinding.root
    }

    private fun initialiseComponents(categoryType: String, comparedTableID: String) {
        //Set up component value selection spinner
        CompareValueSpinner.initializeSpinner(
            ::updateValuesChanged,
            listOfComparedValue,
            categoryType,
            compareHardwareBinding.compareValuesSpinner,
            requireContext()
        )

        //Create and load the last compared components with MutableLiveData features
        mutableLiveComponentList = MutableLiveData<MutableList<Component?>>()
        loadComparedComponents(comparedTableID, categoryType)

        //Have the initial compared values be the components RRP Prices
        currentComparison = Pair(listOfComparedValue[0], categoryType)
        //Assign the correct function references for getting prices and displaying prices
        currentComparedValueFunction = CompareGeneric::compareRRPPrice
        myBarChartFormatting.formatFloat = myBarChartFormatting::formatCurrency

        compareHardwareBinding.compareValues.text =
            requireContext().resources.getString(
                R.string.compareValues,
                currentComparison.first
            )
    }

    private fun updateValuesChanged(
        newValueFunction: (List<Component?>) -> List<Float>,
        newValueName: String
    ) {
        //Assign the new value that the component will compare for the BarChart
        currentComparedValueFunction = newValueFunction

        //Assign the new value name
        val category = currentComparison.second
        currentComparison = Pair(newValueName, category)
        //Re-assign the text
        compareHardwareBinding.compareValues.text =
            requireContext().resources.getString(
                R.string.compareValues,
                currentComparison.first
            )

        //Dynamically assign the new style formatting for the BarChart
        when (currentComparison.first) {
            "Prices" -> myBarChartFormatting.formatFloat = myBarChartFormatting::formatCurrency
            "Wattage" -> myBarChartFormatting.formatFloat = myBarChartFormatting::formatWattage
            "Memory Size" -> myBarChartFormatting.formatFloat = myBarChartFormatting::formatGB
            "Core Speed" -> myBarChartFormatting.formatFloat = myBarChartFormatting::formatGhz
            "Core Count" -> myBarChartFormatting.formatFloat = myBarChartFormatting::formatCoreCount
            else -> myBarChartFormatting.formatFloat = myBarChartFormatting::formatMhz
        }
        updateBarChart()
    }

    /**
     *
     */
    private fun loadComparedComponents(
        comparedTableID: String,
        category: String
    ) {

        //Check if the table exists, if not then create a comparison ID table
        if (fbHardwareDb.checkIfComparedTableExists(comparedTableID) == 0) fbHardwareDb.createComparedComponents(
            comparedTableID
        )

        mutableLiveComponentList.value = mutableListOf()
        val nameReferences = fbHardwareDb.retrieveComparedComponents(comparedTableID)
        var componentOrNull: Component?

        for (i in nameReferences.indices) {
            //Add either a component or null to the comparedList value
            componentOrNull = nameReferences[i]?.let {
                fbHardwareDb.retrieveHardware(
                    it,
                    category.toLowerCase(Locale.ROOT)
                )
            }
            mutableLiveComponentList.value!!.add(componentOrNull)
        }
    }

    /**
     *
     */
    private fun setUpRecyclerList(comparedList: MutableList<Component?>, categoryType: String) {
        val displayComparedHardware = compareHardwareBinding.compareComponentsRecyclerList
        //
        displayComparedHardware.layoutManager = LinearLayoutManager(this.context)
        compareHardwareListAdapter = CompareHardwareRecyclerList(context, categoryType, this)

        compareHardwareListAdapter.setDataList(comparedList)
        displayComparedHardware.adapter = compareHardwareListAdapter
    }

    /**
     *
     */
    private fun updateBarChart() {
        val barEntry: MutableList<BarEntry> = mutableListOf()
        val legendEntries = mutableListOf<LegendEntry>()
        val colorScheme = requireContext().resources.obtainTypedArray(R.array.myGraphColors)
        val colorMutableList = mutableListOf<Int>()

        val valueList = currentComparedValueFunction(mutableLiveComponentList.value!!)
        //Load the slots that have components and add the comparing value to the barEntry mutable list
        for (position in valueList.indices) {
        colorMutableList.add(colorScheme.getColor(position, 0))
            Log.i("VALUE", valueList[position].toString())
            barEntry.add(
                BarEntry(
                    position.toFloat(),
                    valueList[position]
                )
            )

            //Create a custom legend for each slot.
            val legendEntry = LegendEntry()
            legendEntry.label = "Slot ${position + 1}."
            legendEntry.formSize = 12F
            legendEntry.formColor = colorMutableList[position]
            legendEntries.add(legendEntry)

        }
        colorScheme.recycle()
        finaliseBarChart(barEntry, legendEntries, colorMutableList)
        //Update the BarChart view
        compareBarChart.invalidate()
    }

    private fun finaliseBarChart(
        barEntry: MutableList<BarEntry>,
        legendEntries: MutableList<LegendEntry>,
        colorScheme: MutableList<Int>,
        ) {
        //Assign DataSet to the BarChart
        barDataSet = BarDataSet(barEntry, currentComparison.first)
        compareBarChart.data = BarData(barDataSet)
        //Hide unnecessary Axis of BarChart and it's description text
        compareBarChart.axisRight.isEnabled = false
        compareBarChart.xAxis.isEnabled = false
        compareBarChart.description.text = ""
        //Set legend colours and size
        compareBarChart.legend.setCustom(legendEntries)
        compareBarChart.legend.textSize = 12F
        compareBarChart.legend.textColor = ResourcesCompat.getColor(requireContext().resources, R.color.textColor, null)
        //Set data colours, text size and human readable formatting
        barDataSet.valueTextColor = ResourcesCompat.getColor(requireContext().resources, R.color.textColor, null)
        barDataSet.colors = colorScheme
        barDataSet.valueTextSize = 16F
        //Dynamic human readable formatting
        compareBarChart.axisLeft.valueFormatter = myBarChartFormatting
        compareBarChart.axisLeft.textColor = ResourcesCompat.getColor(requireContext().resources, R.color.textColor, null)
        barDataSet.valueFormatter = myBarChartFormatting
    }

    /**
     *
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.hardware_related_toolbar_items, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     *
     */
    override fun addComponent(componentType: String) {
        val addToPcCompare = bundleOf(
            CATEGORY_KEY to componentType,
            LOCAL_OR_NETWORK_KEY to false,
            NOT_FROM_SEARCH to FROM_COMPARE
        )
        val navController =
            activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
        navController?.navigate(
            R.id.action_compareHardware_fragmentID_to_hardwareList_fragmentID,
            addToPcCompare
        )
    }

    /**
     *
     */
    override fun removeComponent(component: Component, position: Int) {
        fbHardwareDb.removeComparedComponent(component.name)
        //Remove the component locally and update the recycler list
        mutableLiveComponentList.value!!.removeAt(position)
        mutableLiveComponentList.value!!.add(null)
        compareHardwareListAdapter.notifyDataSetChanged()
        updateBarChart()
    }
}