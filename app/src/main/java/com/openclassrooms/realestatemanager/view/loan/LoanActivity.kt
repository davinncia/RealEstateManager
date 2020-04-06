package com.openclassrooms.realestatemanager.view.loan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.di.ViewModelFactory

class LoanActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    //TODO: finish euro conversion functionality

    private lateinit var viewModel: LoanViewModel
//    var isEuro = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan)
        supportActionBar?.title = getString(R.string.loan_simulator)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initSeekBars()

        viewModel = ViewModelProvider(this, ViewModelFactory(application)).get(LoanViewModel::class.java)
        //Initial data
        intent.extras?.getString(EXTRA_PRICE)?.let {
            viewModel.setInitialAmount(it.toInt())
        }
        viewModel.setDuration(10)

        //Observing data
        viewModel.amountStr.observe(this, Observer {
            findViewById<TextView>(R.id.tv_amount_loan).text = it
        })

        viewModel.durationStr.observe(this, Observer {
            findViewById<TextView>(R.id.tv_duration_loan).text = it
        })

        viewModel.loanPercentStr.observe(this, Observer {
            findViewById<TextView>(R.id.tv_rate_loan).text = it
        })

        val insuranceRate = 0.34
        findViewById<TextView>(R.id.tv_insurance_loan).text = "$insuranceRate"

        viewModel.monthlyDueStr.observe(this, Observer {
            findViewById<TextView>(R.id.tv_monthly_due_loan).text = it
        })

        viewModel.bankFeeStr.observe(this, Observer {
            findViewById<TextView>(R.id.tv_bank_fees_loan).text = it
        })

        //CURRENCY
//        viewModel.isEuro.observe(this, Observer {
//            isEuro = it
//        })
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
        val prog = progress + 1 //Keep it above

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
    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.advanced_search_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_default_currency_menu -> {
                if (isEuro) {
                    item.setIcon(R.drawable.ic_euro)
                } else {
                    item.setIcon(R.drawable.ic_dollard)
                }
                changeCurrency(!isEuro)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun changeCurrency(euro: Boolean) {
        viewModel.setCurrency(euro)
        
        if (euro) {
            findViewById<TextView>(R.id.tv_amount_currency_loan).text = resources.getString(R.string.euro_currency)
        } else {
            findViewById<TextView>(R.id.tv_amount_currency_loan).text = resources.getString(R.string.dollard_currency)

        }
    }

     */

    //--------------------------------------------------------------------------------------------//
    //                                   C O M P A N I O N
    //--------------------------------------------------------------------------------------------//
    companion object {
        const val EXTRA_PRICE = "price"

        fun newIntent(context: Context, price: String): Intent {
            val intent = Intent(context, LoanActivity::class.java)
            intent.putExtra(EXTRA_PRICE, price)
            return intent
        }
    }


}
