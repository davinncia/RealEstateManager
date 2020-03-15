package com.openclassrooms.realestatemanager.property_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.openclassrooms.realestatemanager.model_ui.AddressUi
import com.openclassrooms.realestatemanager.model_ui.PropertyUi
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository

class ListViewModel(application: Application, private val inMemoRepo: InMemoryRepository,
                    propertyRepo: PropertyRepository) : AndroidViewModel(application) {

    val properties = MediatorLiveData<List<PropertyUi>>()

    private val dbProperties: LiveData<List<PropertyUi>> = Transformations.map(propertyRepo.allProperties) {

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

    init {
        properties.addSource(dbProperties) {
            properties.value = it
        }
        //Ad source criteria
    }


    //Only for description, add address.. ?
    fun filterPropertyByDescription(text: CharSequence) {
        //val filteredList = properties.value ?: return
        dbProperties.value?.let { dbList ->
            properties.value = dbList.filter { it.description.contains(text, true) }
        }
    }



    fun selectProperty(id: Int) {
        val property = dbProperties.value?.find { it.id == id }
        property?.let {inMemoRepo.setPropertySelection(it) }
    }

}