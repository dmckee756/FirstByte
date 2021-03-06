package dam95.android.uk.firstbyte.gui.components.search

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.databinding.RecyclerListBinding
import dam95.android.uk.firstbyte.datasource.api_model.ApiRepository
import dam95.android.uk.firstbyte.datasource.api_model.ApiViewModel
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.*
import retrofit2.Response

/**
 *
 */
private const val CATEGORY_KEY = "CATEGORY"

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
            //MVVM
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
    private fun loadComponentChild(componentType: String): Component? {
        return when (componentType) {
            "gpu" -> Gpu
            "cpu" -> Cpu
            "ram" -> Ram
            "psu" -> Psu
            "storage" -> Storage
            "motherboard" -> Motherboard
            "case" -> Case
            "heatsink" -> Heatsink
            "fan" -> Fan
            else -> null
        }
    }

    companion object {
        /**
         *
         *
         *
         */
        @JvmStatic
        fun newInstance(chosenCategory: Bundle): HardwareList {
            val hardwareList = HardwareList()
            hardwareList.arguments = chosenCategory
            return hardwareList
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
        /*
        val component: Component? = loadComponentChild(componentType)

        if (component != null) {
            val retrofitGet = RetrofitBuild.apiIntegrator.getHardware(componentName)
            //
            retrofitGet.enqueue(object : Callback<List<>?> {
                //
                override fun onResponse(
                    call: Call<List<>?>,
                    response: Response<List<>?>
                ) {
                    val responseBody = response.body()!!
                    setUpHardwareList(responseBody)
                }

                override fun onFailure(call: Call<List<>?>, t: Throwable) {
                    Log.i("FETCH_FAIL", "Error: ${t.message}")
                }
            })

            Log.i("COMPONENT", componentName)
            val categoryBundle = Bundle()
            categoryBundle.putString(COMPONENT_KEY, componentName)
            (activity as HomeActivity).changeFragmentWithArgs(COMPONENT_KEY, categoryBundle)
        } else {
            Log.i("ERROR_HARDWARE", "Category type is Null.")
        }*/
    }
}