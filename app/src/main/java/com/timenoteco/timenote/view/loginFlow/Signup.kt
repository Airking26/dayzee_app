package com.timenoteco.timenote.view.loginFlow

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.timenoteco.timenote.R
import com.timenoteco.timenote.viewModel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_signup.*


class Signup: Fragment(), View.OnClickListener {

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_signup, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        signup_signup_btn.setOnClickListener(this)
        signup_signin_btn.setOnClickListener(this)
        guest_btn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            guest_btn -> viewModel.markAsGuest()
            signup_signin_btn -> {
                signup_signup_btn.apply {
                    setBorderColor(resources.getColor(android.R.color.white))
                    setBorderWidth(2)
                    setBackgroundColor(resources.getColor( android.R.color.transparent))
                    setTextColor(resources.getColor(android.R.color.white))
                }

                signup_signin_btn.apply {
                    setBorderWidth(0)
                    setBorderColor(resources.getColor(android.R.color.transparent))
                    setBackgroundColor(resources.getColor(android.R.color.white))
                    setTextColor(resources.getColor(android.R.color.black))
                }

                identifiant_label.visibility = View.GONE
                signup_identitfiant.visibility = View.GONE
                guest_btn.visibility = View.VISIBLE

                email_label.text = getString(R.string.username_email)
                viewModel.authenticate(signup_mail.text.toString(), signup_password.text.toString())
            }
            signup_signup_btn -> {
                signup_signin_btn.apply {
                    setBorderColor(resources.getColor(android.R.color.white))
                    setBorderWidth(2)
                    setBackgroundColor(resources.getColor( android.R.color.transparent))
                    setTextColor(resources.getColor(android.R.color.white))
                }

                signup_signup_btn.apply {
                    setBorderWidth(0)
                    setBackgroundColor(resources.getColor(android.R.color.white))
                    setTextColor(resources.getColor(android.R.color.black))
                }

                identifiant_label.visibility = View.VISIBLE
                signup_identitfiant.visibility = View.VISIBLE
                guest_btn.visibility = View.GONE

                identifiant_label.text = getString(R.string.identifiant)
                email_label.text = getString(R.string.mail)
            }
                /*viewModel.checkAddUser(UserSignUpBody("sam", "soe", signup_mail.text.toString(), signup_password.text.toString())).observe(viewLifecycleOwner, Observer {
                val m =  it.body()?.user
                when(it.code()){
                    201 -> viewModel.markAsAuthenticated()
                    409 -> viewModel.markAsInvalidAuthentication()
                    400 -> viewModel.markAsInvalidAuthentication()
                }
            })*/
        }
    }

    fun isValidEmail(target: String?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target!!)
            .matches()
    }
}