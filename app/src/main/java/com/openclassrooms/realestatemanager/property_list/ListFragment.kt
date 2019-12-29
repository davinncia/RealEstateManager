package com.openclassrooms.realestatemanager.property_list


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R


class ListFragment : Fragment(), PropertyListAdapter.OnItemClickListener {

    private lateinit var propertyAdapter: PropertyListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_list, container, false)

        initRecyclerView(rootView)

        val dummyData = arrayListOf("1", "2", "3", "4", "5", "6")
        propertyAdapter.populateList(dummyData)

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


    companion object {
        @JvmStatic
        fun newInstance() = ListFragment()
    }

    override fun onItemClick(i: Int) {
        Toast.makeText(requireContext(), "click", Toast.LENGTH_SHORT).show()
    }
}
