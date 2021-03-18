package dam95.android.uk.firstbyte.api.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build

object NetworkCheck {

    /**
     * Check if the android app is connected to the internet.
     *
     * CODE REFERENCE:
     * WEBSITE: Stack Overflow
     * AUTHOR: Ali Shatergholi
     * TOPIC: "NetworkInfo has been deprecated by API 29 duplicate"
     * DATE: 31/07/2019
     * ACCESSED DATE: 12/03/2021
     * AVAILABLE ONLINE: https://stackoverflow.com/questions/57284582/networkinfo-has-been-deprecated-by-api-29
     *
     * NOTE: I have refactored the code to be more human readable.
     */
    @Suppress("DEPRECATION")
    fun isConnectedToServer(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        //If the current android SDK is greater than Marshmallow 23, then check for a connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val network: Network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities: NetworkCapabilities =
                connectivityManager.getNetworkCapabilities(network) ?: return false
            //Determine if there is any type of internet connection, otherwise return false.
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                //for a developer machine
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            //Return statements that are deprecated to check connection
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }
}