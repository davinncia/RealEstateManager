package com.openclassrooms.realestatemanager.property_details


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.openclassrooms.realestatemanager.PropertyViewModel
import com.openclassrooms.realestatemanager.R


class DetailsFragment : Fragment() {

    companion object{
        @JvmStatic
        fun newInstance() = DetailsFragment()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_details, container, false)

        val textView = rootView.findViewById<TextView>(R.id.tv_property_details)

        val viewModel = activity?.run {
            ViewModelProviders.of(this).get(PropertyViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        viewModel.propertySelectionLiveData.observe(this, Observer {
            textView.text = it
        })


        return rootView
    }



}
