package com.openclassrooms.realestatemanager.property_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.openclassrooms.realestatemanager.model_ui.AddressUi
import com.openclassrooms.realestatemanager.model_ui.PropertyUi
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository

class ListViewModel(application: Application, private val inMemoRepo: InMemoryRepository,
                    propertyRepo: PropertyRepository) : AndroidViewModel(application) {

    val allProperties: LiveData<List<PropertyUi>> = Transformations.map(propertyRepo.allProperties) {

        val uiProperties = arrayListOf<PropertyUi>()
        for (item in it){
            val addressUi = AddressUi(item.address.city, item.address.street, item.address.streetNbr)
            val propertyUi = PropertyUi(item.type, item.price, item.area, item.roomNbr,
                    item.description, addressUi, item.agent, item.isSold, item.id)
            propertyUi.thumbnailUri = item.thumbnailUri
            uiProperties.add(propertyUi)
        }
        return@map uiProperties
    }


    fun selectProperty(id: Int) {
        val property = allProperties.value?.find { it.id == id }
        property?.let {inMemoRepo.setPropertySelection(it) }
    }

}