package com.example.csse483finalproject.event

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot

data class Location(var roomId:String? = "", var isRoom: Boolean = false, var lat:Float = 0F, var long:Float = 0F, var locName:String = "" ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(roomId)
        parcel.writeByte(if (isRoom) 1 else 0)
        parcel.writeFloat(lat)
        parcel.writeFloat(long)
        parcel.writeString(locName)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun locString(): String{
        if (isRoom){
            return "TODO: Room name lookup from map" //TODO:Room name lookup
        }
        else{
            return locName
        }
    }

    companion object CREATOR : Parcelable.Creator<Location> {
        override fun createFromParcel(parcel: Parcel): Location {
            return Location(parcel)
        }

        override fun newArray(size: Int): Array<Location?> {
            return arrayOfNulls(size)
        }

        fun fromSnapshot(snapshot: DocumentSnapshot): Location{
            val location=snapshot.toObject(Location::class.java)!!
            return location
        }
    }
}