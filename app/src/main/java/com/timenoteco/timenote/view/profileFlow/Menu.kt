package com.timenoteco.timenote.view.profileFlow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.fragment_menu.*

class Menu : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_menu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        menu_settings_cv.setOnClickListener(this)
        menu_preferences_cv.setOnClickListener(this)
        menu_profile_cv.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            menu_settings_cv -> findNavController().navigate(MenuDirections.actionMenuToSettings())
            menu_profile_cv -> findNavController().navigate(MenuDirections.actionMenuToProfile(true))
            menu_preferences_cv -> findNavController().navigate(MenuDirections.actionMenuToPreferenceCategory(false))
        }
    }
}