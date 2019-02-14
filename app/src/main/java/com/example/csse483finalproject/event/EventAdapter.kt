package com.example.csse483finalproject.event

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.csse483finalproject.R

class EventAdapter(var context: Context, var eventCallback: EventListListener, var isMinimal:Boolean = false) : RecyclerView.Adapter<EventViewHolder>() {
    private val events = ArrayList<EventWrapper>()

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): EventViewHolder {
        lateinit var view: View
        if(isMinimal){
            view = LayoutInflater.from(context).inflate(R.layout.event_card_minimal, parent, false)
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.event_card, parent, false)
        }
        return EventViewHolder(view, this, isMinimal)
    }

    override fun onBindViewHolder(
        viewHolder: EventViewHolder,
        index: Int) {
        viewHolder.bind(events[index])
    }

    override fun getItemCount() = events.size

    fun add(event: EventWrapper) {
        events.add(0, event)
        notifyItemInserted(0)
    }

    interface EventListListener {
        fun onEventClicked(e: EventWrapper)
        fun onCreateEvent()
        fun onEventEditEnd()
        fun onMapClick(filename: String)
    }
}