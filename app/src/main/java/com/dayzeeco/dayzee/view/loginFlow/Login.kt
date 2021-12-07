package com.dayzeeco.dayzee.view.loginFlow

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.renderscript.RenderScript
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
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.viewModel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*

class Login : Fragment() {

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            =  inflater.inflate(R.layout.fragment_signup_new, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button1.setOnClickListener {
            //findNavController().navigate(LoginDirections.actionLoginToSignup())
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.refuseAuthentication()
        }
    }
}
