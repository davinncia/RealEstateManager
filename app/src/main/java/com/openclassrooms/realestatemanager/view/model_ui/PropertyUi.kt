package com.openclassrooms.realestatemanager.view.model_ui

sealed class PropertyWrapper

object EmptyProperty : PropertyWrapper()

// TODO LUCAS N'utilise que des val dans tes models
data class PropertyUi (
        val type: String = "HOUSE",
        val price: Int,
        val area: Float,
        val roomNbr: Int,
        val description: String,
        val address: AddressUi,
        val agentName: String,
        var isSold: Boolean,
        var id: Int
) : PropertyWrapper() {
    var thumbnailUri = "android.resource://com.openclassrooms.realestatemanager/drawable/default_house"
}


