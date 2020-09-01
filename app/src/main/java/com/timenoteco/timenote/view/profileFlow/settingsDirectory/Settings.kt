package com.timenoteco.timenote.view.profileFlow.settingsDirectory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.fragment_settings.*

class Settings : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        profile_settings_notification_manager.setOnClickListener(this)
        profile_settings_edit_personnal_infos.setOnClickListener(this)
        profile_settings_date_format.setOnClickListener(this)
        profile_settings_timenote_format.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            profile_settings_notification_manager -> findNavController().navigate(SettingsDirections.actionSettingsToNotificationManager())
            profile_settings_edit_personnal_infos -> findNavController().navigate(SettingsDirections.actionSettingsToProfilModify())
            profile_settings_date_format -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.timenote_date_format)
                listItems(null, listOf(getString(R.string.date), getString(R.string.countdown))) { dialog, index, text ->

                }
                lifecycleOwner(this@Settings)
            }
            profile_settings_timenote_format -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.timenote_visibility_format)
                listItems(null, listOf(getString(R.string.only_me), getString(R.string.followers), getString(R.string.public_label))) { dialog, index, text ->

                }
                lifecycleOwner(this@Settings)
            }
        }
    }
}
