package com.openclassrooms.realestatemanager.property_map

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.model.*
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.utils.AddressConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsViewModel(private val application: Application, private val inMemoRepo: InMemoryRepository) : ViewModel() {

    private var propertiesMutable = MutableLiveData<List<Property>>()
    val propertiesLiveData: LiveData<List<Property>> = propertiesMutable

    //val markersLiveData: LiveData<List<PropertyMarker>>

    init {
        fetchProperties()

        /*
        markersLiveData = Transformations.map(propertiesMutable) {

            val markers: ArrayList<PropertyMarker> = arrayListOf()

            for (property in it) {
                markers.add(PropertyMarker(LatLng(property.address.latitude, property.address.longitude),
                        property.type.toString()))
            }
            return@map markers
        }

         */

    }

    private fun fetchProperties() {

        //DUMMY DATA
        val property1 = Property(PropertyType.HOUSE, 23_434_555F, 400F, 9,
                "Beauty property, enough for one big family.", Address("Le Mas d'Agenais", "Rue Labarthe", 1), 234242442,
                Agent("Phil", "Delamaison"))
        val property2 = Property(PropertyType.CASTLE, 100_000_000F, 2930F, 28,
                "Expensive domain, better be rich af.", Address("L.A.", "streert", 2), 234242442,
                Agent("Phil", "Delamaison"))
        val property3 = Property(PropertyType.FLAT, 444_444F, 92F, 3,
                "Smallish flat, nice for a first investment.", Address("Chicago", "streert", 39), 234242442,
                Agent("Phil", "Delamaison"))

        //TODO NINO 4: My first coroutine
        viewModelScope.launch {
            checkExistingLatLng(arrayListOf(property1, property2, property3))
        }


    }


    private suspend fun checkExistingLatLng(properties: List<Property>){
        withContext(Dispatchers.IO) {

            val filteredProperties: ArrayList<Property> = arrayListOf()

            for (item in properties) {

                if (item.address.latitude == 0.0 && item.address.longitude == 0.0) {
                    //No Position available
                    val strAddress = "${item.address.streetNbr} ${item.address.street} ${item.address.city}"
                    val latLng = AddressConverter.getLatLng(application, strAddress)

                    if (latLng == LatLng(0.0, 0.0)) {
                        //Still not found : won't show on the map
                        Log.d("debuglog", "GeoCoder failed.")
                    } else {
                        item.address.latitude = latLng.latitude
                        item.address.longitude = latLng.longitude
                        //TODO: save in db
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

        val property = propertiesLiveData.value?.find { it.roomNbr == id } //TODO: Use id again

        if (property != null) inMemoRepo.propertySelectionMutable.value = property
    }
}