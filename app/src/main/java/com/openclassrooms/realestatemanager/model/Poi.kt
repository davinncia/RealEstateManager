package com.openclassrooms.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "poi_table")
data class Poi(
        @PrimaryKey val name: String,
        @ColumnInfo(name = "icon_ressource_id") val iconResourceId: Int
)