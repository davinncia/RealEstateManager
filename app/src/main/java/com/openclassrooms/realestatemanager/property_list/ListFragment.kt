package com.openclassrooms.realestatemanager.property_list


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.di.ViewModelFactory
import com.openclassrooms.realestatemanager.property_details.DetailsActivity


class ListFragment : Fragment(), PropertyAdapter.OnItemClickListener {

    private lateinit var propertyAdapter: PropertyAdapter
    private lateinit var viewModel: ListViewModel

    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("debuglog", "List fragment created")


        rootView = inflater.inflate(R.layout.fragment_list, container, false)

        initRecyclerView(rootView)

        viewModel = ViewModelProvider(this, ViewModelFactory(requireActivity().application))
                .get(ListViewModel::class.java)

        viewModel.allProperties.observe(viewLifecycleOwner, Observer {
            //TODO: empty list view
            propertyAdapter.populateList(ArrayList(it))
        })

        return rootView
    }


    private fun initRecyclerView(rootView: View){

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recycler_view_property_list)
        propertyAdapter = PropertyAdapter(this)

        recyclerView.apply {
            setHasFixedSize(true)
            adapter = propertyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onItemClick(propertyId: Int) {
        viewModel.selectProperty(propertyId)

        //Open details if portrait (handset)
        if (!resources.getBoolean(R.bool.is_landscape)) {
            //PORTRAIT
            startActivity(DetailsActivity.newIntent(this.requireContext()))
        }
    }

    companion object {
        fun newInstance() = ListFragment()
    }
}
