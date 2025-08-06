package com.app.radiotime.ui.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Radio
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Shortcut
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.app.radiotime.data.models.Favorite
import com.app.radiotime.data.models.Station
import com.app.radiotime.ui.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsBottomSheet(
    station: Station, onDismiss: () -> Unit, mainViewModel: MainViewModel, onSleepTimer: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        true
    )
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isFavorite by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        val result = withContext(Dispatchers.IO) {
            val favoriteItem = mainViewModel.getFavoriteItem(station.id)
            favoriteItem != null
        }

        isFavorite = result
    }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        }, sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.background,
    ) {
        Column {
            Text(
                text = station.name,
                style = MaterialTheme.typography.titleMedium,
                fontSize = TextUnit(18f, TextUnitType.Sp),
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 0.dp)
            )
            Text(
                text = station.country,
                fontSize = TextUnit(15f, TextUnitType.Sp),
                modifier = Modifier
                    .padding(horizontal = 15.dp, vertical = 0.dp)
                    .padding(bottom = 15.dp)
            )
            HorizontalDivider(

            )
            ListItem(headlineContent = {
                Text(text = if (isFavorite) "Remove from favorites" else "Add to favorite")
            }, leadingContent = {
                if (isFavorite) Icon(Icons.Outlined.Favorite, contentDescription = null) else Icon(
                    Icons.Outlined.FavoriteBorder, contentDescription = null
                )
            }, modifier = Modifier.clickable {
                scope.launch {
                    mainViewModel.addOrRemoveFromFavorites(station.id)
                    onDismiss()
                }
            })
            ListItem(headlineContent = {
                Text(text = "Create shortcut")
            }, leadingContent = {
                Icon(Icons.Outlined.Shortcut, contentDescription = null)
            }, modifier = Modifier.clickable {
                mainViewModel.createShortcut(station)
                onDismiss()
            })
            ListItem(headlineContent = {
                Text(text = "Sleep timer")
            }, leadingContent = {
                Icon(Icons.Outlined.Timer, contentDescription = null)
            }, modifier = Modifier.clickable {
                onSleepTimer()
                onDismiss()
            })
            ListItem(headlineContent = {
                Text(text = "Share")
            }, leadingContent = {
                Icon(Icons.Outlined.Share, contentDescription = null)
            }, modifier = Modifier.clickable {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Download the free RadioTime app and listen to ${station.name}\nhttps://play.google.com/store/apps/details?id=com.radiotime.app&referrer=sharestation"
                    )
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                try {
                    context.startActivity(shareIntent)
                } catch (_: Exception) {
                    Toast.makeText(
                        context, "No app found to handle this action", Toast.LENGTH_SHORT
                    ).show()
                }
            })
            ListItem(headlineContent = {
                Text(text = "Report problem")
            }, leadingContent = {
                Icon(Icons.Outlined.Mail, contentDescription = null)
            }, modifier = Modifier.clickable {
                val emailIntent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"))
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("radiotimeapp@gmail.com"))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[Report] Radio Station Not Working")
                emailIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Name: " + station.name + "\nID: " + station.id + "\nCountry: " + station.country
                )
                try {
                    context.startActivity(emailIntent)
                } catch (_: ActivityNotFoundException) {
                    Toast.makeText(
                        context, "No app found to handle this action", Toast.LENGTH_SHORT
                    ).show()
                }
            })
            Spacer(Modifier.padding(25.dp))

        }
    }
}