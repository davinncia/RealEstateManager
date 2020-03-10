package com.openclassrooms.realestatemanager.repository

import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.model.PropertyType
import com.openclassrooms.realestatemanager.model_ui.AddressUi
import com.openclassrooms.realestatemanager.model_ui.PropertyUi


class InMemoryRepository {

    //val propertySelectionMutable = MutableLiveData<PropertyWrapper>()
    val propertySelectionMutable = MutableLiveData<PropertyUi>()

    val emptyProperty = PropertyUi(PropertyType.HOUSE, 0F, 0F, 0, "",
            AddressUi("", "", 0), "", false,
            -1)
    init {
        //Empty property with -1 as id by default
        propertySelectionMutable.value = emptyProperty
    }

    fun deselectProperty() {
        propertySelectionMutable.value = emptyProperty
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