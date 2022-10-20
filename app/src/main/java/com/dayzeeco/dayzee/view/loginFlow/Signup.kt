package com.dayzeeco.dayzee.view.loginFlow

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.viewModel.LoginViewModel
import dev.pinkroom.walletconnectkit.WalletConnectKit
import dev.pinkroom.walletconnectkit.WalletConnectKitConfig
import kotlinx.android.synthetic.main.fragment_signup_new.*
import kotlinx.coroutines.launch


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
        val config = WalletConnectKitConfig(
            context = requireContext(),
            bridgeUrl = "wss://bridge.aktionariat.com:8887",
            appUrl = "walletconnectkit.com",
            appName = "WalletConnectKit",
            appDescription = "WalletConnectKit is the Swiss Army toolkit for WalletConnect!"
        )
        val walletConnectKit = WalletConnectKit.Builder(config).build()
        walletconnectbtn.start(walletConnectKit = walletConnectKit){address ->
            var k = address;
        }
    }


    override fun onClick(v: View?) {
        when (v) {
            secon -> findNavController().navigate(SignupDirections.actionSignupToSeConnecter())
            creercom -> findNavController().navigate(SignupDirections.actionSignupToCreateAccount())
            guest_btn_new -> { loginViewModel.markAsGuest() }
        }
    }

}