package com.openclassrooms.realestatemanager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


class SearchFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("debuglog", "SEARCH FRAGMENT CREATED")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }



    companion object {
        fun newInstance() = SearchFragment()
    }
}
