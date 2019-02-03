package com.example.csse483finalproject.group

import android.os.Parcel
import android.os.Parcelable

data class GroupSpec(var groups:ArrayList<GroupWithMembershipType>) : Parcelable {
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
    }
}