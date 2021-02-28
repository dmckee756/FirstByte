package dam95.android.uk.firstbyte.gui.components.hardware

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import dam95.android.uk.firstbyte.R
import dam95.android.uk.firstbyte.api.ConvertImageURL
import dam95.android.uk.firstbyte.api.RetrofitBuild
import dam95.android.uk.firstbyte.databinding.RecyclerListBinding
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 *
 */
private const val CATEGORY_KEY = "CATEGORY"
class HardwareList : Fragment(), HardwareListRecyclerList.OnItemClickListener {

    private lateinit var recyclerListBinding: RecyclerListBinding

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recyclerListBinding = RecyclerListBinding.inflate(inflater, container, false)
        val searchCategory = arguments?.getString(CATEGORY_KEY)

        if (searchCategory != null) {
            Log.i("SEARCH_CATEGORY", searchCategory)
        }

        //
        val retrofitGet = RetrofitBuild.apiIntegrator.getCategory()
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
         */
        @JvmStatic
        fun newInstance(chosenCategory: Bundle): HardwareList {
            val hardwareList = HardwareList()
            hardwareList.arguments = chosenCategory
            return hardwareList
        }
    }

    override fun onButtonClick() {
        TODO("Not yet implemented")
    }
}