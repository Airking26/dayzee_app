package com.timenoteco.timenote.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.fragment_profile.*

class Profile : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        profile_modify_btn.setOnClickListener {
            view.findNavController().navigate(ProfileDirections.actionProfileToProfilModify())
        }
    }


}
