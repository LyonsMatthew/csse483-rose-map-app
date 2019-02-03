package com.example.csse483finalproject.event

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.csse483finalproject.R
import kotlinx.android.synthetic.main.fragment_events.view.*

class EventsFragment : Fragment(), EventAdapter.EventListListener {

    lateinit var adapter: EventAdapter
    lateinit var events: ArrayList<Event>
    lateinit var listener: EventAdapter.EventListListener

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        events = arguments!!.getParcelableArrayList<Event>(ARG_EVENTS)!!
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EventAdapter.EventListListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement EventListListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_events, container, false)
        unpackBundle(view)
        adapter = EventAdapter(this.context!!,this)
        view.recycler_view.layoutManager = LinearLayoutManager(this.context)
        view.recycler_view.setHasFixedSize(false)
        view.recycler_view.adapter = adapter
        for (i in 0 until events.size){
            adapter.add(events[i])
        }
        return view
    }

    private fun unpackBundle(view: View) {
        //use this to pass arguments if necessary
    }

    override fun onEventClicked(e: Event){
        listener.onEventClicked(e)
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        val ARG_EVENTS = "EVENTS"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(events: ArrayList<Event>): EventsFragment {
            val fragment = EventsFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_EVENTS, events)
            fragment.arguments = args
            return fragment
        }
    }
}