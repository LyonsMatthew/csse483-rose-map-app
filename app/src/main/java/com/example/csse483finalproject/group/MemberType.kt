package com.example.csse483finalproject.group

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot

enum class MT {
    OWNER, VIEWER, BOTH, NEITHER
}

data class MemberType(val mt:MT = MT.BOTH):Parcelable {
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

        fun fromSnapshot(snapshot: DocumentSnapshot): MemberType{
            val mbt=snapshot.toObject(MemberType::class.java)!!
            return mbt
        }
    }
}

fun mtToInt(mt:MT): Int {
    return when (mt) {
        MT.OWNER -> 0
        MT.VIEWER -> 1
        MT.BOTH -> 2
        MT.NEITHER -> 3
    }
}

fun mtFromInt(i:Int): MT {
    return when (i) {
        0 -> MT.OWNER
        1 -> MT.VIEWER
        2 -> MT.BOTH
        3 -> MT.NEITHER
        throw IllegalArgumentException("Invalid int to convert to Membertype") -> MT.NEITHER
        else -> MT.NEITHER
    }
}