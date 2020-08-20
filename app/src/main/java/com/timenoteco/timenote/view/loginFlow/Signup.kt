package com.timenoteco.timenote.view.loginFlow

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.UserSignUpBody
import com.timenoteco.timenote.viewModel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_signup.*


class Signup: Fragment(), View.OnClickListener {

    private var availableIdentifiant: Boolean? = null
    private var emailValidForm : Boolean = false
    private var usernameValidForm: Boolean = false
    val TOKEN: String = "TOKEN"
    private var availableMail : Boolean? = null
    private val viewModel: LoginViewModel by activityViewModels()
    private var isOnLogin: Boolean = true
    private val TRIGGER_AUTO_COMPLETE = 200
    private val AUTO_COMPLETE_DELAY: Long = 200
    private lateinit var handlerMail: Handler
    private lateinit var handlerIdentifiant: Handler
    private lateinit var handlerMailUsername: Handler
    private lateinit var prefs: SharedPreferences


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_signup, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        signup_signup_btn.setOnClickListener(this)
        signup_signin_btn.setOnClickListener(this)
        guest_btn.setOnClickListener(this)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)


        handlerMailUsername = Handler(Handler.Callback {
            if(it.what == TRIGGER_AUTO_COMPLETE){
                if(!TextUtils.isEmpty(signin_mail_username.text)){
                    if(signin_mail_username.text.toString().contains('@')){
                        if(!viewModel.isValidEmail(signin_mail_username.text.toString()))
                            signin_mail_username.error = getString(R.string.not_valid_mail_form)
                    }
                }

            }
            false
        })

        handlerMail = Handler(Handler.Callback { msg ->
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(signup_mail.text)) {
                    if(viewModel.isValidEmail(signup_mail.text.toString())){
                        viewModel.checkIfEmailAvailable(signup_mail.text.toString()).observe(viewLifecycleOwner, Observer {
                            if(it.code() == 200) availableMail = it.body()?.isAvailable!!
                            if(!availableMail!! && !isOnLogin) signup_mail.error = getString(R.string.email_already_exists)
                            else emailValidForm = true
                        })
                    } else {
                        signup_mail.error = getString(R.string.not_valid_mail_form)
                    }
                }
            }
            false
        })


        handlerIdentifiant = Handler(Handler.Callback { msg ->
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(signup_identifiant.text)) {
                    if(!viewModel.isValidUsername(signup_identifiant.text.toString())){
                        viewModel.checkIfUsernameAvailable(signup_identifiant.text.toString()).observe(viewLifecycleOwner, Observer {
                            if(it.code() == 200) availableIdentifiant = it.body()?.isAvailable!!
                            if(!availableIdentifiant!! && !isOnLogin) signup_identifiant.error = getString(R.string.usermane_already_exists)
                            else usernameValidForm = true
                        })
                    } else {
                        signup_identifiant.error = getString(R.string.not_username_valid_form)
                    }
                }
            }
            false
        })

        signin_mail_username.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handlerMailUsername.removeMessages(TRIGGER_AUTO_COMPLETE)
                handlerMailUsername.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY)
            }

        })

        signup_mail.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handlerMail.removeMessages(TRIGGER_AUTO_COMPLETE)
                handlerMail.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY)
            }

        })

        signup_identifiant.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handlerIdentifiant.removeMessages(TRIGGER_AUTO_COMPLETE)
                handlerIdentifiant.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY)
            }

        })

    }
    override fun onClick(v: View?) {
        when(v){
            guest_btn -> viewModel.markAsGuest()
            signup_signin_btn -> {

                isOnLogin = true
                signup_signup_btn.apply {
                    setBorderColor(resources.getColor(R.color.colorBackground))
                    setBorderWidth(2)
                    setBackgroundColor(resources.getColor( android.R.color.transparent))
                    setTextColor(resources.getColor(R.color.colorBackground))
                }

                signup_signin_btn.apply {
                    setBorderWidth(0)
                    setBorderColor(resources.getColor(android.R.color.transparent))
                    setBackgroundColor(resources.getColor(R.color.colorBackground))
                    setTextColor(resources.getColor(R.color.colorText))
                }
                signup_password.visibility = View.INVISIBLE
                signup_mail.visibility = View.INVISIBLE
                signin_mail_username.visibility = View.VISIBLE
                signin_password.visibility =View.VISIBLE
                identifiant_label.visibility = View.GONE
                signup_identifiant.visibility = View.GONE
                guest_btn.visibility = View.VISIBLE

                email_label.text = getString(R.string.username_email)

                if(isOnLogin && !signin_mail_username.text.toString().isBlank() && !signin_password.text.toString().isBlank()){
                    viewModel.login(signup_mail.text.toString(), signup_password.text.toString(), viewModel.isValidEmail(signup_mail.text.toString())).observe(viewLifecycleOwner, Observer {
                        when(it.code()){
                            200 -> {
                                viewModel.markAsAuthenticated()
                                prefs.edit().putString(TOKEN, it.body()?.token).apply()
                            }
                            else -> {
                                viewModel.markAsInvalidAuthentication()
                                Toast.makeText(requireContext(), "Invalid Authentication", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }

                if(isOnLogin && signin_mail_username.text.isNullOrBlank()){
                    signin_mail_username.error = getString(R.string.field_cannot_be_empty)
                }
                if(isOnLogin && signin_password.text.isNullOrBlank()){
                    signin_password.error = getString(R.string.field_cannot_be_empty)
                }
            }
            signup_signup_btn -> {

                signup_signin_btn.apply {
                    setBorderColor(resources.getColor(R.color.colorBackground))
                    setBorderWidth(2)
                    setBackgroundColor(resources.getColor( android.R.color.transparent))
                    setTextColor(resources.getColor(R.color.colorBackground))
                }

                signup_signup_btn.apply {
                    setBorderWidth(0)
                    setBackgroundColor(resources.getColor(R.color.colorBackground))
                    setTextColor(resources.getColor(R.color.colorText))
                }

                signup_password.visibility = View.VISIBLE
                signup_mail.visibility = View.VISIBLE
                signin_mail_username.visibility = View.INVISIBLE
                signin_password.visibility =View.INVISIBLE
                identifiant_label.visibility = View.VISIBLE
                signup_identifiant.visibility = View.VISIBLE
                guest_btn.visibility = View.GONE

                identifiant_label.text = getString(R.string.identifiant)
                email_label.text = getString(R.string.mail)

                if(availableIdentifiant != null && availableMail != null){
                    if(!isOnLogin && availableIdentifiant!! && availableMail!!){
                        viewModel.checkAddUser(UserSignUpBody(signup_mail.text.toString(), signup_identifiant.text.toString(), signup_password.text.toString())).observe(viewLifecycleOwner, Observer {
                            when(it.code()){
                                201 -> {
                                    //viewModel.markAsAuthenticated()
                                    findNavController().navigate(SignupDirections.actionSignupToPreferenceCategory(true))
                                    prefs.edit().putString(TOKEN, it.body()?.token).apply()
                                }
                                409 -> {
                                    viewModel.markAsInvalidAuthentication()
                                    Toast.makeText(requireContext(), "Invalid Authentication", Toast.LENGTH_SHORT).show()
                                }
                                400 -> {
                                    viewModel.markAsInvalidAuthentication()
                                    Toast.makeText(requireContext(), "Invalid Authentication", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    }
                }

                if(!isOnLogin && signup_mail.text.isNullOrBlank()){
                    signup_mail.error = getString(R.string.field_cannot_be_empty)
                }
                if(!isOnLogin && signup_identifiant.text.isNullOrBlank()){
                    signup_identifiant.error = getString(R.string.field_cannot_be_empty)
                }
                if(!isOnLogin && signup_password.text.isNullOrBlank()){
                    signup_password.error = getString(R.string.field_cannot_be_empty)
                }

                isOnLogin = false

            }


        }
    }
}