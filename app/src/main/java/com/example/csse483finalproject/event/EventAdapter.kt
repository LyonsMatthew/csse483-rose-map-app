package com.example.csse483finalproject.event

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.csse483finalproject.R

class EventAdapter(var context: Context, var parentFragment: EventsFragment) : RecyclerView.Adapter<EventViewHolder>() {
    private val events = ArrayList<Event>()

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): EventViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.event_card, parent, false)
        return EventViewHolder(view, this)
    }

    override fun onBindViewHolder(
        viewHolder: EventViewHolder,
        index: Int) {
        viewHolder.bind(events[index])
    }

    override fun getItemCount() = events.size

    fun add(event: Event) {
        events.add(0, event)
        notifyItemInserted(0)
    }

}