package dam95.android.uk.firstbyte.api.api_model

import dam95.android.uk.firstbyte.api.RetrofitBuildInstance
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.*
import retrofit2.Response

/**
 *
 */
class ApiRepository {

    /**
     *
     */
    suspend fun repoGetCategory(type: String?): Response<List<SearchedHardwareItem>> {
        return RetrofitBuildInstance.apiIntegrator.getCategory(type)
    }

    /**
     *
     */
    suspend fun repoSearchCategory(type: String?, name: String?): Response<List<SearchedHardwareItem>>{
        return RetrofitBuildInstance.apiIntegrator.searchCategory(type, name)
    }

    /**
     *
     */
    suspend fun repoGetGpu(name: String?): Response<List<Gpu>> {
        return RetrofitBuildInstance.apiIntegrator.getGpu(name)
    }

    /**
     *
     */
    suspend fun repoGetCpu(name: String?): Response<List<Cpu>> {
        return RetrofitBuildInstance.apiIntegrator.getCpu(name)
    }

    /**
     *
     */
    suspend fun repoGetRam(name: String?): Response<List<Ram>> {
        return RetrofitBuildInstance.apiIntegrator.getRam(name)
    }

    /**
     *
     */
    suspend fun repoGetPsu(name: String?): Response<List<Psu>> {
        return RetrofitBuildInstance.apiIntegrator.getPsu(name)
    }

    /**
     *
     */
    suspend fun repoGetStorage(name: String?): Response<List<Storage>> {
        return RetrofitBuildInstance.apiIntegrator.getStorage(name)
    }

    /**
     *
     */
    suspend fun repoGetMotherboard(name: String?): Response<List<Motherboard>> {
        return RetrofitBuildInstance.apiIntegrator.getMotherboard(name)
    }

    /**
     *
     */
    suspend fun repoGetCase(name: String?): Response<List<Case>> {
        return RetrofitBuildInstance.apiIntegrator.getCase(name)
    }

    /**
     *
     */
    suspend fun repoGetHeatsink(name: String?): Response<List<Heatsink>> {
        return RetrofitBuildInstance.apiIntegrator.getHeatsink(name)
    }

    /**
     *
     */
    suspend fun repoGetFan(name: String?): Response<List<Fan>> {
        return RetrofitBuildInstance.apiIntegrator.getFan(name)
    }

}