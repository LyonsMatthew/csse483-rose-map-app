package com.example.csse483finalproject

data class AnnotatedString(var string:String, var id: String){
    override fun toString(): String {
        return string
    }
}