package com.openclassrooms.realestatemanager.view.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.view.model_ui.PropertyUi


class PropertyAdapter(val clickListener: OnItemClickListener) : RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>() {

    private var uiProperties = ArrayList<PropertyUi>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_property, parent, false)
        return PropertyViewHolder(view)
    }

    override fun getItemCount() = uiProperties.size


    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.bind(uiProperties[position])
    }

    fun populateList(data: ArrayList<PropertyUi>){
        this.uiProperties = data
        notifyDataSetChanged()
    }


    inner class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        init {
            itemView.setOnClickListener{ clickListener.onItemClick(uiProperties[adapterPosition].id) }
        }

        private val typeView = itemView.findViewById<TextView>(R.id.tv_property_item_type)
        private val cityView = itemView.findViewById<TextView>(R.id.tv_property_item_city)
        private val priceView = itemView.findViewById<TextView>(R.id.tv_property_item_price)
        private val soldBanner = itemView.findViewById<ImageView>(R.id.iv_sold)
        private val imageView = itemView.findViewById<ImageView>(R.id.image_view_property_item)

        fun bind(uiProperty: PropertyUi){
            typeView.text = uiProperty.type
            cityView.text = uiProperty.address.city
            priceView.text = String.format("%,d", uiProperty.price) + "$"
            Glide.with(imageView.context).load(uiProperty.thumbnailUri).into(imageView)
            if (uiProperty.isSold) soldBanner.visibility = View.VISIBLE
            else soldBanner.visibility = View.INVISIBLE
        }
    }

    interface OnItemClickListener{
        fun onItemClick(propertyId: Int)
    }
}
