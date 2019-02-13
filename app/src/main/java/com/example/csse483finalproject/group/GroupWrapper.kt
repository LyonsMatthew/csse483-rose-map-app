package com.example.csse483finalproject.group

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.csse483finalproject.AnnotatedString
import com.example.csse483finalproject.Constants
import com.example.csse483finalproject.event.EventWrapper
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*
import kotlin.collections.ArrayList

data class GroupWrapper(var groupId: String = "") : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()
    ) {
    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(groupId)
    }

    fun wGetMembers(mt: MemberType): ArrayList<UserWrapper>{
        return myGroup().getMembers(mt)
    }

    fun wGetEvents(): ArrayList<EventWrapper>{ //TODO: Remove and implement properly
        return myGroup().getEvents()
    }

    fun wSetMemberType(u:UserWrapper, mt: MemberType){
        return myGroup().setMemberType(u,mt)
    }

    fun wGetMemberType(u:UserWrapper):MemberType{
        return myGroup().getMemberType(u)
    }

    fun wGetGroupName(): String{
        return myGroup().groupName
    }

    fun wGetGroupOwners(): MemberSpec{
        return myGroup().groupOwners
    }

    fun wGetGroupViewers(): MemberSpec{
        return myGroup().groupViewers
    }

    fun wGetIsSingleUser(): Boolean{
        return myGroup().isSingleUser
    }

    fun wGetIsHidden(): Boolean{
        return myGroup().isHidden
    }

    fun wSetGroupName(gn:String){
        myGroup().groupName=gn
    }

    fun wSetGroupOwners(go:MemberSpec){
        myGroup().groupOwners = go
    }

    fun wSetGroupViewers(gv:MemberSpec){
        myGroup().groupViewers = gv
    }

    fun wSetIsSingleUser(isu: Boolean) {
        myGroup().isSingleUser = isu
    }

    fun wSetIsHidden(ih:Boolean){
        myGroup().isHidden = ih
    }

    fun saveToCloud(){
        groupsRef.document(groupId).set(myGroup()).addOnSuccessListener {
            if (hmog_temp.containsKey(groupId)){
                hmog_temp.remove(groupId)
            }
        }
    }

    fun delete() {
        if (hmog.containsKey(groupId)) {
            groupsRef.document(groupId).delete()
        }
        if (hmog_temp.containsKey(groupId)) {
            hmog_temp.remove(groupId)
        }
    }

    fun wGetId(): String{
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
            if(hmog_temp.containsKey(groupId)){
                return hmog_temp.get(groupId)!!
            }
            else {
                Log.d(Constants.TAG, "Group not found!")
                return Group()
            }
        }
    }

    companion object CREATOR : Parcelable.Creator<GroupWrapper> {
        var hmog = HashMap<String,Group>()
        var hmog_temp = HashMap<String,Group>()

        lateinit var groupsRef: CollectionReference

        fun setupGroups(gl:ArrayList<Group>){
            hmog = HashMap<String,Group>()
            for (g in gl){
                hmog.put(g.id,g)
            }
        }

        fun getGroupSelectorArray(): ArrayList<AnnotatedString>{
            var ssa = ArrayList<AnnotatedString>()
            for ((id,group) in hmog){
                if(!group.isHidden){
                    ssa.add(AnnotatedString(group.groupName,id))
                }
            }
            return ssa
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

        fun newTempGroup():GroupWrapper{
            val grpId = Random().nextLong().toString()
            GroupWrapper.hmog_temp.put(grpId, Group())
            return GroupWrapper(grpId)
        }
    }
}