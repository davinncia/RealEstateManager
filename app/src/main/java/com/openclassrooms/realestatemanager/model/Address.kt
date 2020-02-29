package com.openclassrooms.realestatemanager.model

data class Address(val city: String, val street: String, val streetNbr: Int,
                   var latitude: Double = 0.0, var longitude: Double = 0.0)