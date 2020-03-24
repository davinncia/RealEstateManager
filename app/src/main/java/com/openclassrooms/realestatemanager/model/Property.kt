package com.openclassrooms.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "property_table")
data class Property(
        val type: String = "HOUSE",
        val price: Float, //TODO: Int is enough
        val area: Float,
        @ColumnInfo(name = "room_number") val roomNbr: Int,
        val description: String,
        @Embedded val address: Address, //This will store all Address fields in the same table
        //array POI,
        @ColumnInfo(name = "creation_time") var creationTime: Long,
        val agent: String,//val agent: Agent,
        @ColumnInfo(name = "is_sold") var isSold: Boolean = false,
        @ColumnInfo(name = "selling_time") var sellingTime: Long = 0,
        @ColumnInfo(name = "thumbnail_uri") var thumbnailUri: String =
                "android.resource://com.openclassrooms.realestatemanager/drawable/default_house") {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

/*
enum class PropertyType {
    FLAT, LOFT, HOUSE, CASTLE, MANOR
}

 */

class PropertyType {
    companion object {
        const val HOUSE = "HOUSE"
        const val LOFT = "LOFT"
        const val FLAT = "FLAT"
        const val CASTLE = "CASTLE"
        const val MANOR = "MANOR"
    }
}