package com.app.radiotime

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.radiotime.data.models.AppTheme
import com.app.radiotime.ui.MainViewModel
import com.app.radiotime.ui.components.CustomAlertDialog
import com.app.radiotime.ui.components.MiniPlayerController
import com.app.radiotime.ui.navigation.BottomNavigationBar
import com.app.radiotime.ui.navigation.NavGraph
import com.app.radiotime.ui.navigation.NavigationItem
import com.app.radiotime.ui.screens.player.PlayerScreen
import com.app.radiotime.ui.theme.RadioTimeV3Theme
import com.app.radiotime.utils.ThemeMode
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val LocalTheme = compositionLocalOf { AppTheme() }
        MobileAds.initialize(this@MainActivity)

        Purchases.configure(
            PurchasesConfiguration.Builder(this, getString(R.string.revcat_key)).build()
        )


        setContent {
            val navController = rememberNavController()
            var showAppBar by rememberSaveable { mutableStateOf(true) }
            var showBottomBar by rememberSaveable { mutableStateOf(true) }
            var screenName by rememberSaveable {
                mutableStateOf("")
            }
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val snackbarHostState = remember { SnackbarHostState() }

            screenName = when (navBackStackEntry?.destination?.route) {
                NavigationItem.Discover.route -> NavigationItem.Discover.routeName
                NavigationItem.Favorites.route -> NavigationItem.Favorites.routeName
                NavigationItem.More.route -> NavigationItem.More.routeName
                else -> {
                    ""
                }
            }

            showAppBar = when (navBackStackEntry?.destination?.route) {
                NavigationItem.Discover.route -> false
                else -> true
            }

            showBottomBar = when (navBackStackEntry?.destination?.route) {
                else -> true
            }

            val scrollBehavior = when (navBackStackEntry?.destination?.route) {
                NavigationItem.Favorites.route -> TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                    rememberTopAppBarState()
                )

                else -> TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
            }

            fun String.isValidUrl(): Boolean =
                Patterns.WEB_URL.matcher(this).matches() && this.startsWith("http")

            val mainViewModel: MainViewModel = viewModel()
            val theme = mainViewModel.appTheme

            val darkTheme = when (theme) {
                ThemeMode.AUTO -> AppTheme(isSystemInDarkTheme())
                ThemeMode.LIGHT -> AppTheme(false)
                ThemeMode.DARK -> AppTheme(true)
            }

            var showAddDialog by remember {
                mutableStateOf(false)
            }

            var showPlayerSheet by rememberSaveable {
                mutableStateOf(false)
            }

            fun showInterstitial() {
                val adRequest = AdRequest.Builder().build()

                mainViewModel.totalImpressionCount++
                mainViewModel.lastAdShownTime = System.currentTimeMillis()

                InterstitialAd.load(
                    this@MainActivity,
                    getString(R.string.interstitial_ad_id),
                    adRequest,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
                            interstitialAd.fullScreenContentCallback =
                                object : FullScreenContentCallback() {
                                    override fun onAdClicked() {
                                        mainViewModel.totalClickCount++
                                    }
                                }
                            interstitialAd.show(this@MainActivity)
                            Log.d("mkv", "hh")
                        }
                    })
            }

            CompositionLocalProvider(LocalTheme provides darkTheme) {

                RadioTimeV3Theme(darkTheme = LocalTheme.current.isDark) {

                    Scaffold(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

                        snackbarHost = {
                            SnackbarHost(hostState = snackbarHostState)
                        }, topBar = {
                            if (showAppBar) {
                                TopAppBar(
                                    title = {
                                        Text(screenName)
                                    },
                                    scrollBehavior = scrollBehavior,
                                    colors = TopAppBarDefaults.topAppBarColors().copy(
                                        scrolledContainerColor = TopAppBarDefaults.topAppBarColors().containerColor
                                    ),
                                    navigationIcon = {
                                        if (!showBottomBar && navController.previousBackStackEntry != null) {
                                            IconButton(onClick = { navController.navigateUp() }) {
                                                Icon(Icons.Default.Close, null)
                                            }
                                        }
                                    },
                                    actions = {
                                        if (navBackStackEntry?.destination?.route == NavigationItem.Favorites.route) {
                                            IconButton(onClick = {
                                                showAddDialog = true
                                            }) {
                                                Icon(
                                                    Icons.AutoMirrored.Filled.PlaylistAdd,
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    })
                            }
                        }, bottomBar = {
                            if (showBottomBar) {
                                BottomAppBar {
                                    BottomNavigationBar(navController = navController)
                                }
                            }
                        }) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    PaddingValues(
                                        0.dp,
                                        innerPadding.calculateTopPadding(),
                                        0.dp,
                                        innerPadding.calculateBottomPadding()
                                    )
                                )
                        ) {

                            fun Context.findActivity(): Activity? = when (this) {
                                is Activity -> this
                                is ContextWrapper -> baseContext.findActivity()
                                else -> null
                            }

                            val context = LocalContext.current
                            val activity = context.findActivity()
                            val intent = activity?.intent

                            val appLinkIntent: Intent? = intent
                            val appLinkData: Uri? = appLinkIntent?.data

                            appLinkData?.let {
                                if (appLinkData.host == "shortcut") {
                                    val stationID =
                                        appLinkData.toString().substringAfter("shortcut/")

                                    if (stationID.isNotEmpty() && stationID.isNotBlank()) {
                                        mainViewModel.openRadioFromLink(stationID)
                                        activity?.intent = Intent()
                                    }
                                }
                            }

                            NavGraph(navHostController = navController, modifier = Modifier)

                            mainViewModel.selectedStation?.let {
                                if (showBottomBar) {
                                    MiniPlayerController(
                                        modifier = Modifier.align(Alignment.BottomEnd),
                                        station = it,
                                        isLoading = mainViewModel.isRadioLoading,
                                        isPlaying = mainViewModel.isRadioPlaying,
                                        onPlay = {
                                            mainViewModel.playOrPause()
                                            if (!mainViewModel.isPremium) {
                                                if (mainViewModel.totalPlayCount == 5) {
                                                    if (mainViewModel.totalClickCount < 10 && System.currentTimeMillis() - mainViewModel.lastAdShownTime > 60 * 1000) {
                                                        showInterstitial()
                                                    }
                                                    mainViewModel.totalPlayCount = 0
                                                } else {
                                                    mainViewModel.totalPlayCount++
                                                }
                                            }
                                        },
                                        onClick = {
                                            showPlayerSheet = true
                                        }
                                    )
                                }
                            }

                            if (showPlayerSheet) {
                                PlayerScreen(mainViewModel = mainViewModel) {
                                    showPlayerSheet = false
                                }
                            }

                            if (showAddDialog) {
                                var name by remember { mutableStateOf("") }
                                var link by remember { mutableStateOf("") }

                                var nameErr by remember {
                                    mutableStateOf("")
                                }
                                var linkErr by remember {
                                    mutableStateOf("")
                                }

                                CustomAlertDialog(
                                    title = "Add station",
                                    confirmText = "Add",
                                    dismissText = "Cancel",
                                    icon = Icons.Default.Radio,
                                    onDismiss = {
                                        showAddDialog = false
                                    },
                                    onConfirm = {
                                        if (name.isEmpty() || name.isBlank()) {
                                            nameErr = "Name can't be empty"
                                        } else {
                                            nameErr = ""
                                        }
                                        if (link.isEmpty() || link.isBlank()) {
                                            linkErr = "Streaming link is required"
                                        }
                                        if (!link.isValidUrl()) {
                                            linkErr = "Invalid link"
                                        } else {
                                            linkErr = ""
                                        }
                                        if (nameErr.isBlank() && linkErr.isBlank()) {
                                            mainViewModel.addStation(name, link)
                                            showAddDialog = false
                                        }
                                    }) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),

                                        ) {
                                        Text(
                                            "Missing a station? Add it directly in your favorite list!",
                                            modifier = Modifier.padding(5.dp)
                                        )
                                        OutlinedTextField(
                                            value = name,
                                            isError = nameErr.isNotBlank(),
                                            modifier = Modifier.fillMaxWidth(),
                                            supportingText = {
                                                if (nameErr.isNotBlank()) {
                                                    Text(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        text = nameErr,
                                                        color = MaterialTheme.colorScheme.error
                                                    )
                                                }
                                            },
                                            trailingIcon = {
                                                if (nameErr.isNotBlank()) Icon(
                                                    Icons.Filled.Error,
                                                    null,
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            },
                                            onValueChange = {
                                                name = it
                                                nameErr = ""
                                            },
                                            label = { Text("Station name") })

                                        OutlinedTextField(
                                            value = link,
                                            modifier = Modifier.fillMaxWidth(),
                                            supportingText = {
                                                if (linkErr.isNotBlank()) {
                                                    Text(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        text = linkErr,
                                                        color = MaterialTheme.colorScheme.error
                                                    )
                                                }
                                            },
                                            trailingIcon = {
                                                if (linkErr.isNotBlank()) Icon(
                                                    Icons.Filled.Error,
                                                    null,
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            },
                                            onValueChange = {
                                                linkErr = ""
                                                link = it
                                            },
                                            label = { Text("Streaming link") })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}