package com.app.radiotime.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.radiotime.R
import com.app.radiotime.utils.Constants.RADIO_LOGO


@Composable
fun Station(
    name: String, image: String, label: String, onClick: () -> Unit, onOptions: () -> Unit,modifier: Modifier
) {

    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }

    ) {
        Box(modifier = Modifier.padding(15.dp)) {
            RadioLogoSmall(imageUrl = image, 53)
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = TextUnit(18f, TextUnitType.Sp),
                )
            if (label.isNotBlank()) {
                Text(
                    text = label,
                    fontWeight = FontWeight.W400,
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        IconButton(
            modifier = Modifier.padding(10.dp),
            onClick = {
                onOptions()
            }
        ) {
            Icon(
                Icons.Default.MoreVert,
                modifier = Modifier.size(27.dp),
                contentDescription = null
            )
        }
    }
}