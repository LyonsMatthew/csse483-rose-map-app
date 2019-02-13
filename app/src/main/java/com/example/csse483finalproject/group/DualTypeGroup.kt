package com.example.csse483finalproject.group

import android.os.Parcel
import android.os.Parcelable

data class DualTypeGroup(var gwmt: GroupWithMembershipType = GroupWithMembershipType(), var perm: MemberType = MemberType()):
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(GroupWithMembershipType::class.java.classLoader),
        parcel.readParcelable(MemberType::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(gwmt, flags)
        parcel.writeParcelable(perm, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DualTypeGroup> {
        override fun createFromParcel(parcel: Parcel): DualTypeGroup {
            return DualTypeGroup(parcel)
        }

        override fun newArray(size: Int): Array<DualTypeGroup?> {
            return arrayOfNulls(size)
        }
    }

}