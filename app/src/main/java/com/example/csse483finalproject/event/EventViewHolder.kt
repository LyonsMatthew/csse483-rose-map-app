package com.example.csse483finalproject.event

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.csse483finalproject.R

class EventViewHolder : RecyclerView.ViewHolder {
    var event:Event? = null
    val eventTitle: TextView = itemView.findViewById(R.id.eventName)
    lateinit var eventLocation: TextView
    lateinit var eventDate: TextView
    lateinit var eventTime: TextView
    var cardView: CardView = itemView.findViewById(R.id.eventCardView)
    var adapter: EventAdapter
    var isMinimal = false

    constructor(itemView: View, adapter: EventAdapter, isMinimal:Boolean) : super(itemView) {
        this.adapter = adapter
        this.isMinimal=isMinimal
        if(!isMinimal){
            eventLocation = itemView.findViewById(R.id.eventLocation)
            eventDate = itemView.findViewById(R.id.eventDate)
            eventTime = itemView.findViewById(R.id.eventTime)
        }
    }

    fun bind(event: Event) {
        this.event=event
        eventTitle.text = event.eventName
        //TODO: Better implementation for the below
        if(!isMinimal) {
            eventLocation.text = event.eventLocation.locString()
            val monthStrings = adapter.context.resources.getStringArray(R.array.month_abbrev)
            eventDate.text = monthStrings[event.eventStart.time.month] + " " + event.eventStart.time.date.toString()
            eventTime.text =
                event.eventStart.time.hours.toString() + ":" + event.eventStart.time.minutes.toString() + " to " + event.eventEnd.time.hours.toString() + ":" + event.eventEnd.time.minutes.toString()
        }
        cardView.setOnClickListener {
            adapter.eventCallback.onEventClicked(event)
        }
    }
}

//Adapted from MovieQuote solution