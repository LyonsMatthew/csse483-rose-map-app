package com.example.csse483finalproject.map

import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.*
import android.widget.ImageView
import com.example.csse483finalproject.Constants
import com.example.csse483finalproject.R
import com.example.csse483finalproject.R.drawable.ic_add
import com.example.csse483finalproject.map.data.MapData
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.view.*
import java.io.InputStream
import java.io.InputStreamReader

class MapFragment : Fragment() {

    var data: MapData? = null
    var name: String = ""
    var fileName: String = ""
    var imageName: String = ""
    var screenSize: Point = Point(0, 0)

    val lastTouch: Point = Point(0,0)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_map, container, false)
        unpackBundle(view)
        val imageID = activity!!.resources.getIdentifier(imageName, "drawable", context!!.packageName)
        view.map_image.setImageResource(imageID)
        setMapListener(view.map_image)

        val display: Display = activity!!.windowManager.defaultDisplay
        display.getSize(screenSize)
        screenSize.y -= Constants.ACTIONBAR_HEIGHT

        var size: Double = Math.min(screenSize.x, screenSize.y).toDouble()

        val options = BitmapFactory.Options()
        BitmapFactory.decodeResource(resources, imageID, options)

        val scalingFactor = (size) / (options.outWidth)
        val trueImageWidth = options.outWidth*scalingFactor
        val trueImageHeight = options.outHeight*scalingFactor

        size = Math.max(trueImageWidth, trueImageHeight)

//        Log.d(Constants.TAG, screenSize.toString())
//        Log.d(Constants.TAG, imageSize.toString())
//        Log.d(Constants.TAG, trueImageWidth.toString() + " " + trueImageHeight.toString())
//        Log.d(Constants.TAG, scalingFactor.toString())

        val inputStream: InputStream = context!!.assets.open(fileName)
        data = MapData(name)
        data!!.readFile(inputStream, trueImageWidth, trueImageHeight, size)

        val bitmap = Bitmap.createBitmap(screenSize.x, screenSize.y, Bitmap.Config.ARGB_8888)
        drawPaths(view.map_image_overlay, bitmap)

        return view
    }

    private fun setMapListener(imageView: ImageView) {
        imageView.setOnClickListener { _ ->
            val name = findTouchedRoom()
            if (!name.equals("")) {
                showDialog(name)
            }
//            Log.d(Constants.TAG, lastTouch.x.toString() + " " + lastTouch.y)
//            Log.d(Constants.TAG, name)
        }
        imageView.setOnTouchListener { _, motionEvent: MotionEvent ->
            lastTouch.x = motionEvent.x.toInt()
            lastTouch.y = motionEvent.y.toInt()
            false
        }
    }

    private fun findTouchedRoom() : String {
        for (name in data!!.roomMap.keys) {
            val contains = roomContains(data!!.roomMap[name]!!)
            if (contains) {
                return name
            }
        }
        return ""
    }

    private fun roomContains(room: ArrayList<Pair<Double, Double>>) : Boolean {

        //This algorithm was found on this StackOverflow post:
        //https://stackoverflow.com/questions/217578/how-can-i-determine-whether-a-2d-point-is-within-a-polygon
        //Thanks to the mysterious professor credited there whose original webpage is now dead :(
        var inside = false
        var j = room.size-1
        for (i in 0 until room.size) {
            if ((room[i].second.toInt() > lastTouch.y) != (room[j].second.toInt() > lastTouch.y)
                && lastTouch.x < ((room[j].first.toInt() - room[i].first.toInt())
                                    * (lastTouch.y - room[i].second.toInt())
                                   / (room[j].second.toInt() - room[i].second.toInt())
                                   + room[i].first.toInt())) {
                inside = !inside
            }
            j = i
        }
        return inside
    }

    private fun drawPaths(imageView: ImageView, bitmap: Bitmap) {
        //debug function to test map projection stuff
        val canvas = Canvas(bitmap)
        val paint = Paint()
//        paint.color = Color.BLACK
//        canvas.drawPaint(paint)
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        paint.strokeWidth = 7f
        var path = Path()
        path.fillType = Path.FillType.EVEN_ODD
        for (name in data!!.roomMap.keys) {
            path.reset()
            var firstPoint: Pair<Double, Double>? = null
            for (point in data!!.roomMap[name]!!) {
                if (firstPoint == null) {
                    firstPoint = point
                    path.moveTo(firstPoint.first.toFloat(), firstPoint.second.toFloat())
                } else {
                    path.lineTo(point.first.toFloat(), point.second.toFloat())
                }
            }
            path.lineTo(firstPoint!!.first.toFloat(), firstPoint.second.toFloat())
            canvas.drawPath(path, paint)
        }
        path.close()
        canvas.drawPath(path, paint)
//        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        imageView.background = BitmapDrawable(resources, bitmap)
    }

    private fun showDialog(name: String) {
        val builder = AlertDialog.Builder(context!!)
        builder.setMessage(name)
        builder.create().show()
    }

    private fun unpackBundle(view: View) {
        name = arguments!!.getString("name")!!
        fileName = arguments!!.getString("fileName")!! + ".gpx"
        imageName = arguments!!.getString("fileName")!!
    }
}