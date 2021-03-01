package dam95.android.uk.firstbyte.api

import dam95.android.uk.firstbyte.model.SearchedHardwareItem
import dam95.android.uk.firstbyte.model.components.Component
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Path

/**
 *
 */
interface InterfaceAPI {

    /**
     *
     */
    @GET("category={type}")
    fun getCategory(@Path("type") type: String?): Call<List<SearchedHardwareItem>>

    /**
     *
     */
    @GET("hardware={name}")
    fun getHardware(@Path("name") name: String?): Call<List<String>>
}