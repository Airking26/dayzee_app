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

class Signup: Fragment(), View.OnClickListener {

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_signup, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        signup_signup_btn.setOnClickListener(this)
        signup_signin_btn.setOnClickListener(this)
        signup_as_guest.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            signup_as_guest -> viewModel.markAsGuest()
            signup_signin_btn -> viewModel.authenticate(signup_mail.text.toString(), signup_password.text.toString())
            signup_signup_btn -> viewModel.checkAddUser(UserSignUpBody("sam", "soe", signup_mail.text.toString(), signup_password.text.toString())).observe(viewLifecycleOwner, Observer {
                val m =  it.body()?.user
                when(it.code()){
                    201 -> viewModel.markAsAuthenticated()
                    409 -> viewModel.markAsInvalidAuthentication()
                    400 -> viewModel.markAsInvalidAuthentication()
                }
            })
        }
    }
}