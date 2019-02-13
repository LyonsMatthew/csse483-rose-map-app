package com.example.csse483finalproject.group

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.csse483finalproject.R

class GroupAdapter(var context: Context, var mtypes:Array<String>, var parentFragment: GroupsFragment?) : RecyclerView.Adapter<GroupViewHolder>() {
    private val groups = ArrayList<GroupWithMembershipType>()

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): GroupViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.group_card, parent, false)
        return GroupViewHolder(view, this)
    }

    override fun onBindViewHolder(
        viewHolder: GroupViewHolder,
        index: Int) {
        viewHolder.bind(groups[index])
    }

    override fun getItemCount() = groups.size

    fun add(groupWithMembershipType: GroupWithMembershipType) {
        groups.add(0, groupWithMembershipType)
        notifyItemInserted(0)
    }

}