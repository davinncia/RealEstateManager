package com.openclassrooms.realestatemanager.model

import com.openclassrooms.realestatemanager.model.PropertyType.HOUSE

enum class PropertyType {
    FLAT, LOFT, HOUSE, CASTLE, MANOR
}

data class Property(
        val type: PropertyType = HOUSE,
        val price: Float,
        val area: Float,
        val roomNbr: Int,
        val description: String,
        //val picturesUri: Array<Int>,
        val address: Address,
        //array POI,
        val creationTime: Long,
        val agent: Agent,
        val isSold: Boolean = false,
        val sellingTime: Long = 0)