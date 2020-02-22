package com.openclassrooms.realestatemanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PropertyViewModel : ViewModel() {

    private var propertiesMutable = MutableLiveData<List<String>>()
    val propertiesLiveData: LiveData<List<String>> = propertiesMutable

    private val propertySelectionMutable = MutableLiveData<String>()
    val propertySelectionLiveData = propertySelectionMutable

    init {
        fetchProperties()
    }

    private fun fetchProperties() {
        propertiesMutable.value = arrayListOf("1", "2", "3", "4", "5", "6")
    }

    fun selectProperty(position: Int) {
        if (propertiesLiveData.value == null) return

        propertySelectionMutable.value = propertiesLiveData.value!![position]
    }
}