package com.timenoteco.timenote.view.loginFlow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.UserSignUpBody
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
            viewModel.addUser(UserSignUpBody("", "", signup_mail.text.toString(), signup_password.text.toString()))
        }

        signup_as_guest.setOnClickListener {
            viewModel.authenticationState.postValue(LoginViewModel.AuthenticationState.GUEST)
        }

        viewModel.authenticationState.observe(viewLifecycleOwner, Observer {

        })

    }
}