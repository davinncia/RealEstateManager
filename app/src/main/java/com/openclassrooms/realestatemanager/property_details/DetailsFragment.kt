package com.openclassrooms.realestatemanager.property_details


import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.MainActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.di.ViewModelFactory
import com.openclassrooms.realestatemanager.model.Property


class DetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("debuglog", "Details fragment created")

        val rootView = inflater.inflate(R.layout.fragment_details, container, false)



        //Details ViewModel
        val viewModel = ViewModelProviders.of(
                this, ViewModelFactory()).get(DetailsViewModel::class.java)

        viewModel.propertySelection.observe(viewLifecycleOwner, Observer {
            initPicturesRecyclerView(rootView, arrayListOf())
            completeUi(rootView, it)
        })


        return rootView
    }

    private fun initPicturesRecyclerView(rootView: View, uris: ArrayList<Uri>){

        if (uris.isEmpty()){
            val uri = Uri.parse(
                    "android.resource://com.openclassrooms.realestatemanager/drawable/default_house")
            uris.add(uri)
        }

        rootView.findViewById<RecyclerView>(R.id.recycler_view_property_pictures)
                .apply {
                    adapter = PicturesAdapter(uris)
                    layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                }
    }

    private fun completeUi(rootView: View, property: Property){
        //Description
        rootView.findViewById<TextView>(R.id.tv_property_description).text = property.description
        //Area
        rootView.findViewById<TextView>(R.id.tv_area_data).text = "${property.area}m2"
        //Rooms
        rootView.findViewById<TextView>(R.id.tv_rooms_data).text = property.roomNbr.toString()
        //Location
        rootView.findViewById<TextView>(R.id.tv_location_data).text =
                "${property.address.city}\n${property.address.streetNbr} ${property.address.street}"
    }

    companion object{
        fun newInstance() = DetailsFragment()
    }

}

