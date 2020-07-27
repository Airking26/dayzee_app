package com.timenoteco.timenote.view.loginFlow

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.timenoteco.timenote.R
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.viewModel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*

class Login : Fragment() {

    private val viewModel: LoginViewModel by activityViewModels()
    private val args: LoginArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            =  inflater.inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        button1.setOnClickListener {
            findNavController().navigate(LoginDirections.actionLoginToSignup())
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.refuseAuthentication()
        }

        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> findNavController().popBackStack()
                LoginViewModel.AuthenticationState.UNAUTHENTICATED -> Log.d("", "")
                LoginViewModel.AuthenticationState.INVALID_AUTHENTICATION -> Log.d("", "")
                null -> Log.d("", "")
                LoginViewModel.AuthenticationState.GUEST -> findNavController().popBackStack()
            }
        })
    }

    private fun displayMainOrLoginScreen() {
        val j  = args.x
    }
}
