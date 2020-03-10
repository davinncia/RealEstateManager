package com.openclassrooms.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "picture_table")
data class Picture (
        @ColumnInfo(name = "uri") val strUri: String,
        @ColumnInfo(name = "property_id") val propertyId: Int) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}