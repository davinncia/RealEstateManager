package com.openclassrooms.realestatemanager.view.details


import android.annotation.SuppressLint
import android.os.Bundle
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
import com.openclassrooms.realestatemanager.view.details.DetailsViewModel.PropertyDetailsUi
import com.openclassrooms.realestatemanager.view.edit.EditActivity
import com.openclassrooms.realestatemanager.view.loan.LoanActivity
import com.openclassrooms.realestatemanager.view.map.MapsActivity


class DetailsFragment : Fragment() {

    private lateinit var viewModel: DetailsViewModel
    private val picturesAdapter = PicturesAdapter()

    private var networkAvailable = false
    private var activeSelection = false

    //VIEWS
    private lateinit var descriptionView: TextView
    private lateinit var areaView: TextView
    private lateinit var roomView: TextView
    private lateinit var locationView: TextView
    private lateinit var mapImageView: ImageView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        val rootView = inflater.inflate(R.layout.fragment_details, container, false)
        descriptionView = rootView.findViewById(R.id.tv_property_description)
        areaView = rootView.findViewById(R.id.tv_area_data)
        roomView = rootView.findViewById(R.id.tv_rooms_data)
        locationView = rootView.findViewById(R.id.tv_location_data)
        mapImageView = rootView.findViewById(R.id.iv_static_map)

        initPicturesRecyclerView(rootView)
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
                completeUi(it)
            }
        })

        //Pictures
        viewModel.allPictureUris.observe(viewLifecycleOwner, Observer {
            picturesAdapter.populatePictures(it)
        })

        //Network
        viewModel.networkAvailableLiveData.observe(viewLifecycleOwner, Observer {
            networkAvailable = it
        })

        return rootView
    }

    private fun initPicturesRecyclerView(rootView: View) {

        rootView.findViewById<RecyclerView>(R.id.recycler_view_property_pictures).apply {
            adapter = picturesAdapter
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun completeUi(property: PropertyDetailsUi) {
        //Description
        descriptionView.text = property.description
        //Area
        areaView.text = property.area
        //Rooms
        roomView.text= property.roomNbr.toString()
        //Location
        locationView.text = "${property.city}\n${property.vicinity}"
        //Static map
        Glide.with(this.requireContext()).load(property.staticMapUrl).error(R.drawable.static_map).into(mapImageView)
    }

    private fun launchGoogleMaps() {
        //Check internet connection
        if (networkAvailable)
            //Start map activity
            startActivity(MapsActivity.newIntent(this.requireContext()))
        else
            Toast.makeText(this.requireContext(), getString(R.string.internet_connection_missing), Toast.LENGTH_SHORT).show()

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
                startActivity(LoanActivity.newIntent(this.requireContext(), viewModel.getPropertyPrice()))
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun newInstance() = DetailsFragment()
    }

}

