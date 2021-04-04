package dam95.android.uk.firstbyte.gui.components.search

import android.os.Build
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.api.ApiRepository
import dam95.android.uk.firstbyte.api.ApiViewModel
import dam95.android.uk.firstbyte.databinding.FragmentHardwareListBinding
import dam95.android.uk.firstbyte.datasource.FirstByteDBAccess
import dam95.android.uk.firstbyte.gui.util.MyAnimationList
import dam95.android.uk.firstbyte.gui.components.builds.NOT_FROM_SEARCH
import dam95.android.uk.firstbyte.gui.components.compare.FROM_COMPARE
import dam95.android.uk.firstbyte.gui.components.search.util.HandleComponent
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

/**
 *
 */
const val CATEGORY_KEY = "CATEGORY"
const val NAME_KEY = "NAME"
const val LOCAL_OR_NETWORK_KEY = "LOADING_METHOD"
const val PC_ID = "PC_ID"
class HardwareList : Fragment(), HardwareListRecyclerList.OnItemClickListener,
    SearchView.OnQueryTextListener {

    private lateinit var recyclerListBinding: FragmentHardwareListBinding
    private lateinit var apiViewModel: ApiViewModel
    private lateinit var hardwareListAdapter: HardwareListRecyclerList

    private lateinit var fbHardwareDb: FirstByteDBAccess
    var categoryListLiveData: LiveData<List<SearchedHardwareItem>>? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var isLoadingFromServer: Boolean? = null
    private var searchCategory: String? = null
    private var notFromSearch: String? = null
    private var pcID: Int = -1

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchCategory = arguments?.getString(CATEGORY_KEY)
        isLoadingFromServer = arguments?.getBoolean(LOCAL_OR_NETWORK_KEY)
        pcID = arguments?.getInt(PC_ID)!!
        notFromSearch = arguments?.getString(NOT_FROM_SEARCH)

        recyclerListBinding = FragmentHardwareListBinding.inflate(inflater, container, false)
        if (searchCategory != null) {
            setHasOptionsMenu(true)
            setUpFilterMenu()
            setUpSearch()
            when (isLoadingFromServer) {
                true -> initialiseOnlineLoading()
                false -> initialiseOfflineLoading()
                else -> {
                    Log.e("NULL_CONNECTION?", "Error, Connection is showing null. How?")
                }
            }
        }

        // Inflate the layout for this fragment
        return recyclerListBinding.root
    }

    private fun setUpFilterMenu() {

        val priceSlider = recyclerListBinding.priceSliderRangeBar
        var readFirstValue = true
        var firstValue = R.integer.minFilterPriceSize.toFloat()
        var secondValue = R.integer.maxFilterPriceSize.toFloat()

        //Format the range slider in UK GBP (£)
        priceSlider.setLabelFormatter { price ->
            val getGBP = NumberFormat.getCurrencyInstance()
            getGBP.currency = Currency.getInstance("GBP")
            getGBP.format(price)
        }

        recyclerListBinding.resetFilterBtn.setOnClickListener {
            //Reset SearchView when filters are reset
            recyclerListBinding.hardwareListSearchViewID.setQuery("", false)
            recyclerListBinding.hardwareListSearchViewID.clearFocus()
            if (this::hardwareListAdapter.isInitialized) hardwareListAdapter.resetFilter()
        }
        recyclerListBinding.applyFilterBtn.setOnClickListener {
            priceSlider.values.forEach { price ->
                if (readFirstValue) {
                    firstValue = price
                    readFirstValue = false
                } else {
                    secondValue = price
                    readFirstValue = true
                }
            }
            //Reset SearchView when price is filtered, because I just can't get it all to be fully functional.
            //I genuinely am intrigued in a system that does work. Some evening, and not at 3am.
            recyclerListBinding.hardwareListSearchViewID.setQuery("", false)
            recyclerListBinding.hardwareListSearchViewID.clearFocus()

            if (this::hardwareListAdapter.isInitialized) hardwareListAdapter.filterByPrice(firstValue, secondValue)
        }
    }

    private fun initialiseOnlineLoading() {
        Log.i("ONLINE_METHOD", "Loading from either server or cache.")

        val apiRepository = ApiRepository(requireContext())

        apiViewModel = ApiViewModel(apiRepository)
        //Load the values streamed from the api into a mutable live data list in the "apiRepository".
        apiViewModel.getCategory(searchCategory)
        //Observe the loaded displayDetails from the apiRepository

        apiViewModel.apiCategoryResponse.observe(viewLifecycleOwner, { res ->
            res.body()?.let { setUpHardwareList(it) }
        })
    }

    private fun initialiseOfflineLoading() {
        Log.i("OFFLINE_METHOD", "Load client database.")
        //Load FB_Hardware_Android Instance
        fbHardwareDb =
            context?.let { FirstByteDBAccess.dbInstance(it, Dispatchers.Main) }!!

        coroutineScope.launch {

            searchCategory?.let {
                categoryListLiveData =
                    fbHardwareDb.retrieveCategory(it.toLowerCase(Locale.ROOT))
            }
            categoryListLiveData?.observe(viewLifecycleOwner) { list ->
                var gatheredList = list
                gatheredList = gatheredList.sortedBy { it.name.capitalize(Locale.ROOT) }
                setUpHardwareList(gatheredList)
            }

        }
    }

    /**
     *
     */
    private fun setUpSearch() {
        //Display the search view for the hardware list
        val searchView = recyclerListBinding.hardwareListSearchViewID
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(this)

        //Remove > symbol from search view
        searchView.isSubmitButtonEnabled = false

        searchView.setOnCloseListener {
            //Exit out of the search view
            searchView.onActionViewCollapsed()
            //If above API 24, collapse the keyboard
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                requireActivity().dismissKeyboardShortcutsHelper()
            true
        }
    }

    /**
     *
     */
    private fun setUpHardwareList(res: List<SearchedHardwareItem>) {

        val displayHardwareList = recyclerListBinding.recyclerHardwarelist
        //
        displayHardwareList.layoutManager = LinearLayoutManager(this.context)
        hardwareListAdapter = HardwareListRecyclerList(context, this, isLoadingFromServer!!)

        hardwareListAdapter.setDataList(res)

        displayHardwareList.adapter = hardwareListAdapter
    }

    //Searching Through recycler list methods
    /**
     *
     */
    override fun onQueryTextSubmit(query: String?): Boolean {
        searchList(query)
        return true
    }

    /**
     *
     */
    override fun onQueryTextChange(newText: String?): Boolean {
        searchList(newText)
        return true
    }

    private fun searchList(newText: String?) {
        if (newText != null && this::hardwareListAdapter.isInitialized) {
            //
            if (newText != "") {
                //
                val searchList = hardwareListAdapter.getFullSortedList()

                val updatedList = mutableListOf<SearchedHardwareItem>()
                for (i in searchList.indices) {
                    if (searchList[i].name.contains(newText, ignoreCase = true)) updatedList.add(
                        searchList[i]
                    )
                }
                hardwareListAdapter.setUsedDataList(updatedList)
            } else {
                //If the search is empty, reload the recycler list with the sorted full list
                hardwareListAdapter.setUsedDataList(hardwareListAdapter.getFullSortedList())
            }
        }
    }


    //Recycler list event methods

    /**
     *
     */
    override fun onHardwareClick(componentName: String, componentType: String) {

        //If this fragment was loaded from the personal build screen, then add the clicked component to the PC that called this fragment.
        if (pcID > 0 && this::fbHardwareDb.isInitialized) {
            HandleComponent.calledFromPCBuild(
                requireActivity(),
                fbHardwareDb,
                componentName,
                componentType,
                pcID
            )
            //If this fragment was loaded from the comparison screen, then add the clicked component to the compared list.
        } else if (notFromSearch == FROM_COMPARE && this::fbHardwareDb.isInitialized) {
            HandleComponent.calledFromComparedList(
                requireActivity(),
                fbHardwareDb,
                componentName,
                componentType
            )
        } else {
            //If this fragment was called from a search, then navigate into the components "Hardware Details" fragment
            //A bundle containing component name, type and a check if it's loading it from the online API, or the local client database.
            val nameBundle = bundleOf(
                NAME_KEY to componentName,
                CATEGORY_KEY to componentType,
                LOCAL_OR_NETWORK_KEY to isLoadingFromServer
            )

            //Navigate to Hardware details with the bundle
            val navController =
                activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
            navController?.navigate(
                R.id.action_hardwareList_fragmentID_to_hardwareDetails_fragmentID,
                nameBundle
            )
        }
    }

    //UI Methods

    /**
     *
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.hardwarelist_toolbar_items, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     *
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (this::hardwareListAdapter.isInitialized) {
            when (item.itemId) {
                // Bring up a sorting menu
                R.id.alphabeticalAscendingID -> hardwareListAdapter.sortDataSet(item.itemId)
                R.id.alphabeticalDescendingID -> hardwareListAdapter.sortDataSet(item.itemId)
                R.id.priceAscendingID -> hardwareListAdapter.sortDataSet(item.itemId)
                R.id.priceDescendingID -> hardwareListAdapter.sortDataSet(item.itemId)
                // Bring up a filter menu
                R.id.filterID -> {
                    if (recyclerListBinding.filterViewID.visibility == View.GONE) {
                        MyAnimationList.startCrossFade(
                            recyclerListBinding.filterViewID,
                            0F,
                            1F,
                            1.toLong(),
                            View.VISIBLE
                        )
                        item.icon = ResourcesCompat.getDrawable(
                            requireContext().resources,
                            R.drawable.ic_baseline_arrow_upward_24,
                            null
                        )
                    } else {
                        recyclerListBinding.filterViewID.visibility = View.GONE
                        TransitionManager.beginDelayedTransition(
                            recyclerListBinding.filterViewID,
                            AutoTransition()
                        )
                        item.icon = ResourcesCompat.getDrawable(
                            requireContext().resources,
                            R.drawable.ic_filter,
                            null
                        )
                    }
                    TransitionManager.beginDelayedTransition(
                        recyclerListBinding.root,
                        AutoTransition()
                    )
                }
            }
            Log.i("STATE_SAVED", "onSaveInstanceState")
        }
        return super.onOptionsItemSelected(item)
    }
}
