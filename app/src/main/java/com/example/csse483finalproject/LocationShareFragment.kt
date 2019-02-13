package com.example.csse483finalproject.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.csse483finalproject.R
import kotlinx.android.synthetic.main.fragment_locshare.view.*

class LocationShareFragment : Fragment(), UserAdapter.DeletableUserInterface {
    override fun onDelete(u: UserWrapper) {
        lsg.wSetMemberType(u,MemberType(MT.NEITHER))
        lsg.saveToCloud()
    }

    lateinit var adapter: UserAdapter
    lateinit var lsg: GroupWrapper
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        lsg = arguments!!.getParcelable(ARG_USERS)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_locshare, container, false)
        unpackBundle(view)
        adapter = UserAdapter(this.context!!,this)
        view.recycler_view.layoutManager = LinearLayoutManager(this.context)
        view.recycler_view.setHasFixedSize(false)
        view.recycler_view.adapter = adapter
        ItemTouchHelper(adapter.SwipeCallback()).attachToRecyclerView(view.recycler_view)
        for (uw in lsg.wGetMembers(MemberType(MT.BOTH))){
            adapter.add(uw)
        }
        view.actv_person.setAdapter(ArrayAdapter<String>(view.context, android.R.layout.select_dialog_item, UserWrapper.autoCompleteList()))
        view.addbutton.setOnClickListener {
            val uw = UserWrapper.fromDisplayName(view.actv_person.text.toString())
            if(uw.wGetDisplayName()!=""){
                lsg.wSetMemberType(uw,MemberType(MT.VIEWER))
                lsg.saveToCloud()
                adapter.add(uw)
            }
        }
        return view
    }

    private fun unpackBundle(view: View) {
        //use this to pass arguments if necessary
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        val ARG_USERS = "USERS"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(lsg: GroupWrapper): LocationShareFragment {
            val fragment = LocationShareFragment()
            val args = Bundle()
            args.putParcelable(ARG_USERS, lsg)
            fragment.arguments = args
            return fragment
        }
    }
}