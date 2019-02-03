package com.example.csse483finalproject.group

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.csse483finalproject.R

class UserAdapter(var context: Context, val deleteCallback:DeletableUserInterface?=null, val mtcCallback:mtcInterface?=null) : RecyclerView.Adapter<UserViewHolder>() {
    private val users = ArrayList<User>()

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): UserViewHolder {
        lateinit var view: View
        if (mtcCallback == null){
            view = LayoutInflater.from(context).inflate(R.layout.user_card_basic, parent, false)
        }
        else{
            view = LayoutInflater.from(context).inflate(R.layout.user_card_mt, parent, false)
        }
        return UserViewHolder(view, this, mtcCallback)
    }

    override fun onBindViewHolder(
        viewHolder: UserViewHolder,
        index: Int) {
        viewHolder.bind(users[index])
    }

    override fun getItemCount() = users.size

    fun add(user: User) {
        users.add(0, user)
        notifyItemInserted(0)
    }

    fun delete(user: User) {
        for (i in 0 until users.size){
            if (users[i].userId == user.userId){
                users.removeAt(i)
                notifyItemRemoved(i)
            }
        }
        deleteCallback!!.onDelete(user)
    }

    interface DeletableUserInterface{
        fun onDelete(u: User)
    }

    interface mtcInterface{
        fun onMemberTypeChange(u: User, t: MemberType)
        fun isMemberTypeChangable(u: User):Boolean
        fun getCurrentMt(u:User):MemberType
    }

    inner class SwipeCallback() : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

        override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
            val pos=users.indexOf((vh as UserViewHolder).user)
            delete(users[pos])
        }

        override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
            return false
        }
    }
}