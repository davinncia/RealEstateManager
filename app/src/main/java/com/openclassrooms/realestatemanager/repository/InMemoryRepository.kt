package com.openclassrooms.realestatemanager.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.view.model_ui.EmptyProperty
import com.openclassrooms.realestatemanager.view.model_ui.PropertyWrapper


class InMemoryRepository {

    private val propertySelectionMutable = MutableLiveData<PropertyWrapper>()

    //val emptyProperty = PropertyUi(PropertyType.HOUSE, 0F, 0F, 0, "",
    //        AddressUi("", "", 0), "", false,
    //        -1)

    init {
        //Empty property by default
        propertySelectionMutable.value = EmptyProperty
    }

    fun getPropertySelection(): LiveData<PropertyWrapper> = propertySelectionMutable

    fun setPropertySelection(propertyWrapper: PropertyWrapper) {
        propertySelectionMutable.value = propertyWrapper
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