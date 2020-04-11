package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.utils.getOrAwaitValue
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
        val info = viewModel.loanInfo.getOrAwaitValue()
        //THEN
        Assert.assertEquals("0,80", info.interest)
    }

    @Test
    fun loanRateForTwentyYearsIsAccurate() {
        //GIVEN
        viewModel.setInitialAmount(initialAmount)
        viewModel.setDuration(20)
        //WHEN
        val info = viewModel.loanInfo.getOrAwaitValue()
        //THEN
        Assert.assertEquals("1,20", info.interest)
    }

    //Monthly due
    @Test
    fun monthlyDueIsAccurate() {
        //GIVEN
        viewModel.setInitialAmount(initialAmount)
        viewModel.setDuration(duration)
        //WHEN
        val info = viewModel.loanInfo.getOrAwaitValue()
        //THEN
        Assert.assertEquals("928,33", info.monthlyDue)
    }

    //Bank fee
    @Test
    fun bankFeesAreAccurate() {
        //GIVEN
        viewModel.setInitialAmount(initialAmount)
        viewModel.setDuration(duration)
        //WHEN
        val info = viewModel.loanInfo.getOrAwaitValue()
        //THEN
        Assert.assertEquals("11 400,00", info.bankFee)
    }

    @Test
    fun convertToEuro() {
        //GIVEN
        viewModel.setInitialAmount(initialAmount)
        viewModel.setDuration(duration)
        //WHEN
        val currency = viewModel.changeCurrency()
        val info = viewModel.loanInfo.getOrAwaitValue()
        //THEN
        Assert.assertTrue(viewModel.isEuro)
        Assert.assertEquals(R.string.euro_currency, currency)
        Assert.assertEquals("92 670", info.amount)
    }

    @Test
    fun convertToDollars() {
        //GIVEN
        viewModel.setInitialAmount(initialAmount)
        viewModel.setDuration(duration)
        //WHEN
        viewModel.changeCurrency() //To euro
        val currency = viewModel.changeCurrency() //Back to dollars
        val info = viewModel.loanInfo.getOrAwaitValue()
        //THEN
        Assert.assertFalse(viewModel.isEuro)
        Assert.assertEquals(R.string.dollar_currency, currency)
        Assert.assertEquals("100 000", info.amount)
    }

}