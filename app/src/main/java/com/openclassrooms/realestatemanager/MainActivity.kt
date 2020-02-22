package com.openclassrooms.realestatemanager

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.openclassrooms.realestatemanager.property_details.DetailsFragment
import com.openclassrooms.realestatemanager.property_list.ListFragment

class MainActivity : AppCompatActivity(){

    companion object {
        private const val LIST_FRAG_TAG = "list_fragment"
        private const val DETAILS_FRAG_TAG = "details_fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (findViewById<FrameLayout>(R.id.place_holder_fragments) != null) {
            //Handset layout: Only one fragment is shown

            openListFragment()

            val viewModel = ViewModelProviders.of(this).get(PropertyViewModel::class.java)
            viewModel.propertySelectionLiveData.observe(this, Observer {
               openDetailsFragment()
            })
       }
    }

    override fun onBackPressed() {
        //if (findViewById<FrameLayout>(R.id.place_holder_fragments) != null){
        //    //Handset layout
        //    val detailsFrag = supportFragmentManager.findFragmentByTag(DETAILS_FRAG_TAG)
        //    if (detailsFrag != null && detailsFrag.isVisible) {
        //        openListFragment()
        //    } else super.onBackPressed()
//
        //} else super.onBackPressed()
        super.onBackPressed()
    }

    private fun openListFragment() {
        val listFragment = ListFragment.newInstance()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.place_holder_fragments, listFragment, LIST_FRAG_TAG).commit()
    }

    private fun openDetailsFragment() {
        val detailsFragment = DetailsFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.place_holder_fragments, detailsFragment, DETAILS_FRAG_TAG)
                .addToBackStack(null)
                .commit()
    }
}