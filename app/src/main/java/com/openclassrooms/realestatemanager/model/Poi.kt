package com.openclassrooms.realestatemanager.model

import androidx.room.Entity

@Entity
data class Poi(
        val id: Int,
        val name: String,
        val iconResourceId: Int
) {

    companion object {
        //TODO: stock strings in db on creation
        const val SCHOOL = "School"
        const val PARK = "Park"
        const val GROCERY_STORE = "Grocery store"
        const val TRAIN_STATION = "Train station"
    }

}