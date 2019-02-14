package com.example.csse483finalproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.csse483finalproject.group.UserWrapper
import kotlinx.android.synthetic.main.fragment_splash.view.*
import kotlinx.android.synthetic.main.fragment_startup.view.*

class SplashFragment : Fragment() {
    lateinit var user: UserWrapper
    var listener: OnLoginButtonPressedListener? = null
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_splash, container, false)
        view.login_button.setOnClickListener {
            listener?.onLoginButtonPressed()
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLoginButtonPressedListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnLoginButtonPressedListener")
        }
    }

    interface OnLoginButtonPressedListener {
        fun onLoginButtonPressed()
    }
}