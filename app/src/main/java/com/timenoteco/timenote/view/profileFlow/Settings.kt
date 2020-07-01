package com.timenoteco.timenote.view.profileFlow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.fragment_settings.*

class Settings : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        profile_settings_notification_manager.setOnClickListener(this)
        profile_settings_edit_personnal_infos.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            profile_settings_notification_manager -> findNavController().navigate(SettingsDirections.actionSettingsToNotificationManager())
            profile_settings_edit_personnal_infos -> findNavController().navigate(SettingsDirections.actionSettingsToProfilModify())
        }
    }
}
