package com.openclassrooms.realestatemanager.property_edit

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.di.ViewModelFactory
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.model_ui.AddressUi
import com.openclassrooms.realestatemanager.model_ui.PropertyUi

class EditActivity : AppCompatActivity() {

    private lateinit var viewModel: EditViewModel

    private var isNew: Boolean = true
    private var propertyId = -1
    private var isSold: Boolean = false

    private lateinit var spinner: Spinner
    private lateinit var priceView: EditText
    private lateinit var areaView: EditText
    private lateinit var roomsView: EditText
    private lateinit var descriptionView: EditText
    private lateinit var cityView: EditText
    private lateinit var streetView: EditText
    private lateinit var streetNbrView: EditText
    private lateinit var agentView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        spinner = findViewById(R.id.spinner_edit_type)
        priceView = findViewById(R.id.et_edit_price)
        areaView = findViewById(R.id.et_edit_area)
        roomsView = findViewById(R.id.et_edit_room_nbr)
        descriptionView = findViewById(R.id.et_edit_description)
        cityView = findViewById(R.id.et_edit_city)
        streetView = findViewById(R.id.et_edit_street)
        streetNbrView = findViewById(R.id.et_edit_street_nbr)
        agentView = findViewById(R.id.et_edit_agent_name)

        isNew = intent.getBooleanExtra(IS_NEW_KEY, true)

        viewModel = ViewModelProvider(this, ViewModelFactory(this.application)).get(EditViewModel::class.java)

        if (!isNew) {
            //Editing an already existing property
            viewModel.selectedProperty.observe(this, Observer {
                if (it.id == -1) {
                    //Yet none is selected...
                    Toast.makeText(this, getString(R.string.select_a_property), Toast.LENGTH_SHORT).show()
                    finish()
                }
                propertyId = it.id
                isSold = it.isSold
                completeEditTexts(it)
            })
        }
    }

    private fun completeEditTexts(property: PropertyUi){
        priceView.setText(property.price.toString())
        areaView.setText(property.area.toString())
        roomsView.setText(property.roomNbr.toString())
        descriptionView.setText(property.description)
        cityView.setText(property.address.city)
        streetView.setText(property.address.street)
        streetNbrView.setText(property.address.streetNbr.toString())
        agentView.setText(property.agentName)
    }

    private fun checkInputsAndSave() {
        //TYPE
        val type = spinner.selectedItem.toString()
        //PRICE
        val strPrice = priceView.text.toString()
        //AREA
        val strArea = areaView.text.toString()
        //ROOM NUMBER
        val strRooms = roomsView.text.toString()
        //DESCRIPTION
        val description = descriptionView.text.toString()
        //CITY
        val city = cityView.text.toString()
        //STREET
        val street = streetView.text.toString()
        //STREET NBR
        val strStreetNbr = streetNbrView.text.toString()
        //AGENT
        val agent = agentView.text.toString()

        val inputs = arrayOf(type, strPrice, strArea, strRooms, description, city, street, strStreetNbr, agent)

        if (inputs.any { it.isEmpty() }){
            Toast.makeText(this, getString(R.string.field_missing), Toast.LENGTH_SHORT).show()
        } else {

            val address = AddressUi(city, street, strStreetNbr.toInt())

            val property = PropertyUi(type, strPrice.toFloat(), strArea.toFloat(), strRooms.toInt(),
                    description, address, agent, isSold, propertyId)

            viewModel.saveInDb(property, isNew)
            Toast.makeText(this, getString(R.string.saving), Toast.LENGTH_SHORT).show()
            this.finish()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.item_edit_menu_save -> {
                checkInputsAndSave()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val IS_NEW_KEY = "is_new_key"

        fun newIntent(context: Context, isNew: Boolean): Intent {
            val intent = Intent(context, EditActivity::class.java)
            intent.putExtra(IS_NEW_KEY, isNew)
            return intent
        }
    }
}
