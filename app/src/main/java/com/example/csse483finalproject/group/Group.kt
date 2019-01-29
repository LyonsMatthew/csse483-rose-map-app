package com.example.csse483finalproject.group

import android.os.Parcel
import android.os.Parcelable
import com.example.csse483finalproject.group.Memberspec

data class Group(var groupOwners: Memberspec, var groupViewers: Memberspec, var id:Long ) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Memberspec::class.java.classLoader),
        parcel.readParcelable(Memberspec::class.java.classLoader),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(groupOwners, flags)
        parcel.writeParcelable(groupViewers, flags)
        parcel.writeLong(id)
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