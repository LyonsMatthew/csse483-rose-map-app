package com.example.csse483finalproject.group

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.csse483finalproject.R

class GroupViewHolder : RecyclerView.ViewHolder {
    var group:GroupWithMembershipType? = null
    var cardView: CardView = itemView.findViewById(R.id.groupCardView)
    val groupName: TextView = itemView.findViewById(R.id.groupName)
    val groupMemberType: TextView = itemView.findViewById(R.id.eventDate)
    var adapter: GroupAdapter

    constructor(itemView: View, adapter: GroupAdapter) : super(itemView) {
        this.adapter = adapter
    }

    fun bind(group: GroupWithMembershipType) {
        this.group=group
        groupName.text = group.group.wGetGroupName()
        groupMemberType.text = adapter.mtypes[mtToInt(group.membertype.mt)]
        if(adapter.parentFragment!=null) {
            cardView.setOnClickListener {
                adapter.parentFragment!!.onGroupClicked(group)
            }
        }
    }
}

//Adapted from MovieQuote solution