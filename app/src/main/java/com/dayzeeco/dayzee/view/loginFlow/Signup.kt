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
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.gson.Gson
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.androidView.dialog.input
import com.dayzeeco.dayzee.common.*
import com.dayzeeco.dayzee.model.*
import com.dayzeeco.dayzee.view.homeFlow.HomeDirections
import com.dayzeeco.dayzee.viewModel.AccessTokenForgottenPasswordViewModel
import com.dayzeeco.dayzee.viewModel.LoginViewModel
import com.dayzeeco.dayzee.viewModel.MeViewModel
import kotlinx.android.synthetic.main.fragment_signup.*
import kotlinx.android.synthetic.main.fragment_signup.email_label
import kotlinx.android.synthetic.main.fragment_signup.guest_btn
import kotlinx.android.synthetic.main.fragment_signup.identifiant_label
import kotlinx.android.synthetic.main.fragment_signup.signin_mail_username
import kotlinx.android.synthetic.main.fragment_signup.signin_password
import kotlinx.android.synthetic.main.fragment_signup.signup_forgotten_password
import kotlinx.android.synthetic.main.fragment_signup.signup_identifiant
import kotlinx.android.synthetic.main.fragment_signup.signup_mail
import kotlinx.android.synthetic.main.fragment_signup.signup_password
import kotlinx.android.synthetic.main.fragment_signup.signup_signin_btn
import kotlinx.android.synthetic.main.fragment_signup.signup_signup_btn
import kotlinx.android.synthetic.main.fragment_signup_new.*
import java.util.*


class Signup: Fragment(), View.OnClickListener {


    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var prefs: SharedPreferences


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_signup_new, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        creercom.setOnClickListener(this)
        secon.setOnClickListener(this)
        guest_btn_new.setOnClickListener(this)

    }


    override fun onClick(v: View?) {
        when (v) {
            secon -> findNavController().navigate(SignupDirections.actionSignupToSeConnecter())
            creercom -> findNavController().navigate(SignupDirections.actionSignupToCreateAccount())
            guest_btn_new -> loginViewModel.markAsGuest()
        }
    }

}