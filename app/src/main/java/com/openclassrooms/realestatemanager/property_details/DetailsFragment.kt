package com.openclassrooms.realestatemanager.property_details


import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.di.ViewModelFactory
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.property_map.MapsActivity


class DetailsFragment : Fragment() {

    private lateinit var viewModel: DetailsViewModel
    private var networkAvailable = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("debuglog", "Details fragment created")

        val rootView = inflater.inflate(R.layout.fragment_details, container, false)

        rootView.findViewById<ImageView>(R.id.iv_static_map).setOnClickListener { launchGoogleMaps() }

        //VIEW MODEL
        viewModel = ViewModelProviders.of(
                this, ViewModelFactory(requireActivity().application)).get(DetailsViewModel::class.java)

        //Property
        viewModel.propertySelection.observe(viewLifecycleOwner, Observer {
            initPicturesRecyclerView(rootView, arrayListOf())
            completeUi(rootView, it)
        })

        //Network
        viewModel.networkAvailableLiveData.observe(viewLifecycleOwner, Observer {
            networkAvailable = it
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
        //Static map
        val mapView = rootView.findViewById<ImageView>(R.id.iv_static_map)
        val pictureUrl = viewModel.getStaticMapStringUrlGivenAddress(property.address, resources.getString(R.string.googleApiKey))
        Glide.with(this.requireContext()).load(pictureUrl).error(R.drawable.static_map).into(mapView)
    }

    private fun launchGoogleMaps(){

        //Check internet connection
        if (networkAvailable) {
            //Start map activity
            startActivity(MapsActivity.newIntent(this.requireContext()))
        } else {
            Toast.makeText(this.requireContext(), getString(R.string.internet_connection_missing), Toast.LENGTH_SHORT).show()
        }


    }

    companion object{
        fun newInstance() = DetailsFragment()
    }

}

