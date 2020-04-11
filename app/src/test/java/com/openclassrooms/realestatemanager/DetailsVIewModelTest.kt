package com.openclassrooms.realestatemanager

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.NetworkRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository
import com.openclassrooms.realestatemanager.utils.MainCoroutineRule
import com.openclassrooms.realestatemanager.utils.getOrAwaitValue
import com.openclassrooms.realestatemanager.view.details.DetailsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DetailsVIewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()


    @Mock
    private lateinit var application: Application
    @Mock
    private lateinit var networkRepo: NetworkRepository
    @Mock
    private lateinit var propertyRepo: PropertyRepository
    private lateinit var inMemoRepo: InMemoryRepository
    private lateinit var viewModel: DetailsViewModel

    private val property =
            Property("HOUSE", 1_000, 10F, 1, "Description",
            Address("city", "street", 1), 1L,"agent", false, 1)
    private val apiKey = "Aiy9869869"

    @Before
    fun setUp() {
        Mockito.`when`(application.getString(R.string.googleApiKey))
                .thenReturn(apiKey)
        inMemoRepo = InMemoryRepository.getInstance()
    }

    @Test
    fun correctlyMappingPropertyForView() {
        //GIVEN
        inMemoRepo.setPropertySelection(property)
        viewModel = DetailsViewModel(application, inMemoRepo, networkRepo, propertyRepo)
        //WHEN
        val uiProperty = viewModel.propertyUi.getOrAwaitValue()
        //THEN
        Assert.assertEquals("10.0 m2", uiProperty.area)
        Assert.assertEquals(1, uiProperty.roomNbr)
        Assert.assertEquals("city", uiProperty.city)
        Assert.assertEquals("1 street", uiProperty.vicinity)
        Assert.assertEquals("Description", uiProperty.description)
    }

    @Test
    fun changeSaleStatusIsWorking() = mainCoroutineRule.runBlockingTest {
        //GIVEN
        inMemoRepo.setPropertySelection(property)
        viewModel = DetailsViewModel(application, inMemoRepo, networkRepo, propertyRepo)
        //WHEN
        viewModel.changeSaleStatus()
        val selection = inMemoRepo.getPropertySelection().getOrAwaitValue()
        //THEN
        Assert.assertEquals(true, selection!!.isSold)
    }

    @Test
    fun correctlyParsingStaticMapUrl() {
        //GIVEN
        inMemoRepo.setPropertySelection(property)
        viewModel = DetailsViewModel(application, inMemoRepo, networkRepo, propertyRepo)
        //WHEN
        val uiProperty = viewModel.propertyUi.getOrAwaitValue()
        //THEN
        Assert.assertEquals("https://maps.googleapis.com/maps/api/staticmap?size=300x300&maptype=roadmap%20&markers=color:red%7C1+street+city&key=$apiKey",
                uiProperty.staticMapUrl)
    }
}