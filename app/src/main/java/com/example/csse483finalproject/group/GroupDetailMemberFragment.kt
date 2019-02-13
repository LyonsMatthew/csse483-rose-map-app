package com.example.csse483finalproject.group

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.csse483finalproject.R
import com.example.csse483finalproject.event.EventAdapter
import com.example.csse483finalproject.event.EventWrapper
import kotlinx.android.synthetic.main.fragment_groupdetail_member.view.*

class GroupDetailMemberFragment : Fragment(), UserAdapter.mtcInterface, EventAdapter.EventListListener {
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
        return false
    }

    override fun getCurrentMt(u: UserWrapper): MemberType {
        return group.wGetMemberType(u)
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
        val view: View = inflater.inflate(R.layout.fragment_groupdetail_member, container, false)
        unpackBundle(view)
        memberAdapter = UserAdapter(this.context!!,null,this)
        view.member_recycler_view.layoutManager = LinearLayoutManager(this.context)
        view.member_recycler_view.setHasFixedSize(false)
        view.member_recycler_view.adapter = memberAdapter
        eventAdapter = EventAdapter(this.context!!,this,true)
        view.event_recycler_view.layoutManager = LinearLayoutManager(this.context)
        view.event_recycler_view.setHasFixedSize(false)
        view.event_recycler_view.adapter = eventAdapter
        view.group_title.text = group.wGetGroupName()
        val users = group.wGetMembers(MemberType(MT.BOTH))
        for (i in 0 until users.size){
            memberAdapter.add(users[i])
        }
        val events = group.wGetEvents()
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
        fun newInstance(group: GroupWrapper): GroupDetailMemberFragment {
            val fragment = GroupDetailMemberFragment()
            val args = Bundle()
            args.putParcelable(ARG_GROUP, group)
            fragment.arguments = args
            return fragment
        }
    }
}