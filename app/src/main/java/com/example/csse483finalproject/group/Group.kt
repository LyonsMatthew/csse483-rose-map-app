package com.example.csse483finalproject.group

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.csse483finalproject.Constants
import com.example.csse483finalproject.event.EventWrapper
import com.google.firebase.firestore.DocumentSnapshot

data class Group(var groupName: String = "", var groupOwners: MemberSpec = MemberSpec(),
                 var groupViewers: MemberSpec = MemberSpec(), var isSingleUse: Boolean = true, var id: String = "" ) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(MemberSpec::class.java.classLoader),
        parcel.readParcelable(MemberSpec::class.java.classLoader),
        parcel.readInt()>0,
        parcel.readString()
    ) {
    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(groupName)
        parcel.writeParcelable(groupOwners, flags)
        parcel.writeParcelable(groupViewers, flags)
        if(isSingleUse) {
            parcel.writeInt(1)
        }
        else {
            parcel.writeInt(0)
        }
        parcel.writeString(id)
    }

    fun getMembers(mt: MemberType): ArrayList<UserWrapper>{
        var members = ArrayList<UserWrapper>()
        if(mt.mt == MT.BOTH) {
            members.addAll(groupOwners.members)
            members.addAll(groupViewers.members)
        }
        if(mt.mt == MT.OWNER) {
            members = groupOwners.members
        }
        if(mt.mt == MT.VIEWER) {
            members = groupViewers.members
        }
        return members
    }

    fun getEvents(): ArrayList<EventWrapper>{ //TODO: Remove and implement properly
        val resultantEvents = ArrayList<EventWrapper>()
        for (event in EventWrapper.hmoe.values){
            if(event.eventOwners.containsGroup(GroupWrapper.fromGroup(this), MemberType(MT.BOTH))){
                resultantEvents.add(EventWrapper.fromEvent(event))
            }
        }
        return resultantEvents
    }

    fun setMemberType(u:UserWrapper, mt: MemberType){
        Log.d(Constants.TAG, "TODO: SetMemberType "+mt.mt.toString())
    }

    fun getMemberType(u:UserWrapper):MemberType{
        if(groupOwners.containsUser(u)){
            return MemberType(MT.OWNER)
        }
        if(groupViewers.containsUser(u)){
            return MemberType(MT.VIEWER)
        }
        else{
            return MemberType(MT.NEITHER)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Group> {
        override fun createFromParcel(parcel: Parcel): Group {
            return Group(parcel)
        }

        override fun newArray(size: Int): Array<Group?> {
            return arrayOfNulls(size)
        }

        fun fromSnapshot(snapshot: DocumentSnapshot): Group {
            val group = snapshot.toObject(Group::class.java)!!
            group.id = snapshot.id
            return group
        }
    }
}