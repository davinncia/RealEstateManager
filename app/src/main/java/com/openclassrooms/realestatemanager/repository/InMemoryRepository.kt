package com.openclassrooms.realestatemanager.repository

import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.model.Agent
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.model.PropertyType

//TODO NINO 1: InMemoRepo
class InMemoryRepository {

    val propertySelectionMutable = MutableLiveData<Property>()

    init {

        //Empty property with -1 as id by default
        val property = Property(PropertyType.HOUSE, 0F, 0F, 0,
                "", Address("", "", 0), 0,
               "Phil")
        property.id = -1
        propertySelectionMutable.value = property

    }

    //Singleton
    companion object {
        private var INSTANCE: InMemoryRepository? = null

        fun getInstance(): InMemoryRepository {
            if (INSTANCE == null){
                synchronized(InMemoryRepository){
                    if (INSTANCE == null){
                        INSTANCE = InMemoryRepository()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}