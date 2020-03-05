package com.openclassrooms.realestatemanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.openclassrooms.realestatemanager.property_details.DetailsFragment
import com.openclassrooms.realestatemanager.property_edit.EditActivity
import com.openclassrooms.realestatemanager.property_list.ListFragment
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    companion object {
        const val LIST_FRAG_TAG = "list_fragment"
        const val DETAILS_FRAG_TAG = "details_fragment"
    }

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
                startActivity(EditActivity.newIntent(this, true))
                true
            }
            R.id.item_main_menu_search -> {
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

}