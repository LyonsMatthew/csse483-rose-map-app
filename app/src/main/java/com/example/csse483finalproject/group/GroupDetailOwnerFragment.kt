package com.example.csse483finalproject.group

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.csse483finalproject.R
import com.example.csse483finalproject.event.EventAdapter
import com.example.csse483finalproject.event.EventWrapper
import kotlinx.android.synthetic.main.fragment_groupdetail_owner.view.*

class GroupDetailOwnerFragment : Fragment(), UserAdapter.mtcInterface, UserAdapter.DeletableUserInterface, EventAdapter.EventListListener {
    override fun onCreateEvent() {
        listener.onCreateEvent()
    }

    override fun onEventClicked(e: EventWrapper) {
        listener.onEventClicked(e)
    }

    override fun onMemberTypeChange(u: UserWrapper, mt: MemberType) {
        group.wSetMemberType(u,mt)
    }

    override fun isMemberTypeChangable(u: UserWrapper): Boolean {
        return true
    }

    override fun getCurrentMt(u: UserWrapper): MemberType {
        return group.wGetMemberType(u)
    }

    override fun onDelete(u: UserWrapper) {
        group.wSetMemberType(u, MemberType(MT.NEITHER))
    }

    lateinit var memberAdapter: UserAdapter
    lateinit var eventAdapter: EventAdapter
    lateinit var listener: EventAdapter.EventListListener

    lateinit var group: GroupWrapper
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        group = arguments!!.getParcelable(ARG_GROUP)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_groupdetail_owner, container, false)
        unpackBundle(view)
        memberAdapter = UserAdapter(this.context!!,this,this)
        view.member_recycler_view.layoutManager = LinearLayoutManager(this.context)
        view.member_recycler_view.setHasFixedSize(false)
        view.member_recycler_view.adapter = memberAdapter
        eventAdapter = EventAdapter(this.context!!,this,true)
        view.event_recycler_view.layoutManager = LinearLayoutManager(this.context)
        view.event_recycler_view.setHasFixedSize(false)
        view.event_recycler_view.adapter = eventAdapter
        view.titleview.setOnClickListener {
            val builder = AlertDialog.Builder(view.context)
            builder.setTitle(getString(R.string.edit_groupname))
            val titleEditText = EditText(view.context)
            titleEditText.setText(group.wGetGroupName())
            builder.setView(titleEditText)
            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                group.wSetGroupName(titleEditText.text.toString())
                updateView()
            }
            builder.show()
        }
        view.addmemberbutton.setOnClickListener {
            val builder = AlertDialog.Builder(view.context)
            builder.setTitle(getString(R.string.addmember))
            val memberAddText = AutoCompleteTextView(view.context)
            memberAddText.setText("")
            memberAddText.setHint(getString(R.string.person_prompt))
            memberAddText.setAdapter(ArrayAdapter<String>(view.context, android.R.layout.select_dialog_item, UserWrapper.autoCompleteList()))
            builder.setView(memberAddText)
            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                var user = UserWrapper.fromDisplayName(memberAddText.text.toString())
                if (user.wGetDisplayName()!="" && group.wGetMemberType(user)==MemberType(MT.NEITHER)){
                    group.wSetMemberType(user, MemberType(MT.VIEWER))
                    memberAdapter.add(user)
                }
                updateView()
            }
            builder.show()
        }
        view.savebutton.setOnClickListener {
            group.saveToCloud()
            //Todo: Go back
        }
        view.delbutton.setOnClickListener {
            group.delete()
            //Todo: Go back
        }
        ItemTouchHelper(memberAdapter.SwipeCallback()).attachToRecyclerView(view.member_recycler_view)
        val users = group.wGetMembers(MemberType(MT.BOTH))
        for (i in 0 until users.size){
            memberAdapter.add(users[i])
        }
        val events = group.wGetEvents()
        for (i in 0 until events.size){
            eventAdapter.add(events[i])
        }
        updateView(view)
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
        myView.group_title.text = group.wGetGroupName()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EventAdapter.EventListListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement EventListListener")
        }
    }

    private fun unpackBundle(view: View) {
        //use this to pass arguments if necessary
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        val ARG_GROUP = "GROUP"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(group: GroupWrapper): GroupDetailOwnerFragment {
            val fragment = GroupDetailOwnerFragment()
            val args = Bundle()
            args.putParcelable(ARG_GROUP, group)
            fragment.arguments = args
            return fragment
        }
    }
}