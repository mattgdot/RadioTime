package com.app.radiotime.data.api.stations

import com.app.radiotime.data.models.Station
import retrofit2.http.GET
import retrofit2.http.Path

interface StationsInterface {
    @GET("data/{country}.json")
    suspend fun getStations(
        @Path("country") country: String,
    ): List<Station>

    @GET("version.txt")
    suspend fun getVersion(): String
}