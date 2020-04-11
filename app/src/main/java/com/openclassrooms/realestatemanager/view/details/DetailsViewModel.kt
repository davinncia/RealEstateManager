package com.openclassrooms.realestatemanager.view.details

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.NetworkRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository
import kotlinx.coroutines.launch

class DetailsViewModel(private val application: Application, private val inMemoRepo: InMemoryRepository,
                       networkRepo: NetworkRepository, private val propertyRepo: PropertyRepository)
    : ViewModel() {

    //NETWORK
    val networkAvailableLiveData: LiveData<Boolean> = networkRepo.isConnected

    //CURRENT SELECTION
    private val propertySelection = inMemoRepo.getPropertySelection()

    //PROPERTY UI
    val propertyUi: LiveData<PropertyDetailsUi> = Transformations.map(propertySelection) {
        //Map property for ui
        it?.let {
            PropertyDetailsUi(
                    it.price.toString(), "${it.area} m2", it.roomNbr, it.description,
                    it.address.city, "${it.address.streetNbr} ${it.address.street}",
                    getStaticMapStringUrlGivenAddress(it.address))
        }
    }

    //PICTURES
    private val allPictures = Transformations.switchMap(propertySelection) { property ->
        property?.let { propertyRepo.getPictures(it.id) }
    }

    val allPictureUris: LiveData<List<String>> = Transformations.map(allPictures) { pictures ->
        ///val uris = propertyRepo.getPictures(property.id).value?.map { it.strUri }
        return@map if (pictures.isEmpty()) {
            listOf("android.resource://com.openclassrooms.realestatemanager/drawable/default_house")
        } else
            pictures.map { it.strUri }
    }


    private fun getStaticMapStringUrlGivenAddress(address: Address): String {
        val apiKey = application.getString(R.string.googleApiKey)

        val streetUrl = address.street.replace(" ", "+")
        val cityUrl = address.city.replace(" ", "+")
        val urlAddress = "${address.streetNbr}+$streetUrl+$cityUrl"

        return "https://maps.googleapis.com/maps/api/staticmap?size=300x300&maptype=roadmap%20&markers=color:red%7C$urlAddress&key=$apiKey"
    }


    fun changeSaleStatus() {
        var property = propertySelection.value ?: return
        val saleStatus = !property.isSold
        //Notify observer
        property = property.copy(isSold = saleStatus)
        property.id = propertySelection.value!!.id //Copy() drops the id
        inMemoRepo.setPropertySelection(property)

        //Add in Db
        viewModelScope.launch {
            propertyRepo.updateSaleStatus(property.id, property.isSold, System.currentTimeMillis())
        }
    }

    fun getPropertyPrice(): Int = propertySelection.value!!.price

    data class PropertyDetailsUi(
            val price: String,
            val area: String,
            val roomNbr: Int,
            val description: String,
            val city: String,
            val vicinity: String,
            val staticMapUrl: String
    )
}