package com.example.csse483finalproject.group

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot

data class MemberSpec(var members:ArrayList<UserWrapper> = ArrayList<UserWrapper>()) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(UserWrapper.CREATOR)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(members)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun containsUser(u: UserWrapper) :Boolean{
        for (tu in members){
            if (u.userId==tu.userId){
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

        fun fromSnapshot(snapshot: DocumentSnapshot): MemberSpec{
            val ms=snapshot.toObject(MemberSpec::class.java)!!
            return ms
        }
    }
}