package com.example.csse483finalproject.group

import android.os.Parcel
import android.os.Parcelable

data class MemberSpec(var members:ArrayList<User>) :Parcelable {
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

    fun containsUser(u: User) :Boolean{
        for (tu in members){
            if (u.id==tu.id){
                return true
            }
        }
        return false
    }

    companion object CREATOR : Parcelable.Creator<MemberSpec> {
        override fun createFromParcel(parcel: Parcel): MemberSpec {
            return MemberSpec(parcel)
        }

        override fun newArray(size: Int): Array<MemberSpec?> {
            return arrayOfNulls(size)
        }
    }
}