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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.UserSignUpBody
import com.timenoteco.timenote.viewModel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_signup.*


class Signup: Fragment(), View.OnClickListener {

    private var availableIdentifiant: Boolean = false
    open val TOKEN: String = "TOKEN"
    private var availableMail : Boolean = false
    private val viewModel: LoginViewModel by activityViewModels()
    private var isOnLogin: Boolean = true
    private val TRIGGER_AUTO_COMPLETE = 500
    private val AUTO_COMPLETE_DELAY: Long = 500
    private lateinit var handlerMail: Handler
    private lateinit var handlerIdentifiant: Handler
    private lateinit var prefs: SharedPreferences


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_signup, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        signup_signup_btn.setOnClickListener(this)
        signup_signin_btn.setOnClickListener(this)
        guest_btn.setOnClickListener(this)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        handlerMail = Handler(Handler.Callback { msg ->
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(signup_mail.text)) {
                    if(viewModel.isValidEmail(signup_mail.text.toString())){
                        viewModel.checkIfEmailAvailable(signup_mail.text.toString()).observe(viewLifecycleOwner, Observer {
                            availableMail = it
                        })
                    }
                }
            }
            false;
        })


        handlerIdentifiant = Handler(Handler.Callback { msg ->
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(signup_identifiant.text)) {
                        viewModel.checkIfUsernameAvailable(signup_identifiant.text.toString()).observe(viewLifecycleOwner, Observer {
                            availableIdentifiant = it
                        })
                }
            }
            false;
        })

        signup_mail.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handlerMail.removeMessages(TRIGGER_AUTO_COMPLETE);
                handlerMail.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
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
                signup_identifiant.visibility = View.GONE
                guest_btn.visibility = View.VISIBLE

                email_label.text = getString(R.string.username_email)

                if(isOnLogin && !signup_mail.text.toString().isNullOrBlank() && !signup_password.text.toString().isNullOrBlank()){
                    viewModel.login(signup_mail.text.toString(), signup_password.text.toString(), viewModel.isValidEmail(signup_mail.text.toString())).observe(viewLifecycleOwner, Observer {
                        when(it.code()){
                            201 -> {
                                viewModel.markAsAuthenticated()
                                prefs.edit().putString(TOKEN, it.body()?.token).apply()
                            }
                            else -> viewModel.markAsInvalidAuthentication()
                        }
                    })
                }
            }
            signup_signup_btn -> {
                isOnLogin = false
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
                signup_identifiant.visibility = View.VISIBLE
                guest_btn.visibility = View.GONE

                identifiant_label.text = getString(R.string.identifiant)
                email_label.text = getString(R.string.mail)

                if(!isOnLogin && availableIdentifiant && availableMail){
                    viewModel.checkAddUser(UserSignUpBody(signup_mail.text.toString(), signup_identifiant.text.toString(), signup_password.text.toString())).observe(viewLifecycleOwner, Observer {
                        when(it.code()){
                            201 -> {
                                viewModel.markAsAuthenticated()
                                prefs.edit().putString(TOKEN, it.body()?.token).apply()
                            }
                            409 -> viewModel.markAsInvalidAuthentication()
                            400 -> viewModel.markAsInvalidAuthentication()
                        }
                    })
                }
            }

        }
    }
}