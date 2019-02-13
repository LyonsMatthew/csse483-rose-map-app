package com.example.csse483finalproject.group

import android.os.Parcel
import android.os.Parcelable
import com.example.csse483finalproject.event.EventWrapper
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

data class Group(var groupName: String = "", var groupOwners: MemberSpec = MemberSpec(),
                 var groupViewers: MemberSpec = MemberSpec(), var isSingleUser: Boolean = true, var isHidden:Boolean=true) :Parcelable {
    @get:Exclude var id=""
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(MemberSpec::class.java.classLoader),
        parcel.readParcelable(MemberSpec::class.java.classLoader),
        parcel.readInt()>0,
        parcel.readInt()>0
    ) {
        id=parcel.readString()
    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(groupName)
        parcel.writeParcelable(groupOwners, flags)
        parcel.writeParcelable(groupViewers, flags)
        if(isSingleUser) {
            parcel.writeInt(1)
        }
        else {
            parcel.writeInt(0)
        }
        if(isHidden) {
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
            if(event.eventOwners.containsGroup(GroupWithMembershipType(GroupWrapper.fromGroup(this), MemberType(MT.BOTH)))){
                resultantEvents.add(EventWrapper.fromEvent(event))
            }
        }
        return resultantEvents
    }

    fun setMemberType(u: UserWrapper, mt: MemberType) {
        if(groupOwners.containsUser(u)){
            if(mt.mt == MT.OWNER) {
                return
            }
            if(mt.mt == MT.VIEWER) {
                groupOwners.removeUser(u)
                groupViewers.addUser(u)
                return
            }
            if (mt.mt == MT.NEITHER) {
                groupOwners.removeUser(u)
                return
            }
        }
        if(groupViewers.containsUser(u)){
            if(mt.mt == MT.OWNER) {
                groupOwners.addUser(u)
                groupViewers.removeUser(u)
                return
            }
            if(mt.mt == MT.VIEWER) {
                return
            }
            if (mt.mt == MT.NEITHER) {
                groupViewers.removeUser(u)
                return
            }
        }
        if(mt.mt==MT.OWNER){
            groupOwners.addUser(u)
            return
        }
        if(mt.mt==MT.VIEWER){
            groupViewers.addUser(u)
            return
        }
        return
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