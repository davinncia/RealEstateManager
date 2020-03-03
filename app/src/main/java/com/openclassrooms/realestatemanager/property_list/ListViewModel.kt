package com.openclassrooms.realestatemanager.property_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.model.Agent
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.model.PropertyType
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository
import kotlinx.coroutines.launch

class ListViewModel(application: Application,
                    private val inMemoRepo: InMemoryRepository,
                    private val propertyRepo: PropertyRepository) : AndroidViewModel(application) {

    //private var propertiesMutable = MutableLiveData<List<Property>>()
    val allProperties: LiveData<List<Property>> = propertyRepo.allProperties

    init {
        //fetchProperties()
    }

    /*
    private fun fetchProperties() {

        //DUMMY DATA
        val property1 = Property(PropertyType.CASTLE, 23_434_555F, 400F, 9,
                "Beauty property, enough for one big family.", Address("Le Mas d'Agenais", "Rue Labarthe", 1), 234242442,
                Agent("Phil", "Delamaison"))
        val property2 = Property(PropertyType.CASTLE, 100_000_000F, 2930F, 28,
                "Expensive domain, better be rich af.", Address("L.A.", "streert", 2), 234242442,
                Agent("Phil", "Delamaison"))
        val property3 = Property(PropertyType.FLAT, 444_444F, 92F, 3,
                "Smallish flat, nice for a first investment.", Address("Chicago", "streert", 39), 234242442,
                Agent("Phil", "Delamaison"))

        propertiesMutable.value = arrayListOf(property1, property2, property3)
    }
     */

    fun insert(property: Property) = viewModelScope.launch {
        propertyRepo.insert(property)
    }

    fun update(property: Property) = viewModelScope.launch {
        propertyRepo.update(property)
    }

    fun selectProperty(id: Int) {

        val property = allProperties.value?.find { it.id == id }
        inMemoRepo.propertySelectionMutable.value = property

    }

}