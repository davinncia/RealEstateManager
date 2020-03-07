package com.openclassrooms.realestatemanager.property_details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.model.Picture
import com.openclassrooms.realestatemanager.model_ui.AddressUi
import com.openclassrooms.realestatemanager.model_ui.PropertyUi
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.NetworkRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository
import kotlinx.coroutines.launch

class DetailsViewModel(application: Application, inMemoRepo: InMemoryRepository, networkRepo: NetworkRepository,
                       private val propertyRepo: PropertyRepository)
    : AndroidViewModel(application) {


    private var propertySelectionMutable = inMemoRepo.propertySelectionMutable

    val activeSelection: LiveData<Boolean> = Transformations.map(propertySelectionMutable) {
        return@map it.id != -1
    }
    val networkAvailableLiveData: LiveData<Boolean> = networkRepo.isConnected
    val propertyUi: LiveData<PropertyUi> = propertySelectionMutable
    val allPictures: LiveData<List<Picture>> = Transformations.switchMap(propertyUi) {
        propertyRepo.getPictures(it.id)
    }

    fun changeSaleStatus() {
        //Notify observer
        propertySelectionMutable.value = propertySelectionMutable.value?.also {
            it.isSold = !it.isSold
        }

        //Add in Db
        viewModelScope.launch {
            val property = propertySelectionMutable.value!!
            propertyRepo.updateSaleStatus(property.id, property.isSold, System.currentTimeMillis())
        }
    }

    fun getStaticMapStringUrlGivenAddress(address: AddressUi, apiKey: String): String {

        val streetUrl = address.street.replace(" ", "+")
        val cityUrl = address.city.replace(" ", "+")
        val urlAddress = "${address.streetNbr}+$streetUrl+$cityUrl"

        return "https://maps.googleapis.com/maps/api/staticmap?size=300x300&maptype=roadmap%20&markers=color:red%7C$urlAddress&key=$apiKey"
    }

}

