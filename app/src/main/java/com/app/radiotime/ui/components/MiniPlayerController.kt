package com.app.radiotime.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.Navigation
import com.app.radiotime.R
import com.app.radiotime.data.models.Station
import com.app.radiotime.ui.navigation.NavigationItem

@Composable
fun MiniPlayerController(
    modifier: Modifier,
    station: Station,
    isLoading: Boolean,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        colors = CardDefaults.elevatedCardColors().copy(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        onClick = {
            onClick()
        }) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.padding(vertical = 7.dp, horizontal = 10.dp)) {
                RadioLogoSmall(imageUrl = station.favicon, 50)
            }

            Text(
                text = station.name,
                maxLines = 1,
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(35.dp)
                )
            } else {
                IconButton(
                    onClick = {
                        onPlay()
                    }, Modifier.padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_circle
                        ), contentDescription = null, Modifier.size(35.dp)
                    )
                }
            }
        }
    }
}