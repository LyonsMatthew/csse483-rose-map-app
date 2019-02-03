package com.example.csse483finalproject.group

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
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
        groupName.text = group.group.groupName
        groupMemberType.text = adapter.context.resources.getStringArray(R.array.membertypes)[mtToInt(group.membertype.mt)]
        cardView.setOnClickListener {
            adapter.parentFragment.onGroupClicked(group)
        }
    }
}

//Adapted from MovieQuote solution