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
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.gson.Gson
import com.timenoteco.timenote.R
import com.timenoteco.timenote.androidView.dialog.input
import com.timenoteco.timenote.model.UserSignUpBody
import com.timenoteco.timenote.model.accessToken
import com.timenoteco.timenote.model.refreshToken
import com.timenoteco.timenote.viewModel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_signup.*


class Signup: Fragment(), View.OnClickListener {

    private var availableIdentifiant: Boolean? = null
    private var emailValidForm : Boolean = false
    private var usernameValidForm: Boolean = false
    private var passwordValidForm : Boolean = false
    private var availableMail : Boolean? = null
    private val loginViewModel: LoginViewModel by activityViewModels()
    private var isOnLogin: Boolean = true
    private val TRIGGER_AUTO_COMPLETE = 200
    private val AUTO_COMPLETE_DELAY: Long = 200
    private lateinit var handlerMail: Handler
    private lateinit var handlerIdentifiant: Handler
    private lateinit var handlerMailUsername: Handler
    private lateinit var handlerPasswordLogin: Handler
    private lateinit var handlerPasswordSignup: Handler
    private lateinit var prefs: SharedPreferences


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_signup, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        signup_signup_btn.setOnClickListener(this)
        signup_signin_btn.setOnClickListener(this)
        guest_btn.setOnClickListener(this)
        signup_forgotten_password.setOnClickListener(this)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        handlerPasswordSignup = Handler{
            if(it.what == TRIGGER_AUTO_COMPLETE){
                if(!TextUtils.isEmpty(signup_password.text)){
                    if(signup_password.text.toString().startsWith("dayzee-", true)){
                        signup_password.error = getString(R.string.cant_start_with_password)
                    } else passwordValidForm = true
                }
            }
            false
        }

        handlerPasswordLogin = Handler{
            if(it.what == TRIGGER_AUTO_COMPLETE){
                if(!TextUtils.isEmpty(signin_password.text)){
                 if(signin_password.text.toString().startsWith("dayzee-", true)){
                     prefs.edit().putBoolean("temporary_password", true).apply()
                 } else prefs.edit().putBoolean("temporary_password", false).apply()
                }
            }
            false
        }

        handlerMailUsername = Handler {
            if (it.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(signin_mail_username.text)) {
                    if (signin_mail_username.text.toString().contains('@')) {
                        if (!loginViewModel.isValidEmail(signin_mail_username.text.toString()))
                            signin_mail_username.error = getString(R.string.not_valid_mail_form)
                    }
                }

            }
            false
        }

        handlerMail = Handler { msg ->
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(signup_mail.text)) {
                    if (loginViewModel.isValidEmail(signup_mail.text.toString())) {
                        loginViewModel.checkIfEmailAvailable(signup_mail.text.toString())
                            .observe(viewLifecycleOwner, Observer {
                                if (it.code() == 200) availableMail = it.body()?.isAvailable!!
                                if (!availableMail!! && !isOnLogin) signup_mail.error =
                                    getString(R.string.email_already_exists)
                                else emailValidForm = true
                            })
                    } else {
                        signup_mail.error = getString(R.string.not_valid_mail_form)
                    }
                }
            }
            false
        }


        handlerIdentifiant = Handler(Handler.Callback { msg ->
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(signup_identifiant.text)) {
                    if(!loginViewModel.isValidUsername(signup_identifiant.text.toString())){
                        loginViewModel.checkIfUsernameAvailable(signup_identifiant.text.toString()).observe(viewLifecycleOwner, Observer {
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


        signup_password.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handlerPasswordSignup.removeMessages(TRIGGER_AUTO_COMPLETE)
                handlerPasswordSignup.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY)
            }

        })

        signin_password.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handlerPasswordLogin.removeMessages(TRIGGER_AUTO_COMPLETE)
                handlerPasswordLogin.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY)
            }

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
            guest_btn -> loginViewModel.markAsGuest()
            signup_forgotten_password -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.mail)
                message(R.string.enter_mail)
                input{ _, mail ->
                    loginViewModel.forgotPassword(mail.toString().trim()).observe(viewLifecycleOwner, Observer {
                        if(it.isSuccessful && it.body()?.changed!!){
                            Toast.makeText(requireContext(), "A mail has just been sent", Toast.LENGTH_SHORT).show()
                        } else Toast.makeText(requireContext(), "An error has occured. Please try again", Toast.LENGTH_SHORT).show()
                    })
                }
                lifecycleOwner(this@Signup)
            }
            signup_signin_btn -> {
                signup_forgotten_password.visibility = View.VISIBLE

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
                    loginViewModel.login(signin_mail_username.text.toString(), signin_password.text.toString(), loginViewModel.isValidEmail(signin_mail_username.text.toString())).observe(viewLifecycleOwner, Observer {
                        when(it.code()){
                            201 -> {
                                loginViewModel.markAsAuthenticated()
                                prefs.edit().putString(accessToken, it.body()?.token).apply()
                                prefs.edit().putString(refreshToken, it.body()?.refreshToken).apply()
                                prefs.edit().putString("UserInfoDTO", Gson().toJson(it.body()?.user)).apply()
                                prefs.edit().putInt("followers", it.body()?.user?.followers!!).apply()
                                prefs.edit().putInt("following", it.body()?.user?.following!!).apply()
                            }
                            else -> {
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

                signup_forgotten_password.visibility = View.GONE

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
                    if(!isOnLogin && availableIdentifiant!! && availableMail!! && passwordValidForm){
                        loginViewModel.checkAddUser(UserSignUpBody(signup_mail.text.toString(), signup_identifiant.text.toString(), signup_password.text.toString())).observe(viewLifecycleOwner, Observer {
                            when(it.code()){
                                201 -> {
                                    loginViewModel.markAsAuthenticated()
                                    //findNavController().navigate(SignupDirections.actionSignupToPreferenceCategory(true))
                                    prefs.edit().putString(accessToken, it.body()?.token).apply()
                                    prefs.edit().putString(refreshToken, it.body()?.refreshToken).apply()
                                    prefs.edit().putString("UserInfoDTO", Gson().toJson(it.body()?.user)).apply()
                                    prefs.edit().putInt("followers", it.body()?.user?.followers!!).apply()
                                    prefs.edit().putInt("following", it.body()?.user?.following!!).apply()
                                }
                                409 -> {
                                    Toast.makeText(requireContext(), "Invalid Authentication", Toast.LENGTH_SHORT).show()
                                }
                                400 -> {
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