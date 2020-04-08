package com.openclassrooms.realestatemanager.view.list

import android.app.Application
import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.model.Criteria
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository
import com.openclassrooms.realestatemanager.view.model_ui.AddressUi
import com.openclassrooms.realestatemanager.view.model_ui.PropertyUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListViewModel(application: Application, private val inMemoRepo: InMemoryRepository,
                    private val propertyRepo: PropertyRepository) : AndroidViewModel(application) {

    val properties = MediatorLiveData<List<PropertyUi>>()

    private val dbProperties: LiveData<List<PropertyUi>> = Transformations.map(propertyRepo.allProperties) {
        return@map mapPropertiesForUi(it)
    }


    init {
        properties.addSource(dbProperties) {
            properties.value = it
        }
    }

    private fun mapPropertiesForUi(properties: List<Property>): List<PropertyUi> {
        val uiProperties = arrayListOf<PropertyUi>()
        for (item in properties) {
            val addressUi = AddressUi(item.address.city, item.address.street, item.address.streetNbr)
            val propertyUi = PropertyUi(item.type, item.price, item.area, item.roomNbr,
                    item.description, addressUi, item.agent, item.isSold, item.id)
            propertyUi.thumbnailUri = item.thumbnailUri
            uiProperties.add(propertyUi)
        }
        return uiProperties
    }


    fun filterPropertyByDescription(text: CharSequence) {
        dbProperties.value?.let { dbList ->
            properties.value = dbList.filter { it.description.contains(text, true) }
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
                properties.value = mapPropertiesForUi(filteredProperties)
            }
        }
    }

    // TODO LUCAS Utilise plutôt addSource / removeSource, là c'est du "oneshot",
    //  si dbProperties change plus tard (mise à jour des données, etc), la liste ne sera pas à jour
    fun endAdvancedSearch() {
        properties.value = dbProperties.value
    }
}