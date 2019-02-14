package com.example.csse483finalproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.csse483finalproject.group.UserWrapper
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {
    lateinit var user: UserWrapper
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        user = arguments!!.getParcelable(SettingsFragment.ARG_USER)!!
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_settings, container, false)
        view.accountName.text = getString(R.string.loginText, user.wGetUsername())
        unpackBundle(view)
        view.signout.setOnClickListener {
            (activity as MainActivity).logout()
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
        val ARG_USER = "USER"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(user: UserWrapper): SettingsFragment {
            val fragment = SettingsFragment()
            val args = Bundle()
            args.putParcelable(ARG_USER, user)
            fragment.arguments = args
            return fragment
        }
    }
}