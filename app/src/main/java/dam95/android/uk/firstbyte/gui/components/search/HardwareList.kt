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

const val CATEGORY_KEY = "CATEGORY"
const val NAME_KEY = "NAME"
const val LOCAL_OR_NETWORK_KEY = "LOADING_METHOD"
const val PC_ID = "PC_ID"

/**
 * @author David Mckee
 * @Version 1.0
 * Allows the user to search through all components or all components of the same category that either loaded from
 * the online API or the App's local database. The user can sort and filter the list and search for components.
 *
 * The user then can click on the component to see it's details, or if it was navigated from PCBuilds or Compared fragments,
 * it will then add the component to the PC or the compared list.
 */
class HardwareList : Fragment(), HardwareListRecyclerList.OnItemClickListener,
    SearchView.OnQueryTextListener {

    private lateinit var recyclerListBinding: FragmentHardwareListBinding
    private lateinit var apiViewModel: ApiViewModel
    private lateinit var hardwareListAdapter: HardwareListRecyclerList

    private lateinit var fbHardwareDb: FirstByteDBAccess
    private var categoryListLiveData: LiveData<List<SearchedHardwareItem>>? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var isLoadingFromServer: Boolean? = null
    private var searchCategory: String? = null
    private var notFromSearch: String? = null
    private var pcID: Int = -1

    /**
     * Determines if the HardwareList was loaded from the database or from the online API,
     * then chooses it's initialisation and loading path accordingly.
     * It will either Stream in the hardware display items from the API or will load the list from the database.
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

    /**
     * Load all displayed components that are saved to the online API
     * and then put the retrieved list into the recycler list adapter for the user to select.
     */
    private fun initialiseOnlineLoading() {
        Log.i("ONLINE_METHOD", "Loading from either server or cache.")

        val apiRepository = ApiRepository(requireContext())

        apiViewModel = ApiViewModel(apiRepository)
        //Load the values streamed from the api into a mutable live data list in the "apiRepository".
        apiViewModel.getCategory(searchCategory)

        //Put the response from the apiRepository/API into the recycler view adapter
        apiViewModel.apiCategoryResponse.observe(viewLifecycleOwner, { res ->
            res.body()?.let { body -> setUpHardwareList(body)}
        })
    }

    /**
     * Load all displayed components that are saved to the app's database
     * and then put the retrieved list into the recycler list adapter for the user to select.
     */
    private fun initialiseOfflineLoading() {
        Log.i("OFFLINE_METHOD", "Load client database.")
        //Load FB_Hardware_Android Instance
        fbHardwareDb =
            context?.let { FirstByteDBAccess.dbInstance(it, Dispatchers.Default) }!!

        coroutineScope.launch {

            //Retrieve a list of display component items.
            searchCategory?.let {
                categoryListLiveData =
                    fbHardwareDb.retrieveCategory(it.toLowerCase(Locale.ROOT))
            }
            //Put the list into the recycler view adapter
            categoryListLiveData?.observe(viewLifecycleOwner) { list ->
                var gatheredList = list
                gatheredList = gatheredList.sortedBy { it.name.capitalize(Locale.ROOT) }
                setUpHardwareList(gatheredList)
            }

        }
    }

    /**
     * Set up the Filter price view, allowing the user to filter the components in the list.
     * This utilises Google's Range Slider so that the user can set a minimum and maximum price filter.
     * The user can apply the price range filter, or reset the displayed component list to it's original form.
     * Check Integer.xml for min and max prices.
     */
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
            //Get the minimum and maximum filtered prices.
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

            if (this::hardwareListAdapter.isInitialized) hardwareListAdapter.filterByPrice(
                firstValue,
                secondValue
            )
        }
    }

    /**
     * Set up the search view that allows users to filter through the component display list.
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
     * Setup the recycler list that display each component that can be loaded from either the API
     * or the app's database, depending on where this was navigated from.
     * @param res a list of display component items
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
     * Search through the list when the search view is submitted.
     * @param query The search view query.
     */
    override fun onQueryTextSubmit(query: String?): Boolean {
        searchList(query)
        return true
    }

    /**
     * Search through the list when the search view changes.
     * @param newText The search view query.
     */
    override fun onQueryTextChange(newText: String?): Boolean {
        searchList(newText)
        return true
    }

    /**
     * When the search view is interacted with, either create and display a list of components that only contain the searched characters/string
     * or if the search view is empty, display the full list.
     * @param newText The search view query.
     */
    private fun searchList(newText: String?) {
        if (newText != null && this::hardwareListAdapter.isInitialized) {
            if (newText != "") {
                //Get the current sorted unaltered list and add only components containing the searched characters into a list
                //and set the newly created list into the data set that is being used in the recycler adapter.
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

    //Recycler list event method

    /**
     * When a component has been clicked/pressed, navigate to HardwareDetails and display all of the components specifications.
     *
     * When the HardwareList fragment was navigated to from PCBuild and a component was clicked/pressed,
     * then add that item into the PCBuild on the database and navigate back to the PC.
     *
     * When the HardwareList fragment was navigated to from the comparison and a component was clicked/pressed,
     * then add that item into the compared list on the database.
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
     * Create the HardwareList app bar menu, allowing a price search filter and sorting the list of components.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.hardwarelist_toolbar_items, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Handle app bar items, such as sorting the hardware list alphabetically and by price tag.
     * Also allows the user to open and close the price filter menu.
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
                    //Display the Filter menu
                    if (recyclerListBinding.filterViewID.visibility == View.GONE) {
                        MyAnimationList.startCrossFade(
                            recyclerListBinding.filterViewID,
                            0F,
                            1F,
                            1.toLong(),
                            View.VISIBLE
                        )
                        //Change the filter icon at the top to show the filter menu is open
                        item.icon = ResourcesCompat.getDrawable(
                            requireContext().resources,
                            R.drawable.ic_baseline_arrow_upward_24,
                            null
                        )
                    } else {
                        //Close the Filter menu
                        recyclerListBinding.filterViewID.visibility = View.GONE
                        TransitionManager.beginDelayedTransition(
                            recyclerListBinding.filterViewID,
                            AutoTransition()
                        )
                        //Change the filter icon at the top to show the filter menu is closed
                        item.icon = ResourcesCompat.getDrawable(
                            requireContext().resources,
                            R.drawable.ic_filter,
                            null
                        )
                    }
                    //Do an animation when opening/closing the filter menu
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
