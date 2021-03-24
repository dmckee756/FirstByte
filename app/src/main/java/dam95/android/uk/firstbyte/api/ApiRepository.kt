package dam95.android.uk.firstbyte.api

import android.content.Context
import dam95.android.uk.firstbyte.datasource.api.RetrofitBuildInstance
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.*
import retrofit2.Response

/**
 *
 */
class ApiRepository(context: Context) {
    private val retrofitBuildInstance = RetrofitBuildInstance(context)

    /**
     *
     */
    suspend fun repoGetCategory(type: String?): Response<List<SearchedHardwareItem>> {
        return retrofitBuildInstance.apiIntegrator.getCategory(type)
    }

    /**
     *
     */
    suspend fun repoSearchCategory(type: String?, name: String?): Response<List<SearchedHardwareItem>>{
        return retrofitBuildInstance.apiIntegrator.searchCategory(type, name)
    }

    /**
     *
     */
    suspend fun repoGetGpu(name: String?): Response<List<Gpu>> {
        return retrofitBuildInstance.apiIntegrator.getGpu(name)
    }

    /**
     *
     */
    suspend fun repoGetCpu(name: String?): Response<List<Cpu>> {
        return retrofitBuildInstance.apiIntegrator.getCpu(name)
    }

    /**
     *
     */
    suspend fun repoGetRam(name: String?): Response<List<Ram>> {
        return retrofitBuildInstance.apiIntegrator.getRam(name)
    }

    /**
     *
     */
    suspend fun repoGetPsu(name: String?): Response<List<Psu>> {
        return retrofitBuildInstance.apiIntegrator.getPsu(name)
    }

    /**
     *
     */
    suspend fun repoGetStorage(name: String?): Response<List<Storage>> {
        return retrofitBuildInstance.apiIntegrator.getStorage(name)
    }

    /**
     *
     */
    suspend fun repoGetMotherboard(name: String?): Response<List<Motherboard>> {
        return retrofitBuildInstance.apiIntegrator.getMotherboard(name)
    }

    /**
     *
     */
    suspend fun repoGetCase(name: String?): Response<List<Case>> {
        return retrofitBuildInstance.apiIntegrator.getCase(name)
    }

    /**
     *
     */
    suspend fun repoGetHeatsink(name: String?): Response<List<Heatsink>> {
        return retrofitBuildInstance.apiIntegrator.getHeatsink(name)
    }

    /**
     *
     */
    suspend fun repoGetFan(name: String?): Response<List<Fan>> {
        return retrofitBuildInstance.apiIntegrator.getFan(name)
    }

}