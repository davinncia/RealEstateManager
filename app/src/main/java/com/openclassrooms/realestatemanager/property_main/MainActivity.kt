package com.openclassrooms.realestatemanager.property_main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.model_ui.EmptyProperty
import com.openclassrooms.realestatemanager.property_edit.EditActivity
import com.openclassrooms.realestatemanager.repository.InMemoryRepository

class MainActivity : AppCompatActivity() {

    private val inMemoRepo = InMemoryRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        //menu?.clear()
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.item_main_menu_add -> {
                inMemoRepo.setPropertySelection(EmptyProperty)
                startActivity(EditActivity.newIntent(this))
                true
            }
            R.id.item_main_menu_search -> {
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    companion object {
        const val LIST_FRAG_TAG = "list_fragment"
        const val DETAILS_FRAG_TAG = "details_fragment"
    }
}