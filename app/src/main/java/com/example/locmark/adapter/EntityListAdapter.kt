package com.example.locmark.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.locmark.R
import com.example.locmark.model.Entity

class EntityListAdapter(private var entities: List<Entity>) : RecyclerView.Adapter<EntityListAdapter.EntityViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_entity, parent, false)
        return EntityViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntityViewHolder, position: Int) {
        val entity = entities[position]
        holder.titleText.text = entity.title
    }

    override fun getItemCount(): Int = entities.size

    fun submitList(newEntities: List<Entity>) {
        entities = newEntities
        notifyDataSetChanged()
    }

    class EntityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.entityTitle)
    }
}

