package com.example.csse483finalproject.event

import android.os.Parcel
import android.os.Parcelable
import com.example.csse483finalproject.groupspec.Groupspec
import java.util.Date

data class Event(var eventName:String, var eventStart: Date, var eventEnd: Date, var eventOwners: Groupspec, var eventViewers: Groupspec, val id: Long ) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        TODO("eventStart"),
        TODO("eventEnd"),

        parcel.readParcelable(Groupspec::class.java.classLoader),
        parcel.readParcelable(Groupspec::class.java.classLoader),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(eventName)
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