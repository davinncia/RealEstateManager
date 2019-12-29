package com.openclassrooms.realestatemanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.property_list.ListFragment

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val listFragment = ListFragment.newInstance()
        //val fragmentTransaction = supportFragmentManager.beginTransaction()
        //fragmentTransaction.add(listFragment, "list").commit()

    }
}