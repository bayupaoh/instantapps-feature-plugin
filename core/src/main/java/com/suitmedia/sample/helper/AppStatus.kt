package com.suitmedia.sample.helper

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by DODYDMW19 on 6/6/2016.
 */

class AppStatus {

    companion object {
        private lateinit var connectivityManager: ConnectivityManager
        private var connected = false

        fun isOnline(context: Context?): Boolean {
            if(context != null) {
                try {
                    connectivityManager = context
                            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                    val networkInfo = connectivityManager.activeNetworkInfo
                    connected = networkInfo != null && networkInfo.isAvailable &&
                            networkInfo.isConnected
                    return connected


                } catch (e: Exception) {
                    println("CheckConnectivity Exception: " + e.message)
                }
            }

            return connected
        }

    }

}
