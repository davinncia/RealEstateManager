package com.openclassrooms.realestatemanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.model.Agent
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.model.PropertyType

class MainViewModel : ViewModel() {

    private var propertiesMutable = MutableLiveData<List<Property>>()
    val propertiesLiveData: LiveData<List<Property>> = propertiesMutable

    private val propertySelectionMutable = MutableLiveData<Property>()
    val propertySelectionLiveData = propertySelectionMutable

    init {
        fetchProperties()
    }

    private fun fetchProperties() {

        //DUMMY DATA
        val property1 = Property(PropertyType.HOUSE, 23_434_555F, 100F, 3,
                "beauty", Address("N.Y.", "streert", 0), 234242442,
                Agent("Phil", "Delamaison"))
        val property2 = Property(PropertyType.CASTLE, 100_000_000F, 100F, 3,
                "expensive", Address("L.A.", "streert", 0), 234242442,
                Agent("Phil", "Delamaison"))
        val property3 = Property(PropertyType.FLAT, 444_444F, 100F, 3,
                "smallish", Address("Chicago", "streert", 0), 234242442,
                Agent("Phil", "Delamaison"))

        propertiesMutable.value = arrayListOf(property1, property2, property3)
    }

    fun selectProperty(position: Int) {
        if (propertiesLiveData.value == null) return

        propertySelectionMutable.value = propertiesLiveData.value!![position]
    }

}