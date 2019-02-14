package com.example.csse483finalproject.event

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.csse483finalproject.R

class EventViewHolder : RecyclerView.ViewHolder {
    var event:EventWrapper? = null
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

    fun bind(event: EventWrapper) {
        this.event=event
        eventTitle.text = event.wGetEventName()
        //TODO: Better implementation for the below
        if(!isMinimal) {
            eventLocation.text = event.wGetEventLocation().locString()
            val monthStrings = adapter.context.resources.getStringArray(R.array.month_abbrev)
            eventDate.text = monthStrings[event.wGetEventStart().time.month] + " " + event.wGetEventStart().time.date.toString()
            var startMinutes = event.wGetEventStart().time.minutes.toString()
            if (startMinutes.length == 1) {
                startMinutes = "0" + startMinutes
            }
            var endMinutes = event.wGetEventEnd().time.minutes.toString()
            if (endMinutes.length == 1) {
                endMinutes = "0" + endMinutes
            }
            eventTime.text =
                event.wGetEventStart().time.hours.toString() + ":" + startMinutes + " to " + event.wGetEventEnd().time.hours.toString() + ":" + endMinutes
        }
        cardView.setOnClickListener {
            adapter.eventCallback.onEventClicked(event)
        }
    }
}

//Adapted from MovieQuote solution