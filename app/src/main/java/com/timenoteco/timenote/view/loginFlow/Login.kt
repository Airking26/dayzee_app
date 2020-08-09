package com.timenoteco.timenote.view.loginFlow

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
import com.timenoteco.timenote.R
import com.timenoteco.timenote.androidView.blurry.RSBlurProcessor
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
    }

    private fun displayMainOrLoginScreen() {
        val j  = args.x
    }

    fun getBitmapFromView(view: View): Bitmap? {
        val bitmap =
            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bitmap)
        view.layout(view.left, view.top, view.right, view.bottom)
        view.draw(c)
        return bitmap
    }
}
