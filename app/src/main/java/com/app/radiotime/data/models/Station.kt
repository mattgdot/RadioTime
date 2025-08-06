package com.app.radiotime.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "radio_stations")
data class Station(
    @SerializedName("stationuuid") @PrimaryKey val id: String,
    @ColumnInfo(defaultValue = "") val favicon: String,
    @ColumnInfo(defaultValue = "") val name: String,
    @ColumnInfo(defaultValue = "") val country: String,
    @ColumnInfo(defaultValue = "0") val tags: String,
    @ColumnInfo(defaultValue = "") val countrycode: String,
    @ColumnInfo(defaultValue = "") val url_resolved: String,
    @ColumnInfo(defaultValue = "") val state: String = "",
    @ColumnInfo(defaultValue = "") val homepage: String = "",
    @ColumnInfo(defaultValue = "0") val rank: Int = 0,
)