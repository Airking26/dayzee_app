package com.timenoteco.timenote.view.profileFlow.settingsDirectory

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.R
import com.timenoteco.timenote.androidView.input
import com.timenoteco.timenote.common.intLiveData
import com.timenoteco.timenote.common.stringLiveData
import com.timenoteco.timenote.model.UpdateUserInfoDTO
import com.timenoteco.timenote.viewModel.ProfileModifyViewModel
import com.timenoteco.timenote.webService.ProfileModifyData
import kotlinx.android.synthetic.main.fragment_profil_modify.*
import kotlinx.android.synthetic.main.fragment_settings.*
import java.lang.reflect.Type

class Settings : Fragment(), View.OnClickListener {

    private lateinit var prefs : SharedPreferences
    val TOKEN: String = "TOKEN"
    private var tokenId: String? = null
    private lateinit var profileModifyData: ProfileModifyData
    private val profileModVieModel: ProfileModifyViewModel by activityViewModels()
    private lateinit var dsactv : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        tokenId = prefs.getString(TOKEN, null)
        if(prefs.getString("pmtc", "") == "") prefs.edit().putString("pmtc", "").apply()
        if(prefs.getInt("default_settings_at_creation_time", -1) == -1) prefs.edit().putInt("default_settings_at_creation_time", -1).apply()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        dsactv = profile_settings_default_settings_at_creation_time

        when(prefs.getInt("default_settings_at_creation_time", -1)){
            -1 -> profile_settings_default_settings_at_creation_time.text = getString(R.string.only_me)
            0 -> profile_settings_default_settings_at_creation_time.text = getString(R.string.only_me)
            1 -> profile_settings_default_settings_at_creation_time.text = getString(R.string.public_label)
        }

        profileModifyData = ProfileModifyData(requireContext())
        prefs.stringLiveData("profile", Gson().toJson(profileModifyData.loadProfileModifyModel())).observe(viewLifecycleOwner, Observer {
            val type: Type = object : TypeToken<UpdateUserInfoDTO?>() {}.type
            val profilModifyModel : UpdateUserInfoDTO? = Gson().fromJson<UpdateUserInfoDTO>(it, type)
            when (profilModifyModel?.status) {
                getString(R.string.public_label) -> profile_settings_switch_account_status.isChecked = false
                getString(R.string.private_label)  -> profile_settings_switch_account_status.isChecked = true
                null -> profile_settings_switch_account_status.isChecked = false
            }

            when (profilModifyModel?.dateFormat) {
                getString(R.string.date) -> profile_setting_date_format_tv.text = getString(R.string.date)
                getString(R.string.countdown) -> profile_setting_date_format_tv.text = getString(R.string.countdown)
                null -> profile_setting_date_format_tv.hint =
                    getString(R.string.timenote_date_format)
            }

            if(prefs.getString("pmtc", "") != Gson().toJson(profileModifyData.loadProfileModifyModel())){
                profileModVieModel.modifyProfile(tokenId!!, profileModifyData.loadProfileModifyModel()!!).observe(viewLifecycleOwner, Observer {

                })
            }

            prefs.edit().putString("pmtc", Gson().toJson(profileModifyData.loadProfileModifyModel())).apply()
        })

        profile_settings_change_password.setOnClickListener(this)
        profile_settings_notification_manager.setOnClickListener(this)
        profile_settings_edit_personnal_infos.setOnClickListener(this)
        profile_settings_date_format.setOnClickListener(this)
        profile_settings_timenote_format.setOnClickListener(this)
        profile_settings_switch_account_status.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) profileModifyData.setStatusAccount(getString(R.string.private_label))
            else profileModifyData.setStatusAccount(getString(R.string.public_label))
        }
    }

    override fun onClick(v: View?) {
        when(v){
            profile_settings_notification_manager -> findNavController().navigate(SettingsDirections.actionSettingsToNotificationManager())
            profile_settings_edit_personnal_infos -> findNavController().navigate(SettingsDirections.actionSettingsToProfilModify())
            profile_settings_date_format -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.timenote_date_format)
                listItems(null, listOf(getString(R.string.date), getString(R.string.countdown))) { dialog, index, text ->
                    profileModifyData.setFormatTimenote(text.toString())
                }
                lifecycleOwner(this@Settings)
            }
            profile_settings_timenote_format -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.timenote_visibility_format)
                listItems(null, listOf(getString(R.string.only_me),  getString(R.string.public_label))) { dialog, index, text ->
                    when(index){
                        0 -> {
                            dsactv.text = text
                            prefs.edit().putInt("default_settings_at_creation_time", index).apply()
                        }
                        1 -> {
                            dsactv.text = text
                            prefs.edit().putInt("default_settings_at_creation_time", index).apply()
                        }
                    }

                }
                lifecycleOwner(this@Settings)
            }
            profile_settings_change_password -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.change_password)
                input(hintRes = R.string.actual_password) { _, oldPassword ->
                    MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(R.string.change_password)
                        input (hintRes = R.string.new_password) { _, newPassword ->
                            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                                title(R.string.change_password)
                                input (hintRes = R.string.new_password_again){ _, newPasswordAgain ->
                                    if(newPassword == newPasswordAgain){

                                    }
                                }
                                lifecycleOwner(this@Settings)
                            }
                        }
                        lifecycleOwner(this@Settings)
                    }
                    lifecycleOwner(this@Settings)
                }
            }
        }
    }
}
