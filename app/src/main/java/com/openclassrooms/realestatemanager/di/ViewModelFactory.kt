package com.openclassrooms.realestatemanager.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.property_details.DetailsViewModel
import com.openclassrooms.realestatemanager.property_edit.EditViewModel
import com.openclassrooms.realestatemanager.property_list.ListViewModel
import com.openclassrooms.realestatemanager.property_map.MapsViewModel
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.NetworkRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository


class ViewModelFactory(private val application: Application): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(application, InMemoryRepository.getInstance(),
                    NetworkRepository.getInstance(application),
                    PropertyRepository.getInstance(application)) as T

        } else if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            return ListViewModel(application,
                    InMemoryRepository.getInstance(),
                    PropertyRepository.getInstance(application)) as T

        } else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(application, InMemoryRepository.getInstance(),
                    PropertyRepository.getInstance(application)) as T

        } else if (modelClass.isAssignableFrom(EditViewModel::class.java)) {
            return EditViewModel(application,
                    InMemoryRepository.getInstance(),
                    PropertyRepository.getInstance(application)) as T

        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}