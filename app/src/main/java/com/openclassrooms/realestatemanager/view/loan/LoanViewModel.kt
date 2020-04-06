package com.openclassrooms.realestatemanager.view.loan

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.math.BigDecimal
import kotlin.math.roundToInt

class LoanViewModel : ViewModel() {

    //LiveData exposed
    val amountStr = MutableLiveData<String>()
    val durationStr = MutableLiveData<String>()
    val loanPercentStr = MutableLiveData<String>()
    val monthlyDueStr = MutableLiveData<String>()
    val bankFeeStr = MutableLiveData<String>()

    //BigDecimal workers
    private var initialAmount = BigDecimal(0)
    private var amount = BigDecimal(0)
    private var duration = BigDecimal(0)
    private var loanRate = BigDecimal(0)
    private var monthlyDue = BigDecimal(0)
    private var bankFee = BigDecimal(0)

    fun setInitialAmount(price: Int) {
        initialAmount = BigDecimal(price)
        amount = BigDecimal(price)
        amountStr.value = decimalToIntString(amount)
    }

    fun setDuration(years: Int) {
        duration = years.toBigDecimal()
        durationStr.value = decimalToIntString(duration)
        computeLoanRate(years)
        computeMonthlyDue()
    }

    fun adjustAmount(progress: Int) {
        val ratio = (progress.toDouble() / 10).toBigDecimal()
        amount = initialAmount * ratio //Getting percentage of the amount given progress bar
        amountStr.value = decimalToIntString(amount)
        computeMonthlyDue()
    }

    fun adjustDuration(years: Int) {
        duration = years.toBigDecimal()
        durationStr.value = decimalToIntString(duration)
        computeLoanRate(years)
        computeMonthlyDue()
    }

    private fun computeLoanRate(years: Int) {
        var rate = 0.006

        if (years >= 5) {
            for (i in 6..years) {
                rate += 0.0004
            }
        }
        loanRate = rate.toBigDecimal()
        loanPercentStr.value = decimalToString(loanRate * BigDecimal(100))
    }

    private fun computeMonthlyDue() {
        val totalInsurance = (amount * BigDecimal(0.0034)) * duration
        val totalInterest = (amount * loanRate) * duration
        val totalAmount = amount + totalInterest + totalInsurance

        monthlyDue = (totalAmount / (duration * 12.toBigDecimal()))
        monthlyDueStr.value = decimalToString(monthlyDue)
        bankFee = totalInsurance + totalInterest
        bankFeeStr.value = decimalToString(bankFee)
    }

    private fun decimalToString(dec: BigDecimal): String = String.format("%1$,.2f", dec)
    private fun decimalToIntString(dec: BigDecimal): String = String.format("%,d", dec.toDouble().roundToInt())

    //val isEuro = MutableLiveData<Boolean>(false)

    //fun setCurrency(euro: Boolean) {
    //    isEuro.value = euro
    //}
}