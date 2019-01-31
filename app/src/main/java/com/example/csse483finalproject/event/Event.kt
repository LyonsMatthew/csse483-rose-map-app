package com.example.csse483finalproject.event

import android.os.Parcel
import android.os.Parcelable
import com.example.csse483finalproject.group.Groupspec
import java.util.*

data class Event(var eventName:String, var eventLocation:Location, var eventStart: Date, var eventEnd: Date, var eventOwners: Groupspec, var eventViewers: Groupspec, val id: Long ) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readParcelable(Location::class.java.classLoader)!!,
        parcel.readSerializable() as Date,
        parcel.readSerializable() as Date,
        parcel.readParcelable(Groupspec::class.java.classLoader)!!,
        parcel.readParcelable(Groupspec::class.java.classLoader)!!,
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(eventName)
        parcel.writeParcelable(eventLocation, flags)
        parcel.writeSerializable(eventStart)
        parcel.writeSerializable(eventEnd)
        parcel.writeParcelable(eventOwners, flags)
        parcel.writeParcelable(eventViewers, flags)
        parcel.writeLong(id)
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
    }
}