package com.example.csse483finalproject.group

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.csse483finalproject.Constants
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot

data class UserWrapper(var userId: String = "") : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun wGetUsername(): String{
        return myUser().username
    }

    fun wSetUsername(username: String) {
        myUser().username = username
    }

    fun wGetId(): String{
        return myUser().id
    }

    fun wGetDisplayName(): String{
        return myUser().displayName
    }

    fun wSetDisplayName(name: String) {
        myUser().displayName = name
    }

    fun wGetLocationShareGroup(): GroupWrapper{
        return myUser().locationShareGroup
    }

    fun wSetLocShare(locShare: Int) {
        myUser().locShare = locShare
    }

    fun wGetSingleUserGroup(): GroupWrapper{
        return myUser().singleUserGroup
    }

    fun saveToCloud(){
//        usersRef.document(userId).set(myUser())
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

        lateinit var usersRef: CollectionReference

        fun setupUsers(ul:ArrayList<User>){
            hmou = HashMap<String,User>()
            for (u in ul){
                hmou.put(u.id,u)
            }
        }

        fun autoCompleteList():ArrayList<String>{
            var acl = ArrayList<String>()
            for (user in hmou.values){
                acl.add(user.displayName)
            }
            return acl
        }

        fun fromDisplayName(dn:String):UserWrapper{
            for ((k,v) in hmou){
                if (v.displayName == dn){
                    return UserWrapper(k)
                }
            }
            return UserWrapper() //Todo: Somthing more useful here
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