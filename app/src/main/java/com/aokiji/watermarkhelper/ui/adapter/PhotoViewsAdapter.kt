package com.aokiji.watermarkhelper.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aokiji.watermarkhelper.R
import com.aokiji.watermarkhelper.models.entities.Image
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recycler_item_photo.view.*

class PhotoViewsAdapter(private val context: Context, private val list: MutableList<Image>) : RecyclerView.Adapter<PhotoViewsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(context: Context, item: Image) = with(itemView) {
            Glide.with(context)
                    .load(item.path)
                    .placeholder(R.color.color_e6)
                    .error(R.color.color_e6)
                    .into(itemView.photoView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_photo, parent, false)
        )

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, list[position])
    }


}