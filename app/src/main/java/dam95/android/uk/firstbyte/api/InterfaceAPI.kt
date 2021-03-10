package dam95.android.uk.firstbyte.api

import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.*
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Path

/**
 *
 */
interface InterfaceAPI {

    @GET("category={type}")
    suspend fun getCategory(@Path("type") type: String?): Response<List<SearchedHardwareItem>>

    @GET("category={type}&search={name}")
    suspend fun searchCategory(@Path("type") type: String?, @Path("name") name: String?): Response<List<SearchedHardwareItem>>

    @GET("hardware={name}")
    suspend fun getGpu(@Path("name") name: String?): Response<List<Gpu>>

    @GET("hardware={name}")
    suspend fun getCpu(@Path("name") name: String?): Response<List<Cpu>>

    @GET("hardware={name}")
    suspend fun getRam(@Path("name") name: String?): Response<List<Ram>>

    @GET("hardware={name}")
    suspend fun getPsu(@Path("name") name: String?): Response<List<Psu>>

    @GET("hardware={name}")
    suspend fun getStorage(@Path("name") name: String?): Response<List<Storage>>

    @GET("hardware={name}")
    suspend fun getMotherboard(@Path("name") name: String?): Response<List<Motherboard>>

    @GET("hardware={name}")
    suspend fun getCase(@Path("name") name: String?): Response<List<Case>>

    @GET("hardware={name}")
    suspend fun getHeatsink(@Path("name") name: String?): Response<List<Heatsink>>

    @GET("hardware={name}")
    suspend fun getFan(@Path("name") name: String?): Response<List<Fan>>
}