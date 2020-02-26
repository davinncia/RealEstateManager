package com.openclassrooms.realestatemanager

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.openclassrooms.realestatemanager.property_details.DetailsFragment
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

    }

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
}