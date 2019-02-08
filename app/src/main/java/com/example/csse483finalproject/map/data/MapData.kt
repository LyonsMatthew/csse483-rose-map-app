package com.example.csse483finalproject.map.data

import android.graphics.Point
import android.util.Log
import com.example.csse483finalproject.Constants
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

class MapData(val name: String) {
    val roomMap: MutableMap<String, ArrayList<Pair<Double, Double>>> = HashMap<String, ArrayList<Pair<Double, Double>>>()
    var minLat: Double = 0.0
    var minLon: Double = 0.0
    var maxLat: Double = 0.0
    var maxLon: Double = 0.0

    var pBottomScal: Double = 0.0
    var pTopScal: Double = 0.0
    var pLeftScal: Double = 0.0
    var pRightScal: Double = 0.0





    fun readFile(inputStream: InputStream, width: Double, height: Double, screenHeight: Double) {
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
                nextList.add(Pair(lon, lat))
            } else if (line.length >= 8 && line.substring(2, 8).equals("</trk>")) {
                roomMap.put(nextName, nextList)
                nextName = ""
                nextList = ArrayList<Pair<Double, Double>>()
            } else if (line.length >= 11 && line.substring(4, 11).equals("<bounds")) {
                val lineArray = line.split("\"")
                minLat = lineArray[1].toDouble()
                minLon = lineArray[3].toDouble()
                maxLat = lineArray[5].toDouble()
                maxLon = lineArray[7].toDouble()
            } else if (line.length >= 12 && line.substring(4, 12).equals("<padding")) {
                val lineArray = line.split("\"")
                pBottomScal = lineArray[1].toDouble()
                pTopScal = lineArray[3].toDouble()
                pLeftScal = lineArray[5].toDouble()
                pRightScal = lineArray[7].toDouble()
            }
        }
        reader.close()

        scaleDataInitial(width, height, screenHeight)
//        for (s in roomMap.keys) {
//            Log.d(Constants.TAG, s + " " + roomMap[s])
//        }
//        Log.d(Constants.TAG, minLat.toString() + " " + minLon + " " + maxLat + " " + maxLon)
    }

    private fun scaleDataInitial(width: Double, height: Double, screenHeight: Double) {
        val paddingBottom = 60
        val paddingTop = 20
        val paddingLeft = 30
        val paddingRight = 30
        Log.d(Constants.TAG, "sf: " + paddingBottom/width + " " + paddingTop/width + " " + paddingLeft/height + " " + paddingRight/height)
        Log.d(Constants.TAG, "WIDTH HEIGHT " + width + " " + height)
        Log.d(Constants.TAG, "padding " + paddingBottom + " " + paddingTop + " " + paddingLeft + " " + paddingRight + " " + pBottomScal + " " + pTopScal + " " + pLeftScal + " " + pRightScal)

//        val paddingBottom = width * pBottomScal
//        val paddingTop = width * pTopScal
//        val paddingLeft = height * pLeftScal
//        val paddingRight = height * pRightScal
        val paddedWidth = width - paddingLeft - paddingRight
        val paddedHeight = height - paddingTop - paddingBottom

        for (s in roomMap.keys) {
            val currentList = roomMap[s]!!
            for (i in 0 until currentList.size) {
                val newFirst = paddingLeft + (currentList[i].first - minLon) * paddedWidth / (maxLon - minLon)
//                val newSecond = (currentList[i].second - minLat) * size / (maxLat - minLat)
                val newSecond = height - (paddingBottom + (currentList[i].second - minLat) * paddedHeight / (maxLat - minLat)) + screenHeight/2
                currentList[i] = Pair(newFirst, newSecond)
            }
        }
    }
}