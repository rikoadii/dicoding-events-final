package com.submissionandroid.dicodingevents

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_events")
data class FavoriteEvent(
    @PrimaryKey val id: Int,
    val name: String,
    val date: String,
    val imageLogo: String,
    val description: String
)
