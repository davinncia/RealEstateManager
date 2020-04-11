package com.openclassrooms.realestatemanager.view.loan

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.di.ViewModelFactory

class LoanActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    private lateinit var viewModel: LoanViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan)
        supportActionBar?.title = getString(R.string.loan_simulator)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initSeekBars()

        viewModel = ViewModelProvider(this, ViewModelFactory(application)).get(LoanViewModel::class.java)
        //Initial data
        intent.extras?.getInt(EXTRA_PRICE)?.let {
            viewModel.setInitialAmount(it)
        }
        viewModel.setDuration(10)


        viewModel.loanInfo.observe(this, Observer {
            findViewById<TextView>(R.id.tv_amount_loan).text = it.amount
            findViewById<TextView>(R.id.tv_duration_loan).text = it.duration
            findViewById<TextView>(R.id.tv_rate_loan).text = it.interest
            findViewById<TextView>(R.id.tv_insurance_loan).text = it.insuranceRate
            findViewById<TextView>(R.id.tv_monthly_due_loan).text = it.monthlyDue
            findViewById<TextView>(R.id.tv_bank_fees_loan).text = it.bankFee
        })

    }

    //--------------------------------------------------------------------------------------------//
    //                                          S E E K B A R S
    //--------------------------------------------------------------------------------------------//
    private fun initSeekBars() {
        findViewById<SeekBar>(R.id.seekbar_amount_loan).setOnSeekBarChangeListener(this)
        findViewById<SeekBar>(R.id.seekbar_duration_loan).setOnSeekBarChangeListener(this)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        seekBar ?: return
        val prog = progress + 1 //Keep it above 0

        when (seekBar.id) {
            R.id.seekbar_amount_loan -> viewModel.adjustAmount(prog)
            R.id.seekbar_duration_loan -> viewModel.adjustDuration(prog)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    //--------------------------------------------------------------------------------------------//
    //                                            M E N U
    //--------------------------------------------------------------------------------------------//
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.advanced_search_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_default_currency_menu -> {
                if (viewModel.isEuro) {
                    item.setIcon(R.drawable.ic_dollard)
                } else {
                    item.setIcon(R.drawable.ic_euro)
                }
                changeCurrencyUi()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun changeCurrencyUi() {
        val currencyPath = viewModel.changeCurrency()
        val currency = resources.getString(currencyPath)

        findViewById<SeekBar>(R.id.seekbar_amount_loan).progress = 10
        findViewById<TextView>(R.id.tv_amount_currency_loan).text = currency
        findViewById<TextView>(R.id.tv_bank_fee_currency_loan).text = currency
        findViewById<TextView>(R.id.tv_monthly_due_unit_loan).text = "$currency ${resources.getString(R.string.per_month)}"
    }

    //--------------------------------------------------------------------------------------------//
    //                                   C O M P A N I O N
    //--------------------------------------------------------------------------------------------//
    companion object {
        const val EXTRA_PRICE = "price"

        fun newIntent(context: Context, price: Int): Intent {
            val intent = Intent(context, LoanActivity::class.java)
            intent.putExtra(EXTRA_PRICE, price)
            return intent
        }
    }

}
