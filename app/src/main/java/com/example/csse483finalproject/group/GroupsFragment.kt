package com.example.csse483finalproject.group

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.csse483finalproject.R
import kotlinx.android.synthetic.main.fragment_groups.view.*

class GroupsFragment : Fragment() {

    lateinit var adapter: GroupAdapter
    lateinit var groups: ArrayList<GroupWithMembershipType>
    lateinit var listener: GroupListListener

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        groups = arguments!!.getParcelableArrayList<GroupWithMembershipType>(ARG_GROUPS)!!
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is GroupListListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement GroupListListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_groups, container, false)
        unpackBundle(view)
        adapter = GroupAdapter(this.context!!,this)
        view.recycler_view.layoutManager = LinearLayoutManager(this.context)
        view.recycler_view.setHasFixedSize(false)
        view.recycler_view.adapter = adapter
        for (i in 0 until groups.size){
            adapter.add(groups[i])
        }
        return view
    }

    private fun unpackBundle(view: View) {
        //use this to pass arguments if necessary
    }

    fun onGroupClicked(g: GroupWithMembershipType){
        listener.onGroupClicked(g.group)
    }

    interface GroupListListener {
        fun onGroupClicked(g: Group)
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        val ARG_GROUPS = "GROUPS"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(groups: ArrayList<GroupWithMembershipType>): GroupsFragment {
            val fragment = GroupsFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_GROUPS, groups)
            fragment.arguments = args
            return fragment
        }
    }
}