package dam95.android.uk.firstbyte.gui.components.search

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.api.RetrofitBuild
import dam95.android.uk.firstbyte.databinding.RecyclerListBinding
import dam95.android.uk.firstbyte.gui.mainactivity.HomeActivity
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 *
 */
private const val CATEGORY_KEY = "CATEGORY"
private const val COMPONENT_KEY = "HARDWARE_DETAILS"
class HardwareList : Fragment(), HardwareListRecyclerList.OnItemClickListener {

    private lateinit var recyclerListBinding: RecyclerListBinding

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val searchCategory = arguments?.getString(CATEGORY_KEY)
        recyclerListBinding = RecyclerListBinding.inflate(inflater, container, false)
        if (searchCategory != null) {
            Log.i("SEARCH_CATEGORY", searchCategory)

            //
            val retrofitGet = RetrofitBuild.apiIntegrator.getCategory(searchCategory)
            //
            retrofitGet.enqueue(object : Callback<List<SearchedHardwareItem>?> {
                //
                override fun onResponse(
                    call: Call<List<SearchedHardwareItem>?>,
                    response: Response<List<SearchedHardwareItem>?>
                ) {
                    val responseBody = response.body()!!
                    setUpHardwareList(responseBody)
                }
                override fun onFailure(call: Call<List<SearchedHardwareItem>?>, t: Throwable) {
                    Log.i("FETCH_FAIL", "Error: ${t.message}")
                }
            })
        } else {
            Log.i("SEARCH_FAILED", "Error: Cannot search")
        }

        // Inflate the layout for this fragment
        return recyclerListBinding.root
    }

    /**
     *
     */
    private fun setUpHardwareList(hardwareFullList: List<SearchedHardwareItem>) {

        val displayHardwareList = recyclerListBinding.recyclerList
        //
        displayHardwareList.layoutManager = LinearLayoutManager(this.context)
        displayHardwareList.adapter = HardwareListRecyclerList(context, this, hardwareFullList)

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

    private fun loadComponentChild(componentType: String): Component?{
        return when(componentType){
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
}