package dam95.android.uk.firstbyte.datasource.api_model

import dam95.android.uk.firstbyte.api.RetrofitBuildInstance
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.Gpu
import retrofit2.Response

/**
 *
 */
class ApiRepository {

    suspend fun repoGetCategory(type: String?): Response<List<SearchedHardwareItem>> {
        return RetrofitBuildInstance.apiIntegrator.getCategory(type)
    }

    suspend fun repoSearchCategory(type: String?, name: String?): Response<List<SearchedHardwareItem>>{
        return RetrofitBuildInstance.apiIntegrator.searchCategory(type, name)
    }

    suspend fun repoGetGpu(name: String?): Response<Gpu> {
        return RetrofitBuildInstance.apiIntegrator.getGpu(name)
    }
}