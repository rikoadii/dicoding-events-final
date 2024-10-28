package com.submissionandroid.dicodingevents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Pastikan Anda sudah mengimpor Glide untuk memuat gambar

class FavoriteEventsAdapter(
    private var events: List<FavoriteEvent>,
    private val onClick: (FavoriteEvent) -> Unit
) : RecyclerView.Adapter<FavoriteEventsAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventImage: ImageView = itemView.findViewById(R.id.ivEventImage)
        val eventName: TextView = itemView.findViewById(R.id.tvEventName)
        val eventDate: TextView = itemView.findViewById(R.id.tvEventDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.eventName.text = event.name
        holder.eventDate.text = event.date
        // Jika event memiliki URL gambar, Anda bisa memuatnya dengan Glide
        Glide.with(holder.itemView.context)
            .load(event.imageLogo) // Ganti dengan properti yang sesuai dari FavoriteEvent
            .into(holder.eventImage)

        holder.itemView.setOnClickListener {
            onClick(event)
        }
    }

    override fun getItemCount(): Int = events.size

    fun updateEvents(newEvents: List<FavoriteEvent>) {
        events = newEvents
        notifyDataSetChanged()
    }
}
