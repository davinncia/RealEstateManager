package com.openclassrooms.realestatemanager.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.property_details.DetailsViewModel
import com.openclassrooms.realestatemanager.property_list.ListViewModel
import com.openclassrooms.realestatemanager.repository.InMemoryRepository


class ViewModelFactory: ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {


        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(InMemoryRepository.getInstance()) as T
        } else if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            return ListViewModel(InMemoryRepository.getInstance()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}