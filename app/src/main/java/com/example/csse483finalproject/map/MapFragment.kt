package com.example.csse483finalproject.map

import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
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

    val lastTouch = PointF(0f,0f)
    val trueImageDim = PointF(0f, 0f)

    var mapDataHolder: MapDataHolder? = null

    var upwards_view: View? = null
    var downwards_view: View? = null
    var stairs_view: View? = null

    var mapImageID = 0

    var isCampusMap = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_map, container, false)
        unpackBundle(view)
        mapImageID = activity!!.resources.getIdentifier(imageName, "drawable", context!!.packageName)
        view.map_image.setImage(ImageSource.resource(mapImageID))
        upwards_view = view.upwards
        downwards_view = view.downwards
        stairs_view = view.stairs

        upwards_view!!.setOnClickListener { _ ->
            upwards()
        }
        downwards_view!!.setOnClickListener { _ ->
            downwards()
        }

        upwards_view!!.visibility = View.INVISIBLE
        downwards_view!!.visibility = View.INVISIBLE
        stairs_view!!.visibility = View.INVISIBLE

        mapDataHolder = context!! as MapDataHolder

        val viewTreeObserver: ViewTreeObserver = view.viewTreeObserver
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(CustomOnGlobalLayoutListener())
        }

        setMapListener(view.map_image)

//        Log.d(Constants.TAG, view.map_image.scale.toString())

        scaleImageView = view.map_image
//        Log.d(Constants.TAG, scaleImageView!!.scale.toString() + " " + scaleImageView!!.center.toString())

        return view
    }

    private fun setMapListener(imageView: SubsamplingScaleImageView) {
        imageView.setOnClickListener { _ ->
            val name = findTouchedRoom()
            if (!name.equals("")) {
                if(isCampusMap) {
                    mapDataHolder!!.goToMap(name)
                } else {
                    showDialog(name)
//                    Log.d(Constants.TAG, name)
                }
            }
        }
        imageView.setOnTouchListener { _, motionEvent: MotionEvent ->

            if (initialMapScale != null) {
                val scaleMultiplier = scaleImageView!!.scale / initialMapScale!!
                val trueViewSize = PointF(viewSize.x/scaleMultiplier, viewSize.y/scaleMultiplier)

                val topMargin = (viewSize.y - trueImageDim.y)/2
                val topMarginHidden = if (trueViewSize.y <= trueImageDim.y) topMargin else topMargin - (trueViewSize.y - trueImageDim.y)/2

                val leftMargin = (viewSize.x - trueImageDim.x)/2
                val leftMarginHidden = if (trueViewSize.x <= trueImageDim.x) leftMargin else leftMargin - (trueViewSize.x - trueImageDim.x)/2

                val panRemaining = RectF(0f,0f, 0f, 0f)
                scaleImageView!!.getPanRemaining(panRemaining)

                val imageHeightHiddenTop = panRemaining.top / scaleMultiplier
                val imageWidthHiddenLeft = panRemaining.left / scaleMultiplier

                val projTouchY = motionEvent.y / scaleMultiplier
                val projTouchX = motionEvent.x / scaleMultiplier

                lastTouch.x = leftMarginHidden + imageWidthHiddenLeft + projTouchX
                lastTouch.y = topMarginHidden + imageHeightHiddenTop + projTouchY

//            Log.d(Constants.TAG, "lasttouch " + lastTouch.x + " " + lastTouch.y)
            }
            false
        }
    }

    private fun upwards() {
//        Log.d(Constants.TAG, data!!.upwards_filename)
        if (data!!.upwards_filename != "") {
            mapDataHolder!!.goToMap(data!!.upwards_filename)
        }
    }

    private fun downwards() {
//        Log.d(Constants.TAG, data!!.downwards_filename)
        if (data!!.downwards_filename != "") {
            mapDataHolder!!.goToMap(data!!.downwards_filename)
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
        fileName = arguments!!.getString("fileName")!! + ".gpx"
        imageName = arguments!!.getString("fileName")!!
        isCampusMap = arguments!!.getBoolean("isCampusMap")!!
    }

    private inner class CustomOnGlobalLayoutListener : ViewTreeObserver.OnGlobalLayoutListener {

        override fun onGlobalLayout() {

            if (view != null) {
                viewSize.x = view!!.width
                viewSize.y = view!!.height

                val options = BitmapFactory.Options()
                BitmapFactory.decodeResource(resources, mapImageID, options)

                val scalingFactor = viewSize.x.toDouble() / (options.outWidth)
                trueImageDim.x = options.outWidth*scalingFactor.toFloat()
                trueImageDim.y = options.outHeight*scalingFactor.toFloat()

                if (initialMapScale == null && scaleImageView!!.center != null) {
                    initialMapScale = scaleImageView!!.scale
                    initialMapCenter = scaleImageView!!.center!!
                }

                if (data == null) {
                    data = mapDataHolder!!.getMapData(imageName)
                    data!!.scaleDataInitial(trueImageDim.x.toDouble(), trueImageDim.y.toDouble(), viewSize.y-trueImageDim.y.toDouble())
                    name = data!!.name
                    if (data!!.upwards_filename != "") {
                        upwards_view!!.visibility = View.VISIBLE
                        stairs_view!!.visibility = View.VISIBLE
                    }
                    if (data!!.downwards_filename != "") {
                        downwards_view!!.visibility = View.VISIBLE
                        stairs_view!!.visibility = View.VISIBLE
                    }
                }

                val bitmap = Bitmap.createBitmap(viewSize.x, viewSize.y, Bitmap.Config.ARGB_8888)
            drawPaths(view!!.map_image, bitmap)
//                drawPaths(view!!.map_image_overlay, bitmap)
            }
        }
    }

    interface MapDataHolder {
        fun getMapData(filename: String) : MapData
        fun goToMap(filename: String)
    }
}