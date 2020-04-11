package com.openclassrooms.realestatemanager.view.loan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.utils.Utils
import java.math.BigDecimal
import kotlin.math.roundToInt

class LoanViewModel : ViewModel() {

    private val loanInfoMutable = MutableLiveData(LoanInfo())
    val loanInfo: LiveData<LoanInfo> = loanInfoMutable

    //BigDecimal workers
    private var initialAmount = BigDecimal(0)
    private var amount = BigDecimal(0)
    private var duration = BigDecimal(0)
    private var loanRate = BigDecimal(0)
    private var monthlyDue = BigDecimal(0)
    private var bankFee = BigDecimal(0)

    var isEuro = false

    fun setInitialAmount(price: Int) {
        initialAmount = BigDecimal(price)
        amount = BigDecimal(price)
        loanInfoMutable.value = loanInfo.value?.copy(amount = decimalToIntString(amount))
    }

    fun setDuration(years: Int) {
        duration = years.toBigDecimal()
        loanInfoMutable.value = loanInfo.value?.copy(duration = decimalToIntString(duration))
        computeLoanRate(years)
        computeMonthlyDue()
    }

    fun adjustAmount(progress: Int) {
        val ratio = (progress.toDouble() / 10).toBigDecimal()
        amount = initialAmount * ratio //Getting percentage of the amount given progress bar
        loanInfoMutable.value = loanInfo.value?.copy(amount = decimalToIntString(amount))
        computeMonthlyDue()
    }

    fun adjustDuration(years: Int) {
        duration = years.toBigDecimal()
        loanInfoMutable.value = loanInfo.value?.copy(duration = decimalToIntString(duration))
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
        loanInfoMutable.value = loanInfo.value?.copy(interest = decimalToString(loanRate * BigDecimal(100)))
    }

    private fun computeMonthlyDue() {
        val totalInsurance = (amount * BigDecimal(0.0034)) * duration
        val totalInterest = (amount * loanRate) * duration
        val totalAmount = amount + totalInterest + totalInsurance

        monthlyDue = (totalAmount / (duration * 12.toBigDecimal()))
        loanInfoMutable.value = loanInfo.value?.copy(monthlyDue = decimalToString(monthlyDue))
        bankFee = totalInsurance + totalInterest
        loanInfoMutable.value = loanInfo.value?.copy(bankFee = decimalToString(bankFee))
    }

    fun changeCurrency(): Int {
        isEuro = !isEuro

        return if (isEuro) {
            setInitialAmount(Utils.convertDollarToEuro(initialAmount).toInt())
            setDuration(duration.toInt()) //Update values
            R.string.euro_currency
        } else {
            setInitialAmount(Utils.convertEuroToDollar(initialAmount).toInt())
            setDuration(duration.toInt()) //Update values
            R.string.dollar_currency
        }
    }

    private fun decimalToString(dec: BigDecimal): String = String.format("%1$,.2f", dec)
    private fun decimalToIntString(dec: BigDecimal): String = String.format("%,d", dec.toDouble().roundToInt())

    data class LoanInfo (
            var amount: String = "0",
            val duration: String = "0",
            val interest: String = "0",
            val monthlyDue: String = "0",
            val bankFee: String = "0",
            val insuranceRate: String = "0.34"
    )
}