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

        /*
        Old implementation
        if (savedInstanceState == null) {
            //First activity opening
            if (findViewById<FrameLayout>(R.id.place_holder_fragment_details) != null) {
                //LANDSCAPE
                openListFragment()
                openDetailsFragment()

            } else {
                //PORTRAIT
                openListFragment()

            }

        } else if (findViewById<FrameLayout>(R.id.place_holder_fragment_details) != null) {
            //From portrait to landscape
            //TODO NINO 0: Once opened, recreated every rotation by activity
            openDetailsFragment()
        }

         */

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        if (resources.getBoolean(R.bool.is_landscape)) {
            //Showing edit option if landscape mode
            menu?.findItem(R.id.item_main_menu_edit)?.isVisible = true
        }
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
            R.id.item_main_menu_edit -> {
                Toast.makeText(this, "Edit", Toast.LENGTH_SHORT).show()
                //TODO check something is selected
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    /*
    private fun openListFragment() {
        val listFragment = ListFragment.newInstance()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.place_holder_fragment_list, listFragment, LIST_FRAG_TAG).commit()
    }

    private fun openDetailsFragment() {
        val detailsFragment = DetailsFragment.newInstance()
        supportFragmentManager.beginTransaction()
                //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.place_holder_fragment_details, detailsFragment, DETAILS_FRAG_TAG)
                .commit()
    }

     */
}