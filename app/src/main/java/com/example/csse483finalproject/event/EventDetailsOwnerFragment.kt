package com.example.csse483finalproject.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.csse483finalproject.AnnotatedString
import com.example.csse483finalproject.R
import com.example.csse483finalproject.group.*
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_eventdetails_owner.view.*
import kotlinx.android.synthetic.main.groupeditor.view.*
import java.util.*


class EventDetailsOwnerFragment : Fragment(), DTGAdapter.dtgInterface {
    override fun onMemberTypeChange(dtg: DualTypeGroup, t: MemberType) {
        dtg.gwmt.membertype=t
    }

    override fun onPermissionTypeChange(dtg: DualTypeGroup, t: MemberType) {
        dtg.perm = t
    }

    override fun onDelete(dtg: DualTypeGroup) {
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_eventdetails_owner, container, false)
        unpackBundle(view)
        updateView(view)
        view.stdtext.setOnClickListener{
            DatePickerDialog.newInstance(sDateSetListener,
                event.wGetEventStart().get(Calendar.YEAR),
                event.wGetEventStart().get(Calendar.MONTH),
                event.wGetEventStart().get(Calendar.DAY_OF_MONTH))
                .show(getFragmentManager(), "Datepickerdialog");
        }
        view.etdtext.setOnClickListener{
            DatePickerDialog.newInstance(eDateSetListener,
                event.wGetEventEnd().get(Calendar.YEAR),
                event.wGetEventEnd().get(Calendar.MONTH),
                event.wGetEventEnd().get(Calendar.DAY_OF_MONTH))
                .show(getFragmentManager(), "Datepickerdialog");
        }
        view.titleview.setOnClickListener {
            val builder = AlertDialog.Builder(view.context)
            builder.setTitle(getString(R.string.edit_eventname))
            val titleEditText = EditText(view.context)
            titleEditText.setText(event.wGetEventName())
            builder.setView(titleEditText)
            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                event.wSetEventName(titleEditText.text.toString())
                updateDatesAndTitle()
            }
            builder.show()
        }
        view.grptext.setOnClickListener {
            val builder = AlertDialog.Builder(view.context)
            builder.setTitle(getString(R.string.addgroups))
            val eventEditorView = layoutInflater.inflate(R.layout.groupeditor, container, false)
            builder.setView(eventEditorView)
            val ssa = GroupWrapper.getGroupSelectorArray()
            var ga = DTGAdapter(eventEditorView.context,this)
            eventEditorView.group_actv.setAdapter(ArrayAdapter<AnnotatedString>(eventEditorView.context, android.R.layout.select_dialog_item, ssa))
            eventEditorView.addbutton.setOnClickListener {
                for (annotatedString in ssa){
                    if (annotatedString.string == eventEditorView.group_actv.text.toString()){
                        ga.add(DualTypeGroup(GroupWithMembershipType(GroupWrapper(annotatedString.id),
                            MemberType(MT.OWNER)
                        ),
                            MemberType(MT.VIEWER)))
                    }
                }
            }
            eventEditorView.groupSelRecycler.setAdapter(ga)
            eventEditorView.groupSelRecycler.layoutManager = LinearLayoutManager(this.context)
            for (gwmt in event.wGetEventOwners().groups){
                ga.add(DualTypeGroup(gwmt, MemberType(MT.OWNER)))
            }
            for (gwmt in event.wGetEventViewers().groups){
                ga.add(DualTypeGroup(gwmt,MemberType(MT.VIEWER)))
            }
            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                event.wGetEventViewers().groups.clear()
                event.wGetEventOwners().groups.clear()
                for (dtg in ga.dtgs){
                    event.wSetGroupAccessLevel(dtg.gwmt,dtg.perm)
                }
            }
            ItemTouchHelper(ga.SwipeCallback()).attachToRecyclerView(eventEditorView.groupSelRecycler)
            builder.show()
        }
        val search = view.loctext
        search.threshold = 1
        search.setAdapter(ArrayAdapter<String>(view.context, android.R.layout.select_dialog_item, EventWrapper.locationAutoCompleteList))
        search.setOnItemClickListener { parent, view, position, id ->
            var locstring = search.text.toString()
            event.wSetEventLocation(Location("",false,0F,0F,locstring)) //Todo: Better locations
        }
        view.savebutton.setOnClickListener {
            var locstring = search.text.toString()
            event.wSetEventLocation(Location("",false,0F,0F,locstring))
            event.wSetEventDescription(view.dtext.text.toString())
            event.saveToCloud()
            //Todo: Return to previous fragment
        }
        view.delbutton.setOnClickListener {
            event.delete()
            //Todo: Return to previous fragment
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
        myView.event_title.text = event.wGetEventName()
        myView.dtext.setText(event.wGetEventDescription())
        myView.loctext.setText(event.wGetEventLocation().locString())
        myView.stdtext.setText(event.wGetEventStart().time.toLocaleString())
        myView.etdtext.setText(event.wGetEventEnd().time.toLocaleString())
    }

    fun updateDatesAndTitle(v: View? = null){
        lateinit var myView:View
        if(v != null){
            myView=v
        }
        else{
            myView=this.view!!
        }
        myView.event_title.text = event.wGetEventName()
        myView.stdtext.setText(event.wGetEventStart().time.toLocaleString())
        myView.etdtext.setText(event.wGetEventEnd().time.toLocaleString())
    }

    val sDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        event.wGetEventStart().set(Calendar.YEAR,year)
        event.wGetEventStart().set(Calendar.MONTH,monthOfYear)
        event.wGetEventStart().set(Calendar.DAY_OF_MONTH,dayOfMonth)
        TimePickerDialog.newInstance(sTimeSetListener,
            event.wGetEventStart().get(Calendar.HOUR_OF_DAY),
            event.wGetEventStart().get(Calendar.MINUTE),false)
            .show(getFragmentManager(), "Timepickerdialog");
    }

    val sTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hour, minute, _ ->
        event.wGetEventStart().set(Calendar.HOUR_OF_DAY,hour)
        event.wGetEventStart().set(Calendar.MINUTE,minute)
        event.wGetEventStart().set(Calendar.SECOND,0)
        event.wGetEventStart().set(Calendar.MILLISECOND,0)
        updateDatesAndTitle()
    }

    val eDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        event.wGetEventEnd().set(Calendar.YEAR,year)
        event.wGetEventEnd().set(Calendar.MONTH,monthOfYear)
        event.wGetEventEnd().set(Calendar.DAY_OF_MONTH,dayOfMonth)
        TimePickerDialog.newInstance(eTimeSetListener,
            event.wGetEventEnd().get(Calendar.HOUR_OF_DAY),
            event.wGetEventEnd().get(Calendar.MINUTE),false)
            .show(getFragmentManager(), "Timepickerdialog");
    }

    val eTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hour, minute, _ ->
        event.wGetEventEnd().set(Calendar.HOUR_OF_DAY,hour)
        event.wGetEventEnd().set(Calendar.MINUTE,minute)
        event.wGetEventEnd().set(Calendar.SECOND,0)
        event.wGetEventEnd().set(Calendar.MILLISECOND,0)
        updateDatesAndTitle()
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