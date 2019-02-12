package com.example.csse483finalproject.event

import android.os.Parcel
import android.os.Parcelable
import com.example.csse483finalproject.group.GroupSpec
import com.example.csse483finalproject.group.MT
import com.example.csse483finalproject.group.MemberType
import com.example.csse483finalproject.group.UserWrapper
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

data class Event(var eventName:String = "", var eventLocation:Location = Location(),
                 var eventDescription:String = "", var eventStart: Calendar = Calendar.getInstance(), var eventEnd: Calendar = Calendar.getInstance(),
                 var eventOwners: GroupSpec = GroupSpec(), var eventViewers: GroupSpec = GroupSpec(),
                 var id: String = "" ) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readParcelable(Location::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readSerializable() as Calendar,
        parcel.readSerializable() as Calendar,
        parcel.readParcelable(GroupSpec::class.java.classLoader)!!,
        parcel.readParcelable(GroupSpec::class.java.classLoader)!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(eventName)
        parcel.writeParcelable(eventLocation, flags)
        parcel.writeString(eventDescription)
        parcel.writeSerializable(eventStart)
        parcel.writeSerializable(eventEnd)
        parcel.writeParcelable(eventOwners, flags)
        parcel.writeParcelable(eventViewers, flags)
        parcel.writeString(id)
    }

    fun getAccessLevel(u: UserWrapper): MemberType {
        if(eventOwners.containsUser(u)){
            return MemberType(MT.OWNER)
        }
        if(eventViewers.containsUser(u)){
            return MemberType(MT.VIEWER)
        }
        else{
            return MemberType(MT.NEITHER)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Event> {
        override fun createFromParcel(parcel: Parcel): Event {
            return Event(parcel)
        }

        override fun newArray(size: Int): Array<Event?> {
            return arrayOfNulls(size)
        }

        fun fromSnapshot(snapshot: DocumentSnapshot): Event{
            val event=snapshot.toObject(Event::class.java)!!
            event.id = snapshot.id
            return event
        }
    }
}