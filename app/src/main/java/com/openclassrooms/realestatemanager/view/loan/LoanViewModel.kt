package com.openclassrooms.realestatemanager.view.loan

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.math.BigDecimal

class LoanViewModel : ViewModel() {

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

    //fun setCurrency(euro: Boolean) {
    //    isEuro.value = euro
    //}
}