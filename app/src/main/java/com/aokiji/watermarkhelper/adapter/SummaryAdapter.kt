package com.aokiji.watermarkhelper.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aokiji.watermarkhelper.R
import com.aokiji.watermarkhelper.models.SummaryItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recycler_item_main.view.*

class SummaryAdapter(private val context: Context, private val list: List<SummaryItem>, private val listener: (position: Int) -> Unit) : RecyclerView.Adapter<SummaryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(context: Context, item: SummaryItem, listener: (position: Int) -> Unit) = with(itemView) {
            Glide.with(context)
                    .load(item.images[0].path)
                    .centerCrop()
                    .placeholder(R.color.color_e6)
                    .error(R.color.color_e6)
                    .into(itemView.ivSummary)
            itemView.tvNumber.text = item.number.toString()
            itemView.tvDate.text = item.date

            setOnClickListener { listener(layoutPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_main, parent, false))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, list[position], listener)
    }
}