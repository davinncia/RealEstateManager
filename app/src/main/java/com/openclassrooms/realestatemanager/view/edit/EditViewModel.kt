package com.openclassrooms.realestatemanager.view.edit

import android.app.Application
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.model.Picture
import com.openclassrooms.realestatemanager.model.PoiForProperty
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.NotificationRepository
import com.openclassrooms.realestatemanager.repository.PoiRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository
import com.openclassrooms.realestatemanager.utils.AddressConverter
import com.openclassrooms.realestatemanager.view.model_ui.PoiUi
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditViewModel(application: Application, private val inMemoRepo: InMemoryRepository,
                    private val propertyRepo: PropertyRepository, private val addressConverter: AddressConverter,
                    poiRepo: PoiRepository, private val notifRepo: NotificationRepository)
    : AndroidViewModel(application) {

    //CURRENT SELECTION
    private val isNew = inMemoRepo.getPropertySelection().value == null
    val selectedProperty = MutableLiveData<Property>()

    //PICTURES
    val allPictures = MediatorLiveData<List<String>>() //Combination of pics from db and the ones being added
    private val dbPictures: LiveData<List<Picture>> = Transformations.switchMap(selectedProperty) {
        propertyRepo.getPictures(it.id)
    }

    private val addedPictures = MutableLiveData<List<String>>()

    //POI
    val allPoi = MediatorLiveData<List<PoiUi>>()

    private val savedPoi = Transformations.switchMap(selectedProperty) { property ->
        Transformations.map(propertyRepo.getPoiList(property.id)) { poiList ->
            poiList.map { it.poiName }
        }
    }

    init {
        inMemoRepo.getPropertySelection().value?.let {
            //Get the property that is currently selected
            selectedProperty.value = it
        }

        allPictures.addSource(dbPictures) { mergePictureLists() }
        allPictures.addSource(addedPictures) { mergePictureLists() }

        allPoi.addSource(poiRepo.allPoi()) { list ->
            allPoi.value = list.map {
                PoiUi(it.name, it.iconResourceId, false)
            }

            allPoi.addSource(savedPoi) { names ->
                val poiSelection = allPoi.value
                for (name in names) {
                    poiSelection?.find { it.name == name }?.let {
                        it.isSelected = true
                    }
                }
                allPoi.value = poiSelection
                allPoi.removeSource(savedPoi)
            }

        }
    }

    fun saveDataInDb(type: String, strPrice: String, strArea: String, strRooms: String,
                     description: String, city: String, street: String, strStreetNbr: String, agent: String) {

        //Parsing object
        val address = Address(city, street, strStreetNbr.toInt())

        val property = Property(type, strPrice.toInt(), strArea.toFloat(), strRooms.toInt(),
                description, address, 0L, agent, false, 0L)

        //Insert or Update
        if (isNew) {
            insert(property)
            notifRepo.sendNewPropertyNotif(getApplication())
        } else
            update(property)
    }

    //--------------------------------------------------------------------------------------//
    //                                 O L D   P R O P E R T Y
    //--------------------------------------------------------------------------------------//
    private fun update(updatedProperty: Property) {

        viewModelScope.launch {
            //Getting corresponding Property
            val oldProperty = propertyRepo.getProperty(selectedProperty.value!!.id)

            //Restore immutable fields
            updatedProperty.apply {
                creationTime = oldProperty.creationTime
                isSold = oldProperty.isSold
                sellingTime = oldProperty.sellingTime
                id = oldProperty.id
                thumbnailUri = oldProperty.thumbnailUri
            }

            //Updating LatLng if address changed
            if (updatedProperty.address != oldProperty.address) {
                val latLng = findLatLng(updatedProperty.address)
                updatedProperty.address.latitude = latLng.latitude
                updatedProperty.address.longitude = latLng.longitude
            }

            inMemoRepo.setPropertySelection(updatedProperty)
            propertyRepo.update(updatedProperty)

            //Saving new pictures if any
            addedPictures.value?.let {
                for (uri in it) {
                    savePictureInDb(uri, updatedProperty.id)
                }
            }

            //Poi if any change
            updatePoiForProperty(updatedProperty.id)
        }
    }


    //--------------------------------------------------------------------------------------//
    //                               N E W     P R O P E R T Y
    //--------------------------------------------------------------------------------------//
    private fun insert(property: Property) {

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
                    savePictureInDb(uri, id)
                }
            }

            //Poi
            savePoiForProperty(id)

            //Select
            property.id = id
            inMemoRepo.setPropertySelection(property)
        }
    }

    //--------------------------------------------------------------------------------------//
    //                                     P I C T U R E S
    //--------------------------------------------------------------------------------------//
    private fun mergePictureLists() {
        val pics = arrayListOf<String>()

        if (dbPictures.value != null) {
            pics.addAll(dbPictures.value!!.map { it.strUri })
        }
        if (addedPictures.value != null) {
            pics.addAll(addedPictures.value!!)
        }
        allPictures.value = pics
    }

    //DATABASE
    private suspend fun savePictureInDb(uri: String, propertyId: Int) {

        val pic = Picture(uri, propertyId)

        if (allPictures.value!!.isNotEmpty() && uri == allPictures.value?.get(0)) {
            //1st -> save as thumbnail
            propertyRepo.updateThumbnail(uri, propertyId)
        }

        propertyRepo.insertPicture(pic)
    }

    fun addPictureFromIntent(data: Intent) {

        val uri: String? =
                if (data.data != null) {
                    //GALLERY
                    data.data.toString()

                } else {
                    //CAMERA
                    val imageBitmap = data.extras?.get("data") as Bitmap
                    saveImageInMediaStore(imageBitmap)?.toString()
                }
        uri?: return //No picture found in intent

        val pics = arrayListOf<String>()
        addedPictures.value?.let { pics.addAll(it) }
        pics.add(uri)
        addedPictures.value = pics
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
                propertyRepo.updateThumbnail(thumbnailUri, selectedProperty.value!!.id)
            }

            val pic = dbPictures.value?.find { it.strUri == uri }
            pic?.apply { propertyRepo.deletePicture(pic) }
        }
    }

    //MEDIA STORE
    private fun saveImageInMediaStore(pic: Bitmap): Uri? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, timeStamp)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        }

        val resolver = getApplication<Application>().contentResolver

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        uri?.let {
            resolver.openOutputStream(uri)?.use { outputStream ->
                pic.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            }

            resolver.update(uri, values, null, null)

        } ?: throw RuntimeException("MediaStore failed for some reason")
        return uri
    }

    //--------------------------------------------------------------------------------------//
    //                                        P O I
    //--------------------------------------------------------------------------------------//
    fun handlePoiSelection(poi: PoiUi) {
        val poiList = allPoi.value!!
        poiList[poiList.indexOf(poi)].isSelected = !poiList[poiList.indexOf(poi)].isSelected
        allPoi.value = poiList
    }

    private suspend fun savePoiForProperty(id: Int) {
        val selectedPoi = allPoi.value?.filter { it.isSelected }
        //If some selected save in db
        selectedPoi?.let { list ->
            propertyRepo.addPoiForProperty(list.map { PoiForProperty(id, it.name) })
        }
    }

    private fun updatePoiForProperty(id: Int) {
        //Check if there is any differences
        val currentSelection = allPoi.value!!.filter { it.isSelected }.map { it.name }
        val oldSelection = savedPoi.value ?: listOf()

        if (currentSelection.size == oldSelection.size && currentSelection.containsAll(oldSelection))
            return //No changes

        viewModelScope.launch {
            propertyRepo.deleteAllPoiForProperty(id)
            savePoiForProperty(id)
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
