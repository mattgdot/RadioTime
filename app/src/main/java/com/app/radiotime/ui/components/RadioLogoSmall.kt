package com.app.radiotime.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.radiotime.R
import com.app.radiotime.utils.Constants
import org.checkerframework.checker.regex.qual.PartialRegex

@Composable

fun RadioLogoSmall(
    imageUrl: String, size: Int, round: Int = 18
) {
    val showShimmer = remember { mutableStateOf(true) }
    val logoUrl = imageUrl.ifBlank { Constants.RADIO_LOGO }

    AsyncImage(
        model = logoUrl,
        contentDescription = null,
        modifier = Modifier
            .size(size.dp, size.dp)
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                RoundedCornerShape(round.dp)
            )
            .clip(RoundedCornerShape(round.dp))
            .background(ShimmerBrush(targetValue = 1300f, showShimmer = showShimmer.value)),
        onSuccess = { showShimmer.value = false },
        contentScale = ContentScale.Crop,
        error = painterResource(id = R.drawable.icon_launcher)
    )
}