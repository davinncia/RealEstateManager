package com.openclassrooms.realestatemanager

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository
import com.openclassrooms.realestatemanager.utils.AddressConverter
import com.openclassrooms.realestatemanager.utils.MainCoroutineRule
import com.openclassrooms.realestatemanager.utils.getOrAwaitValue
import com.openclassrooms.realestatemanager.view.map.MapsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {

    // Run tasks synchronously
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    // Sets the main coroutines dispatcher to a TestCoroutineScope for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    lateinit var application: Application
    @Mock
    lateinit var addressConverter: AddressConverter
    @Mock
    lateinit var propertyRepo: PropertyRepository


    // Class under test. Uses Dispatchers.Main so that the MainCoroutineRule can control it.
    private lateinit var viewModel: MapsViewModel

    @Before
    fun setUp() {

        MockitoAnnotations.initMocks(this)
        // Initialize the ViewModel after the [MainCoroutineRule] is applied so that it has the
        // right test dispatcher.

    }


    //Getting corresponding markers for each property with valid LatLng
    @Test
    fun getMarkersForProperties() = mainCoroutineRule.runBlockingTest {
        //GIVEN
        val property1 = Property("HOUSE", 0F, 0F, 0, "",
                Address("", "", 0, 1.0, 1.0),
                1L, "")
        val property2 = Property("HOUSE", 0F, 0F, 0, "",
                Address("", "", 0, 2.0, 2.0),
                1L, "")
        val list = listOf(property1, property2)
        val listLiveData = MutableLiveData(list)

        Mockito.`when`(propertyRepo.allProperties).thenReturn(listLiveData)
        viewModel = MapsViewModel(application, InMemoryRepository.getInstance(), propertyRepo, addressConverter)

        //WHEN
        val markers = viewModel.markersLiveData.getOrAwaitValue()

        //THEN
        Assert.assertTrue(markers.size == 2)
        Assert.assertTrue(markers[0].latLng == LatLng(1.0, 1.0))
        Assert.assertTrue(markers[1].latLng == LatLng(2.0, 2.0))
    }

    //Fetching lat lng if non existent
    @Test
    fun latLngIsFetchedWhenNonExistent() = mainCoroutineRule.runBlockingTest{
        //GIVEN
        val address = Address("", "", 0, 0.0, 0.0)
        val strAddress = "${address.streetNbr} ${address.street} ${address.city}"
        val property = Property("HOUSE", 0F, 0F, 0, "",
                address,1L, "")
        val list = listOf(property)
        val listLiveData = MutableLiveData(list)

        val newLatLng = LatLng(2.2, 2.2)

        Mockito.`when`(propertyRepo.allProperties).thenReturn(listLiveData)
        Mockito.`when`(addressConverter.getLatLng(application, strAddress)).thenReturn(newLatLng)
        viewModel = MapsViewModel(application, InMemoryRepository.getInstance(), propertyRepo, addressConverter)

        //WHEN
        val markers = viewModel.markersLiveData.getOrAwaitValue()

        //THEN
        Assert.assertTrue(markers[0].latLng == newLatLng)
    }

    //property selection
    @Test
    fun ifNoLatLngFoundMarkerIsNotCreated() {
        //GIVEN
        val address = Address("", "", 0, 0.0, 0.0)
        val strAddress = "${address.streetNbr} ${address.street} ${address.city}"
        val property = Property("HOUSE", 0F, 0F, 0, "",
                address,1L, "")
        val list = listOf(property)
        val listLiveData = MutableLiveData(list)

        val nullLatLng = LatLng(0.0, 0.0)

        Mockito.`when`(propertyRepo.allProperties).thenReturn(listLiveData)
        Mockito.`when`(addressConverter.getLatLng(application, strAddress)).thenReturn(nullLatLng)
        viewModel = MapsViewModel(application, InMemoryRepository.getInstance(), propertyRepo, addressConverter)

        //WHEN
        val markers = viewModel.markersLiveData.getOrAwaitValue()

        //THEN
        Assert.assertTrue(markers.isEmpty())
    }
}