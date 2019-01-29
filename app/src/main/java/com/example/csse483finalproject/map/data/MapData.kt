package com.example.csse483finalproject.map.data

import android.util.Log
import com.example.csse483finalproject.Constants
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

class MapData(val name: String) {
    val roomMap: MutableMap<String, ArrayList<Pair<Double, Double>>> = HashMap<String, ArrayList<Pair<Double, Double>>>()

    companion object {
        fun readFile(inputStream: InputStream, data: MapData) {
            val reader = BufferedReader(InputStreamReader(inputStream))

            var inTrk = false
            var nextName = ""
            var nextList: ArrayList<Pair<Double, Double>> = ArrayList<Pair<Double, Double>>()
            val lines = reader.readLines()

            for (line in lines) {
                if (line.length >= 7 && line.substring(2, 7).equals("<trk>")) {
                    inTrk = true
                } else if (line.length >= 10 && line.substring(4, 10).equals("<name>")) {
                    nextName = line.substring(10, line.length-1).split('<')[0]
                } else if (line.length >= 13 && line.substring(6, 12).equals("<trkpt")) {
                    val lat = line.split("\"")[1].toDouble()
                    val lon = line.split("\"")[3].toDouble()
                    nextList.add(Pair(lat, lon))
                } else if (line.length >= 8 && line.substring(2, 8).equals("</trk>")) {
                    data.roomMap.put(nextName, nextList)
                    nextName = ""
                    nextList = ArrayList<Pair<Double, Double>>()
                }
            }
            reader.close()
//            for (s in data.roomMap.keys) {
//                Log.d(Constants.TAG, s + " " + data.roomMap.get(s))
//            }
        }
    }
}