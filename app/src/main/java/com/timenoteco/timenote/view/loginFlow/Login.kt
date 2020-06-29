package com.timenoteco.timenote.view.loginFlow

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import com.timenoteco.timenote.R
import com.timenoteco.timenote.viewModel.LoginViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*

class Login : Fragment() {

    private val viewModel: LoginViewModel by activityViewModels()
    private val args: LoginArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_login, container, false)
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return view
    }

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
                LoginViewModel.AuthenticationState.UNAUTHENTICATED_CHOOSED -> findNavController().popBackStack()
            }
        })
    }

    private fun displayMainOrLoginScreen() {
        val j  = args.x
    }
}
