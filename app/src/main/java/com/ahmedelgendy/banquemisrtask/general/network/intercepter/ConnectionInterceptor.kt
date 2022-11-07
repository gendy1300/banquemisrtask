package com.ahmedelgendy.banquemisrtask.general.network.intercepter

import android.content.Context
import android.net.ConnectivityManager
import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Response

class ConnectionInterceptor(private val mContext: Context) :
    Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (!isConnected) {
            throw NoConnectivityException()

            // Throwing our custom exception 'NoConnectivityException'
        }

        return response
    }

    val isConnected: Boolean
        get() {
            val connectivityManager =
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }
}
