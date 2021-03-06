package dam95.android.uk.firstbyte.datasource.api_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.Component
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 *
 */
class ApiViewModel(private val apiRepository: ApiRepository) : ViewModel() {

    val apiCategoryResponse: MutableLiveData<Response<List<SearchedHardwareItem>>> =
        MutableLiveData()
    val apiSearchCategoryResponse: MutableLiveData<Response<List<SearchedHardwareItem>>> =
        MutableLiveData()
    val apiHardwareResponse: MutableLiveData<Response<Component>> = MutableLiveData()

    fun getCategory(type: String?) {
        viewModelScope.launch {
            val response = apiRepository.repoGetCategory(type)
            apiCategoryResponse.value = response
        }
    }

    fun searchCategory(type: String, name: String) {
        viewModelScope.launch {
            val response = apiRepository.repoSearchCategory(type, name)
            apiSearchCategoryResponse.value = response
        }
    }

    fun getHardware(name: String, type: String) {
        viewModelScope.launch {
            loadCorrectComponent(type)
            //val response = apiHardwareResponse.//addfuntion
        }
    }

    //Return function
    private fun loadCorrectComponent(type: String) {


    }
}