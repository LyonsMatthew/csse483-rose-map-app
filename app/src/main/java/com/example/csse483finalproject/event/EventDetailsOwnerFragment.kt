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
                event.eventStart.get(Calendar.YEAR),
                event.eventStart.get(Calendar.MONTH),
                event.eventStart.get(Calendar.DAY_OF_MONTH))
                .show(getFragmentManager(), "Datepickerdialog");
        }
        view.etdtext.setOnClickListener{
            DatePickerDialog.newInstance(eDateSetListener,
                event.eventEnd.get(Calendar.YEAR),
                event.eventEnd.get(Calendar.MONTH),
                event.eventEnd.get(Calendar.DAY_OF_MONTH))
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
        myView.event_title.text = event.eventName
        myView.dtext.setText(event.eventDescription)
        myView.loctext.setText(event.eventLocation.locString())
        myView.stdtext.setText(event.eventStart.time.toLocaleString())
        myView.etdtext.setText(event.eventEnd.time.toLocaleString())
    }

    val sDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        event.eventStart.set(Calendar.YEAR,year)
        event.eventStart.set(Calendar.MONTH,monthOfYear)
        event.eventStart.set(Calendar.DAY_OF_MONTH,dayOfMonth)
        TimePickerDialog.newInstance(sTimeSetListener,
            event.eventStart.get(Calendar.HOUR_OF_DAY),
            event.eventStart.get(Calendar.MINUTE),false)
            .show(getFragmentManager(), "Timepickerdialog");
    }

    val sTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hour, minute, _ ->
        event.eventStart.set(Calendar.HOUR_OF_DAY,hour)
        event.eventStart.set(Calendar.MINUTE,minute)
        event.eventStart.set(Calendar.SECOND,0)
        event.eventStart.set(Calendar.MILLISECOND,0)
        updateView()
    }

    val eDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        event.eventEnd.set(Calendar.YEAR,year)
        event.eventEnd.set(Calendar.MONTH,monthOfYear)
        event.eventEnd.set(Calendar.DAY_OF_MONTH,dayOfMonth)
        TimePickerDialog.newInstance(eTimeSetListener,
            event.eventEnd.get(Calendar.HOUR_OF_DAY),
            event.eventEnd.get(Calendar.MINUTE),false)
            .show(getFragmentManager(), "Timepickerdialog");
    }

    val eTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hour, minute, _ ->
        event.eventEnd.set(Calendar.HOUR_OF_DAY,hour)
        event.eventEnd.set(Calendar.MINUTE,minute)
        event.eventEnd.set(Calendar.SECOND,0)
        event.eventEnd.set(Calendar.MILLISECOND,0)
        updateView()
    }

    lateinit var event:Event

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        event = arguments!!.getParcelable<Event>(EventDetailsFragment.ARG_EVENT)!!
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
        fun newInstance(event: Event): EventDetailsOwnerFragment {
            val fragment = EventDetailsOwnerFragment()
            val args = Bundle()
            args.putParcelable(ARG_EVENT, event)
            fragment.arguments = args
            return fragment
        }
    }
}