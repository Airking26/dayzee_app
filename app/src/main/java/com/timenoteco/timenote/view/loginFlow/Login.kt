package com.timenoteco.timenote.view.loginFlow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.fragment_login.*

class Login : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button.setOnClickListener { it.findNavController().navigate(LoginDirections.actionLoginToSignup()) }
        buttonAfter.setOnClickListener {
            it.findNavController().navigate(
                LoginDirections.actionLoginToMainActivity(
                    true
                )
            )
        }
    }
}
