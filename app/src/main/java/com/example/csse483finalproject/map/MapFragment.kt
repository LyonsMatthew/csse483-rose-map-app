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
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.example.csse483finalproject.Constants
import com.example.csse483finalproject.R
import com.example.csse483finalproject.R.drawable.ic_add
import com.example.csse483finalproject.map.data.MapData
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.view.*
import uk.co.senab.photoview.PhotoView
import uk.co.senab.photoview.PhotoViewAttacher
import java.io.InputStream
import java.io.InputStreamReader

class MapFragment : Fragment() {

    var data: MapData? = null
    var name: String = ""
    var fileName: String = ""
    var imageName: String = ""
    var viewSize: Point = Point(0, 0)
    var scaleImageView: SubsamplingScaleImageView? = null

    var initialMapScale: Float? = null
    var initialMapCenter: PointF? = null
    var centerProjCoeff: PointF? = null

    val lastTouch: Point = Point(0,0)

    var mapImageID = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_map, container, false)
        unpackBundle(view)
        mapImageID = activity!!.resources.getIdentifier(imageName, "drawable", context!!.packageName)
        view.map_image.setImage(ImageSource.resource(mapImageID))

        val viewTreeObserver: ViewTreeObserver = view.viewTreeObserver
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(CustomOnGlobalLayoutListener())
        }

        setMapListener(view.map_image)

        Log.d(Constants.TAG, view.map_image.scale.toString())

        scaleImageView = view.map_image
        Log.d(Constants.TAG, scaleImageView!!.scale.toString() + " " + scaleImageView!!.center.toString())

        return view
    }

    private fun setMapListener(imageView: SubsamplingScaleImageView) {
        imageView.setOnClickListener { _ ->

            val name = findTouchedRoom()
            if (!name.equals("")) {
                showDialog(name)
            }
            Log.d(Constants.TAG, "current " + scaleImageView!!.scale.toString() + " " + scaleImageView!!.center.toString() + " initial " + initialMapScale + " " + initialMapCenter)
            Log.d(Constants.TAG, "lasttouch " + lastTouch.x.toString() + " " + lastTouch.y)
        }
        imageView.setOnTouchListener { _, motionEvent: MotionEvent ->
//            val mapScale = scaleImageView!!.scale
//            val scaleMultiplier = 2 - 1/(mapScale/initialMapScale!!)
//            Log.d(Constants.TAG, "scalemult " + scaleMultiplier)
//            lastTouch.x = (motionEvent.x.toInt() * scaleMultiplier).toInt()
//            lastTouch.y = (motionEvent.y.toInt() * scaleMultiplier).toInt()
            val projectedCenter = PointF(scaleImageView!!.center!!.x * centerProjCoeff!!.x, scaleImageView!!.center!!.y * centerProjCoeff!!.y)
            val mapScale = scaleImageView!!.scale
            val scaleMultiplier = mapScale / initialMapScale!!
            val trueViewSize = PointF(viewSize.x/scaleMultiplier, viewSize.y/scaleMultiplier)
            val leftBound = projectedCenter.x - trueViewSize.x/2
            val topBound = projectedCenter.y - trueViewSize.y/2
            val projCoeff = PointF(motionEvent.x/viewSize.x, motionEvent.y/viewSize.y)

            lastTouch.x = (leftBound + trueViewSize.x * projCoeff.x).toInt()
            lastTouch.y = (topBound + trueViewSize.y * projCoeff.y).toInt()
            Log.d(Constants.TAG, "true touch " + motionEvent.x + " " + motionEvent.y)
            Log.d(Constants.TAG, "bounds: " + leftBound + " " + topBound + " " + trueViewSize + " " + projectedCenter)
            Log.d(Constants.TAG, "projcoeff: " + projCoeff + " " + scaleMultiplier)

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

    private fun drawPaths(imageView: SubsamplingScaleImageView, bitmap: Bitmap) {
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

    private inner class CustomOnGlobalLayoutListener : ViewTreeObserver.OnGlobalLayoutListener {

        override fun onGlobalLayout() {
            val display: Display = activity!!.windowManager.defaultDisplay
            val screenSize: Point = Point(0, 0)
            display.getSize(screenSize)

            viewSize.x = view!!.width
            viewSize.y = view!!.height

            val options = BitmapFactory.Options()
            BitmapFactory.decodeResource(resources, mapImageID, options)

            val scalingFactor = viewSize.x.toDouble() / (options.outWidth)
            val trueImageWidth = options.outWidth*scalingFactor
            val trueImageHeight = options.outHeight*scalingFactor

            if (initialMapScale == null && scaleImageView!!.center != null) {
                initialMapScale = scaleImageView!!.scale
                initialMapCenter = scaleImageView!!.center!!
                centerProjCoeff = PointF(viewSize.x/initialMapCenter!!.x/2, viewSize.y/initialMapCenter!!.y/2)
                Log.d(Constants.TAG, "initial center " + initialMapCenter!!.x + " " + initialMapCenter!!.y)
                Log.d(Constants.TAG, "projected center " + scaleImageView!!.center!!.x*centerProjCoeff!!.x + " " + scaleImageView!!.center!!.y*centerProjCoeff!!.y)

            }

//            Log.d(Constants.TAG, screenSize.toString())
            Log.d(Constants.TAG, viewSize.toString())
            Log.d(Constants.TAG, Point(trueImageWidth.toInt(), trueImageHeight.toInt()).toString())
//            Log.d(Constants.TAG, Point(view!!.map_image.width, view!!.map_image.height).toString())
//            Log.d(Constants.TAG, scalingFactor.toString())

//            Log.d(Constants.TAG, "current " + scaleImageView!!.scale.toString() + " " + scaleImageView!!.center.toString() + " initial " + initialMapScale + " " + initialMapCenter)
            Log.d(Constants.TAG, "lasttouch " + lastTouch.x.toString() + " " + lastTouch.y)

            val inputStream: InputStream = context!!.assets.open(fileName)
            data = MapData(name)
            data!!.readFile(inputStream, trueImageWidth, trueImageHeight, viewSize.y-trueImageHeight)

            val bitmap = Bitmap.createBitmap(viewSize.x, viewSize.y, Bitmap.Config.ARGB_8888)
            drawPaths(view!!.map_image, bitmap)
//            drawPaths(view!!.map_image_overlay, bitmap)
        }
    }
}