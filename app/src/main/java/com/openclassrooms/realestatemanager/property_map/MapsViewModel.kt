package com.openclassrooms.realestatemanager.property_map

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.model_ui.AddressUi
import com.openclassrooms.realestatemanager.model_ui.PropertyMarker
import com.openclassrooms.realestatemanager.model_ui.PropertyUi
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository
import com.openclassrooms.realestatemanager.utils.AddressConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsViewModel(application: Application, private val inMemoRepo: InMemoryRepository,
                    private val propertyRepo: PropertyRepository, private val addressConverter: AddressConverter) : AndroidViewModel(application) {

    private var propertiesMutable = MutableLiveData<List<Property>>()

    val markersLiveData: LiveData<List<PropertyMarker>> = Transformations.map(propertiesMutable) {
        val markers = arrayListOf<PropertyMarker>()

        for (item in it) {
            val latLng = LatLng(item.address.latitude, item.address.longitude)
            val marker = PropertyMarker(latLng, item.type, item.id)
            markers.add(marker)
        }
        return@map markers
    }



    init {
        fetchProperties()
    }

    private fun fetchProperties() {

        //Get data
        val properties = propertyRepo.allProperties.value ?: return

        viewModelScope.launch(Dispatchers.IO) { //Better to inject dispatchers for testing
            checkExistingLatLng(properties)
        }
    }

    private suspend fun checkExistingLatLng(properties: List<Property>){

            val filteredProperties: ArrayList<Property> = arrayListOf()

            for (item in properties) {

                if (item.address.latitude == 0.0 && item.address.longitude == 0.0) {
                    //No Position available
                    val strAddress = "${item.address.streetNbr} ${item.address.street} ${item.address.city}"
                    val latLng = addressConverter.getLatLng(getApplication(), strAddress) //add in factory

                    if (latLng == LatLng(0.0, 0.0)) {
                        //Still not found : won't show on the map
                    } else {
                        item.address.latitude = latLng.latitude
                        item.address.longitude = latLng.longitude
                        propertyRepo.update(item) //Saving in db
                        filteredProperties.add(item)
                    }
                } else {
                    filteredProperties.add(item)
                }
            }

        withContext(Dispatchers.Main) {
            propertiesMutable.value = filteredProperties
        }
    }


    fun selectProperty(id: Int){
        val property = propertiesMutable.value?.find { it.id == id } ?: return

        val addressUi = AddressUi(property.address.city, property.address.street, property.address.streetNbr)
        val uiProperty = PropertyUi(property.type, property.price, property.area, property.roomNbr,
                property.description, addressUi, property.agent, property.isSold, id)

         inMemoRepo.setPropertySelection(uiProperty)
    }
}