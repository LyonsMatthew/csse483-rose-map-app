package com.example.csse483finalproject.group

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.csse483finalproject.Constants
import com.example.csse483finalproject.event.EventWrapper
import com.google.firebase.firestore.DocumentSnapshot

data class GroupWrapper(var groupId: String = "") : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()
    ) {
    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(groupId)
    }

    fun getMembers(mt: MemberType): ArrayList<UserWrapper>{
        return myGroup().getMembers(mt)
    }

    fun getEvents(): ArrayList<EventWrapper>{ //TODO: Remove and implement properly
        return myGroup().getEvents()
    }

    fun setMemberType(u:UserWrapper, mt: MemberType){
        return myGroup().setMemberType(u,mt)
    }

    fun getMemberType(u:UserWrapper):MemberType{
        return myGroup().getMemberType(u)
    }

    fun getGroupName(): String{
        return myGroup().groupName
    }

    fun getGroupOwners(): MemberSpec{
        return myGroup().groupOwners
    }

    fun getGroupViewers(): MemberSpec{
        return myGroup().groupViewers
    }

    fun getIsSingleUse(): Boolean{
        return myGroup().isSingleUse
    }

    fun getId(): String{
        return myGroup().id
    }

    override fun describeContents(): Int {
        return 0
    }

    fun myGroup(): Group{
        if(hmog.containsKey(groupId)) {
            return hmog.get(groupId)!!
        }
        else{
            Log.d(Constants.TAG, "Group not found!")
            return Group()
        }
    }

    companion object CREATOR : Parcelable.Creator<GroupWrapper> {
        var hmog = HashMap<String,Group>()

        fun setupGroups(gl:ArrayList<Group>){
            hmog = HashMap<String,Group>()
            for (g in gl){
                hmog.put(g.id,g)
            }
        }
        override fun createFromParcel(parcel: Parcel): GroupWrapper {
            return GroupWrapper(parcel)
        }

        override fun newArray(size: Int): Array<GroupWrapper?> {
            return arrayOfNulls(size)
        }

        fun fromSnapshot(snapshot: DocumentSnapshot): GroupWrapper {
            val gw = snapshot.toObject(GroupWrapper::class.java)!!
            return gw
        }

        fun fromGroup(g: Group) : GroupWrapper{
            return GroupWrapper(g.id)
        }
    }
}