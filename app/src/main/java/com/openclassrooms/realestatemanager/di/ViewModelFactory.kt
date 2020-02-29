package com.openclassrooms.realestatemanager.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.property_details.DetailsViewModel
import com.openclassrooms.realestatemanager.property_list.ListViewModel
import com.openclassrooms.realestatemanager.property_map.MapsViewModel
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.NetworkRepository


class ViewModelFactory(private val application: Application): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(InMemoryRepository.getInstance(), NetworkRepository.getInstance(application),
                    application) as T

        } else if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            return ListViewModel(InMemoryRepository.getInstance()) as T

        } else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(application) as T

        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}