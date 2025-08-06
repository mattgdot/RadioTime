package com.app.radiotime.data.api.location

import com.app.radiotime.data.models.Location
import retrofit2.http.GET

interface LocationInterface {
    @GET("json")
    suspend fun getIpInfo(): Location
}