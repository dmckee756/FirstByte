package dam95.android.uk.firstbyte.datasource.api

import android.content.Context
import android.util.Log
import dam95.android.uk.firstbyte.api.InterfaceAPI
import dam95.android.uk.firstbyte.api.util.NetworkCheck
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

/**
 * @author David Mckee
 * @Version 1.0
 * A cleaner implementation of converting the json array of hardware components using a gson converter
 * and supplying the base URL, allowing for future commands.
 * This is the test in memory version, no caching is needed.
 */
private const val URL = "https://users.dcs.aber.ac.uk/dam95/MMP/testing/"

class RetrofitBuildInstance(context: Context) {

    //Put the Server's HTTPS url into the retrofit builder when it gets initialised and have gson handle json conversion
    private val retrofitBuild by lazy {
            Retrofit.Builder().baseUrl(URL)
                //Have gson control json parsing automatically
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    //Initialise the retrofitBuilder
    val apiIntegrator: InterfaceAPI by lazy {
        retrofitBuild.create(InterfaceAPI::class.java)
    }
}