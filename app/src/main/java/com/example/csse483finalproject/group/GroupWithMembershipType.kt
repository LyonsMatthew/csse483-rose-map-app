package com.example.csse483finalproject.group

import android.os.Parcel
import android.os.Parcelable

data class GroupWithMembershipType(var group: Group, var membertype: MemberType ) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable<Group>(Group::class.java.classLoader),
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

    companion object CREATOR : Parcelable.Creator<Group> {
        override fun createFromParcel(parcel: Parcel): Group {
            return Group(parcel)
        }

        override fun newArray(size: Int): Array<Group?> {
            return arrayOfNulls(size)
        }
    }
}