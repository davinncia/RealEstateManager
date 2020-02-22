package com.openclassrooms.realestatemanager.property_list


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.PropertyViewModel
import com.openclassrooms.realestatemanager.R


class ListFragment : Fragment(), PropertyListAdapter.OnItemClickListener {

    private lateinit var propertyAdapter: PropertyListAdapter
    private lateinit var viewModel: PropertyViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_list, container, false)

        initRecyclerView(rootView)

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(PropertyViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        viewModel.propertiesLiveData.observe(this, Observer {
            propertyAdapter.populateList(ArrayList(it))
        })

        return rootView
    }


    private fun initRecyclerView(rootView: View){

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recycler_view_property_list)
        propertyAdapter = PropertyListAdapter(this)

        recyclerView.apply {
            setHasFixedSize(true)
            adapter = propertyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }


    override fun onItemClick(i: Int) {
        viewModel.selectProperty(i)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ListFragment()
    }
}
