package com.app.radiotime.data.repositories

import android.app.Application
import com.app.radiotime.data.database.AppDatabase
import com.app.radiotime.data.database.EntityDao
import com.app.radiotime.data.models.Favorite
import com.app.radiotime.data.models.Station

class DatabaseRepository(application: Application) {

    private val entityDao: EntityDao

    init {
        val db = AppDatabase.getInstance(application)
        entityDao = db.radioStationDao()
    }

    suspend fun getAllStations(countryCode: String): List<Station> {
        return entityDao.getStations(countryCode)
    }

    suspend fun insertStations(radioStations: List<Station>) {
        entityDao.insertStation(radioStations)
    }

    suspend fun getRadioStationByID(id:String):Station? {
        return entityDao.getStationById(id)
    }

    suspend fun getRadioStationByName(name:String):List<Station> {
        return entityDao.getStationByName(name)
    }

    suspend fun getRadioStationByNameAndCountry(name:String, countryCode: String):List<Station> {
        return entityDao.getStationByNameAndCountry(name,countryCode)
    }

    suspend fun insertFavoriteItem(favoriteStation: Favorite) {
        entityDao.insertFavoriteItem(favoriteStation)
    }

    suspend fun getFavoriteStations(): Map<Station, Favorite> {
        return entityDao.getFavoriteStations()
    }

    suspend fun getFavoriteItemById(id:String):Favorite? {
        return entityDao.getFavoriteStationById(id)
    }

    suspend fun deleteFavoriteItem(item:Favorite) {
        entityDao.deleteFavoriteStation(item)
    }

    suspend fun updateFavoriteItem(item:Favorite){
        entityDao.updateFavoriteItem(item)
    }
}