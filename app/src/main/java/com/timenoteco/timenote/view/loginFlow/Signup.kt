package com.timenoteco.timenote.view.loginFlow

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.timenoteco.timenote.R
import com.timenoteco.timenote.viewModel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_signup.*

class Signup: Fragment() {

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        signup_signin_btn.setOnClickListener {
            viewModel.authenticate(signup_mail.text.toString(), signup_password.text.toString())
        }

        signup_signup_btn.setOnClickListener {
            findNavController().navigate(SignupDirections.actionSignupToPreferenceCategory())
        }

        signup_as_guest.setOnClickListener {
            viewModel.authenticationState.postValue(LoginViewModel.AuthenticationState.UNAUTHENTICATED_CHOOSED)
        }

    }
}