package com.openclassrooms.realestatemanager.view.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.view.model_ui.PoiUi

class PoiAdapter(private val listener: OnPoiCLickListener)
    : RecyclerView.Adapter<PoiAdapter.PoiViewHolder>() {

    private var poiList: List<PoiUi>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoiViewHolder {
        val textView = LayoutInflater.from(parent.context).inflate(R.layout.item_poi, parent, false)
        return PoiViewHolder(textView)
    }

    override fun getItemCount(): Int = poiList?.size ?: 0

    override fun onBindViewHolder(holder: PoiViewHolder, position: Int) {
        holder.bind(poiList!![position])
    }

    fun populateData(allPoi: List<PoiUi>) {
        poiList = allPoi
        notifyDataSetChanged()
    }

    inner class PoiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val poiTextView = itemView.findViewById<TextView>(R.id.tv_poi_name_item)
        private val iconView = itemView.findViewById<ImageView>(R.id.iv_poi_item)

        init {
            itemView.setOnClickListener{
                listener.onPoiClicked(poiList!![adapterPosition])
            }
        }

        fun bind(poi: PoiUi) {
            poiTextView.text = poi.name
            iconView.setImageResource(poi.iconResourceId)

            if (poi.isSelected) {
                //iconView.setColorFilter(R.color.colorAccent)
                iconView.setBackgroundColor(iconView.context.resources.getColor(R.color.colorAccent))
            } else {
                iconView.setBackgroundColor(iconView.context.resources.getColor(android.R.color.transparent))
            }
        }
    }

    interface OnPoiCLickListener {
        fun onPoiClicked(poi: PoiUi)
    }
}