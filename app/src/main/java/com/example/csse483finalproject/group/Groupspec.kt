package com.example.csse483finalproject.group

import android.os.Parcel
import android.os.Parcelable
import com.example.csse483finalproject.group.User

enum class Membertype {
    OWNER, VIEWER, BOTH
}
private fun mtArrayToIntArray(mts:ArrayList<Membertype>): ArrayList<Int> {
    val ia = arrayListOf<Int>()
    for (mt in mts) {
        ia.add(
            when (mt) {
                Membertype.OWNER -> 0
                Membertype.VIEWER -> 1
                Membertype.BOTH -> 2
            }
        )
    }
    return ia
}
private fun mtArrayFromIntArray(ia:ArrayList<Int>): ArrayList<Membertype> {
    val mta = arrayListOf<Membertype>()
    for (i in ia) {
        mta.add(
            when (i) {
                0 -> Membertype.OWNER
                1 -> Membertype.VIEWER
                2 -> Membertype.BOTH
                throw IllegalArgumentException("Invalid int to convert to Membertype") -> Membertype.BOTH
                else -> Membertype.BOTH
            }
        )
    }
    return mta
}

data class Groupspec(var groups:ArrayList<User>, var memberType: ArrayList<Membertype>) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(User.CREATOR)!!,
        mtArrayFromIntArray(parcel.createIntArray().toCollection(ArrayList()))
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(groups)
        parcel.writeIntArray(mtArrayToIntArray(memberType).toIntArray())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Groupspec> {
        override fun createFromParcel(parcel: Parcel): Groupspec {
            return Groupspec(parcel)
        }

        override fun newArray(size: Int): Array<Groupspec?> {
            return arrayOfNulls(size)
        }
    }
}