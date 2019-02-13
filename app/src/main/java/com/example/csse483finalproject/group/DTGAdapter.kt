package com.example.csse483finalproject.group

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.csse483finalproject.R

class DTGAdapter(var context: Context, val dtgCallback:dtgInterface) : RecyclerView.Adapter<DTGViewHolder>() {
    val dtgs = ArrayList<DualTypeGroup>()

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): DTGViewHolder {
        lateinit var view: View
        view = LayoutInflater.from(context).inflate(R.layout.group_card_dual, parent, false)
        return DTGViewHolder(view, this, dtgCallback)
    }

    override fun onBindViewHolder(
        viewHolder: DTGViewHolder,
        index: Int) {
        viewHolder.bind(dtgs[index])
    }

    override fun getItemCount() = dtgs.size

    fun add(dtg: DualTypeGroup) {
        dtgs.add(0, dtg)
        notifyItemInserted(0)
    }

    fun delete(dtg: DualTypeGroup) {
        var rindex = -1
        for (i in 0 until dtgs.size){
            if (dtgs[i].gwmt.group.wGetId() == dtg.gwmt.group.wGetId() && dtgs[i].gwmt.membertype.mt == dtg.gwmt.membertype.mt && dtgs[i].perm==dtg.perm){
                rindex = i
            }
        }
        if(rindex>=0) {
            dtgs.removeAt(rindex)
            notifyItemRemoved(rindex)
        }
        dtgCallback.onDelete(dtg)
    }


    interface dtgInterface{
        fun onMemberTypeChange(dtg: DualTypeGroup, t: MemberType)
        fun onPermissionTypeChange(dtg: DualTypeGroup, t: MemberType)
        fun onDelete(dtg: DualTypeGroup)
    }

    inner class SwipeCallback() : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

        override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
            val pos=dtgs.indexOf((vh as DTGViewHolder).dtg)
            delete(dtgs[pos])
        }

        override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
            return false
        }
    }
}