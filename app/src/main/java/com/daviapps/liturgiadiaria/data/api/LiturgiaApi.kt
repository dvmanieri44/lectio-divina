package com.daviapps.liturgiadiaria.data.api

import com.daviapps.liturgiadiaria.data.model.LiturgiaResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface LiturgiaApi {
    @GET("v2/")
    suspend fun getLiturgiaHoje(): LiturgiaResponse
}

object RetrofitClient {
    val api: LiturgiaApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://liturgia.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LiturgiaApi::class.java)
    }
}
