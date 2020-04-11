package com.openclassrooms.realestatemanager.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.model.Property

//Keeps track of the property selected by the user
class InMemoryRepository {

    private val propertySelectionMutable = MutableLiveData<Property?>(null)

    fun getPropertySelection(): LiveData<Property?> = propertySelectionMutable

    fun setPropertySelection(property: Property?) {
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