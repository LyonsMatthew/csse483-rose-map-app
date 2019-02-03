package com.example.csse483finalproject.group

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.csse483finalproject.Constants
import com.example.csse483finalproject.R
import com.example.csse483finalproject.event.Event
import com.example.csse483finalproject.event.EventAdapter
import kotlinx.android.synthetic.main.fragment_groupdetail_member.view.*

class GroupDetailOwnerFragment : Fragment(), UserAdapter.mtcInterface, UserAdapter.DeletableUserInterface, EventAdapter.EventListListener {
    override fun onEventClicked(e: Event) {
        listener.onEventClicked(e)
    }

    override fun onMemberTypeChange(u: User, mt: MemberType) {
        group.setMemberType(u,mt)
    }

    override fun isMemberTypeChangable(u: User): Boolean {
        return true
    }

    override fun getCurrentMt(u: User): MemberType {
        return group.getMemberType(u)
    }

    override fun onDelete(u: User) {
        Log.d(Constants.TAG,"TODO: Userdel "+u.toString())
    }

    lateinit var memberAdapter: UserAdapter
    lateinit var eventAdapter: EventAdapter
    lateinit var listener: EventAdapter.EventListListener

    lateinit var group: Group
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
        view.group_title.text = group.groupName
        ItemTouchHelper(memberAdapter.SwipeCallback()).attachToRecyclerView(view.member_recycler_view)
        val users = group.getMembers(MemberType(MT.BOTH))
        for (i in 0 until users.size){
            memberAdapter.add(users[i])
        }
        val events = group.getEvents()
        for (i in 0 until users.size){
            eventAdapter.add(events[i])
        }
        return view
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
        fun newInstance(group: Group): GroupDetailOwnerFragment {
            val fragment = GroupDetailOwnerFragment()
            val args = Bundle()
            args.putParcelable(ARG_GROUP, group)
            fragment.arguments = args
            return fragment
        }
    }
}