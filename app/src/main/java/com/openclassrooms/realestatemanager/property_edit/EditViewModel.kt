package com.openclassrooms.realestatemanager.property_edit

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.model.Picture
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.model_ui.AddressUi
import com.openclassrooms.realestatemanager.model_ui.EmptyProperty
import com.openclassrooms.realestatemanager.model_ui.PropertyUi
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository
import com.openclassrooms.realestatemanager.utils.AddressConverter
import kotlinx.coroutines.launch

class EditViewModel(application: Application, private val inMemoRepo: InMemoryRepository,
                    private val propertyRepo: PropertyRepository, private val addressConverter: AddressConverter)
    : AndroidViewModel(application) {

    //CURRENT SELECTION
    private val isNew: LiveData<Boolean> = Transformations.map(inMemoRepo.getPropertySelection()) {
        return@map when(it) {
            is EmptyProperty -> true
            else -> false
        }
    }

    val selectedProperty = MediatorLiveData<PropertyUi>()

    //PICTURES
    val allPictures = MediatorLiveData<List<String>>() //Combination of pics from db and the ones being added
    private val dbPictures: LiveData<List<Picture>> = Transformations.switchMap(selectedProperty) {
        propertyRepo.getPictures(it.id)
    }
    private val addedPictures = MutableLiveData<List<String>>()

    init {
        selectedProperty.addSource(isNew) {isNew ->
            if (!isNew) {
                selectedProperty.value = inMemoRepo.getPropertySelection().value as PropertyUi
            }
        }

        allPictures.addSource(dbPictures) { mergePictureLists() }
        allPictures.addSource(addedPictures) { mergePictureLists() }
    }

    /*fun setAsNewProperty(isNew: Boolean) {
        if (isNew) inMemoRepo.setPropertySelection(EmptyProperty)

        selectedProperty.value = inMemoRepo.getPropertySelection().value as PropertyUi
    }

     */

    fun addPicture(uri: String) {
        val pics = arrayListOf<String>()
        addedPictures.value?.let { pics.addAll(it) }
        pics.add(uri)
        addedPictures.value = pics
    }

    fun saveInDb(uiProperty: PropertyUi, isNew: Boolean) {

        if (isNew)
            insert(uiProperty)
        else
            update(uiProperty)
    }

    private fun mergePictureLists(){
        val pics = arrayListOf<String>()

        if (dbPictures.value != null) {
            pics.addAll(dbPictures.value!!.map { it.strUri })
        }
        if (addedPictures.value != null) {
            pics.addAll(addedPictures.value!!)
        }
        allPictures.value = pics
    }

    //--------------------------------------------------------------------------------------//
    //                                 O L D   P R O P E R T Y
    //--------------------------------------------------------------------------------------//
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
            updatedProperty.thumbnailUri = oldProperty.thumbnailUri

            //Updating LatLng if address changed
            val oldAddress = AddressUi(oldProperty.address.city, oldProperty.address.street, oldProperty.address.streetNbr)
            if (uiProperty.address != oldAddress) {
                val latLng = findLatLng(address)
                updatedProperty.address.latitude = latLng.latitude
                updatedProperty.address.longitude = latLng.longitude
            }

            //Updating new property
            inMemoRepo.setPropertySelection(uiProperty)
            propertyRepo.update(updatedProperty)

            //Saving new pictures if any
            addedPictures.value?.let {
                for (uri in it) {
                    savePicture(uri, updatedProperty.id)
                }
            }

        }
    }


    //--------------------------------------------------------------------------------------//
    //                               N E W     P R O P E R T Y
    //--------------------------------------------------------------------------------------//
    private fun insert(uiProperty: PropertyUi) {

        val address = Address(uiProperty.address.city, uiProperty.address.street, uiProperty.address.streetNbr)
        val property = Property(uiProperty.type, uiProperty.price, uiProperty.area, uiProperty.roomNbr, uiProperty.description,
                address, System.currentTimeMillis(), uiProperty.agentName, uiProperty.isSold)

        viewModelScope.launch {
            val latLng = findLatLng(property.address)
            property.address.latitude = latLng.latitude
            property.address.longitude = latLng.longitude

            propertyRepo.insert(property)

            //Pictures
            //Get property Id for picture table
            val id = propertyRepo.getLastId()
            addedPictures.value?.let {
                for (uri in it) {
                    savePicture(uri, id)
                }
            }

        }
    }

    //--------------------------------------------------------------------------------------//
    //                                     P I C T U R E S
    //--------------------------------------------------------------------------------------//
    private suspend fun savePicture(uri: String, propertyId: Int) {

        val pic = Picture(uri, propertyId)

        if (allPictures.value!!.isNotEmpty() && uri == allPictures.value?.get(0)) {
            //1st -> save as thumbnail
            propertyRepo.updateThumbnail(uri, propertyId)
        }

        propertyRepo.insertPicture(pic)
    }

    fun deletePictureFromDb(uri: String, position: Int) {
        //Checking if it was the thumbnail
        viewModelScope.launch {
            if (position == 0) {
                //Change thumbnail
                var thumbnailUri = "android.resource://com.openclassrooms.realestatemanager/drawable/default_house"
                allPictures.value?.let {
                    if (it.size > 1) {
                        thumbnailUri = it[1]
                    }
                }
                propertyRepo.updateThumbnail(thumbnailUri, selectedProperty.value!!.id)  //TODO NPE ?
            }

            val pic = dbPictures.value?.find { it.strUri == uri }
            pic?.apply { propertyRepo.deletePicture(pic) }
        }
    }

    //--------------------------------------------------------------------------------------//
    //                                     L O C A T I O N
    //--------------------------------------------------------------------------------------//
    private fun findLatLng(address: Address): LatLng {
        val strAddress = "${address.streetNbr} ${address.street} ${address.city}"
        return addressConverter.getLatLng(getApplication(), strAddress)
    }
}