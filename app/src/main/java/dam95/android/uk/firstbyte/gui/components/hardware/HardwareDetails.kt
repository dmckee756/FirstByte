package dam95.android.uk.firstbyte.gui.components.hardware

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dam95.android.uk.firstbyte.api.api_model.ApiRepository
import dam95.android.uk.firstbyte.api.api_model.ApiViewModel
import dam95.android.uk.firstbyte.databinding.FragmentHardwareDetailsBinding
import dam95.android.uk.firstbyte.datasource.ComponentDBHelper
import dam95.android.uk.firstbyte.model.components.Component
import dam95.android.uk.firstbyte.model.components.ComponentsEnum
import java.util.*

/**
 *
 */
private const val NAME_KEY = "NAME"
private const val CATEGORY_KEY = "CATEGORY"
private const val ONLINE_LOAD_KEY = "ONLINE"
private const val OFFLINE_LOAD_KEY = "OFFLINE"

class HardwareDetails : Fragment() {

    private lateinit var hardwareDetailsBinding: FragmentHardwareDetailsBinding
    private lateinit var componentsComponentDB: ComponentDBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hardwareDetailsBinding = FragmentHardwareDetailsBinding.inflate(inflater, container, false)
        val componentName = arguments?.getString(NAME_KEY)
        val componentType = arguments?.getString(CATEGORY_KEY)

        if (componentName != null && componentType != null) {
            componentsComponentDB = ComponentDBHelper(requireContext())

            Log.i("SEARCH_CATEGORY", componentName)
            //Check which fragment this was navigated from.
            val onlineLoad = arguments?.getString(ONLINE_LOAD_KEY)
            val offlineLoad = arguments?.getString(OFFLINE_LOAD_KEY)

            //Determine if offline load from Room databases or online.
            when {
                onlineLoad != null -> {
                    streamInHardware(componentName, componentType)
                }
                offlineLoad != null -> {
                    loadSavedHardware(componentName, componentType)
                }
                else -> {
                    Log.i(
                        "NULL_CATEGORY",
                        "Error, null category imported into HardwareDetails. How?"
                    )
                }
            }
        }
        // Inflate the layout for this fragment
        return hardwareDetailsBinding.root
    }

    /**
     *
     */
    private fun streamInHardware(name: String, type: String) {
        val apiRepository = ApiRepository()
        val apiViewModel = ApiViewModel(apiRepository)

        val component: Component
        apiViewModel.getHardware(name, type)
        when (type.toUpperCase(Locale.ROOT)) {
            ComponentsEnum.GPU.toString() -> apiViewModel.apiGpuResponse
            ComponentsEnum.CPU.toString() -> apiViewModel.apiCpuResponse
            ComponentsEnum.RAM.toString() -> apiViewModel.apiRamResponse
            ComponentsEnum.PSU.toString() -> apiViewModel.apiPsuResponse
            ComponentsEnum.STORAGE.toString() -> apiViewModel.apiStorageResponse
            ComponentsEnum.MOTHERBOARD.toString() -> apiViewModel.apiMotherboardResponse
            ComponentsEnum.CASES.toString() -> apiViewModel.apiCaseResponse
            ComponentsEnum.HEATSINK.toString() -> apiViewModel.apiHeatsinkResponse
            ComponentsEnum.FAN.toString() -> apiViewModel.apiFanResponse
            else -> null
        }
            ?.observe(viewLifecycleOwner, { res ->
            if (res.isSuccessful) {
                //ConvertImageURL()
                hardwareDetailsBinding.tempDisplayHardwareSpecs.text = res.body().toString()
                setUpButtons(name, type, res)
            } else {
                Log.i("HARDWARE_RES_FAIL", "FAILED LOAD")
            }
        })!!
    }

    /**
     *
     */
    private fun loadSavedHardware(name: String, type: String) {

    }

    private fun setUpButtons(name: String, type: String, res: Any) {
       /* val addHardware = hardwareDetailsBinding.addHardwareBtn
        val removeHardware = hardwareDetailsBinding.removeHardwareBtn*/
/*
        if (componentsViewModel.hardwareExists(name, type)){
            addHardware.isClickable = false
        } else {
            removeHardware.isClickable = false
        }


        addHardware.setOnClickListener {
        }
        removeHardware.setOnClickListener {

        }*/
    }

}
