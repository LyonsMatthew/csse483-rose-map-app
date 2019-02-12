package com.example.csse483finalproject.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.csse483finalproject.R
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_eventdetails_owner.view.*
import java.util.*



class EventDetailsOwnerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_eventdetails_owner, container, false)
        unpackBundle(view)
        updateView(view)
        view.stdtext.setOnClickListener{
            DatePickerDialog.newInstance(sDateSetListener,
                event.getEventStart().get(Calendar.YEAR),
                event.getEventStart().get(Calendar.MONTH),
                event.getEventStart().get(Calendar.DAY_OF_MONTH))
                .show(getFragmentManager(), "Datepickerdialog");
        }
        view.etdtext.setOnClickListener{
            DatePickerDialog.newInstance(eDateSetListener,
                event.getEventEnd().get(Calendar.YEAR),
                event.getEventEnd().get(Calendar.MONTH),
                event.getEventEnd().get(Calendar.DAY_OF_MONTH))
                .show(getFragmentManager(), "Datepickerdialog");
        }
        return view
    }

    fun updateView(v: View? = null){
        lateinit var myView:View
        if(v != null){
            myView=v
        }
        else{
            myView=this.view!!
        }
        myView.event_title.text = event.getEventName()
        myView.dtext.setText(event.getEventDescription())
        myView.loctext.setText(event.getEventLocation().locString())
        myView.stdtext.setText(event.getEventStart().time.toLocaleString())
        myView.etdtext.setText(event.getEventEnd().time.toLocaleString())
    }

    val sDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        event.getEventStart().set(Calendar.YEAR,year)
        event.getEventStart().set(Calendar.MONTH,monthOfYear)
        event.getEventStart().set(Calendar.DAY_OF_MONTH,dayOfMonth)
        TimePickerDialog.newInstance(sTimeSetListener,
            event.getEventStart().get(Calendar.HOUR_OF_DAY),
            event.getEventStart().get(Calendar.MINUTE),false)
            .show(getFragmentManager(), "Timepickerdialog");
    }

    val sTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hour, minute, _ ->
        event.getEventStart().set(Calendar.HOUR_OF_DAY,hour)
        event.getEventStart().set(Calendar.MINUTE,minute)
        event.getEventStart().set(Calendar.SECOND,0)
        event.getEventStart().set(Calendar.MILLISECOND,0)
        updateView()
    }

    val eDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        event.getEventEnd().set(Calendar.YEAR,year)
        event.getEventEnd().set(Calendar.MONTH,monthOfYear)
        event.getEventEnd().set(Calendar.DAY_OF_MONTH,dayOfMonth)
        TimePickerDialog.newInstance(eTimeSetListener,
            event.getEventEnd().get(Calendar.HOUR_OF_DAY),
            event.getEventEnd().get(Calendar.MINUTE),false)
            .show(getFragmentManager(), "Timepickerdialog");
    }

    val eTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hour, minute, _ ->
        event.getEventEnd().set(Calendar.HOUR_OF_DAY,hour)
        event.getEventEnd().set(Calendar.MINUTE,minute)
        event.getEventEnd().set(Calendar.SECOND,0)
        event.getEventEnd().set(Calendar.MILLISECOND,0)
        updateView()
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
        fun newInstance(event: EventWrapper): EventDetailsOwnerFragment {
            val fragment = EventDetailsOwnerFragment()
            val args = Bundle()
            args.putParcelable(ARG_EVENT, event)
            fragment.arguments = args
            return fragment
        }
    }
}