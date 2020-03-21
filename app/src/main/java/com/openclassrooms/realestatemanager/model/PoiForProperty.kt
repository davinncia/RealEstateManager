package com.openclassrooms.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "poi_for_property", primaryKeys = ["property_id", "poi_name"])

class PoiForProperty (
        @ColumnInfo(name = "property_id") val propertyId: Int,
        @ColumnInfo(name = "poi_name") val poiName: String
)