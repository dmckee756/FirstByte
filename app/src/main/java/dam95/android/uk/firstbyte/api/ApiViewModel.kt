package dam95.android.uk.firstbyte.api

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.*
import dam95.android.uk.firstbyte.model.util.ComponentsEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException
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
    val apiHardwareResponse: MutableLiveData<Response<List<Component>>> = MutableLiveData()

    /**
     *
     */
    @Throws(SocketTimeoutException::class)
    fun getCategory(type: String?) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = apiRepository.repoGetCategory(type)
                apiCategoryResponse.value = response
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    /**
     *
     */
    @Throws(SocketTimeoutException::class)
    fun searchCategory(type: String, name: String) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = apiRepository.repoSearchCategory(type, name)
                apiSearchCategoryResponse.value = response
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    /**
     *
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(SocketTimeoutException::class)
    fun getHardware(name: String, type: String) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                apiHardwareResponse.value = (when (type.toUpperCase(Locale.ROOT)) {
                    ComponentsEnum.GPU.toString() -> apiRepository.repoGetGpu(name)
                    ComponentsEnum.CPU.toString() -> apiRepository.repoGetCpu(name)
                    ComponentsEnum.RAM.toString() -> apiRepository.repoGetRam(name)
                    ComponentsEnum.PSU.toString() -> apiRepository.repoGetPsu(name)
                    ComponentsEnum.STORAGE.toString() -> apiRepository.repoGetStorage(name)
                    ComponentsEnum.MOTHERBOARD.toString() -> apiRepository.repoGetMotherboard(name)
                    ComponentsEnum.CASES.toString() -> apiRepository.repoGetCase(name)
                    ComponentsEnum.HEATSINK.toString() -> apiRepository.repoGetHeatsink(name)
                    ComponentsEnum.FAN.toString() -> apiRepository.repoGetFan(name)
                    else -> null
                }) as Response<List<Component>>
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }
}