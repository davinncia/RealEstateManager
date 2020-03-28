package com.openclassrooms.realestatemanager.view.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.model.Picture
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.NetworkRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository
import com.openclassrooms.realestatemanager.view.model_ui.AddressUi
import com.openclassrooms.realestatemanager.view.model_ui.PropertyUi
import kotlinx.coroutines.launch

class DetailsViewModel(application: Application, private val inMemoRepo: InMemoryRepository, networkRepo: NetworkRepository,
                       private val propertyRepo: PropertyRepository)
    : AndroidViewModel(application) {

    //NETWORK
    val networkAvailableLiveData: LiveData<Boolean> = networkRepo.isConnected

    //CURRENT SELECTION
    private val propertySelectionMutable = inMemoRepo.getPropertySelection()

    val propertyUi: LiveData<PropertyUi> = Transformations.map(propertySelectionMutable) {
        return@map when (it) {
            is PropertyUi -> propertySelectionMutable.value as PropertyUi
            else -> null //Empty property
        }
    }

    //PICTURES
    val allPictures: LiveData<List<Picture>> = Transformations.switchMap(propertyUi) {
        it?.let { propertyRepo.getPictures(it.id) }
    }


    fun changeSaleStatus() {
        val property = propertyUi.value ?: return

        //Notify observer
        inMemoRepo.setPropertySelection(property.also {it.isSold = !it.isSold })

        //Add in Db
        viewModelScope.launch {
            propertyRepo.updateSaleStatus(property.id, property.isSold, System.currentTimeMillis())
        }
    }

    fun getPropertyPriceStr(): String = (propertySelectionMutable.value as PropertyUi).price.toString()

    fun getStaticMapStringUrlGivenAddress(address: AddressUi, apiKey: String): String {

        val streetUrl = address.street.replace(" ", "+")
        val cityUrl = address.city.replace(" ", "+")
        val urlAddress = "${address.streetNbr}+$streetUrl+$cityUrl"

        return "https://maps.googleapis.com/maps/api/staticmap?size=300x300&maptype=roadmap%20&markers=color:red%7C$urlAddress&key=$apiKey"
    }

}

