package com.dayzeeco.dayzee.view.loginFlow

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.already_signed_in
import com.dayzeeco.dayzee.common.notifications_saved
import com.dayzeeco.dayzee.viewModel.AccessTokenForgottenPasswordViewModel
import com.dayzeeco.dayzee.viewModel.LoginViewModel
import com.dayzeeco.dayzee.viewModel.MeViewModel
import kotlinx.android.synthetic.main.fragment_new_password.*


class ChangePassword: Fragment(), View.OnClickListener {

    private val meViewModel : MeViewModel by activityViewModels()
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val args : ChangePasswordArgs by navArgs()
    private val accessTokenForgottenPasswordViewModel: AccessTokenForgottenPasswordViewModel by activityViewModels()
    private lateinit var prefs: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_new_password, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        confirm_new_password.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v) {
            confirm_new_password -> {
                if (new_password.text.isNullOrBlank()) {
                    new_password.error = getString(R.string.field_cannot_be_empty)
                }
                if (new_password_again.text.isNullOrBlank()) {
                    new_password_again.error = getString(R.string.field_cannot_be_empty)
                }
                if (new_password.text.toString() != new_password_again.text.toString()) {
                    new_password.error = getString(R.string.not_same_value)
                    new_password_again.error = getString(R.string.not_same_value)
                }

                if (!new_password.text.isNullOrBlank() && !new_password_again.text.isNullOrBlank() && new_password.text.toString() == new_password_again.text.toString()) {
                    meViewModel.changePassword(args.token, new_password.text.toString())
                        .observe(viewLifecycleOwner) { o ->
                            if (o.isSuccessful) {
                                triggerRebirth(requireContext())
                            } else Toast.makeText(
                                requireContext(),
                                getString(R.string.error_try_again),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
        }
    }

    private fun triggerRebirth(context: Context) {
        findNavController().popBackStack()
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.removeExtra("accessToken")
        val componentName = intent!!.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }
}