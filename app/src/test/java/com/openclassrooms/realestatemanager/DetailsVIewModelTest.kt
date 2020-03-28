package com.openclassrooms.realestatemanager

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.repository.InMemoryRepository
import com.openclassrooms.realestatemanager.repository.NetworkRepository
import com.openclassrooms.realestatemanager.repository.PropertyRepository
import com.openclassrooms.realestatemanager.utils.MainCoroutineRule
import com.openclassrooms.realestatemanager.utils.getOrAwaitValue
import com.openclassrooms.realestatemanager.view.details.DetailsViewModel
import com.openclassrooms.realestatemanager.view.model_ui.AddressUi
import com.openclassrooms.realestatemanager.view.model_ui.PropertyUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
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

    private val property = PropertyUi("HOUSE", 0, 0F, 0, "",
            AddressUi("", "", 0), "", false, 1)

    @Before
    fun setUp() {

        inMemoRepo = InMemoryRepository.getInstance()
    }

    @Test
    fun getCurrentActiveSelection() {
        //GIVEN
        inMemoRepo.setPropertySelection(property)
        viewModel = DetailsViewModel(application, inMemoRepo, networkRepo, propertyRepo)
        //WHEN
        val selection = viewModel.propertyUi.getOrAwaitValue()
        //THEN
        Assert.assertEquals(property, selection)
    }

    @Test
    fun changeSaleStatusIsWorking() = mainCoroutineRule.runBlockingTest {
        //GIVEN
        inMemoRepo.setPropertySelection(property)
        viewModel = DetailsViewModel(application, inMemoRepo, networkRepo, propertyRepo)
        //WHEN
        val selection = viewModel.propertyUi.getOrAwaitValue()
        viewModel.changeSaleStatus()
        //THEN
        Assert.assertEquals(true, selection.isSold)
    }
}