package com.example.csse483finalproject.group

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.example.csse483finalproject.R

class UserViewHolder : RecyclerView.ViewHolder, AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        mtcCallback!!.onMemberTypeChange(user, MemberType(mtFromInt(pos)))
    }


    lateinit var user:User
    var mtcCallback: UserAdapter.mtcInterface?=null
    var cardView: CardView = itemView.findViewById(R.id.userCardView)
    val userName: TextView = itemView.findViewById(R.id.userName)
    var adapter: UserAdapter

    constructor(itemView: View, adapter: UserAdapter, mtcCallback: UserAdapter.mtcInterface?) : super(itemView) {
        this.adapter = adapter
        this.mtcCallback = mtcCallback
    }

    fun bind(user: User) {
        this.user=user
        userName.text = user.displayName
        if(mtcCallback!=null){
            val spinner: Spinner = itemView.findViewById(R.id.mt_spinner)
            val enableSpinner = mtcCallback!!.isMemberTypeChangable(user)
            spinner.setEnabled(enableSpinner);
            spinner.setClickable(enableSpinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter.createFromResource(
                adapter.context,
                R.array.membertypes,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the memberAdapter to the spinner
                spinner.adapter = adapter
            }
            spinner.setSelection(mtToInt(mtcCallback!!.getCurrentMt(user).mt))
            spinner.onItemSelectedListener = this
        }
    }

}

//Adapted from MovieQuote solution