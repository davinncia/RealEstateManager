package com.openclassrooms.realestatemanager.property_edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository
import kotlinx.coroutines.launch

class EditViewModel(application: Application, inMemoRepo: InMemoryRepository, val propertyRepo: PropertyRepository)
    : AndroidViewModel(application) {

    private val selectedProperty: LiveData<Property> = inMemoRepo.propertySelectionMutable


    fun saveInDb(property: Property, isNew: Boolean){
        if (isNew)
            insert(property)
        else
            update(property)
    }

    private fun update(property: Property){
        property.id = selectedProperty.value!!.id //Careful NPE here
        property.creationTime = selectedProperty.value!!.creationTime
        //TODO: if address != find latLng
    }

    private fun insert(property: Property){
        //TODO: try to find LatLng
        viewModelScope.launch {
            propertyRepo.insert(property)
        }

    }
}