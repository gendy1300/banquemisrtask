package com.ahmedelgendy.banquemisrtask.general.network

import android.content.Context
import android.util.Log
import com.ahmedelgendy.banquemisrtask.BuildConfig
import com.ahmedelgendy.banquemisrtask.R
import com.ahmedelgendy.banquemisrtask.general.network.intercepter.ConnectionInterceptor
import com.google.gson.GsonBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RetrofitImplementation @Inject constructor(
    @ApplicationContext val context: Context
) {




    @Throws(IOException::class)
    fun <Api> buildApi(api: Class<Api>): Api {

        val gson = GsonBuilder()
            .setLenient()
            .disableHtmlEscaping()
            .create()

        return Retrofit.Builder()
            .baseUrl(context.getString(R.string.baseUrl))
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(ConnectionInterceptor(context))
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor { chain ->

                        /**
                         * will be using the api key as a string instead of saving it into the local.properties
                         * so anyone who is wanting to test the can run it without any problems
                         */

//                        val apikey = BuildConfig.ApiKey


                        val newRequest = chain.request().newBuilder().addHeader(
                            "apikey",
                            context.getString(R.string.apikey)
                        ).build()

                        val request = chain.proceed(newRequest)

                        if (request.code == 401) {
                            Log.d("network", "${request.message}  need to change api key")
                        }

                        request
                    }
                    .also { client ->
                        if (BuildConfig.DEBUG) {
                            val logging = HttpLoggingInterceptor()
                            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                            client.addInterceptor(logging)
                        }
                    }
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(api)
    }


}
