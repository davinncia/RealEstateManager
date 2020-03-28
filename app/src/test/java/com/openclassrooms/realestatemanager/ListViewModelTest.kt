package com.openclassrooms.realestatemanager

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository
import com.openclassrooms.realestatemanager.utils.MainCoroutineRule
import com.openclassrooms.realestatemanager.utils.getOrAwaitValue
import com.openclassrooms.realestatemanager.view.list.ListViewModel
import com.openclassrooms.realestatemanager.view.model_ui.PropertyUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class ListViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Sets the main coroutines dispatcher to a TestCoroutineScope for unit testing.
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: ListViewModel
    private lateinit var dbProperties: ArrayList<Property>

    private lateinit var inMemoRepo: InMemoryRepository
    @Mock
    private lateinit var propertyRepo: PropertyRepository
    @Mock
    private lateinit var application: Application

    @Before
    fun setUp() {
        //DUMMY DATA
        dbProperties = arrayListOf(
                Property("HOUSE", 100_000, 100F, 1, "One", Address("", "", 1, 1.0, 1.0),
                        1L, ""),
                Property("HOUSE", 200_000, 200F, 2, "Two", Address("", "", 2, 2.0, 2.0),
                        2L, "")
        )
        Mockito.`when`(propertyRepo.allProperties).thenReturn(MutableLiveData(dbProperties))

        inMemoRepo = InMemoryRepository.getInstance()
        viewModel = ListViewModel(application, inMemoRepo, propertyRepo)
    }

    //Mapping
    @Test
    fun propertiesMappedCorrectlyForUi() {
        //GIVEN set up
        //WHEN
        val uiProperties = viewModel.properties.getOrAwaitValue()
        //THEN
        Assert.assertTrue(uiProperties.size == 2)
        Assert.assertEquals(dbProperties[0].price, uiProperties[0].price)
        Assert.assertEquals(dbProperties[0].description, uiProperties[0].description)
        Assert.assertEquals(dbProperties[1].price, uiProperties[1].price)
        Assert.assertEquals(dbProperties[1].description, uiProperties[1].description)
    }

    //Filter
    @Test
    fun propertyFilterByDescriptionIsWorking() {
        //GIVEN
        val charSeq = "tw"
        //WHEN
        val uiProperties = viewModel.properties.getOrAwaitValue() //Needed to trigger Transformations.map
        viewModel.filterPropertyByDescription(charSeq)
        val filteredProperties = viewModel.properties.getOrAwaitValue()
        //THEN
        Assert.assertTrue(filteredProperties.size == 1)
        Assert.assertTrue(filteredProperties[0].description == "Two")
    }

    //Selection
    @Test
    fun changeActiveSelection() {
        //GIVEN
        dbProperties[0].id = 1
        //WHEN
        val uiProperties = viewModel.properties.getOrAwaitValue() //Needed to trigger Transformations.map
        viewModel.selectProperty(1)
        val selected = inMemoRepo.getPropertySelection().getOrAwaitValue() as PropertyUi
        //THEN
        Assert.assertTrue(selected.description == "One")
    }

    /* TODO advanced search test
    //SEARCH
    //Picture
    @Test
    fun advancedSearchMinPictureNbrCriterion() = mainCoroutineRule.runBlockingTest {
        //GIVEN
        val crtr = Criteria(0, 150_00, "", arrayListOf(false, false), 0, 10_000,
                0L, 0L, 1, arrayListOf())
        Mockito.`when`(propertyRepo.advancedSearch(crtr.minPrice, crtr.maxPrice, crtr.minArea, crtr.maxArea,
                crtr.city, crtr.minCreationTime, crtr.isSold)).thenReturn(
                listOf( Property("HOUSE", 100_000, 100F, 1, "One",
                        Address("", "", 1, 1.0, 1.0), 1L, "")
                ))
        //WHEN
        viewModel.advancedSearch(crtr)
        val uiProperties = viewModel.properties.getOrAwaitValue()
        //THEN
        Assert.assertTrue(uiProperties.size == 1)

    }
    */


}