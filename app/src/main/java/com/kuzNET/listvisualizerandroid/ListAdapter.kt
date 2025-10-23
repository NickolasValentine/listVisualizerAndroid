package com.kuzNET.listvisualizerandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListAdapter(private var items: List<Any?>) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIndex: TextView = view.findViewById(R.id.tvIndex)
        val tvValue: TextView = view.findViewById(R.id.tvValue)
        val ivArrow: ImageView = view.findViewById(R.id.ivArrow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvIndex.text = position.toString()
        holder.tvValue.text = items[position]?.toString() ?: "null"

        // Скрываем стрелку у последнего элемента
        if (position == items.size - 1) {
            holder.ivArrow.visibility = View.GONE
        } else {
            holder.ivArrow.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = items.size

    // Метод для обновления данных в адаптере
    fun updateData(newItems: List<Any?>) {
        items = newItems
        notifyDataSetChanged() // Говорим RecyclerView перерисоваться
    }
}