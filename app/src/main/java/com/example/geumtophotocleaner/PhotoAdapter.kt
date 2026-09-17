package com.example.geumtophotocleaner

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhotoAdapter(val items: MutableList<PhotoItem>) : RecyclerView.Adapter<PhotoAdapter.Holder>() {
    class Holder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)) {
        val thumb: ImageView = itemView.findViewById(R.id.thumb)
        val date: TextView = itemView.findViewById(R.id.date)
        val check: CheckBox = itemView.findViewById(R.id.check)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(parent)
    override fun getItemCount() = items.size
    override fun onBindViewHolder(h: Holder, position: Int) {
        val item = items[position]
        h.thumb.setImageURI(item.uri)
        h.date.text = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA).format(Date(item.takenMillis))
        h.check.setOnCheckedChangeListener(null)
        h.check.isChecked = item.selected
        h.check.setOnCheckedChangeListener { _, checked -> item.selected = checked }
        h.itemView.setOnClickListener { item.selected = !item.selected; notifyItemChanged(position) }
    }
    fun selectAll() { items.forEach { it.selected = true }; notifyDataSetChanged() }
}
