package com.openclassrooms.realestatemanager.model_ui

data class PropertyUi(

        val type: String = "HOUSE",
        val price: Float,
        val area: Float,
        val roomNbr: Int,
        val description: String,
        //val picturesUri: Array<Int>,
        val address: AddressUi,
        //array POI,
        val agentName: String,
        var isSold: Boolean,
        var id: Int
        )
