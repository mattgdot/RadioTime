package com.app.radiotime.media

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.app.radiotime.data.api.stations.StationsClient
import com.app.radiotime.data.api.stations.StationsInterface
import com.app.radiotime.data.models.Station
import com.app.radiotime.data.repositories.DatabaseRepository
import com.app.radiotime.data.repositories.SharedPreferencesRepository
import com.app.radiotime.utils.Constants
import com.app.radiotime.utils.Constants.DISCOVER_ID
import com.app.radiotime.utils.Constants.FAVORITES_ID
import com.app.radiotime.utils.Constants.ROOT_ID
import com.app.radiotime.utils.Constants.SHARED_PREF
import com.app.radiotime.utils.DownloadStationsWorker

object MediaItemFactory {
    var discoverList: List<Station> = listOf()
    private var favoriteList: List<Station> = listOf()

    var onFinishedLoading: (() -> Unit)? = null
    var onFinishedReadingDiscover: (() -> Unit)? = null
    var onFinishedReadingFavorite: (() -> Unit)? = null

    val retrofit = StationsClient.getInstance()
    val apiInterface: StationsInterface = retrofit.create(StationsInterface::class.java)


    private fun getStationLogoURL(station: Station): String {
        var url = station.favicon.replaceFirst("http://", "https://")
        if (station.favicon.isEmpty()) {
            url = Constants.RADIO_LOGO
        }
        return url
    }

    fun getStations(countryCode: String, application: Application, tag: String) {

        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val inputData = Data.Builder().putString("countryCode", countryCode).build()

        val getStationsWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<DownloadStationsWorker>().addTag(countryCode + tag)
                .setConstraints(constraints).setInputData(inputData).build()

        WorkManager.getInstance(application).getWorkInfosByTagLiveData(countryCode + tag)
            .observeForever { workInfo ->
                try {
                    if (!workInfo.isNullOrEmpty()) {
                        if (workInfo[0].state != WorkInfo.State.FAILED) {
                            onFinishedLoading?.invoke()
                        } else {
                            WorkManager.getInstance(application).enqueue(getStationsWorkRequest)
                        }
                    } else {
                        WorkManager.getInstance(application).enqueue(getStationsWorkRequest)
                    }
                } catch (e: Exception) {
                }
            }
    }

    suspend fun checkVersion(countryCode: String, application: Application) {
        val repository = SharedPreferencesRepository(
            application.getSharedPreferences(
                SHARED_PREF, Context.MODE_PRIVATE
            )
        )

        try {
            val version = apiInterface.getVersion()
            val curVersion = repository.getDBVersion()

            if (version.toInt() > curVersion) {
                getStations(countryCode, application, "check_$version")
            }
        } catch (e: Exception) {
        }

    }

    fun stationToMediaItem(it: Station, tag: String): MediaItem {
        return MediaItem.Builder().setMediaId(tag + it.id).setUri(it.url_resolved).setMediaMetadata(
                MediaMetadata.Builder().setAlbumTitle(it.name)
                    //.setTitle(it.name)
                    .setArtist(it.name).setDescription(it.country).setSubtitle(it.state)
                    .setWriter(it.countrycode)
                    //.setDisplayTitle(it.name)
                    .setIsBrowsable(false).setIsPlayable(true)
                    .setArtworkUri(getStationLogoURL(it).toUri())
                    .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC).setExtras(Bundle().apply {
                        putString("STREAMING_URL_RESOLVED", it.url_resolved)
                        putString("COUNTRY_CODE", it.countrycode)
                        putString("COUNTRY", it.country)
                        putString("STATE", it.state)
                        putString("GENRE", it.tags)
                        putString("NAME", it.name)
                        putString("ARTWORK", it.favicon)
                    }).build()
            ).build()
    }


    fun getDiscover(): List<MediaItem> {
        return discoverList.map {
            stationToMediaItem(it, DISCOVER_ID)
        }
    }

    fun getFavorite(): List<MediaItem> {
        return favoriteList.map {
            stationToMediaItem(it, FAVORITES_ID)
        }
    }

    suspend fun loadDiscover(dbRepository: DatabaseRepository, countryCode: String) {
        discoverList = dbRepository.getAllStations(countryCode.uppercase()).reversed()
        onFinishedReadingDiscover?.invoke()
    }

    suspend fun loadFavorite(dbRepository: DatabaseRepository) {
        favoriteList = dbRepository.getFavoriteStations().keys.toList()
        onFinishedReadingFavorite?.invoke()
    }

    suspend fun getItemFromDB(dbRepository: DatabaseRepository, stationId: String): MediaItem {
        var tag = ""
        tag = if (stationId.startsWith(DISCOVER_ID)) {
            DISCOVER_ID
        } else {
            FAVORITES_ID
        }

        val item = dbRepository.getRadioStationByID(
            stationId.replace(DISCOVER_ID, "").replace(FAVORITES_ID, "")
        )
        return if (item != null) {
            stationToMediaItem(item, tag)
        } else MediaItem.EMPTY
    }


    suspend fun getItemFromDBByName(dbRepository: DatabaseRepository, query: String): MediaItem {
        val item = dbRepository.getRadioStationByName(query)

        return if (item.isNotEmpty()) {
            stationToMediaItem(item[0], DISCOVER_ID)
        } else MediaItem.EMPTY
    }


    fun getItemIndex(item: MediaItem): Int {
        return if (item.mediaId.startsWith(DISCOVER_ID)) {
            val stationItem = discoverList.find {
                it.id == item.mediaId.replace(DISCOVER_ID, "")
            }

            discoverList.indexOf(stationItem)
        } else {
            val stationItem = favoriteList.find {
                it.id == item.mediaId.replace(FAVORITES_ID, "")
            }

            favoriteList.indexOf(stationItem)
        }
    }

    private fun getFavoritesBrowsable(): MediaItem {
        return MediaItem.Builder().setMediaId(FAVORITES_ID).setMediaMetadata(
                MediaMetadata.Builder().setIsBrowsable(true).setIsPlayable(false)
                    .setTitle("Favorites").setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
                    .build()
            ).build()
    }

    private fun getDiscoverBrowsable(): MediaItem {
        return MediaItem.Builder().setMediaId(DISCOVER_ID).setMediaMetadata(
                MediaMetadata.Builder().setIsBrowsable(true).setIsPlayable(false)
                    .setTitle("Discover").setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
                    .build()
            ).build()
    }

    fun getRoot(): MediaItem {
        return MediaItem.Builder().setMediaId("root").setMediaMetadata(
                MediaMetadata.Builder().setIsBrowsable(true).setIsPlayable(false)
                    .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED).build()
            ).build()
    }

    suspend fun getChildrenWithParent(
        parentId: String,
        page: Int,
        pageSize: Int,
        dbRepository: DatabaseRepository,
        countryCode: String
    ): List<MediaItem> {
        return when (parentId) {
            FAVORITES_ID -> {
                getFavorite()
            }

            DISCOVER_ID -> {
                dbRepository.getAllStations(countryCode.uppercase()).reversed().take(pageSize).map {
                    stationToMediaItem(it, DISCOVER_ID)
                }
            }

            ROOT_ID -> {
                listOf(
                    getDiscoverBrowsable(), getFavoritesBrowsable()
                )
            }

            else -> {
                listOf()
            }
        }
    }
}