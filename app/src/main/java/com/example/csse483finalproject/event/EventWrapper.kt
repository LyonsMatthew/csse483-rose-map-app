package com.example.csse483finalproject.event

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.csse483finalproject.Constants
import com.example.csse483finalproject.group.GroupSpec
import com.example.csse483finalproject.group.GroupWithMembershipType
import com.example.csse483finalproject.group.MemberType
import com.example.csse483finalproject.group.UserWrapper
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*
import kotlin.collections.ArrayList

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
    fun wGetUserAccessLevel(u: UserWrapper): MemberType {
        return myEvent().getUserAccessLevel(u)
    }

    fun wGetGroupAccessLevel(g: GroupWithMembershipType): MemberType {
        return myEvent().getGroupAccessLevel(g)
    }

    fun wSetGroupAccessLevel(g: GroupWithMembershipType, mt: MemberType) {
        myEvent().setGroupAccessLevel(g,mt)
    }

    fun wGetEventName(): String{
        return myEvent().eventName
    }

    fun wGetEventLocation(): Location{
        return myEvent().eventLocation
    }

    fun wGetEventDescription(): String{
        return myEvent().eventDescription
    }

    fun wGetEventStart(): Calendar{
        return myEvent().eventStart
    }

    fun wGetEventEnd(): Calendar{
        return myEvent().eventEnd
    }

    fun wGetEventOwners(): GroupSpec{
        return myEvent().eventOwners
    }

    fun wGetEventViewers(): GroupSpec{
        return myEvent().eventViewers
    }

    fun wGetId(): String{
        return myEvent().id
    }

    fun wSetEventName(eventname: String) {
        myEvent().eventName=eventname
    }

    fun wSetEventLocation(loc: Location){
        myEvent().eventLocation = loc
    }

    fun wSetEventDescription(desc: String){
        myEvent().eventDescription = desc
    }

    fun wSetEventStart(es: Calendar){
        myEvent().eventStart = es
    }

    fun wSetEventEnd(ee: Calendar){
        myEvent().eventEnd=ee
    }

    fun wSetEventOwners(eo: GroupSpec){
        myEvent().eventOwners=eo
    }

    fun wSetEventViewers(ev: GroupSpec){
        myEvent().eventViewers = ev
    }

    fun saveToCloud(){
        eventsRef.document(eventId).set(myEvent()).addOnSuccessListener {
            if (hmoe_temp.containsKey(eventId)){
                hmoe_temp.remove(eventId)
            }
        }
    }

    fun delete() {
        if (hmoe.containsKey(eventId)) {
            eventsRef.document(eventId).delete()
        }
        if (hmoe_temp.containsKey(eventId)) {
            hmoe_temp.remove(eventId)
        }
    }


    fun myEvent(): Event {
        if(hmoe.containsKey(eventId)) {
            return hmoe.get(eventId)!!
        }
        else{
            if(hmoe_temp.containsKey(eventId)) {
                return hmoe_temp.get(eventId)!!
            }
            else {
                Log.d(Constants.TAG, "Event not found!")
                return Event()
            }
        }
    }
    companion object CREATOR : Parcelable.Creator<EventWrapper> {
        var hmoe = HashMap<String,Event>()
        var hmoe_temp = HashMap<String,Event>()
        var locationAutoCompleteList = ArrayList<String>()

        lateinit var eventsRef: CollectionReference

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

        fun newTempEvent():EventWrapper{
            val evId = Random().nextLong().toString()
            hmoe_temp.put(evId,Event())
            return EventWrapper(evId)
        }
    }
}