package dam95.android.uk.firstbyte.api.api_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.*
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.*

/**
 *
 */
class ApiViewModel(private val apiRepository: ApiRepository) : ViewModel() {

    //Search Category responses
    val apiCategoryResponse: MutableLiveData<Response<List<SearchedHardwareItem>>> =
        MutableLiveData()
    val apiSearchCategoryResponse: MutableLiveData<Response<List<SearchedHardwareItem>>> =
        MutableLiveData()

    //Hardware specifications responses
    val apiGpuResponse: MutableLiveData<Response<List<Gpu>>> = MutableLiveData()
    val apiCpuResponse: MutableLiveData<Response<List<Cpu>>> = MutableLiveData()
    val apiRamResponse: MutableLiveData<Response<List<Ram>>> = MutableLiveData()
    val apiPsuResponse: MutableLiveData<Response<List<Psu>>> = MutableLiveData()
    val apiStorageResponse: MutableLiveData<Response<List<Storage>>> = MutableLiveData()
    val apiMotherboardResponse: MutableLiveData<Response<List<Motherboard>>> = MutableLiveData()
    val apiCaseResponse: MutableLiveData<Response<List<Case>>> = MutableLiveData()
    val apiHeatsinkResponse: MutableLiveData<Response<List<Heatsink>>> = MutableLiveData()
    val apiFanResponse: MutableLiveData<Response<List<Fan>>> = MutableLiveData()

    /**
     *
     */
    fun getCategory(type: String?) {
        viewModelScope.launch {
            val response = apiRepository.repoGetCategory(type)
            apiCategoryResponse.value = response
        }
    }

    /**
     *
     */
    fun searchCategory(type: String, name: String) {
        viewModelScope.launch {
            val response = apiRepository.repoSearchCategory(type, name)
            apiSearchCategoryResponse.value = response
        }
    }

    /**
     *
     */
    fun getHardware(name: String, type: String) {
        viewModelScope.launch {
            when (type.toUpperCase(Locale.ROOT)) {
                ComponentsEnum.GPU.toString() -> apiGpuResponse.value = apiRepository.repoGetGpu(name)
                ComponentsEnum.CPU.toString() -> apiCpuResponse.value = apiRepository.repoGetCpu(name)
                ComponentsEnum.RAM.toString() -> apiRamResponse.value = apiRepository.repoGetRam(name)
                ComponentsEnum.PSU.toString() -> apiPsuResponse.value = apiRepository.repoGetPsu(name)
                ComponentsEnum.STORAGE.toString() -> apiStorageResponse.value = apiRepository.repoGetStorage(name)
                ComponentsEnum.MOTHERBOARD.toString() -> apiMotherboardResponse.value = apiRepository.repoGetMotherboard(name)
                ComponentsEnum.CASES.toString() -> apiCaseResponse.value = apiRepository.repoGetCase(name)
                ComponentsEnum.HEATSINK.toString() -> apiHeatsinkResponse.value = apiRepository.repoGetHeatsink(name)
                ComponentsEnum.FAN.toString() -> apiFanResponse.value = apiRepository.repoGetFan(name)
            }
        }
    }
}