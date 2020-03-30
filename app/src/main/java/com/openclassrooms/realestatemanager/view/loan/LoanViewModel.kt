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
    var initialAmount = BigDecimal(0)
    var amount = BigDecimal(0)
    var duration = BigDecimal(0)
    var loanRate = BigDecimal(0)
    var monthlyDue = BigDecimal(0)
    var bankFee = BigDecimal(0)

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

    /*
    val initialAmount = MutableLiveData<BigDecimal>()
    val amount = MutableLiveData<BigDecimal>()
    val duration = MutableLiveData(BigDecimal.TEN) //Years
    val loanRate = MutableLiveData(BigDecimal(0.01))
    val monthlyDue = MediatorLiveData<BigDecimal>()
    val bankFee = MutableLiveData<BigDecimal>()

    //val isEuro = MutableLiveData<Boolean>(false)


    init {
        monthlyDue.addSource(amount) {
            computeMonthlyDue()
        }
        monthlyDue.addSource(duration) {
            computeLoanRate(it.toInt())
            computeMonthlyDue()
        }
    }

    private fun computeLoanRate(years: Int) {
        var rate = 0.006

        if (years >= 5) {
            for (i in 6..years) {
                rate += 0.0004
            }
        }
        loanRate.value = rate.toBigDecimal()
    }

    private fun computeMonthlyDue() {
        val totalInsurance = (amount.value!! * BigDecimal(0.0034)) * duration.value!!
        val totalInterest = (amount.value!! * loanRate.value!!) * duration.value!!
        val totalAmount = amount.value!! + totalInterest + totalInsurance

        monthlyDue.value = (totalAmount / (duration.value!! * 12.toBigDecimal()))
        bankFee.value = totalInsurance + totalInterest
    }

    fun adjustAmount(progress: Int) {
        val ratio = (progress.toDouble() / 10).toBigDecimal()
        initialAmount.value?.let {
            amount.value = it * ratio //Getting percentage of the amount given progress bar
        }
    }

    fun adjustDuration(progress: Int) {
        duration.value = progress.toBigDecimal()
    }
    */

    //fun setCurrency(euro: Boolean) {
    //    isEuro.value = euro
    //}
}