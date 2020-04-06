package com.openclassrooms.realestatemanager.view.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.di.ViewModelFactory
import com.openclassrooms.realestatemanager.model.Criteria
import com.openclassrooms.realestatemanager.utils.bind
import com.openclassrooms.realestatemanager.view.model_ui.PoiUi
import java.util.*

class SearchActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    private lateinit var viewModel: SearchViewModel
    private lateinit var poiAdapter: PoiAdapter

    //TEXT VIEWS
    private val minPriceView by bind<TextView>(R.id.tv_min_price_search)
    private val maxPriceView by bind<TextView>(R.id.tv_max_price_search)
    private val minAreaView by bind<TextView>(R.id.tv_min_area_search)
    private val maxAreaView by bind<TextView>(R.id.tv_max_area_search)
    private val minPicsView by bind<TextView>(R.id.tv_min_pictures_search)
    //CHECK BOXES
    private val soldCheckBox by bind<CheckBox>(R.id.checkbox_is_sold_yes_search)
    private val onSaleCheckBox by bind<CheckBox>(R.id.checkbox_is_sold_no_search)
    //SEEK BARS
    private val minPriceSeekBar by bind<SeekBar>(R.id.seekBar_min_price_search)
    private val maxPriceSeekBar by bind<SeekBar>(R.id.seekBar_max_price_search)
    private val minAreaSeekBar by bind<SeekBar>(R.id.seekBar_min_area_search)
    private val maxAreaSeekBar by bind<SeekBar>(R.id.seekBar_max_area_search)
    private val minPicsSeekBar by bind<SeekBar>(R.id.seekBar_min_pictures_search)
    //DATE PICKER
    private val datePicker by bind<DatePicker>(R.id.date_picker_search)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        supportActionBar?.title = getString(R.string.advanced_search)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initSeekBars()
        datePicker.updateDate(2000, 0, 1)
        initPoiRecyclerView()

        //ViewModel
        viewModel = ViewModelProvider(this, ViewModelFactory(application)).get(SearchViewModel::class.java)
        viewModel.allPoi.observe(this, androidx.lifecycle.Observer {
            poiAdapter.populateData(it)
        })
    }

    //-------------------------------------------------------------------------------------------//
    //                                    S E E K B A R S
    //-------------------------------------------------------------------------------------------//
    private fun initSeekBars() {
        minPriceSeekBar.setOnSeekBarChangeListener(this)
        maxPriceSeekBar.setOnSeekBarChangeListener(this)
        minAreaSeekBar.setOnSeekBarChangeListener(this)
        maxAreaSeekBar.setOnSeekBarChangeListener(this)
        minPicsSeekBar.setOnSeekBarChangeListener(this)

        minPriceView.text = "0"
        maxPriceView.text = (10 * 100_000).formattedString() //TODO: max or more
        minAreaView.text = "0"
        maxAreaView.text = (10 * 1_000).formattedString() //TODO: max or more
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        seekBar?: return

        when (seekBar.id) {
            minPriceSeekBar.id -> minPriceView.text = (seekBar.progress * 100_000).formattedString()
            maxPriceSeekBar.id -> maxPriceView.text = (seekBar.progress * 100_000).formattedString()
            minAreaSeekBar.id -> minAreaView.text = (seekBar.progress * 1_000).formattedString()
            maxAreaSeekBar.id -> maxAreaView.text = (seekBar.progress * 1_000).formattedString()
            minPicsSeekBar.id -> minPicsView.text = (seekBar.progress).formattedString()
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    //-------------------------------------------------------------------------------------------//
    //                                 D A T E    P I C K E R
    //-------------------------------------------------------------------------------------------//
    private fun getEpochFromDatePicker(): Long {
        var epoch = 0L

        if (datePicker.year != 2000 || datePicker.month != 0 || datePicker.dayOfMonth != 1) {
            //User has set a date
            val calendar = Calendar.getInstance()
            calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            epoch = calendar.timeInMillis
        }
        return epoch
    }

    //-------------------------------------------------------------------------------------------//
    //                                 C H E C K   B O X E S
    //-------------------------------------------------------------------------------------------//
    fun onCheckBoxClicked(view: View) {
        if(view !is CheckBox) return

        val checked: Boolean = view.isChecked

        //Both can't be checked at the same time
        when (view.id) {
            soldCheckBox.id -> {
                if (checked && onSaleCheckBox.isChecked) {
                    onSaleCheckBox.isChecked = false
                }
            }
            onSaleCheckBox.id -> {
                if (checked && soldCheckBox.isChecked) {
                    soldCheckBox.isChecked = false
                }
            }
        }
    }

    //-------------------------------------------------------------------------------------------//
    //                                         P O I
    //-------------------------------------------------------------------------------------------//
    private fun initPoiRecyclerView() {

        poiAdapter = PoiAdapter(object : PoiAdapter.OnPoiCLickListener {
            override fun onPoiClicked(poi: PoiUi) {
                viewModel.handlePoiSelection(poi)
            }
        })

        findViewById<RecyclerView>(R.id.recycler_view_poi_search).apply {
            layoutManager = LinearLayoutManager(
                    this@SearchActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = poiAdapter
        }
    }

    //-------------------------------------------------------------------------------------------//
    //                                   C R I T E R I A
    //-------------------------------------------------------------------------------------------//
    private fun confirmCriteria() {
        val minPrice = minPriceView.text.toString()
        val maxPrice = maxPriceView.text.toString()
        val city = findViewById<EditText>(R.id.et_city_search).text.toString()
        val minArea = minAreaView.text.toString()
        val maxArea = maxAreaView.text.toString()
        val date = getEpochFromDatePicker()
        val minPicsNbr = minPicsView.text.toString()

        val criteria = Criteria(
                minPrice.ridOfSpaces(), maxPrice.ridOfSpaces(),
                city,
                arrayListOf(soldCheckBox.isChecked, !onSaleCheckBox.isChecked),
                minArea.ridOfSpaces(), maxArea.ridOfSpaces(),
                date, 0,
                minPicsNbr.toInt(),
                viewModel.getSelectedPoi())

        //Putting criteria in intent
        intent.putExtra(CRITERIA_EXTRA, criteria)
        if (parent == null)
            setResult(Activity.RESULT_OK, intent)
         else
            parent.setResult(Activity.RESULT_OK, intent)
        finish()
    }

    //-------------------------------------------------------------------------------------------//
    //                                         M E N U
    //-------------------------------------------------------------------------------------------//
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_search_menu_confirm -> {
                confirmCriteria()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //-------------------------------------------------------------------------------------------//
    //                                    C O M P A N I O N S
    //-------------------------------------------------------------------------------------------//
    companion object {
        const val CRITERIA_EXTRA = "criteria"
        const val CRITERIA_RC = 101

        fun newIntent(context: Context): Intent = Intent(context, SearchActivity::class.java)
    }

    //Remove spaces before converting to Int
    private fun String.ridOfSpaces(): Int = this.replace("\\s".toRegex(), "").toInt()

    //Return a thousands separated String
    private fun Int.formattedString(): String = String.format("%,d", this)


}
