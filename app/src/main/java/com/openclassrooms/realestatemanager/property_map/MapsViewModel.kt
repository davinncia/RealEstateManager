package com.openclassrooms.realestatemanager.property_map

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.model.*
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository
import com.openclassrooms.realestatemanager.utils.AddressConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsViewModel(application: Application, private val inMemoRepo: InMemoryRepository,
                    private val propertyRepo: PropertyRepository) : AndroidViewModel(application) {

    private var propertiesMutable = MutableLiveData<List<Property>>()
    val propertiesLiveData: LiveData<List<Property>> = propertiesMutable

    //val markersLiveData: LiveData<List<PropertyMarker>>

    init {
        fetchProperties()
    }

    private fun fetchProperties() {

        //Get data
        val properties = propertyRepo.allProperties.value ?: return

        //TODO NINO 4: My first coroutine
        viewModelScope.launch {
            checkExistingLatLng(properties)
        }
    }

    private suspend fun checkExistingLatLng(properties: List<Property>){
        withContext(Dispatchers.IO) {

            val filteredProperties: ArrayList<Property> = arrayListOf()

            for (item in properties) {

                if (item.address.latitude == 0.0 && item.address.longitude == 0.0) {
                    //No Position available
                    val strAddress = "${item.address.streetNbr} ${item.address.street} ${item.address.city}"
                    val latLng = AddressConverter.getLatLng(getApplication(), strAddress)

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
            propertiesMutable.postValue(filteredProperties)
        }
    }


    fun selectProperty(id: Int){

        val property = propertiesLiveData.value?.find { it.id == id }
        if (property != null) inMemoRepo.propertySelectionMutable.value = property
    }
}