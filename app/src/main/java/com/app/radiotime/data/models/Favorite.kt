package com.app.radiotime.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_items")
data class Favorite(
    @PrimaryKey(autoGenerate = true) val favId: Long?,
    val id: String,
    val order: Long?,
)
