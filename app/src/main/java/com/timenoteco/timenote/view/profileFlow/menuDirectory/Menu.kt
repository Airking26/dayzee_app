package com.timenoteco.timenote.view.profileFlow.menuDirectory

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
        menu_invite_friends_cv.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            menu_settings_cv -> findNavController().navigate(MenuDirections.actionMenuToSettings())
            menu_profile_cv -> findNavController().navigate(MenuDirections.actionMenuToProfilePreview())
            menu_preferences_cv -> findNavController().navigate(MenuDirections.actionMenuToPreferenceCategory())
            menu_invite_friends_cv -> {
                val PERMISSIONS_STORAGE = arrayOf(
                    Manifest.permission.READ_CONTACTS)
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                        findNavController().navigate(MenuDirections.actionMenuToContacts())
                    } else {
                        requestPermissions(PERMISSIONS_STORAGE, 10)
                    }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 10){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findNavController().navigate(MenuDirections.actionMenuToContacts())
            }
        }
    }
}