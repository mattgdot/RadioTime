package com.app.radiotime.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
fun ShimmerStation(
    size: Int = 53, round: Int = 18
) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()

    ) {
        val showShimmer = remember { mutableStateOf(true) }

        AsyncImage(
            model = "",
            contentDescription = null,
            modifier = Modifier
                .padding(15.dp)
                .size(size.dp)
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    RoundedCornerShape(round.dp)
                )
                .clip(RoundedCornerShape(round.dp))
                .background(ShimmerBrush(targetValue = 1300f, showShimmer = showShimmer.value))
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = " ".repeat(50),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                modifier = Modifier.background(
                    ShimmerBrush(
                        targetValue = 1300f, showShimmer = showShimmer.value
                    )
                ),
                overflow = TextOverflow.Ellipsis,
                fontSize = TextUnit(18f, TextUnitType.Sp),

                )
            Text(
                text = " ".repeat(30),
                maxLines = 1,
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .background(ShimmerBrush(targetValue = 1300f, showShimmer = showShimmer.value)),
                fontWeight = FontWeight.W300,
                fontSize = TextUnit(14f, TextUnitType.Sp),

                )
        }
    }
}