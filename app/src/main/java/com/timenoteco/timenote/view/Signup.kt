package com.timenoteco.timenote.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.fragment_signup.*

class Signup: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sign.setOnClickListener { it.findNavController().navigate(SignupDirections.actionSignupToPreferenceCategory()) }
    }
}