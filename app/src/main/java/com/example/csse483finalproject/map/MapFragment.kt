package com.example.csse483finalproject.map

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.csse483finalproject.R
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_map, container, false)
        unpackBundle(view)
        val inputStream: InputStream = context!!.assets.open(fileName)
        data = MapData(name)
        MapData.readFile(inputStream, data!!)
        view.map_image.setImageResource(context!!.resources.getIdentifier(imageName, "drawable", context!!.packageName))
        return view
    }

    private fun unpackBundle(view: View) {
        name = arguments!!.getString("name")!!
        fileName = arguments!!.getString("fileName")!! + ".gpx"
        imageName = arguments!!.getString("fileName")!!
    }
}