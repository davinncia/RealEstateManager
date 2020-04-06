package com.openclassrooms.realestatemanager.view.details

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R

class PicturesAdapter(private val pictureUris: List<String>) : RecyclerView.Adapter<PicturesAdapter.PicturesViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicturesViewHolder {
        val imageView = LayoutInflater.from(parent.context).inflate(
                R.layout.item_picture, parent, false) as ImageView
        return PicturesViewHolder(imageView)
    }

    override fun getItemCount(): Int = pictureUris.size

    override fun onBindViewHolder(holder: PicturesViewHolder, position: Int) {

        val currentItem = pictureUris[position]
        holder.bindView(currentItem)
    }

    inner class PicturesViewHolder(private val imageView: ImageView) : RecyclerView.ViewHolder(imageView) {

        fun bindView(uri: String) {
            Glide.with(imageView.context).load(uri).into(imageView)
        }
    }


}