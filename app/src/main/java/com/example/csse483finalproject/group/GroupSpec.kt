package com.example.csse483finalproject.group

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot

data class GroupSpec(var groups:ArrayList<GroupWithMembershipType> = ArrayList<GroupWithMembershipType>()) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelableArray(GroupWithMembershipType::class.java.classLoader).toCollection(ArrayList()) as ArrayList<GroupWithMembershipType>
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelableArray(groups.toArray() as Array<GroupWithMembershipType>,flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GroupSpec> {
        override fun createFromParcel(parcel: Parcel): GroupSpec {
            return GroupSpec(parcel)
        }

        override fun newArray(size: Int): Array<GroupSpec?> {
            return arrayOfNulls(size)
        }

        fun fromSnapshot(snapshot: DocumentSnapshot): GroupSpec{
            val gs=snapshot.toObject(GroupSpec::class.java)!!
            return gs
        }
    }

    fun containsUser(u: UserWrapper) : Boolean{
        for (g in groups){
            var groupUsers = g.group.wGetMembers(g.membertype)
            for(tu in groupUsers){
                if (u.wGetId() == tu.wGetId()){
                    return true
                }
            }
        }
        return false
    }

    fun containsGroup(gwmt: GroupWithMembershipType) : Boolean{
        for (tg in groups){
            if(gwmt.membertype.mt==MT.BOTH){
                if (tg.group.wGetId() == gwmt.group.wGetId()){
                    return true
                }
            }
            if(gwmt.membertype.mt==tg.membertype.mt){
                if (tg.group.wGetId() == gwmt.group.wGetId()){
                    return true
                }
            }

        }
        return false
    }

    fun removeGroup(g: GroupWithMembershipType){
        for(tg in groups) {
            if ((g.group.wGetId() == tg.group.wGetId())&&(g.membertype.mt==tg.membertype.mt)) {
                groups.remove(tg)
            }
        }
    }

    fun addGroup(g:GroupWithMembershipType){
        groups.add(g)
    }
}