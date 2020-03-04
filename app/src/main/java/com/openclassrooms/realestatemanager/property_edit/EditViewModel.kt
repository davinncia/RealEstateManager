package com.openclassrooms.realestatemanager.property_edit

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository
import com.openclassrooms.realestatemanager.utils.AddressConverter
import kotlinx.coroutines.launch

class EditViewModel(application: Application, private val inMemoRepo: InMemoryRepository,
                    private val propertyRepo: PropertyRepository)
    : AndroidViewModel(application) {

    val selectedProperty: LiveData<Property> = inMemoRepo.propertySelectionMutable


    fun saveInDb(property: Property, isNew: Boolean) {
        if (isNew)
            insert(property)
        else
            update(property)
    }

    private fun update(property: Property) {
        //Setting previous non editable attributes
        property.id = selectedProperty.value!!.id
        property.creationTime = selectedProperty.value!!.creationTime
        property.address.latitude = selectedProperty.value!!.address.latitude
        property.address.longitude = selectedProperty.value!!.address.longitude
        property.isSold = selectedProperty.value!!.isSold
        property.sellingTime = selectedProperty.value!!.sellingTime

        viewModelScope.launch {
            //Updating LatLng if address changed
            if (property.address != selectedProperty.value!!.address) {
                val latLng = findLatLng(property.address)
                property.address.latitude = latLng.latitude
                property.address.longitude = latLng.longitude
            }
            Log.d("debuglog", property.address.latitude.toString())

            inMemoRepo.propertySelectionMutable.value = property
            propertyRepo.update(property)
        }
    }


    private fun insert(property: Property) {

        viewModelScope.launch {
            val latLng = findLatLng(property.address)
            property.address.latitude = latLng.latitude
            property.address.longitude = latLng.longitude

            propertyRepo.insert(property)
        }
    }

    private fun findLatLng(address: Address): LatLng {
        val strAddress = "${address.streetNbr} ${address.street} ${address.city}"
        return AddressConverter.getLatLng(getApplication(), strAddress)
    }
}