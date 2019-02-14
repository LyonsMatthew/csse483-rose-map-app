package com.example.csse483finalproject.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.csse483finalproject.R
import kotlinx.android.synthetic.main.fragment_eventdetail.view.*

class EventDetailsFragment : Fragment() {

    var listener: EventAdapter.EventListListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_eventdetail, container, false)
        listener = activity!! as EventAdapter.EventListListener
        unpackBundle(view)
        view.event_title.text = event.wGetEventName()
        view.dtext.text = event.wGetEventDescription()
        view.loctext.text = event.wGetEventLocation().locString()
        view.tdtext.text = getString(R.string.date_seperator,event.wGetEventStart().time.toLocaleString(), event.wGetEventEnd().time.toLocaleString())
        val map_image = view.eventmapmember
        map_image!!.setImageResource(activity!!.resources.getIdentifier(arguments!!.getString("filename"),
            "drawable", context!!.packageName))
        map_image.setOnClickListener { _ ->
            listener!!.onMapClick(arguments!!.getString("filename"))
        }
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
        fun newInstance(event: EventWrapper, filename: String): EventDetailsFragment {
            val fragment = EventDetailsFragment()
            val args = Bundle()
            args.putParcelable(ARG_EVENT, event)
            args.putString("filename", filename)
            fragment.arguments = args
            return fragment
        }
    }
}