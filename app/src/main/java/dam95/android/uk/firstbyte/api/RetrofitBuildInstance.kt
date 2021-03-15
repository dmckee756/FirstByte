package dam95.android.uk.firstbyte.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

/**
 * A cleaner implementation of converting the json array of hardware components using a gson converter
 * and supplying the base URL, allowing for future commands.
 */
private const val URL = "https://users.dcs.aber.ac.uk/dam95/MMP/components/"

class RetrofitBuildInstance(context: Context) {

    //Cache of 5MB of data
    private val cacheSize: Long = (4 * 1024 * 1024)

    //Allocate size to Cache Directory
    private val cacheLocation = okhttp3.Cache(context.cacheDir, cacheSize)

    //Put the Server's HTTPS url into the retrofit builder when it gets initialised and have gson handle json conversion
    private val retrofitBuild by lazy {
            Retrofit.Builder().baseUrl(URL)
                //Have gson control json parsing automatically
                .addConverterFactory(GsonConverterFactory.create())
                //apply OkHttp3 caching
                .client(buildCachingSystem(context))
                .build()
    }

    //Initialise the retrofitBuilder
    val apiIntegrator: InterfaceAPI by lazy {
        retrofitBuild.create(InterfaceAPI::class.java)
    }

    /**
     * Builds a retrofit caching system that will save cached items for up to 30 minutes if online,
     * or if offline will store cache data for up to 1 week.
     */
    @Throws(SocketTimeoutException::class)
    fun buildCachingSystem(context: Context): OkHttpClient {
        //Send all cache to the designated cache location in context.cacheDir
        //this is attached to retrofit

        return OkHttpClient.Builder().cache(cacheLocation).connectTimeout(10, TimeUnit.SECONDS)
            .addInterceptor {
                var request = it.request()
                try {
                    //If online use cache from up to 1 hour ago
                    val cacheController: CacheControl = if (NetworkCheck.isConnectedToServer(context)) {
                        Log.i("ONLINE_CACHE","")
                        CacheControl.Builder().maxStale(30, TimeUnit.MINUTES).build()
                        //If offline use cache from up to 5 days ago
                    } else {
                        Log.i("OFFLINE_CACHE","")
                        CacheControl.Builder().maxStale(7, TimeUnit.DAYS).build()
                    }
                    request = request.newBuilder().header("Cache-Control", "public")
                        .cacheControl(cacheController).build()
                } catch (exception: Exception) {
                    //Catch the exception so the app doesn't crash whilst offline
                    exception.printStackTrace()

                }
                it.proceed(request)
            }.build()
    }
}