package com.openclassrooms.realestatemanager.view.loan

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.math.BigDecimal

class LoanViewModel : ViewModel() {

    //TODO work with BigDec
    val initialAmount = MutableLiveData<BigDecimal>()
    val amount = MutableLiveData<BigDecimal>()
    val duration = MutableLiveData(BigDecimal.TEN) //Starting with 10 years
    val monthlyDue = MediatorLiveData<BigDecimal>()

    init {
        monthlyDue.addSource(amount) {
            computeMonthlyDue()
        }
        monthlyDue.addSource(duration) {
            computeMonthlyDue()
        }
    }

    private fun computeMonthlyDue() {
        monthlyDue.value = (amount.value!! / (duration.value!! * 12.toBigDecimal()))
    }
    /*private fun computeMonthlyDue() {
        monthlyDue.value = (amount.value!!.div(duration.value!! * 12.toBigDecimal()))
    }*/

    fun adjustAmount(progress: Int) {
        val ratio = (progress.toDouble() / 10).toBigDecimal()
        initialAmount.value?.let {
            amount.value = it * ratio //Getting percentage of the amount given progress bar
        }
    }

    fun adjustDuration(progress: Int) {
        duration.value = progress.toBigDecimal()
    }
}