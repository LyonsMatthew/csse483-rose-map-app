package com.example.csse483finalproject.group

import android.os.Parcel
import android.os.Parcelable

data class Memberspec(var members:ArrayList<User>) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(User.CREATOR)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(members)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Memberspec> {
        override fun createFromParcel(parcel: Parcel): Memberspec {
            return Memberspec(parcel)
        }

        override fun newArray(size: Int): Array<Memberspec?> {
            return arrayOfNulls(size)
        }
    }
}