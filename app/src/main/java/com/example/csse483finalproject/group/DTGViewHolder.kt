package com.example.csse483finalproject.group

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.csse483finalproject.R

class DTGViewHolder : RecyclerView.ViewHolder {

    inner class mtListener : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
        }
        override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
            dtgCallback!!.onMemberTypeChange(dtg, MemberType(mtFromInt(pos)))
        }
    }

    inner class ptListener : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
        }
        override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
            dtgCallback!!.onPermissionTypeChange(dtg, MemberType(mtFromInt(pos)))
        }
    }


    lateinit var dtg:DualTypeGroup
    var dtgCallback: DTGAdapter.dtgInterface?=null
    var cardView: CardView = itemView.findViewById(R.id.userCardView)
    val userName: TextView = itemView.findViewById(R.id.userName)
    var adapter: DTGAdapter

    constructor(itemView: View, adapter: DTGAdapter, dtgCallback: DTGAdapter.dtgInterface?) : super(itemView) {
        this.adapter = adapter
        this.dtgCallback = dtgCallback
    }

    fun bind(dtg: DualTypeGroup) {
        this.dtg=dtg
        userName.text = dtg.gwmt.group.wGetGroupName()
        val spinnermt: Spinner = itemView.findViewById(R.id.mt_spinner)
        spinnermt.setEnabled(!dtg.gwmt.group.wGetIsSingleUser());
        spinnermt.setClickable(!dtg.gwmt.group.wGetIsSingleUser());
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            adapter.context,
            R.array.membertypes,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the memberAdapter to the spinner
            spinnermt.adapter = adapter
        }
        spinnermt.setSelection(mtToInt(dtg.gwmt.membertype.mt))
        spinnermt.onItemSelectedListener = mtListener()
        val spinnerpt: Spinner = itemView.findViewById(R.id.pt_spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            adapter.context,
            R.array.editview,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the memberAdapter to the spinner
            spinnerpt.adapter = adapter
        }
        spinnerpt.setSelection(mtToInt(dtg.gwmt.membertype.mt))
        spinnerpt.onItemSelectedListener = ptListener()
    }

}

//Adapted from MovieQuote solution