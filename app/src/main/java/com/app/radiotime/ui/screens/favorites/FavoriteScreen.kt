package com.app.radiotime.ui.screens.favorites

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.app.radiotime.data.models.Station
import com.app.radiotime.ui.MainViewModel
import com.app.radiotime.ui.components.OptionsBottomSheet
import com.app.radiotime.ui.components.SleepTimerSheet
import com.app.radiotime.ui.components.Station
import com.app.radiotime.ui.components.rememberDragDropListState
import com.app.radiotime.utils.Constants
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@SuppressLint("UnnecessaryComposedModifier")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoriteScreen(
    mainViewModel: MainViewModel
) {
    var showSleepSheet by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var optionsStation by remember {
        mutableStateOf<Station?>(null)
    }

    val scope = rememberCoroutineScope()

    var overscrollJob by remember { mutableStateOf<Job?>(null) }

    val dragDropListState = rememberDragDropListState(onMove = { from, to ->
        mainViewModel.moveFav(from, to)
    }, onInterrupt = {
        scope.launch {
            mainViewModel.reorderStations()
        }
    })
    if (mainViewModel.favoritesStations.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
        ) {
            Text(
                "No favorite stations yet",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = TextUnit(
                    22f, TextUnitType.Sp
                )
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()

                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress(onDrag = { change, offset ->
                        change.consume()
                        dragDropListState.onDrag(offset)

                        if (overscrollJob?.isActive == true) return@detectDragGesturesAfterLongPress

                        dragDropListState
                            .checkForOverScroll()
                            .takeIf { it != 0f }
                            ?.let {
                                overscrollJob =
                                    scope.launch { dragDropListState.lazyListState.scrollBy(it) }
                            } ?: run { overscrollJob?.cancel() }
                    },
                        onDragStart = { offset -> dragDropListState.onDragStart(offset) },
                        onDragEnd = { dragDropListState.onDragInterrupted() },
                        onDragCancel = { dragDropListState.onDragInterrupted() })
                }, state = dragDropListState.lazyListState
        ) {
            itemsIndexed(mainViewModel.favoritesStations) { index, station ->

                Station(name = station.name,
                    image = station.favicon,
                    label = station.country,
                    onClick = {
                        mainViewModel.playStation(station, Constants.FAVORITES_ID)
                    },
                    onOptions = {
                        showBottomSheet = true
                        optionsStation = station
                    },
                    modifier = Modifier.composed {
                        val offsetOrNull = dragDropListState.elementDisplacement.takeIf {
                            index == dragDropListState.currentIndexOfDraggedItem
                        }

                        Modifier.graphicsLayer {
                                translationY = offsetOrNull ?: 0f
                            }
                    })


            }

            item{
                Spacer(
                    modifier = Modifier.padding(bottom = 80.dp)
                )
            }
        }
    }
    if (showBottomSheet) {
        optionsStation?.let {
            OptionsBottomSheet(
                onDismiss = {
                    showBottomSheet = false
                },
                station = it,
                mainViewModel = mainViewModel,
                onSleepTimer = {
                    showBottomSheet=false
                    showSleepSheet=true
                }
            )
        }
    }

    if(showSleepSheet){
        SleepTimerSheet(
            onDismiss = {
                showSleepSheet=false
            },onSelected = {
                mainViewModel.sleepTimer(it)
            }
        )
    }
}