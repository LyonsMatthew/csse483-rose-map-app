package com.example.csse483finalproject.group

import android.os.Parcel
import android.os.Parcelable

enum class MT {
    OWNER, VIEWER, BOTH
}

data class MemberType(val mt:MT):Parcelable {
    constructor(parcel: Parcel) : this(
        mtFromInt(parcel.readInt())
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(mtToInt(mt))
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MemberType> {
        override fun createFromParcel(parcel: Parcel): MemberType {
            return MemberType(parcel)
        }

        override fun newArray(size: Int): Array<MemberType?> {
            return arrayOfNulls(size)
        }
    }
}

fun mtToInt(mt:MT): Int {
    return when (mt) {
        MT.OWNER -> 0
        MT.VIEWER -> 1
        MT.BOTH -> 2
    }
}

fun mtFromInt(i:Int): MT {
    return when (i) {
        0 -> MT.OWNER
        1 -> MT.VIEWER
        2 -> MT.BOTH
        throw IllegalArgumentException("Invalid int to convert to Membertype") -> MT.BOTH
        else -> MT.BOTH
    }
}