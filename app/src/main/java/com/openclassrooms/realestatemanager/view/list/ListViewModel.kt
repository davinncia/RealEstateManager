package com.openclassrooms.realestatemanager.view.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.model.Criteria
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListViewModel(application: Application, private val inMemoRepo: InMemoryRepository,
                    private val propertyRepo: PropertyRepository) : AndroidViewModel(application) {

    //Entity
    private val dbProperties = propertyRepo.allProperties

    //UI
    private val allUiProperties = Transformations.map(propertyRepo.allProperties) {
        return@map mapPropertiesForUi(it)
    }
    val uiProperties = MediatorLiveData<List<PropertyItemUi>>() //Filtered on search


    init {
        uiProperties.addSource(allUiProperties) {
            uiProperties.value = it
        }
    }

    private fun mapPropertiesForUi(properties: List<Property>): List<PropertyItemUi> {
        val uiProperties = arrayListOf<PropertyItemUi>()
        for (item in properties) {
            val propertyUi = PropertyItemUi(
                                    item.id, item.description, item.type,
                                    String.format("%,d", item.price) + "$",
                                    item.address.city, item.isSold, item.thumbnailUri)
            uiProperties.add(propertyUi)
        }
        return uiProperties
    }


    fun filterPropertyByDescription(text: CharSequence) {
        allUiProperties.value?.let { dbList ->
            uiProperties.value = dbList.filter { it.description.contains(text, true) }
        }

    }

    fun selectProperty(id: Int) {
        val property = dbProperties.value?.find { it.id == id }
        property?.let { inMemoRepo.setPropertySelection(it) }
    }

    //--------------------------------------------------------------------------------------------//
    //                                A D V A N C E D     S E A R C H
    //--------------------------------------------------------------------------------------------//
    fun advancedSearch(crtr: Criteria) {
        //String fields standardization
        crtr.city.trim()
        if (crtr.city.isEmpty()) crtr.city = "%"

        viewModelScope.launch(Dispatchers.IO) {
            var filteredProperties = propertyRepo.advancedSearch(
                    crtr.minPrice, crtr.maxPrice, crtr.minArea, crtr.maxArea,
                    crtr.city, crtr.minCreationTime, crtr.isSold)

            val propertiesWorker = ArrayList(filteredProperties)

            //PICTURE CRITERION
            if (crtr.minPictureNbr > 0) {
                for (property in filteredProperties) {
                    //Check pic number
                    val count = propertyRepo.getPictureCount(property.id)
                    if (count < crtr.minPictureNbr) {
                        propertiesWorker.remove(property)
                    }
                }
                filteredProperties = propertiesWorker
            }

            //POI CRITERION
            if (crtr.poiNames.isNotEmpty()) {
                for (property in filteredProperties) {
                    //Get near by poi
                    val propertyPoiList = propertyRepo.getPoiNames(property.id)
                    //Check if there are all the asked for
                    if (!propertyPoiList.containsAll(crtr.poiNames)) {
                        //A poi isn't there
                        propertiesWorker.remove(property)
                    }
                }
                filteredProperties = propertiesWorker
            }

            withContext(Dispatchers.Main) {
                uiProperties.value = mapPropertiesForUi(filteredProperties)
            }
        }
    }

    fun endAdvancedSearch() {
        uiProperties.value = allUiProperties.value
    }

    data class PropertyItemUi(
            val id: Int,
            val description: String,
            val type: String = "HOUSE",
            val price: String,
            val city: String,
            var isSold: Boolean,
            var thumbnailUri: String = "android.resource://com.openclassrooms.realestatemanager/drawable/default_house"
    )
}