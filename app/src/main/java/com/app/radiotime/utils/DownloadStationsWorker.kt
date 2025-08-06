package com.app.radiotime.utils

import android.app.Application
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.radiotime.data.api.stations.StationsClient
import com.app.radiotime.data.api.stations.StationsInterface
import com.app.radiotime.data.repositories.DatabaseRepository
import com.app.radiotime.data.repositories.SharedPreferencesRepository
import com.app.radiotime.utils.Constants.COUNTRY_CODE
import com.app.radiotime.utils.Constants.SHARED_PREF

class DownloadStationsWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val dbRepository: DatabaseRepository =
        DatabaseRepository(applicationContext as Application)

    private val repository = SharedPreferencesRepository(
        applicationContext.getSharedPreferences(
            SHARED_PREF,
            Context.MODE_PRIVATE
        )
    )

    override suspend fun doWork(): Result {
        val retrofit = StationsClient.getInstance()
        val apiInterface: StationsInterface = retrofit.create(StationsInterface::class.java)

        return try {
            val countryCode = inputData.getString(COUNTRY_CODE)?.lowercase()
            val dbVersion = apiInterface.getVersion()
            repository.setDBVersion(dbVersion.toInt())
            val radioStations = apiInterface.getStations(countryCode.toString())
            dbRepository.insertStations(radioStations.reversed())
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}