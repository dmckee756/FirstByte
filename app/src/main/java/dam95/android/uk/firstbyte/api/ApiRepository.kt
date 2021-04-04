package dam95.android.uk.firstbyte.api

import android.content.Context
import dam95.android.uk.firstbyte.datasource.api.RetrofitBuildInstance
import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.*
import retrofit2.Response

/**
 * @author David Mckee
 * @Version 1.0
 * This repository class sends the end of a URL to the Retrofit instance, telling the API to execute a specific GET command.
 * It then retrieves the API parsed response from Retrofit and sends it back to the ApiViewModel.
 * @param context used to find the cache directory, and set the size of data that can be cached. (4MB)
 */
class ApiRepository(context: Context) {
    private val retrofitBuildInstance = RetrofitBuildInstance(context)

    /**
     * Loads and parses a json list of all hardware in display format, or a specific category of hardware.
     * @param type The end of a url command informing the API what type of hardware we want returned.
     * @return A response list of display hardware objects
     */
    suspend fun repoGetCategory(type: String?): Response<List<SearchedHardwareItem>> {
        return retrofitBuildInstance.apiIntegrator.getCategory(type)
    }

    /**
     * Loads and parses a json list of all a specific Graphics Card's details.
     * @param name The name of the Graphics Card
     * @return A response of the Graphics Card object and all it's details
     */
    suspend fun repoGetGpu(name: String?): Response<List<Gpu>> {
        return retrofitBuildInstance.apiIntegrator.getGpu(name)
    }

    /**
     * Loads and parses a json list of all a specific Processor's details.
     * @param name The name of the Processor
     * @return A response of the Processor object and all it's details
     */
    suspend fun repoGetCpu(name: String?): Response<List<Cpu>> {
        return retrofitBuildInstance.apiIntegrator.getCpu(name)
    }

    /**
     * Loads and parses a json list of a specific Ram details.
     * @param name The name of the Ram
     * @return A response of the Ram object and all it's details
     */
    suspend fun repoGetRam(name: String?): Response<List<Ram>> {
        return retrofitBuildInstance.apiIntegrator.getRam(name)
    }

    /**
     * Loads and parses a json list of a specific Power Supply's details.
     * @param name The name of the Power Supply
     * @return A response of thePower Supply object and all it's details
     */
    suspend fun repoGetPsu(name: String?): Response<List<Psu>> {
        return retrofitBuildInstance.apiIntegrator.getPsu(name)
    }

    /**
     * Loads and parses a json list of a specific Storage Component details.
     * @param name The name of the Storage
     * @return A response of the Storage object and all it's details
     */
    suspend fun repoGetStorage(name: String?): Response<List<Storage>> {
        return retrofitBuildInstance.apiIntegrator.getStorage(name)
    }

    /**
     * Loads and parses a json list of a specific Motherboard's details.
     * @param name The name of the Motherboard
     * @return A response of the Motherboard object and all it's details
     */
    suspend fun repoGetMotherboard(name: String?): Response<List<Motherboard>> {
        return retrofitBuildInstance.apiIntegrator.getMotherboard(name)
    }

    /**
     * Loads and parses a json list of a specific Case's details.
     * @param name The name of the Case
     * @return A response of the Case object and all it's details
     */
    suspend fun repoGetCase(name: String?): Response<List<Case>> {
        return retrofitBuildInstance.apiIntegrator.getCase(name)
    }

    /**
     * Loads and parses a json list of a specific Heatsink's details.
     * @param name The name of the Heatsink
     * @return A response of the Heatsink object and all it's details
     */
    suspend fun repoGetHeatsink(name: String?): Response<List<Heatsink>> {
        return retrofitBuildInstance.apiIntegrator.getHeatsink(name)
    }

    /**
     * Loads and parses a json list of a specific Fan's details.
     * @param name The name of the Fan
     * @return A response of the Fan object and all it's details
     */
    suspend fun repoGetFan(name: String?): Response<List<Fan>> {
        return retrofitBuildInstance.apiIntegrator.getFan(name)
    }

}