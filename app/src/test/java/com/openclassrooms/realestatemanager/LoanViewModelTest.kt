package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.utils.awaitValue
import com.openclassrooms.realestatemanager.view.loan.LoanViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.math.BigDecimal

@RunWith(JUnit4::class)
class LoanViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: LoanViewModel

    private val initialAmount = BigDecimal(100_000)
    private val duration = BigDecimal(10)

    @Before
    fun setUp() {
        viewModel = LoanViewModel()
    }

    //Loan rate
    @Test
    fun loanRateForTenYearsIsAccurate() {
        //GIVEN
        viewModel.initialAmount.value = initialAmount
        viewModel.amount.value = initialAmount
        viewModel.duration.value = duration
        //WHEN
        viewModel.monthlyDue.observeForever {  } //Needed to trigger mediator sources
        val rate = awaitValue(viewModel.loanRate)
        //THEN
        Assert.assertEquals(0.008, rate!!.toDouble(), 0.001)
    }

    @Test
    fun loanRateForTwentyYearsIsAccurate() {
        //GIVEN
        viewModel.initialAmount.value = initialAmount
        viewModel.amount.value = initialAmount
        viewModel.duration.value = BigDecimal(20)
        //WHEN
        viewModel.monthlyDue.observeForever {  } //Needed to trigger mediator sources
        val rate = awaitValue(viewModel.loanRate)
        //THEN
        Assert.assertEquals(0.012, rate!!.toDouble(), 0.001)
    }

    //Monthly due
    @Test
    fun monthlyDueIsAccurate() {
        //GIVEN
        viewModel.initialAmount.value = initialAmount
        viewModel.amount.value = initialAmount
        viewModel.duration.value = duration
        //WHEN
        val monthlyDue = awaitValue(viewModel.monthlyDue)
        //THEN
        Assert.assertEquals(928.33, monthlyDue!!.toDouble(), 0.01)
    }

    //Bank fee
    @Test
    fun bankFeesAreAccurate() {
        //GIVEN
        viewModel.initialAmount.value = initialAmount
        viewModel.amount.value = initialAmount
        viewModel.duration.value = duration
        //WHEN
        viewModel.monthlyDue.observeForever {  } //Triggering mediator sources
        val fees = awaitValue(viewModel.bankFee)
        //THEN
        Assert.assertEquals(11_400.00, fees!!.toDouble(), 0.01)
    }
}