package com.app.radiotime.data.api.location

import com.app.radiotime.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LocationClient {
    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(Constants.LOCATION_API_URL).addConverterFactory(
            GsonConverterFactory.create()
        ).build()
    }
}