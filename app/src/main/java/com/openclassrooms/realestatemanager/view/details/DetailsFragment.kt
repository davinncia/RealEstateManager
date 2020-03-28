package com.openclassrooms.realestatemanager.view.details


import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.di.ViewModelFactory
import com.openclassrooms.realestatemanager.view.edit.EditActivity
import com.openclassrooms.realestatemanager.view.loan.LoanActivity
import com.openclassrooms.realestatemanager.view.map.MapsActivity
import com.openclassrooms.realestatemanager.view.model_ui.PropertyUi


class DetailsFragment : Fragment() {

    private lateinit var viewModel: DetailsViewModel

    private var networkAvailable = false
    private var activeSelection = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("debuglog", "Details fragment created")
        setHasOptionsMenu(true)

        val rootView = inflater.inflate(R.layout.fragment_details, container, false)

        rootView.findViewById<ImageView>(R.id.iv_static_map).setOnClickListener { launchGoogleMaps() }

        //VIEW MODEL
        viewModel = ViewModelProvider(this, ViewModelFactory(requireActivity().application))
                .get(DetailsViewModel::class.java)

        //Property
        viewModel.propertyUi.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                //No selection
                rootView.visibility = View.INVISIBLE
            } else {
                rootView.visibility = View.VISIBLE
                activeSelection = true
                completeUi(rootView, it)
            }
        })

        //Pictures
        viewModel.allPictures.observe(viewLifecycleOwner, Observer { pictures ->
            initPicturesRecyclerView(rootView, ArrayList(pictures.map { it.strUri }))
        })

        //Network
        viewModel.networkAvailableLiveData.observe(viewLifecycleOwner, Observer {
            networkAvailable = it
        })

        return rootView
    }

    private fun initPicturesRecyclerView(rootView: View, uris: ArrayList<String>) {

        if (uris.isEmpty()) {
            val uri = "android.resource://com.openclassrooms.realestatemanager/drawable/default_house"
            uris.add(uri)
        }

        rootView.findViewById<RecyclerView>(R.id.recycler_view_property_pictures)
                .apply {
                    adapter = PicturesAdapter(uris)
                    layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                }
    }

    private fun completeUi(rootView: View, property: PropertyUi) {
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

    private fun launchGoogleMaps() {

        //Check internet connection
        if (networkAvailable) {
            //Start map activity
            startActivity(MapsActivity.newIntent(this.requireContext()))
        } else {
            Toast.makeText(this.requireContext(), getString(R.string.internet_connection_missing), Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.details_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!activeSelection) {
            Toast.makeText(requireContext(), getString(R.string.select_a_property), Toast.LENGTH_SHORT).show()
            return super.onOptionsItemSelected(item)
        }

        return when (item.itemId) {
            R.id.item_details_menu_edit -> {
                startActivity(EditActivity.newIntent(requireContext()))
                true
            }
            R.id.item_details_menu_sale_status -> {
                viewModel.changeSaleStatus()
                true
            }
            R.id.item_details_menu_loan -> {
                startActivity(LoanActivity.newIntent(this.requireContext(), viewModel.getPropertyPriceStr()))
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun newInstance() = DetailsFragment()
    }

}

