package com.openclassrooms.realestatemanager.repository

import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.model.Agent
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.model.PropertyType

//TODO NINO 1: InMemoRepo
class InMemoryRepository {

    //TODO Set a default property in case garbage collected (?)
    val propertySelectionMutable = MutableLiveData<Property>()

    init {
        /*
        //DEBUG
        val property = Property(PropertyType.HOUSE, 23_434_555F, 100F, 3,
                "beauty", Address("N.Y.", "streert", 0), 234242442,
                Agent("Phil", "Delamaison"))
        propertySelectionMutable.value = property

         */
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