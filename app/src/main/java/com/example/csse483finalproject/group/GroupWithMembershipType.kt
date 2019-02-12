package com.example.csse483finalproject.group

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot

data class GroupWithMembershipType(var group: GroupWrapper = GroupWrapper(), var membertype: MemberType = MemberType()) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable<GroupWrapper>(GroupWrapper::class.java.classLoader),
        parcel.readParcelable<MemberType>(MemberType::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(group, flags)
        parcel.writeParcelable(membertype, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GroupWithMembershipType> {
        override fun createFromParcel(parcel: Parcel): GroupWithMembershipType {
            return GroupWithMembershipType(parcel)
        }

        override fun newArray(size: Int): Array<GroupWithMembershipType?> {
            return arrayOfNulls(size)
        }

        fun fromSnapshot(snapshot: DocumentSnapshot): GroupWithMembershipType{
            val gwmt=snapshot.toObject(GroupWithMembershipType::class.java)!!
            return gwmt
        }
    }
}