package com.example.csse483finalproject.group

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.csse483finalproject.Constants
import com.google.firebase.firestore.DocumentSnapshot

data class UserWrapper(var userId: String = "") : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun getUsername(): String{
        return myUser().username
    }

    fun getId(): String{
        return myUser().id
    }

    fun getDisplayName(): String{
        return myUser().displayName
    }

    fun getLocationShareGroup(): GroupWrapper{
        return myUser().locationShareGroup
    }

    fun myUser(): User {
        if(UserWrapper.hmou.containsKey(userId)) {
            return UserWrapper.hmou.get(userId)!!
        }
        else{
            Log.d(Constants.TAG, "User not found!")
            return User()
        }
    }

    companion object CREATOR : Parcelable.Creator<UserWrapper> {
        var hmou = HashMap<String,User>()

        fun setupEvents(ul:ArrayList<User>){
            hmou = HashMap<String,User>()
            for (u in ul){
                hmou.put(u.id,u)
            }
        }

        override fun createFromParcel(parcel: Parcel): UserWrapper {
            return UserWrapper(parcel)
        }

        override fun newArray(size: Int): Array<UserWrapper?> {
            return arrayOfNulls(size)
        }

        fun fromSnapshot(snapshot: DocumentSnapshot): UserWrapper{
            val uw=snapshot.toObject(UserWrapper::class.java)!!
            return uw
        }

        fun fromUser(u: User): UserWrapper{
            return UserWrapper(u.id)
        }
    }
}