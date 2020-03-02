package com.openclassrooms.realestatemanager.model

import androidx.room.ColumnInfo

data class Address(
        val city: String,
        val street: String,
        @ColumnInfo(name = "street_number") val streetNbr: Int,
        var latitude: Double = 0.0,
        var longitude: Double = 0.0)