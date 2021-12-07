package com.dayzeeco.dayzee.view.loginFlow

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
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.androidView.dialog.input
import com.dayzeeco.dayzee.common.*
import com.dayzeeco.dayzee.viewModel.LoginViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_se_connecter.*

class SeConnecter: Fragment(), View.OnClickListener {
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val TRIGGER_AUTO_COMPLETE = 200
    private val AUTO_COMPLETE_DELAY: Long = 200
    private lateinit var handlerMailUsername: Handler
    private lateinit var handlerPasswordLogin: Handler
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_se_connecter, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        handlerPasswordLogin = Handler{
            if(it.what == TRIGGER_AUTO_COMPLETE){
                if(!TextUtils.isEmpty(signin_password?.text)){
                    if(signin_password?.text.toString().startsWith(dayzee_prefix, true)){
                        prefs.edit().putBoolean(temporary_password, true).apply()
                    } else prefs.edit().putBoolean(temporary_password, false).apply()
                }
            }
            false
        }
        handlerMailUsername = Handler {
            if (it.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(signin_email_username?.text)) {
                    if (signin_email_username?.text.toString().contains('@')) {
                        if (!loginViewModel.isValidEmail(signin_email_username.text.toString()))
                            signin_email_username?.error = getString(R.string.not_valid_mail_form)
                    }
                }

            }
            false
        }

        signin_password.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handlerPasswordLogin.removeMessages(TRIGGER_AUTO_COMPLETE)
                handlerPasswordLogin.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY)
            }

        })
        signin_email_username.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handlerMailUsername.removeMessages(TRIGGER_AUTO_COMPLETE)
                handlerMailUsername.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY)
            }

        })

        signin_connect.setOnClickListener(this)
        signin_forgotten_password.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            signin_forgotten_password -> {
                MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    title(R.string.mail)
                    message(R.string.enter_mail)
                    input{ _, mail ->
                        loginViewModel.forgotPassword(mail.toString().trim()).observe(viewLifecycleOwner){
                            if(it.isSuccessful) {
                                prefs.edit().putBoolean(already_signed_in, true).apply()
                                Toast.makeText(requireContext(), getString(R.string.email_has_been_sent), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            signin_connect ->  {
                if(signin_email_username.text.toString().isNotBlank() && signin_password.text.toString()
                        .isNotBlank()){
                    loginViewModel.login(signin_email_username.text.toString(), signin_password.text.toString(), loginViewModel.isValidEmail(signin_email_username.text.toString())).observe(viewLifecycleOwner, Observer {
                        when(it.code()){
                            201 -> {
                                loginViewModel.markAsAuthenticated()
                                prefs.edit().putString(accessToken, it.body()?.token).apply()
                                prefs.edit().putString(refreshToken, it.body()?.refreshToken).apply()
                                prefs.edit().putString(user_info_dto, Gson().toJson(it.body()?.user)).apply()
                                prefs.edit().putInt(followers, it.body()?.user?.followers!!).apply()
                                prefs.edit().putInt(following, it.body()?.user?.following!!).apply()
                            }
                            else -> {
                                Toast.makeText(requireContext(), getString(R.string.invalid_authentication), Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }

                if(signin_email_username.text.isNullOrBlank()){
                    signin_email_username.error = getString(R.string.field_cannot_be_empty)
                }
                if(signin_password.text.isNullOrBlank()){
                    signin_password.error = getString(R.string.field_cannot_be_empty)
                }
            }
        }
    }
}