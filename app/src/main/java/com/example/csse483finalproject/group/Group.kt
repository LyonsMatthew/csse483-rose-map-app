package com.example.csse483finalproject.group

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.csse483finalproject.Constants
import com.example.csse483finalproject.event.Event
import com.example.csse483finalproject.event.Location
import java.util.*

data class Group(var groupName: String, var groupOwners: MemberSpec, var groupViewers: MemberSpec, var id:Long ) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(MemberSpec::class.java.classLoader),
        parcel.readParcelable(MemberSpec::class.java.classLoader),
        parcel.readLong()
    ) {
    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(groupName)
        parcel.writeParcelable(groupOwners, flags)
        parcel.writeParcelable(groupViewers, flags)
        parcel.writeLong(id)
    }

    fun getMembers(mt: MemberType): ArrayList<User>{
        var members = ArrayList<User>()
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

    fun getEvents(): ArrayList<Event>{ //TODO: Remove and implement properly
        var testEvents = ArrayList<Event>()
        var testUser = User(0,"crenshch@rose-hulman.edu", "Connor Crenshaw")
        var  testMembers = ArrayList<User>()
        testMembers.add(testUser)
        var testOwnerSpec = MemberSpec(testMembers)
        var testViewerSpec = MemberSpec(ArrayList<User>())
        var testGroup = Group("Test Users",testOwnerSpec,testViewerSpec,0)
        var gwmto = GroupWithMembershipType(testGroup, MemberType(MT.OWNER))
        var gwmtv = GroupWithMembershipType(testGroup, MemberType(MT.VIEWER))
        var gwmtoa = ArrayList<GroupWithMembershipType>()
        gwmtoa.add(gwmto)
        var gwmtov = ArrayList<GroupWithMembershipType>()
        gwmtov.add(gwmtv)
        var testOwner = GroupSpec(gwmtoa)
        var testViewer = GroupSpec(gwmtov)
        testEvents.add(Event("Test RoseMaps", Location(null,false,0F,0F, "Lakeside 402"),"Rose maps is great", Calendar.getInstance(), Calendar.getInstance(),testOwner, testViewer,0))
        testEvents.add(Event("Making events manually is a real pain...", Location(null,false,0F,0F, "Speed Beach"),"Manual event", Calendar.getInstance(), Calendar.getInstance(),testOwner, testViewer,1))
        return testEvents
    }

    fun setMemberType(u:User, mt: MemberType){
        Log.d(Constants.TAG, "TODO: SetMemberType "+mt.mt.toString())
    }

    fun getMemberType(u:User):MemberType{
        if(groupOwners.members.contains(u)){
            return MemberType(MT.OWNER)
        }
        if(groupViewers.members.contains(u)){
            return MemberType(MT.OWNER)
        }
        else{
            throw IllegalArgumentException("Cannot get member type: user not part of group")
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
    }
}