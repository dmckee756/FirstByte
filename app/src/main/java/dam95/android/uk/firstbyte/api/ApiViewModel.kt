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
 * @author David Mckee
 * @Version 1.0
 * ViewModel class dedicated to dealing with API commands and retrieving the APIs responses into MutableLiveData variables.
 * Used for displaying loaded data into the "HardwareList" when navigated to from "SearchComponents"
 * @param apiRepository Repository class used to send the commands to the API and return the responses back to this ViewModel.
 */
class ApiViewModel(private val apiRepository: ApiRepository) : ViewModel() {

    //Search Category responses
    val apiCategoryResponse: MutableLiveData<Response<List<SearchedHardwareItem>>> =
        MutableLiveData()

    //Hardware specifications responses
    val apiHardwareResponse: MutableLiveData<Response<List<Component>>> = MutableLiveData()

    /**
     * Sends the desired category that needs to be loaded from the API, this can be a specific category or all components.
     * Assigns the response into apiCategoryResponse's value
     * @param type The end of a url command informing the API what type of hardware we want returned.
     * @throws SocketTimeoutException avoids the app crashing when there is no internet connection.
     */
    @Throws(SocketTimeoutException::class)
    fun getCategory(type: String?) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                //Send the type of hardware we want displayed and store the response
                val response = apiRepository.repoGetCategory(type)
                apiCategoryResponse.value = response
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    /**
     * Check the type of component that is being loaded and load the specific component from the API using it's name.
     * Then have gson automatically parse the json response into the correct object.
     * Assigns the response into apiHardwareResponse's value
     * @param name The name of the component that we want to load from the API.
     * @param type The category of component that we want to load, so that when the API sends a response we can tell gson to parse it to the correct object.
     * @throws SocketTimeoutException avoids the app crashing when there is no internet connection.
     * @suppress UNCHECKED_CAST: "as Response<List<Component>>" All results will be a child object of the Component class.
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(SocketTimeoutException::class)
    fun getHardware(name: String, type: String) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                //Determine the type of component that the result should be loaded into and store it.
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