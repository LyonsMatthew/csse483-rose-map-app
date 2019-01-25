package com.example.csse483finalproject.event

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.csse483finalproject.R

class EventDetailsOwnerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_eventdetails_owner, container, false)
        unpackBundle(view)
        return view
    }

    private fun unpackBundle(view: View) {
        //use this to pass arguments if necessary
    }
}