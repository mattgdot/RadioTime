package com.app.radiotime.media

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.app.radiotime.MainActivity
import com.app.radiotime.data.api.location.LocationClient
import com.app.radiotime.data.api.location.LocationInterface
import com.app.radiotime.data.repositories.DatabaseRepository
import com.app.radiotime.data.repositories.SharedPreferencesRepository
import com.app.radiotime.utils.Constants
import com.app.radiotime.utils.Constants.DISCOVER_ID
import com.app.radiotime.utils.Constants.FAVORITES_ID
import com.app.radiotime.utils.Constants.SHARED_PREF
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class PlayerService : MediaLibraryService() {
    lateinit var player: ExoPlayer
    private lateinit var mediaLibrarySession: MediaLibrarySession
    private lateinit var dbRepository: DatabaseRepository
    private lateinit var repository: SharedPreferencesRepository
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var countryCode = "US"

    private var timer: CountDownTimer? = null

    private val retrofit = LocationClient.getInstance()
    var apiInterface: LocationInterface = retrofit.create(LocationInterface::class.java)


    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this).setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true).build()

        dbRepository = DatabaseRepository(application)

        repository = SharedPreferencesRepository(
            application.getSharedPreferences(
                SHARED_PREF, MODE_PRIVATE
            )
        )

        fun getLastPlay() {
            val stationId = repository.getLastPlayID()
            serviceScope.launch {
                if (!stationId.isNullOrEmpty()) {
                    try {
                        val item = dbRepository.getRadioStationByID(
                            stationId.replace(DISCOVER_ID, "").replace(FAVORITES_ID, "")
                        )
                        if (item != null) {
                            player.setMediaItem(
                                MediaItemFactory.stationToMediaItem(
                                    item, DISCOVER_ID
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("error", e.message.toString())
                    }
                }
            }
        }

        getLastPlay()

        serviceScope.launch {
            val isFirstStartup = repository.isFirstStartUp()

            if (isFirstStartup) {
                try {
                    repository.setUserCountry(apiInterface.getIpInfo().countryCode)
                } catch (e: Exception) {
                    repository.setUserCountry(
                        "US"
                    )
                }
            }

            countryCode = repository.getCountryCode().toString()

            MediaItemFactory.getStations(countryCode, application, "load")
        }

        val sessionActivityPendingIntent = TaskStackBuilder.create(this).run {
            addNextIntent(Intent(this@PlayerService, MainActivity::class.java))

            val immutableFlag = PendingIntent.FLAG_IMMUTABLE
            getPendingIntent(0, immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        mediaLibrarySession =
            MediaLibrarySession.Builder(this, player, MediaLibrarySessionCallback())
                .setSessionActivity(sessionActivityPendingIntent).build()


        MediaItemFactory.onFinishedLoading = {
            serviceScope.launch {
                MediaItemFactory.loadDiscover(dbRepository, countryCode)
                MediaItemFactory.loadFavorite(dbRepository)
            }
        }

        MediaItemFactory.onFinishedReadingDiscover = {
            mediaLibrarySession.notifyChildrenChanged(
                "discover", MediaItemFactory.getDiscover().size, null
            )
            serviceScope.launch {
                MediaItemFactory.checkVersion(countryCode, application)
            }
        }

        MediaItemFactory.onFinishedReadingFavorite = {
            mediaLibrarySession.notifyChildrenChanged(
                "favorites", MediaItemFactory.getFavorite().size, LibraryParams.Builder().apply {
                    setExtras(Bundle().apply {
                        putString("type", FAVORITES_ID)
                    })
                }.build()
            )
        }

    }


    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession {
        return mediaLibrarySession
    }

    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean) {
        super.onUpdateNotification(session, true)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {

        val repository = SharedPreferencesRepository(
            application.getSharedPreferences(
                SHARED_PREF, MODE_PRIVATE
            )
        )

        if (player.currentMediaItem != null) {
            repository.setLastPlayID(player.currentMediaItem!!.mediaId)
        }

        super.onTaskRemoved(rootIntent)
        release()
        stopSelf()
    }

    override fun onDestroy() {
        release()
        super.onDestroy()
    }

    private fun release() {
        mediaLibrarySession.run {
            release()
            if (player.playbackState != Player.STATE_IDLE) {
                player.release()
            }
        }
        serviceScope.cancel()
    }

    private inner class MediaLibrarySessionCallback : MediaLibrarySession.Callback {
        override fun onConnect(
            session: MediaSession, controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val connectionResult = super.onConnect(session, controller)
            val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()

            availableSessionCommands.add(
                SessionCommand(
                    Constants.CHANGE_COUNTRY_COMMAND, Bundle.EMPTY
                )
            )

            availableSessionCommands.add(
                SessionCommand(
                    Constants.UPDATE_FAVORITE_COMMAND, Bundle.EMPTY
                )
            )

            availableSessionCommands.add(
                SessionCommand(
                    Constants.SET_TIMER_COMMAND, Bundle.EMPTY
                )
            )

            return MediaSession.ConnectionResult.accept(
                availableSessionCommands.build(), connectionResult.availablePlayerCommands
            )
        }


        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            if (Constants.CHANGE_COUNTRY_COMMAND == customCommand.customAction) {
                countryCode = args.getString(Constants.CHANGE_COUNTRY_KEY).toString()
                MediaItemFactory.getStations(countryCode, application, "load")
            }
            if (Constants.UPDATE_FAVORITE_COMMAND == customCommand.customAction) {
                serviceScope.launch {
                    MediaItemFactory.loadFavorite(dbRepository)
                }
            }

            if (Constants.SET_TIMER_COMMAND == customCommand.customAction) {
                val stopTimeMillis = args.getLong(Constants.SET_TIMER_KEY)

                timer?.cancel()

                if (stopTimeMillis.toInt() != 0) {
                    timer?.cancel()
                    timer = object : CountDownTimer(stopTimeMillis, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                        }

                        override fun onFinish() {
                            player.pause()
                        }
                    }.start()
                }
            }

            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }

        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            return Futures.immediateFuture(LibraryResult.ofItem(MediaItemFactory.getRoot(), params))
        }

        override fun onGetItem(
            session: MediaLibrarySession, browser: MediaSession.ControllerInfo, mediaId: String
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val future = SettableFuture.create<LibraryResult<MediaItem>>()

            serviceScope.launch {
                try {
                    val mediaItem = MediaItemFactory.getItemFromDB(dbRepository, mediaId)
                    val result = LibraryResult.ofItem(mediaItem, null)
                    future.set(result)
                } catch (e: Exception) {
                    future.setException(e)
                }
            }

            return future
        }


        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {

            val future = SettableFuture.create<LibraryResult<ImmutableList<MediaItem>>>()

            serviceScope.launch {
                try {
                    val result = MediaItemFactory.getChildrenWithParent(
                        parentId, page, pageSize, dbRepository, countryCode
                    )
                    future.set(LibraryResult.ofItemList(result, null))
                } catch (e: Exception) {
                    future.setException(e)
                }
            }
            return future
        }

        override fun onSubscribe(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<Void>> {
            val future = SettableFuture.create<LibraryResult<Void>>()

            serviceScope.launch {
                try {
                    val children = MediaItemFactory.getChildrenWithParent(
                        parentId, 1, 20, dbRepository, countryCode
                    )
                    future.set(LibraryResult.ofVoid())
                    session.notifyChildrenChanged(browser, parentId, children.size, params)
                } catch (e: Exception) {
                    future.setException(e)
                }
            }

            return future
        }

        @UnstableApi
        override fun onSetMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>,
            startIndex: Int,
            startPositionMs: Long
        ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {

            if (mediaItems[0].requestMetadata.searchQuery != null) {
                val future = SettableFuture.create<MediaSession.MediaItemsWithStartPosition>()

                serviceScope.launch {
                    try {
                        val mediaItem = MediaItemFactory.getItemFromDBByName(
                            dbRepository,
                            mediaItems[0].requestMetadata.searchQuery.toString()
                                .substringBefore("on").substringBefore("from").trim().lowercase()
                        )
                        val result = MediaSession.MediaItemsWithStartPosition(
                            listOf(mediaItem), C.INDEX_UNSET, 0
                        )
                        future.set(result)
                    } catch (e: Exception) {
                        future.setException(e)
                    }
                }

                return future

            }
            return if (mediaItems[0].mediaId.startsWith(DISCOVER_ID)) {
                val future = SettableFuture.create<MediaSession.MediaItemsWithStartPosition>()

                serviceScope.launch {
                    try {
                        val mediaItem =
                            MediaItemFactory.getItemFromDB(dbRepository, mediaItems[0].mediaId)
                        val result = MediaSession.MediaItemsWithStartPosition(
                            listOf(mediaItem), 0, C.TIME_UNSET
                        )
                        future.set(result)
                    } catch (e: Exception) {
                        future.setException(e)
                    }
                }

                return future
            } else {
                Futures.immediateFuture(
                    MediaSession.MediaItemsWithStartPosition(
                        MediaItemFactory.getFavorite(),
                        MediaItemFactory.getItemIndex(mediaItems[0]),
                        C.TIME_UNSET
                    )
                )
            }
        }

    }
}
