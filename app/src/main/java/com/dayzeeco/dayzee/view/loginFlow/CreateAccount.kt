package com.dayzeeco.dayzee.view.loginFlow

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.*
import com.dayzeeco.dayzee.model.*
import com.dayzeeco.dayzee.viewModel.LoginViewModel
import com.dayzeeco.dayzee.viewModel.MeViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_create_account.*
import java.util.*


class CreateAccount : Fragment(), View.OnClickListener {

    private var availableIdentifiant: Boolean? = null
    private var emailValidForm : Boolean = false
    private var usernameValidForm: Boolean = false
    private var passwordValidForm : Boolean = false
    private var availableMail : Boolean? = null
    private lateinit var handlerPasswordSignup: Handler
    private lateinit var handlerMail: Handler
    private lateinit var handlerIdentifiant: Handler
    private val TRIGGER_AUTO_COMPLETE = 200
    private val AUTO_COMPLETE_DELAY: Long = 200
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val meViewModel: MeViewModel by activityViewModels()
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_create_account, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        agreee_tv.makeLinks(
            Pair(resources.getString(R.string.terms), View.OnClickListener {  }),
            Pair(resources.getString(R.string.privacy_policy), View.OnClickListener {  })
        )

        handlerMail = Handler { msg ->
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(signup_mail?.text)) {
                    if (loginViewModel.isValidEmail(signup_mail.text.toString())) {
                        loginViewModel.checkIfEmailAvailable(signup_mail.text.toString())
                            .observe(viewLifecycleOwner, Observer {
                                if (it.code() == 200) availableMail = it.body()?.isAvailable!!
                                if (!availableMail!!) signup_mail.error =
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
        handlerIdentifiant = Handler { msg ->
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(signup_username?.text)) {
                    if (!loginViewModel.isValidUsername(signup_username?.text.toString())) {
                        loginViewModel.checkIfUsernameAvailable(signup_username?.text.toString())
                            .observe(viewLifecycleOwner, Observer {
                                if (it.code() == 200) availableIdentifiant =
                                    it.body()?.isAvailable!!
                                if (!availableIdentifiant!!) signup_username?.error =
                                    getString(R.string.usermane_already_exists)
                                else usernameValidForm = true
                            })
                    } else {
                        signup_username?.error = getString(R.string.not_username_valid_form)
                    }
                }
            }
            false
        }
        handlerPasswordSignup = Handler{
            if(it.what == TRIGGER_AUTO_COMPLETE){
                if(!TextUtils.isEmpty(signup_password?.text)){
                    passwordValidForm = true
                }
            }
            false
        }

        signup_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handlerPasswordSignup.removeMessages(TRIGGER_AUTO_COMPLETE)
                handlerPasswordSignup.sendEmptyMessageDelayed(
                    TRIGGER_AUTO_COMPLETE,
                    AUTO_COMPLETE_DELAY
                )
            }

        })
        signup_username.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handlerIdentifiant.removeMessages(TRIGGER_AUTO_COMPLETE)
                handlerIdentifiant.sendEmptyMessageDelayed(
                    TRIGGER_AUTO_COMPLETE,
                    AUTO_COMPLETE_DELAY
                )
            }

        })
        signup_mail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handlerMail.removeMessages(TRIGGER_AUTO_COMPLETE)
                handlerMail.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY)
            }

        })

        signup_connect.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            signup_connect -> {

                if (availableIdentifiant != null && availableMail != null) {
                    if (availableIdentifiant!! && availableMail!! && passwordValidForm) {
                        loginViewModel.checkAddUser(
                            UserSignUpBody(
                                signup_mail.text.toString(),
                                signup_username.text.toString(),
                                signup_password.text.toString()
                            )
                        ).observe(viewLifecycleOwner,
                            {
                                when (it.code()) {
                                    201 -> {
                                        meViewModel.modifyProfile(
                                            it.body()?.token!!, UpdateUserInfoDTO(
                                                language = Locale.getDefault().language,
                                                status = 0,
                                                dateFormat = 0,
                                                socialMedias = SocialMedias(
                                                    Youtube("", false),
                                                    Facebook("", false),
                                                    Instagram(
                                                        "",
                                                        false
                                                    ),
                                                    WhatsApp("", false),
                                                    LinkedIn("", false),
                                                    Twitter("", false),
                                                    Discord("", false),
                                                    Telegram(
                                                        "",
                                                        false
                                                    )
                                                )
                                            )
                                        ).observe(viewLifecycleOwner, { rp ->
                                            if (rp.isSuccessful) {
                                                findNavController().navigate(
                                                    CreateAccountDirections.actionCreateAccountToPreferenceCategory(
                                                        true
                                                    )
                                                )
                                                prefs.edit().putString(
                                                    accessToken,
                                                    it.body()?.token
                                                ).apply()
                                                prefs.edit().putString(
                                                    refreshToken,
                                                    it.body()?.refreshToken
                                                ).apply()
                                                prefs.edit().putString(
                                                    user_info_dto, Gson().toJson(
                                                        it.body()?.user
                                                    )
                                                ).apply()
                                                prefs.edit().putInt(
                                                    followers,
                                                    it.body()?.user?.followers!!
                                                ).apply()
                                                prefs.edit().putInt(
                                                    following,
                                                    it.body()?.user?.following!!
                                                ).apply()
                                            }
                                        })

                                    }
                                    409 -> {
                                        Toast.makeText(
                                            requireContext(),
                                            getString(R.string.invalid_authentication),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    400 -> {
                                        Toast.makeText(
                                            requireContext(),
                                            getString(R.string.invalid_authentication),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            })
                    }
                }

                if (signup_mail.text.isNullOrBlank()) {
                    signup_mail.error = getString(R.string.field_cannot_be_empty)
                }
                if (signup_username.text.isNullOrBlank()) {
                    signup_username.error = getString(R.string.field_cannot_be_empty)
                }
                if (signup_password.text.isNullOrBlank()) {
                    signup_password.error = getString(R.string.field_cannot_be_empty)
                }

            }
        }
    }

}

fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = Color.GRAY
                textPaint.color = resources.getColor(R.color.colorText)
                textPaint.isUnderlineText = false
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}