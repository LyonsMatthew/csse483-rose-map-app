package com.example.csse483finalproject.event

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.csse483finalproject.Constants
import com.example.csse483finalproject.group.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import java.util.*

data class Event(var eventName:String = "", var eventLocation:Location = Location(),
                 var eventDescription:String = "", var eventStart: Long = System.currentTimeMillis(), var eventEnd: Long = System.currentTimeMillis(),
                 var eventOwners: GroupSpec = GroupSpec(), var eventViewers: GroupSpec = GroupSpec()) :Parcelable {
    @get:Exclude var id=""
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readParcelable(Location::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readLong(),
        parcel.readParcelable(GroupSpec::class.java.classLoader)!!,
        parcel.readParcelable(GroupSpec::class.java.classLoader)!!
    ) {
        id=parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(eventName)
        parcel.writeParcelable(eventLocation, flags)
        parcel.writeString(eventDescription)
        parcel.writeLong(eventStart)
        parcel.writeLong(eventEnd)
        parcel.writeParcelable(eventOwners, flags)
        parcel.writeParcelable(eventViewers, flags)
        parcel.writeString(id)
    }

    fun getUserAccessLevel(u: UserWrapper): MemberType {
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

    fun getGroupAccessLevel(gwmt: GroupWithMembershipType): MemberType {
        if(eventOwners.containsGroup(gwmt)){
            return MemberType(MT.OWNER)
        }
        if(eventViewers.containsGroup(gwmt)){
            return MemberType(MT.VIEWER)
        }
        else{
            return MemberType(MT.NEITHER)
        }
    }

    fun setGroupAccessLevel(gwmt: GroupWithMembershipType, al:MemberType) {
        if(eventOwners.containsGroup(gwmt)){
            if(al.mt == MT.OWNER) {
                return
            }
            if(al.mt == MT.VIEWER) {
                eventOwners.removeGroup(gwmt)
                eventViewers.addGroup(gwmt)
                return
            }
            if (al.mt == MT.NEITHER) {
                eventOwners.removeGroup(gwmt)
                return
            }
        }
        if(eventViewers.containsGroup(gwmt)){
            if(al.mt == MT.OWNER) {
                eventOwners.addGroup(gwmt)
                eventViewers.removeGroup(gwmt)
                return
            }
            if(al.mt == MT.VIEWER) {
                return
            }
            if (al.mt == MT.NEITHER) {
                eventViewers.removeGroup(gwmt)
                return
            }
        }
        if(al.mt==MT.OWNER){
            eventOwners.addGroup(gwmt)
            return
        }
        if(al.mt==MT.VIEWER){
            eventViewers.addGroup(gwmt)
            return
        }
        Log.d(Constants.TAG, "Failed setting group access level")
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