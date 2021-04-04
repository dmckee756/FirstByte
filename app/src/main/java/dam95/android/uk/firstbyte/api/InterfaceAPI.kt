package dam95.android.uk.firstbyte.api

import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.*
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Path

/**
 * @author David Mckee
 * @Version 1.0
 * This interface supplies retrofit with the needed methods that communicate with the API.
 * Each method sends a query to the API and in response will get a list of json objects which are automatically
 * parsed with gson into the desired return objects.
 * I.e Component child objects, SearchHardwareItem.
 */
interface InterfaceAPI {

    /**
     * Loads and parses a json list of all hardware in display format, or a specific category of hardware.
     * @param type The end of a url command informing the API what type of hardware we want returned.
     * @return A response list of display hardware objects
     */
    @GET("category={type}")
    suspend fun getCategory(@Path("type") type: String?): Response<List<SearchedHardwareItem>>

    /**
     * Loads and parses a json list of all a specific Graphics Card's details.
     * @param name The name of the Graphics Card
     * @return A response of the Graphics Card object and all it's details
     */
    @GET("hardware={name}")
    suspend fun getGpu(@Path("name") name: String?): Response<List<Gpu>>

    /**
     * Loads and parses a json list of all a specific Processor's details.
     * @param name The name of the Processor
     * @return A response of the Processor object and all it's details
     */
    @GET("hardware={name}")
    suspend fun getCpu(@Path("name") name: String?): Response<List<Cpu>>

    /**
     * Loads and parses a json list of a specific Ram details.
     * @param name The name of the Ram
     * @return A response of the Ram object and all it's details
     */
    @GET("hardware={name}")
    suspend fun getRam(@Path("name") name: String?): Response<List<Ram>>

    /**
     * Loads and parses a json list of a specific Power Supply's details.
     * @param name The name of the Power Supply
     * @return A response of thePower Supply object and all it's details
     */
    @GET("hardware={name}")
    suspend fun getPsu(@Path("name") name: String?): Response<List<Psu>>

    /**
     * Loads and parses a json list of a specific Storage Component details.
     * @param name The name of the Storage
     * @return A response of the Storage object and all it's details
     */
    @GET("hardware={name}")
    suspend fun getStorage(@Path("name") name: String?): Response<List<Storage>>

    /**
     * Loads and parses a json list of a specific Motherboard's details.
     * @param name The name of the Motherboard
     * @return A response of the Motherboard object and all it's details
     */
    @GET("hardware={name}")
    suspend fun getMotherboard(@Path("name") name: String?): Response<List<Motherboard>>

    /**
     * Loads and parses a json list of a specific Case's details.
     * @param name The name of the Case
     * @return A response of the Case object and all it's details
     */
    @GET("hardware={name}")
    suspend fun getCase(@Path("name") name: String?): Response<List<Case>>

    /**
     * Loads and parses a json list of a specific Heatsink's details.
     * @param name The name of the Heatsink
     * @return A response of the Heatsink object and all it's details
     */
    @GET("hardware={name}")
    suspend fun getHeatsink(@Path("name") name: String?): Response<List<Heatsink>>

    /**
     * Loads and parses a json list of a specific Fan's details.
     * @param name The name of the Fan
     * @return A response of the Fan object and all it's details
     */
    @GET("hardware={name}")
    suspend fun getFan(@Path("name") name: String?): Response<List<Fan>>
}