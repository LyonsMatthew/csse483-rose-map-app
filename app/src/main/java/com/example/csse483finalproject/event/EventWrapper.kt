package com.example.csse483finalproject.event

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.csse483finalproject.Constants
import com.example.csse483finalproject.group.GroupSpec
import com.example.csse483finalproject.group.MemberType
import com.example.csse483finalproject.group.UserWrapper
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

data class EventWrapper(var eventId: String = "" ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(eventId)
    }

    override fun describeContents(): Int {
        return 0
    }
    fun getAccessLevel(u: UserWrapper): MemberType {
        return myEvent().getAccessLevel(u)
    }

    fun getEventName(): String{
        return myEvent().eventName
    }

    fun getEventLocation(): Location{
        return myEvent().eventLocation
    }

    fun getEventDescription(): String{
        return myEvent().eventDescription
    }

    fun getEventStart(): Calendar{
        return myEvent().eventStart
    }

    fun getEventEnd(): Calendar{
        return myEvent().eventEnd
    }

    fun getEventOwners(): GroupSpec{
        return myEvent().eventOwners
    }

    fun getEventViewers(): GroupSpec{
        return myEvent().eventViewers
    }

    fun getId(): String{
        return myEvent().id
    }

    fun myEvent(): Event {
        if(hmoe.containsKey(eventId)) {
            return hmoe.get(eventId)!!
        }
        else{
            Log.d(Constants.TAG, "Event not found!")
            return Event()
        }
    }
    companion object CREATOR : Parcelable.Creator<EventWrapper> {
        var hmoe = HashMap<String,Event>()

        fun setupEvents(el:ArrayList<Event>){
            hmoe = HashMap<String,Event>()
            for (e in el){
                hmoe.put(e.id,e)
            }
        }

        override fun createFromParcel(parcel: Parcel): EventWrapper {
            return EventWrapper(parcel)
        }

        override fun newArray(size: Int): Array<EventWrapper?> {
            return arrayOfNulls(size)
        }

        fun fromSnapshot(snapshot: DocumentSnapshot): EventWrapper{
            val ew=snapshot.toObject(EventWrapper::class.java)!!
            return ew
        }

        fun fromEvent(e: Event): EventWrapper{
            return EventWrapper(e.id)
        }
    }
}