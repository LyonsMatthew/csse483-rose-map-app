package com.example.csse483finalproject.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.csse483finalproject.R
import kotlinx.android.synthetic.main.fragment_eventdetail.view.*

class EventDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_eventdetail, container, false)
        unpackBundle(view)
        view.event_title.text = event.getEventName()
        view.dtext.text = event.getEventDescription()
        view.loctext.text = event.getEventLocation().locString()
        view.tdtext.text = getString(R.string.date_seperator,event.getEventStart().time.toLocaleString(), event.getEventEnd().time.toLocaleString())
        return view
    }
    lateinit var event:EventWrapper

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        event = arguments!!.getParcelable<EventWrapper>(EventDetailsFragment.ARG_EVENT)!!
    }

    private fun unpackBundle(view: View) {
        //use this to pass arguments if necessary
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        val ARG_EVENT = "EVENT"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(event: EventWrapper): EventDetailsFragment {
            val fragment = EventDetailsFragment()
            val args = Bundle()
            args.putParcelable(ARG_EVENT, event)
            fragment.arguments = args
            return fragment
        }
    }
}