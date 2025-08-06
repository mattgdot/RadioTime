package com.app.radiotime.ui.screens.discover

import android.Manifest.permission.POST_NOTIFICATIONS
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.app.radiotime.data.models.Station
import com.app.radiotime.ui.MainViewModel
import com.app.radiotime.ui.components.AppSearchBar
import com.app.radiotime.ui.components.LargeDropdownMenu
import com.app.radiotime.ui.components.OptionsBottomSheet
import com.app.radiotime.ui.components.ShimmerStation
import com.app.radiotime.ui.components.SleepTimerSheet
import com.app.radiotime.ui.components.Station
import com.app.radiotime.utils.Constants.DISCOVER_ID
import com.app.radiotime.utils.countryList
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DiscoverScreen(mainViewModel: MainViewModel) {
    var selectedIndex = countryList.indexOfFirst { it.second == mainViewModel.selectedCountryCode }
    val keyboardController = LocalSoftwareKeyboardController.current
    var showBottomSheet by remember { mutableStateOf(false) }
    var showSleepSheet by remember { mutableStateOf(false) }
    var optionsStation by remember {
        mutableStateOf<Station?>(null)
    }
    val state = rememberLazyListState()
    val isAtBottom = !state.canScrollForward
    val scope = rememberCoroutineScope()
    var jumpTop by rememberSaveable {
        mutableStateOf(false)
    }
    val visibleItem by remember { derivedStateOf { state.firstVisibleItemIndex } }

    val notificationsPermissionState = rememberPermissionState(POST_NOTIFICATIONS)

    LaunchedEffect(notificationsPermissionState) {
        if (!notificationsPermissionState.status.isGranted) {
            notificationsPermissionState.launchPermissionRequest()
        }
    }

    if (jumpTop) {
        LaunchedEffect(key1 = "scroll") {
            state.scrollToItem(0, 0)
            jumpTop = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        AppSearchBar(mainViewModel.searchStations, onSearch = {
            mainViewModel.search(it)
        }, onOptions = {
            showBottomSheet = true
            optionsStation = it
        }, onClick = {
            mainViewModel.playStation(it, DISCOVER_ID)
            keyboardController?.hide()
        })

        Box(
            contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(state = state) {
                item("dr") {
                    LargeDropdownMenu(
                        label = "View stations from",
                        items = countryList.map { it.first },
                        selectedIndex = selectedIndex,
                        onItemSelected = { index, _ ->
                            selectedIndex = index
                            mainViewModel.setCountryCode(index)
                        },
                        modifier = Modifier.padding(10.dp)
                    )
                }
                if (mainViewModel.discoverStations.isEmpty()) {
                    items(15) {
                        ShimmerStation()
                    }
                } else {
                    items(mainViewModel.discoverStations) { station ->
                        val label = (station.tags.split(",").take(4)
                            .joinToString(separator = ", ")).capitalize()
                        LaunchedEffect(isAtBottom) {
                            if (isAtBottom) mainViewModel.loadMore()
                        }
                        Station(
                            name = station.name,
                            image = station.favicon,
                            label = if (label.isNotBlank()) label else station.country,
                            onClick = {
                                mainViewModel.playStation(station, DISCOVER_ID)
                            },
                            onOptions = {
                                showBottomSheet = true
                                optionsStation = station
                            },
                            Modifier
                        )
                    }
                    item {
                        Spacer(
                            modifier = Modifier.padding(bottom = 80.dp)
                        )
                    }
                }
            }

            AnimatedContent(targetState = !state.isScrollingUp() && visibleItem > 15, label = "") {
                if (it) {
                    FloatingActionButton(
                        modifier = Modifier.padding(10.dp),
                        onClick = {
                            jumpTop = true
                        },
                    ) {
                        Icon(Icons.Default.ArrowUpward, null)
                    }
                }
            }

            if (showBottomSheet) {
                optionsStation?.let {
                    OptionsBottomSheet(onDismiss = {
                        showBottomSheet = false
                    }, station = it, mainViewModel = mainViewModel, onSleepTimer = {
                        showBottomSheet = false
                        showSleepSheet = true
                    })
                }
            }

            if (showSleepSheet) {
                SleepTimerSheet(onDismiss = {
                    showSleepSheet = false
                }, onSelected = {
                    mainViewModel.sleepTimer(it)
                })
            }

        }


    }

}

@Composable
fun LazyListState.isScrollingDown(): Boolean {
    val offset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) { derivedStateOf { (firstVisibleItemScrollOffset - offset) > 0 } }.value
}

@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}