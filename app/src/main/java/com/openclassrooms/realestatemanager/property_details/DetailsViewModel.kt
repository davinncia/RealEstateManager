package com.openclassrooms.realestatemanager.property_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.repository.InMemoryRepository

class DetailsViewModel(inMemoRepo: InMemoryRepository) : ViewModel() {

    private var propertySelectionMutable = inMemoRepo.propertySelectionMutable
    val propertySelection: LiveData<Property> = propertySelectionMutable


}