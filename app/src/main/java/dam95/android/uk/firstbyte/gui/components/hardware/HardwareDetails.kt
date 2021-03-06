package dam95.android.uk.firstbyte.gui.components.hardware

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dam95.android.uk.firstbyte.databinding.FragmentHardwareDetailsBinding
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