package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.repository.NetworkRepository
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class NetworkRepositoryTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun networkIsConnected(){
        //GIVEN
        val networkRepo = NetworkRepository.getInstance(context)
        //WHEN Phone network activated
        //THEN
        Assert.assertTrue(LiveDataTestUtils.getOrAwaitValue(networkRepo.isConnected))
    }

}