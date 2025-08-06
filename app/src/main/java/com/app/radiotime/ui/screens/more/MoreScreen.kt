package com.app.radiotime.ui.screens.more

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SecurityUpdateGood
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.SecurityUpdateGood
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.app.radiotime.R
import com.app.radiotime.ui.MainViewModel
import com.app.radiotime.ui.components.PremiumCard
import com.app.radiotime.ui.components.TextAlertDialog
import com.app.radiotime.ui.components.ThemeSelectionDialog
import com.app.radiotime.utils.ThemeMode

@Composable
fun MoreScreen(
    mainViewModel: MainViewModel
) {
    var openThemeDialog by remember { mutableStateOf(false) }
    var openChangelogDialog by remember { mutableStateOf(false) }

    val themeOptions = listOf(ThemeMode.AUTO, ThemeMode.DARK, ThemeMode.LIGHT)
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    LazyColumn {
        item {
            PremiumCard(isPremium = mainViewModel.isPremium) {
                mainViewModel.onPurchase()
            }
            OutlinedButton(
                onClick = {
                    try {
                        uriHandler.openUri("https://docs.google.com/forms/d/e/1FAIpQLSejDu1XaN-iCQ_8wlwbfducJyZeQtwrXodn3G_x3OLpPFW8UA/viewform")
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "No app found to handle this action",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text("Submit radio station")
            }
            ListItem(headlineContent = {
                Text(text = "Set app theme")
            }, leadingContent = {
                Icon(Icons.Outlined.Settings, contentDescription = null)
            }, modifier = Modifier.clickable {
                openThemeDialog = true
            })
            ListItem(headlineContent = {
                Text(text = "Instagram")
            }, leadingContent = {
                Icon(painterResource(id = R.drawable.ic_instagram), contentDescription = null)
            }, modifier = Modifier.clickable {
                try {
                    uriHandler.openUri("https://bit.ly/radiotimeinstagram")
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "No app found to handle this action",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

            ListItem(headlineContent = {
                Text(text = "Share this app")
            }, leadingContent = {
                Icon(Icons.Outlined.Share, contentDescription = null)
            }, modifier = Modifier.clickable {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "https://play.google.com/store/apps/details?id=com.radiotime.app"
                    )
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                try {
                    context.startActivity(shareIntent)
                } catch (_: Exception) {
                    Toast.makeText(
                        context,
                        "No app found to handle this action",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

            ListItem(headlineContent = {
                Text(text = "Rate this app")
            }, leadingContent = {
                Icon(Icons.Outlined.ThumbUp, contentDescription = null)
            }, modifier = Modifier.clickable {
                try {
                    uriHandler.openUri("https://play.google.com/store/apps/details?id=com.radiotime.app")
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "No app found to handle this action",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

            ListItem(headlineContent = {
                Text(text = "Contact the developer")
            }, leadingContent = {
                Icon(Icons.Outlined.Email, contentDescription = null)
            }, modifier = Modifier.clickable {
                val emailIntent =
                    Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:radiotimeapp@gmail.com"))
                try {
                    context.startActivity(emailIntent)
                } catch (_: ActivityNotFoundException) {
                    Toast.makeText(
                        context,
                        "No app found to handle this action",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            //HorizontalDivider()
            ListItem(headlineContent = {
                Text(text = "Update changelog")
            }, leadingContent = {
                Icon(Icons.Outlined.SecurityUpdateGood, contentDescription = null)
            }, modifier = Modifier.clickable {
                openChangelogDialog = true
            })
        }
    }
    when {
        openThemeDialog -> {
            ThemeSelectionDialog(
                onDismiss = { openThemeDialog = false },
                onSubmit = { mainViewModel.setTheme(it) },
                themeOptions = themeOptions,
                initialTheme = mainViewModel.appTheme
            )
        }

        openChangelogDialog -> {
            TextAlertDialog(
                onDismissRequest = { openChangelogDialog = false },
                onConfirmation = { openChangelogDialog = false },
                dialogTitle = "App changelog",
                dialogText = "Completely rebuilt the app with Jetpack Compose.",
                icon = Icons.Default.SecurityUpdateGood
            )
        }
    }
}