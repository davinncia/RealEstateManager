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

@RunWith(JUnit4::class)
class LoanViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: LoanViewModel

    private val initialAmount = 100_000
    private val duration = 10

    @Before
    fun setUp() {
        viewModel = LoanViewModel()
    }

    //Loan rate
    @Test
    fun loanRateForTenYearsIsAccurate() {
        //GIVEN
        viewModel.setInitialAmount(initialAmount)
        viewModel.setDuration(duration)
        //WHEN
        val rate = awaitValue(viewModel.loanPercentStr)
        //THEN
        //Assert.assertEquals(0.008, viewModel.loanRate.toDouble(), 0.001)
        Assert.assertEquals("0,80", rate!!)
    }

    @Test
    fun loanRateForTwentyYearsIsAccurate() {
        //GIVEN
        viewModel.setInitialAmount(initialAmount)
        viewModel.setDuration(20)
        //WHEN
        val rate = awaitValue(viewModel.loanPercentStr)
        //THEN
        //Assert.assertEquals(0.012, viewModel.loanRate.toDouble(), 0.001)
        Assert.assertEquals("1,20", rate!!)
    }

    //Monthly due
    @Test
    fun monthlyDueIsAccurate() {
        //GIVEN
        viewModel.setInitialAmount(initialAmount)
        viewModel.setDuration(duration)
        //WHEN
        val monthlyDue = awaitValue(viewModel.monthlyDueStr)
        //THEN
        //Assert.assertEquals(928.33, viewModel.monthlyDue.toDouble(), 0.01)
        Assert.assertEquals("928,33", monthlyDue!!)
    }

    //Bank fee
    @Test
    fun bankFeesAreAccurate() {
        //GIVEN
        viewModel.setInitialAmount(initialAmount)
        viewModel.setDuration(duration)
        //WHEN
        val fees = awaitValue(viewModel.bankFeeStr)
        //THEN
        //Assert.assertEquals(11_400.00, viewModel.bankFee.toDouble(), 0.01)
        Assert.assertEquals("11 400,00", fees!!)
    }

    /*
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

     */

}