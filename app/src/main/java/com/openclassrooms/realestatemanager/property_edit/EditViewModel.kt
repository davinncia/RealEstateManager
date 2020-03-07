package com.openclassrooms.realestatemanager.property_edit

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.model.Picture
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.model_ui.AddressUi
import com.openclassrooms.realestatemanager.model_ui.PropertyUi
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository
import com.openclassrooms.realestatemanager.utils.AddressConverter
import kotlinx.coroutines.launch

class EditViewModel(application: Application, private val inMemoRepo: InMemoryRepository,
                    private val propertyRepo: PropertyRepository, private val addressConverter: AddressConverter)
    : AndroidViewModel(application) {

    val selectedProperty: LiveData<PropertyUi> = inMemoRepo.propertySelectionMutable


    fun saveInDb(uiProperty: PropertyUi, isNew: Boolean) {

        if (isNew)
            insert(uiProperty)
        else
            update(uiProperty)
    }

    private fun update(uiProperty: PropertyUi) {

        viewModelScope.launch {
            //Getting corresponding Property
            val oldProperty = propertyRepo.getProperty(uiProperty.id)

            //Modify fields
            val address = Address(uiProperty.address.city, uiProperty.address.street, uiProperty.address.streetNbr)
            val updatedProperty = Property(uiProperty.type, uiProperty.price, uiProperty.area, uiProperty.roomNbr,
                    uiProperty.description, address, oldProperty.creationTime, uiProperty.agentName,
                    oldProperty.isSold, oldProperty.sellingTime)
            updatedProperty.id = oldProperty.id

            //Updating LatLng if address changed
            val oldAddress = AddressUi(oldProperty.address.city, oldProperty.address.street, oldProperty.address.streetNbr)
            if (uiProperty.address != oldAddress) {
                val latLng = findLatLng(address)
                updatedProperty.address.latitude = latLng.latitude
                updatedProperty.address.longitude = latLng.longitude
            }

            //Updating new property
            inMemoRepo.propertySelectionMutable.value = uiProperty
            propertyRepo.update(updatedProperty)
        }
    }


    private fun insert(uiProperty: PropertyUi) {

        val address = Address(uiProperty.address.city, uiProperty.address.street, uiProperty.address.streetNbr)
        val property = Property(uiProperty.type, uiProperty.price, uiProperty.area, uiProperty.roomNbr, uiProperty.description,
                address, System.currentTimeMillis(), uiProperty.agentName, uiProperty.isSold)

        viewModelScope.launch {
            val latLng = findLatLng(property.address)
            property.address.latitude = latLng.latitude
            property.address.longitude = latLng.longitude

            propertyRepo.insert(property)
        }
    }

    fun savePicture(uri: String) {
        val pic = Picture(uri, selectedProperty.value!!.id)

        viewModelScope.launch {
            propertyRepo.insertPicture(pic)
        }
    }

    private fun findLatLng(address: Address): LatLng {
        val strAddress = "${address.streetNbr} ${address.street} ${address.city}"
        return addressConverter.getLatLng(getApplication(), strAddress)
    }
}