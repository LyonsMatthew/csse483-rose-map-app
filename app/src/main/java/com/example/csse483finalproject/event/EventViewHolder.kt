package com.example.csse483finalproject.event

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.csse483finalproject.R

class EventViewHolder : RecyclerView.ViewHolder {
    var event:Event? = null
    val eventTitle: TextView = itemView.findViewById(R.id.eventTitle)
    val eventLocation: TextView = itemView.findViewById(R.id.eventLocation)
    val eventDate: TextView = itemView.findViewById(R.id.eventDate)
    val eventTime: TextView = itemView.findViewById(R.id.eventTime)
    var cardView: CardView = itemView.findViewById(R.id.eventCardView)
    var adapter: EventAdapter

    constructor(itemView: View, adapter: EventAdapter) : super(itemView) {
        this.adapter = adapter
    }

    fun bind(event: Event) {
        this.event=event
        eventTitle.text = event.eventName
        //TODO: Better implementation for the below
        eventLocation.text = event.eventLocation.locString()
        val monthStrings = adapter.context.resources.getStringArray(R.array.month_abbrev)
        eventDate.text = monthStrings[event.eventStart.month] + " " + event.eventStart.date.toString()
        eventTime.text = event.eventStart.hours.toString() + ":" + event.eventStart.minutes.toString() +" to " + event.eventEnd.hours.toString() + ":" + event.eventEnd.minutes.toString()
        cardView.setOnClickListener {
            adapter.parentFragment.onEventClicked(event)
        }
    }
}

//Adapted from MovieQuote solution