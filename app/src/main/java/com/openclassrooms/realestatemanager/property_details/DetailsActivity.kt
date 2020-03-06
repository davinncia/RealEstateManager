package com.openclassrooms.realestatemanager.property_details

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.openclassrooms.realestatemanager.MainActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.property_edit.EditActivity

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        Log.d("debuglog", "Details Activity created")

        if (resources.getBoolean(R.bool.is_landscape)) {
            //BACK TO LANDSCAPE
            //Finish activity -> back to main with clear BackStack
            startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        } else {
            //PORTRAIT
            openDetailsFragment()
        }

    }

    private fun openDetailsFragment() {
        val detailsFragment = DetailsFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.place_holder_fragment_details, detailsFragment, MainActivity.DETAILS_FRAG_TAG)
                .commit()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, DetailsActivity::class.java)
    }
}
