package com.vadim.hasdfa.musicwave.repositories

import com.google.gson.GsonBuilder
import com.vadim.hasdfa.musicwave.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

object Apifactory {

    val client = OkHttpClient().newBuilder().build()
    /*
    .addInterceptor { chain ->
        val originalRequest = chain.request()

        val url = originalRequest.url().newBuilder()
        url.addQueryParameter("api_key", Constants.API_KEY)
        url.addQueryParameter("format", "json")

        val newRequest = originalRequest.newBuilder().url(url.build()).build()
        return@addInterceptor chain.proceed(newRequest)
    }
    */

   val gson = GsonBuilder()
        .setLenient()
        .create()

//   val retrofit: Retrofit = Retrofit.Builder()
//        .baseUrl(Constants.BASE_URL)
//        .client(client)
//        .build()
}