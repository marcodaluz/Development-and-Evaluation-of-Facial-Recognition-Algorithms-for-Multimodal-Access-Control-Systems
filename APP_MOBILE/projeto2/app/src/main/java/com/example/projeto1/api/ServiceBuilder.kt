package com.example.projeto1.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object ServiceBuilder {
    private val client = OkHttpClient.Builder().build()
    private val retrofit = Retrofit.Builder() .baseUrl("http://192.168.1.5:5000/") .addConverterFactory(GsonConverterFactory.create()) .client(client)
        .build()
    fun<T> buildService(service: Class<T>): T{ return retrofit.create(service) }
}
