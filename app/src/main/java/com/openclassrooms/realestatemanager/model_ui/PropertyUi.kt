package com.openclassrooms.realestatemanager.model_ui

sealed class PropertyWrapper

object EmptyProperty : PropertyWrapper()

data class PropertyUi (

        val type: String = "HOUSE",
        val price: Float,
        val area: Float,
        val roomNbr: Int,
        val description: String,
        val address: AddressUi,
        //array POI,
        val agentName: String,
        var isSold: Boolean,
        var id: Int
) : PropertyWrapper() {
    var thumbnailUri = "android.resource://com.openclassrooms.realestatemanager/drawable/default_house"
}


