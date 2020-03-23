package com.openclassrooms.realestatemanager.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.repository.*
import com.openclassrooms.realestatemanager.utils.AddressConverter
import com.openclassrooms.realestatemanager.view.details.DetailsViewModel
import com.openclassrooms.realestatemanager.view.edit.EditViewModel
import com.openclassrooms.realestatemanager.view.list.ListViewModel
import com.openclassrooms.realestatemanager.view.loan.LoanViewModel
import com.openclassrooms.realestatemanager.view.map.MapsViewModel
import com.openclassrooms.realestatemanager.view.search.SearchViewModel


class ViewModelFactory(private val application: Application): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        when {
            modelClass.isAssignableFrom(DetailsViewModel::class.java) -> {
                return DetailsViewModel(application, InMemoryRepository.getInstance(),
                        NetworkRepository.getInstance(application),
                        PropertyRepository.getInstance(application)) as T
            }
            modelClass.isAssignableFrom(ListViewModel::class.java) -> {
                return ListViewModel(application,
                        InMemoryRepository.getInstance(),
                        PropertyRepository.getInstance(application)) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                return MapsViewModel(application, InMemoryRepository.getInstance(),
                        PropertyRepository.getInstance(application), AddressConverter()) as T
            }
            modelClass.isAssignableFrom(EditViewModel::class.java) -> {
                return EditViewModel(application,
                        InMemoryRepository.getInstance(),
                        PropertyRepository.getInstance(application),
                        AddressConverter(),
                        PoiRepository.getInstance(application),
                        NotificationRepository()) as T
            }
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                return SearchViewModel(application, PoiRepository.getInstance(application)) as T
            }
            modelClass.isAssignableFrom(LoanViewModel::class.java) -> {
                return LoanViewModel() as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}