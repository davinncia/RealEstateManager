package com.openclassrooms.realestatemanager.property_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R

class PropertyListAdapter(val clickListener: OnItemClickListener) : RecyclerView.Adapter<PropertyListAdapter.PropertyViewHolder>() {

    private var properties = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_property_list, parent, false)
        return PropertyViewHolder(view)
    }

    override fun getItemCount() = properties.size


    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.bind(properties[position])
    }

    fun populateList(data: ArrayList<String>){
        this.properties = data
        notifyDataSetChanged()
    }


    inner class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        init {
            itemView.setOnClickListener{ clickListener.onItemClick(adapterPosition) }
        }

        private val textView = itemView.findViewById<TextView>(R.id.tv_list_test)

        fun bind(item: String){
            textView.text = item
        }
    }

    interface OnItemClickListener{
        fun onItemClick(i: Int)
    }
}