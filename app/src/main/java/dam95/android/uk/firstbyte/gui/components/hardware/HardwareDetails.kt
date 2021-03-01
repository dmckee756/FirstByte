package dam95.android.uk.firstbyte.gui.components.hardware

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dam95.android.uk.firstbyte.api.RetrofitBuild
import dam95.android.uk.firstbyte.databinding.FragmentHardwareDetailsBinding
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.Component
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 *
 */
private const val COMPONENT_KEY = "HARDWARE_DETAILS"
class HardwareDetails : Fragment() {

    private lateinit var hardwareDetailsBinding: FragmentHardwareDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hardwareDetailsBinding = FragmentHardwareDetailsBinding.inflate(inflater, container, false)
        val componentName = arguments?.getString(COMPONENT_KEY)
        //REWORK
        if (componentName != null) {

            Log.i("SEARCH_CATEGORY", componentName)

            //REMOVE/REWORK and change back from string to component... maybe pass in category to get correct Component object... NOTE ME
            val retrofitGet = RetrofitBuild.apiIntegrator.getHardware(componentName)
            //
            retrofitGet.enqueue(object : Callback<List<String>?> {
                //
                override fun onResponse(
                    call: Call<List<String>?>,
                    response: Response<List<String>?>
                ) {
                    val responseBody = response.body()!!
                    hardwareDetailsBinding.tempDisplayHardwareSpecs.text = responseBody.toString()
                }
                //
                override fun onFailure(call: Call<List<String>?>, t: Throwable) {
                    Log.i("FETCH_FAIL", "Error: ${t.message}")
                }
            })

        } else {
            Log.i("SEARCH_FAILED", "Error: Cannot search")
        }
        // Inflate the layout for this fragment
        return hardwareDetailsBinding.root
    }

    companion object {
        /**
         *
         *
         *
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(chosenComponent: Bundle): HardwareDetails {
            val hardwareDetails = HardwareDetails()
            hardwareDetails.arguments = chosenComponent
            return hardwareDetails
        }
    }

}