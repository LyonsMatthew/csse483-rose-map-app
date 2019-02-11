package com.example.csse483finalproject.group

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.csse483finalproject.Constants
import com.example.csse483finalproject.R
import kotlinx.android.synthetic.main.fragment_groups.view.*

class LocationShareFragment : Fragment(), UserAdapter.DeletableUserInterface {
    override fun onDelete(u: User) {
        Log.d(Constants.TAG,"Userdel "+u.toString())
    }

    lateinit var adapter: UserAdapter
    lateinit var users: ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        users = arguments!!.getParcelableArrayList<User>(ARG_USERS)!!
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
        for (i in 0 until users.size){
            adapter.add(users[i])
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
        fun newInstance(users: ArrayList<User>): LocationShareFragment {
            val fragment = LocationShareFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_USERS, users)
            fragment.arguments = args
            return fragment
        }
    }
}