package com.app.radiotime.ui.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Shortcut
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import org.w3c.dom.Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepTimerSheet(onDismiss: () -> Unit, onSelected: (Int) -> Unit) {
    val timeList = listOf(5, 10, 15, 20, 30, 45, 60, 90, 120, 180, 240)

    val sheetState = rememberModalBottomSheetState(true)

    ModalBottomSheet(onDismissRequest = { onDismiss() }, sheetState = sheetState) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Set timer",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = TextUnit(20f, TextUnitType.Sp),
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
                )
                TextButton(onClick = {
                    onSelected(0)
                    onDismiss()
                }, modifier = Modifier.padding(horizontal = 0.dp, vertical = 10.dp)) {
                    Text(
                        text = "Cancel previous",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = TextUnit(18f, TextUnitType.Sp),
                    )
                }
            }
            HorizontalDivider()
            Spacer(Modifier.padding(10.dp))
            LazyColumn {
                items(timeList) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 15.dp, vertical = 10.dp)
                            .fillMaxWidth()
                            .clickable {
                                onSelected(it)
                                onDismiss()
                            }) {
                        Text("$it min", fontSize = TextUnit(16f, TextUnitType.Sp))
                    }
                }
            }

            Spacer(Modifier.padding(25.dp))

        }
    }
}