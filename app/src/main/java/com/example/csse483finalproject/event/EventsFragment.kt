package com.example.csse483finalproject.event

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.csse483finalproject.R
import com.example.csse483finalproject.group.*
import kotlinx.android.synthetic.main.fragment_events.view.*
import java.util.*
import kotlin.collections.ArrayList

class EventsFragment : Fragment() {

    lateinit var adapter: EventAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_events, container, false)
        unpackBundle(view)
        adapter = EventAdapter(this.context!!)
        view.recycler_view.layoutManager = LinearLayoutManager(this.context)
        view.recycler_view.setHasFixedSize(true)
        view.recycler_view.adapter = adapter
        var testEvents = ArrayList<Event>()
        var testUser = User(0,"crenshch@rose-hulman.edu")
        var testMembers = ArrayList<User>()
        testMembers.add(testUser)
        var testOwnerSpec = Memberspec(testMembers)
        var testViewerSpec = Memberspec(ArrayList<User>())
        var testGroup = Group(testOwnerSpec,testViewerSpec,0)
        var testgArr=ArrayList<Group>()
        testgArr.add(testGroup)
        var testgMTarr = ArrayList<Membertype>()
        testgMTarr.add(Membertype.OWNER)
        var testgVTarr = ArrayList<Membertype>()
        testgVTarr.add(Membertype.VIEWER)
        var testOwner = Groupspec(testgArr,testgMTarr)
        var testViewer = Groupspec(testgArr,testgMTarr)
        testEvents.add(Event("Test RoseMaps", Location(null,false,0F,0F, "Lakeside 402"), Date(119,0,31,8,30), Date(119,0,31,9,30),testOwner, testViewer,0))
        testEvents.add(Event("Making events manually is a real pain...", Location(null,false,0F,0F, "Speed Beach"), Date(119,0,31,8,30), Date(119,0,31,9,30),testOwner, testViewer,1))

        for (i in 0 until testEvents.size){
            adapter.add(testEvents[i])
        }
        return view
    }

    private fun unpackBundle(view: View) {
        //use this to pass arguments if necessary
    }
}