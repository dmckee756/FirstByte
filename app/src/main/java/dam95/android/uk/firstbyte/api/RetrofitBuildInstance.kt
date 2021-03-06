package dam95.android.uk.firstbyte.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A cleaner implementation of converting the json array of hardware components using a gson converter
 * and supplying the base URL, allowing for future commands.
 */
object RetrofitBuildInstance {

    private const val URL = "https://users.dcs.aber.ac.uk/dam95/MMP/components/"

    //Put the Server's HTTPS url into the retrofit builder when it gets initialised and have gson handle json conversion
    private val retrofitBuild by lazy {
        Retrofit.Builder().baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //Initialise the retrofitBuilder
    val apiIntegrator: InterfaceAPI by lazy {
        retrofitBuild.create(InterfaceAPI::class.java)
    }

}