package com.openclassrooms.realestatemanager.property_details

import android.app.Application
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.NetworkRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository
import com.openclassrooms.realestatemanager.utils.AddressConverter
import kotlinx.coroutines.launch

class DetailsViewModel(application: Application, inMemoRepo: InMemoryRepository, networkRepo: NetworkRepository,
                       private val propertyRepo: PropertyRepository)
    : AndroidViewModel(application) {


    private var propertySelectionMutable = inMemoRepo.propertySelectionMutable
    val propertySelection: LiveData<Property> = propertySelectionMutable

    val networkAvailableLiveData: LiveData<Boolean> = networkRepo.isConnected

    fun getStaticMapStringUrl(lat: String, lng: String) : String {
        //40.718217
        //-73.998284%20
        val apiKey = Resources.getSystem().getString(R.string.googleApiKey)

        return "https://maps.googleapis.com/maps/api/staticmap?size=300x300&maptype=roadmap%20&markers=color:red%7C$lat,$lng%20&key=$apiKey"
    }

    fun getStaticMapStringUrlGivenAddress(address: Address, apiKey: String) : String {

        val streetUrl = address.street.replace(" ", "+")
        val cityUrl = address.city.replace(" ", "+")
        val urlAddress = "${address.streetNbr}+$streetUrl+$cityUrl"

        return "https://maps.googleapis.com/maps/api/staticmap?size=300x300&maptype=roadmap%20&markers=color:red%7C$urlAddress&key=$apiKey"
    }

    fun changeSaleStatus(){
        //Notify observer
        //propertySelectionMutable.value?.apply { isSold = !isSold }
        propertySelectionMutable.value = propertySelection.value?.also {
            it.isSold = !it.isSold
            it.sellingTime = System.currentTimeMillis()
        }

        viewModelScope.launch {
            propertyRepo.update(propertySelection.value!!)
        }
    }

}