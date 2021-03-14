package dam95.android.uk.firstbyte.gui.components.search

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.RecyclerListBinding
import dam95.android.uk.firstbyte.api.api_model.ApiRepository
import dam95.android.uk.firstbyte.api.api_model.ApiViewModel
import dam95.android.uk.firstbyte.datasource.ComponentDBAccess
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.NullPointerException

/**
 *
 */
private const val CATEGORY_KEY = "CATEGORY"
private const val NAME_KEY = "NAME"
private const val LOCAL_OR_NETWORK_KEY = "LOADING_METHOD"

class HardwareList : Fragment(), HardwareListRecyclerList.OnItemClickListener,
    SearchView.OnQueryTextListener {

    private lateinit var recyclerListBinding: RecyclerListBinding
    private lateinit var apiViewModel: ApiViewModel
    private lateinit var hardwareListAdapter: HardwareListRecyclerList

    private lateinit var fb_Hardware_DB: ComponentDBAccess
    private lateinit var categoryListLiveData: LiveData<List<SearchedHardwareItem>>
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var isLoadingFromServer: Boolean? = null
    private var searchCategory: String? = null

    /**
     *
     */
    @Throws(NullPointerException::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchCategory = arguments?.getString(CATEGORY_KEY)
        isLoadingFromServer = arguments?.getBoolean(LOCAL_OR_NETWORK_KEY)

        recyclerListBinding = RecyclerListBinding.inflate(inflater, container, false)
        if (searchCategory != null) {
            Log.i("SEARCH_CATEGORY", searchCategory!!)

            val apiRepository = ApiRepository(requireContext())
            apiViewModel = ApiViewModel(apiRepository)

            setUpSearch()
            when (isLoadingFromServer) {
                true -> {
                    Log.i("ONLINE_METHOD", "Loading from either server or cache.")
                    //Load the values streamed from the api into a mutable live data list in the "apiRepository".
                    apiViewModel.getCategory(searchCategory)
                    //Observe the loaded displayDetails from the apiRepository
                    apiViewModel.apiCategoryResponse.observe(viewLifecycleOwner, { res ->
                        res.body()?.let { setUpHardwareList(it) }
                    })
                }
                false -> {
                    Log.i("OFFLINE_METHOD", "Load client database.")
                    //Load FB_Hardware_Android Instance
                    fb_Hardware_DB = context?.let { ComponentDBAccess.dbInstance(it) }!!
                    coroutineScope.launch {
                        try {
                            categoryListLiveData = fb_Hardware_DB.getCategory(searchCategory!!)!!
                            categoryListLiveData.observe(viewLifecycleOwner) {
                                setUpHardwareList(it)
                            }
                        } catch (exception: Exception) {
                            exception.printStackTrace()

                        }
                    }
                }
                else -> {
                    Log.e("NULL_CONNECTION?", "Error, Connection is showing null. How?")
                }
            }
        }
        // Inflate the layout for this fragment
        return recyclerListBinding.root
    }

    /**
     *
     */
    private fun setUpSearch() {
        //Display the search view for the hardware list
        val searchView = recyclerListBinding.hardwareListSearchViewID
        searchView.visibility = View.VISIBLE
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(this)
    }

    /**
     *
     */
    private fun setUpHardwareList(res: List<SearchedHardwareItem>) {

        val displayHardwareList = recyclerListBinding.recyclerList
        //
        displayHardwareList.layoutManager = LinearLayoutManager(this.context)
        hardwareListAdapter = HardwareListRecyclerList(context, this)

        hardwareListAdapter.setDataList(res)

        displayHardwareList.adapter = hardwareListAdapter
    }

    /**
     *
     */
    private fun searchThroughDatabase(newText: String) {
        coroutineScope.launch {
            try {
                //
                if (newText != "") {
                    categoryListLiveData =
                        fb_Hardware_DB.getCategorySearch(searchCategory!!, newText)!!
                    categoryListLiveData.observe(viewLifecycleOwner) {
                        hardwareListAdapter.setDataList(it)
                    }
                    //
                } else {
                    categoryListLiveData =
                        fb_Hardware_DB.getCategory(searchCategory!!)!!
                    categoryListLiveData.observe(viewLifecycleOwner) {
                        hardwareListAdapter.setDataList(it)
                    }
                }
            } catch (exception: Exception) {
            }
        }
    }

    /**
     *
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.hardwarelist_toolbar_items, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * TO FINISH and to get working
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Bring up a sorting menu
            R.id.sortID -> Log.i("SORT", "Sort")
            // Bring up a filter menu
            R.id.filterID -> Log.i("FILTER", "Filter")
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     *
     */
    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    /**
     *
     */
    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            when (isLoadingFromServer) {
                //
                true -> {
                    if (newText != "") {
                        //
                        searchCategory?.let { apiViewModel.searchCategory(it, newText) }
                        apiViewModel.apiSearchCategoryResponse.observe(
                            viewLifecycleOwner, { res ->
                                res.body()?.let { hardwareListAdapter.setDataList(it) }
                            })
                        //
                    } else {
                        apiViewModel.getCategory(searchCategory)
                        apiViewModel.apiCategoryResponse.observe(
                            viewLifecycleOwner, { res ->
                                res.body()?.let { hardwareListAdapter.setDataList(it) }
                            })
                    }
                }
                //
                false -> {
                    searchThroughDatabase(newText)
                }
            }
        }
        return true
    }

    /**
     *
     */
    override fun onHardwareClick(componentName: String, componentType: String) {
        val nameBundle = bundleOf(
            NAME_KEY to componentName,
            CATEGORY_KEY to componentType,
            LOCAL_OR_NETWORK_KEY to isLoadingFromServer
        )

        //
        val navController = activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
        navController?.navigate(
            R.id.action_hardwareList_fragmentID_to_hardwareDetails_fragmentID,
            nameBundle
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        isLoadingFromServer?.let { onlineSearch ->
            if (!onlineSearch) fb_Hardware_DB.closeDatabase()
        }
    }
}