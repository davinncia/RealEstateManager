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
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.di.ViewModelFactory
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.model.Property

class EditActivity : AppCompatActivity() {

    private lateinit var viewModel: EditViewModel

    private var isNew: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        isNew = intent.getBooleanExtra(IS_NEW_KEY, true)

        viewModel = ViewModelProvider(this, ViewModelFactory(this.application)).get(EditViewModel::class.java)
    }

    private fun completeEditTexts(){

    }

    private fun checkInputsAndSave() {
        //TYPE
        val spinner = findViewById<Spinner>(R.id.spinner_edit_type)
        val type = spinner.selectedItem.toString()
        //PRICE
        val strPrice = findViewById<EditText>(R.id.et_edit_price).text.toString()
        //AREA
        val strArea = findViewById<EditText>(R.id.et_edit_area).text.toString()
        //ROOM NUMBER
        val strRooms = findViewById<EditText>(R.id.et_edit_room_nbr).text.toString()
        //DESCRIPTION
        val description = findViewById<EditText>(R.id.et_edit_description).text.toString()
        //CITY
        val city = findViewById<EditText>(R.id.et_edit_city).text.toString()
        //STREET
        val street = findViewById<EditText>(R.id.et_edit_street).text.toString()
        //STREET NBR
        val strStreetNbr = findViewById<EditText>(R.id.et_edit_street_nbr).text.toString()
        //CREATION TIME
        val creationTime = System.currentTimeMillis()
        //AGENT
        val agent = findViewById<EditText>(R.id.et_edit_agent_name).text.toString()

        val inputs = arrayOf(type, strPrice, strArea, strRooms, description, city, street, strStreetNbr, agent)

        if (inputs.any { it.isEmpty() }){
            Toast.makeText(this, getString(R.string.field_missing), Toast.LENGTH_SHORT).show()
        } else {

            val address = Address(city, street, strStreetNbr.toInt())

            val property = Property(type, strPrice.toFloat(), strArea.toFloat(), strRooms.toInt(),
                    description, address, creationTime, agent)

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
