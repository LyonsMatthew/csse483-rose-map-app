package com.example.csse483finalproject.group

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

data class User(var username: String = "", var displayName:String = "",
                var locationShareGroup: GroupWrapper = GroupWrapper(),
                var locShare: Int = 0,
                var singleUserGroup:GroupWrapper = GroupWrapper()) :Parcelable
                {
    @get:Exclude var id=""
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(GroupWrapper::class.java.classLoader),
        parcel.readInt(),
        parcel.readParcelable(GroupWrapper::class.java.classLoader)
    ) {
        id=parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(displayName)
        parcel.writeParcelable(locationShareGroup, flags)
        parcel.writeParcelable(singleUserGroup, flags)
        parcel.writeInt(locShare)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }

        fun fromSnapshot(snapshot: DocumentSnapshot): User{
            val user=snapshot.toObject(User::class.java)!!
            user.id = snapshot.id
            return user
        }
    }
}