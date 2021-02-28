package dam95.android.uk.firstbyte.api

import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.Component
import retrofit2.http.GET
import retrofit2.Call

/**
 *
 */
interface InterfaceAPI {


    @GET("category=all")
    fun getCategory(): Call<List<SearchedHardwareItem>>

    @GET("hardware=")
    suspend fun getHardware(): Call<List<Component>>
}