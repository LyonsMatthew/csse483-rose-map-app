package com.example.csse483finalproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class StartupFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_blank, container, false)
        unpackBundle(view)
        return view
    }

    private fun unpackBundle(view: View) {
        //use this to pass arguments if necessary
    }
}