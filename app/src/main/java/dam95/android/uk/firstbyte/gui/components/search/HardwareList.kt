package dam95.android.uk.firstbyte.gui.components.search

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.RecyclerListBinding
import dam95.android.uk.firstbyte.api.api_model.ApiRepository
import dam95.android.uk.firstbyte.api.api_model.ApiViewModel
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import retrofit2.Response

/**
 *
 */
private const val CATEGORY_KEY = "CATEGORY"
private const val NAME_KEY = "NAME"
private const val ONLINE_LOAD_KEY = "ONLINE"
class HardwareList : Fragment(), HardwareListRecyclerList.OnItemClickListener,
    SearchView.OnQueryTextListener {

    private lateinit var recyclerListBinding: RecyclerListBinding
    private lateinit var apiViewModel: ApiViewModel

    private lateinit var hardwareListAdapter: HardwareListRecyclerList

    private var searchCategory: String? = null

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchCategory = arguments?.getString(CATEGORY_KEY)
        recyclerListBinding = RecyclerListBinding.inflate(inflater, container, false)
        if (searchCategory != null) {
            Log.i("SEARCH_CATEGORY", searchCategory!!)

            val apiRepository = ApiRepository()
            apiViewModel = ApiViewModel(apiRepository)

            setUpSearch()

            //Load the values streamed from the api into a mutable live data list in the "apiRepository".
            apiViewModel.getCategory(searchCategory)
            //Observe the loaded displayDetails from the apiRepository
            apiViewModel.apiCategoryResponse.observe(viewLifecycleOwner, Observer { res ->
                setUpHardwareList(res)
            })
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
    private fun setUpHardwareList(res: Response<List<SearchedHardwareItem>>) {

        val displayHardwareList = recyclerListBinding.recyclerList
        //
        displayHardwareList.layoutManager = LinearLayoutManager(this.context)
        hardwareListAdapter = HardwareListRecyclerList(context, this)

        assignListToRecycler(res)

        displayHardwareList.adapter = hardwareListAdapter
    }

    /**
     *
     */
    private fun assignListToRecycler(res: Response<List<SearchedHardwareItem>>) {
        //if res is successful, load the retrieved hardware list data into the recycler adapter.
        if (res.isSuccessful) {
            res.body()?.let { hardwareListAdapter.setDataList(it) }
            //Otherwise, throw an error in the log for the developer to read.
        } else {
            Log.i("FAILED_RESPONSE", res.errorBody().toString())
            //Toast.makeText(activity?.applicationContext, res.code(), Toast.LENGTH_SHORT).show()
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
            if (newText != "") {
                searchCategory?.let { apiViewModel.searchCategory(it, newText) }
                apiViewModel.apiSearchCategoryResponse.observe(
                    viewLifecycleOwner, { res ->
                        assignListToRecycler(res)
                    })
            } else {
                apiViewModel.getCategory(searchCategory)
                apiViewModel.apiCategoryResponse.observe(viewLifecycleOwner, Observer { res ->
                    assignListToRecycler(res)
                })
            }
        }
        return true
    }

    /**
     *
     */
    override fun onHardwareClick(componentName: String, componentType: String) {

        val nameBundle = bundleOf(NAME_KEY to componentName, CATEGORY_KEY to componentType, ONLINE_LOAD_KEY to ONLINE_LOAD_KEY)

        //
        val navController = activity?.let { Navigation.findNavController(it, R.id.nav_fragment) }
        navController?.navigate(R.id.action_hardwareList_fragmentID_to_hardwareDetails_fragmentID, nameBundle)
    }
}