package com.openclassrooms.realestatemanager.property_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.model.Property

class PropertyAdapter(val clickListener: OnItemClickListener) : RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>() {

    private var properties = ArrayList<Property>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.property_item, parent, false)
        return PropertyViewHolder(view)
    }

    override fun getItemCount() = properties.size


    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.bind(properties[position])
    }

    fun populateList(data: ArrayList<Property>){
        this.properties = data
        notifyDataSetChanged()
    }


    inner class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        init {
            itemView.setOnClickListener{ clickListener.onItemClick(adapterPosition) }
        }

        private val typeView = itemView.findViewById<TextView>(R.id.tv_property_item_type)
        private val cityView = itemView.findViewById<TextView>(R.id.tv_property_item_city)
        private val priceView = itemView.findViewById<TextView>(R.id.tv_property_item_price)

        fun bind(property: Property){
            typeView.text = property.type.toString()
            cityView.text = property.address.city
            priceView.text = "${property.price}$"
        }
    }

    interface OnItemClickListener{
        fun onItemClick(i: Int)
    }
}