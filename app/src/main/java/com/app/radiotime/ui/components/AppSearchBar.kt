package com.app.radiotime.ui.components

import android.widget.Toast
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.radiotime.data.models.Station
import com.app.radiotime.utils.countryList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSearchBar(
    searchStations: List<Station>,
    onSearch: (query: String) -> Unit,
    onClick: (Station) -> Unit,
    onOptions: (Station) -> Unit
) {
    var queryString by remember {
        mutableStateOf("")
    }

    var isActive by remember {
        mutableStateOf(false)
    }

    if (!isActive) {
        queryString = ""
        onSearch(queryString)
    }

    val onActiveChange = { activeChange: Boolean ->
        isActive = activeChange
    }
    val colors1 = SearchBarDefaults.colors()
    SearchBar(
        windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        inputField = {
            SearchBarDefaults.InputField(
                query = queryString,
                onQueryChange = { newQueryString ->
                    queryString = newQueryString
                    onSearch(queryString)
                },
                onSearch = {

                },
                expanded = isActive,
                onExpandedChange = onActiveChange,
                placeholder = {
                    Text(text = "Search")
                },
                leadingIcon = {
                    if (isActive) {
                        IconButton(onClick = {
                            isActive = false
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = null
                            )
                        }
                    } else Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (isActive && queryString.isNotBlank()) {
                        IconButton(onClick = {
                            queryString = ""
                            onSearch(queryString)
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                colors = colors1.inputFieldColors,
            )
        },
        expanded = isActive,
        onExpandedChange = onActiveChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (isActive) 0.dp else 8.dp),
        shape = SearchBarDefaults.inputFieldShape,
        colors = colors1,
        tonalElevation = SearchBarDefaults.TonalElevation,
        shadowElevation = SearchBarDefaults.ShadowElevation,

        content = {
            if (searchStations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No results for your search",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 25.dp, horizontal = 10.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                    items(searchStations) { station ->
                        val label = (station.tags.split(",").take(4)
                            .joinToString(separator = ", ")).capitalize()
                        Station(
                            name = station.name,
                            image = station.favicon,
                            label = if (label.isNotBlank()) label else station.country,
                            onClick = {
                                onClick(station)
                            },
                            onOptions = {
                                onOptions(station)
                            },
                            modifier = Modifier
                        )
                    }
                    item {
                        Spacer(
                            modifier = Modifier.padding(bottom = 80.dp)
                        )
                    }
                }
            }
        },
    )

}