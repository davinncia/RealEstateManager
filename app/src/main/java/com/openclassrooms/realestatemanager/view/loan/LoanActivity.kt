package com.openclassrooms.realestatemanager.view.loan

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.di.ViewModelFactory

class LoanActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    private lateinit var viewModel: LoanViewModel
    private val amountView by bind<TextView>(R.id.tv_amount_loan)
    private val durationView by bind<TextView>(R.id.tv_duration_title_loan)
    private val monthlyDueView by bind<TextView>(R.id.tv_monthly_due_loan)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan)
        actionBar?.title = getString(R.string.loan_simulator)

        initSeekBars()

        viewModel = ViewModelProvider(this, ViewModelFactory(application)).get(LoanViewModel::class.java)
        //Initial data
        intent.extras?.getString(EXTRA_PRICE)?.let {
            viewModel.initialAmount.value = it.toBigDecimal()
            viewModel.amount.value = it.toBigDecimal()
        }
        viewModel.duration.value = 10.toBigDecimal()

        //Observing data
        //val nf = NumberFormat.getNumberInstance(Locale.getDefault())
        viewModel.amount.observe(this, Observer {
            findViewById<TextView>(R.id.tv_amount_loan).text = String.format("%,d", it.toInt())
        })

        viewModel.duration.observe(this, Observer {
            findViewById<TextView>(R.id.tv_duration_loan).text = "$it"
        })

        viewModel.monthlyDue.observe(this, Observer {
            findViewById<TextView>(R.id.tv_monthly_due_loan).text = "$it"
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
        val prog = progress + 1 //Keep it above

        when (seekBar.id) {
            R.id.seekbar_amount_loan -> viewModel.adjustAmount(prog)
            R.id.seekbar_duration_loan -> viewModel.adjustDuration(prog)
        }
    }


    //--------------------------------------------------------------------------------------------//
    //                                   C O M P A N I O N S
    //--------------------------------------------------------------------------------------------//
    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    companion object {
        const val EXTRA_PRICE = "price"

        fun newIntent(context: Context, price: String): Intent {
            val intent = Intent(context, LoanActivity::class.java)
            intent.putExtra(EXTRA_PRICE, price)
            return intent
        }
    }

    private fun <T: View> Activity.bind(@IdRes res: Int) : Lazy<T> {
        return lazy { findViewById<T>(res) }
    }

}
