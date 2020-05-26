package com.timenoteco.timenote.view

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.fragment_profil_modify.*


class ProfilModify: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_profil_modify, container, false)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        profile_modify_done_btn.setOnClickListener {view.findNavController().navigate(ProfilModifyDirections.actionProfilModifyToProfile())}
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    view?.findNavController()?.navigate(ProfilModifyDirections.actionProfilModifyToProfile())
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}