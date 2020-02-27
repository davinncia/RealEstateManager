package com.openclassrooms.realestatemanager.property_details

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.repository.InMemoryRepository

class DetailsViewModel(inMemoRepo: InMemoryRepository) : ViewModel() {

    private var propertySelectionMutable = inMemoRepo.propertySelectionMutable
    val propertySelection: LiveData<Property> = propertySelectionMutable



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

}