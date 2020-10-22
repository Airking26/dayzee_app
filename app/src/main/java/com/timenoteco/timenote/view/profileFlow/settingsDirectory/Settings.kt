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
import androidx.lifecycle.lifecycleScope
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
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.viewModel.FollowViewModel
import com.timenoteco.timenote.viewModel.LoginViewModel
import com.timenoteco.timenote.viewModel.MeViewModel
import com.timenoteco.timenote.viewModel.ProfileModifyViewModel
import com.timenoteco.timenote.webService.ProfileModifyData
import com.timenoteco.timenote.webService.service.FollowService
import kotlinx.android.synthetic.main.fragment_profil_modify.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.Type

class Settings : Fragment(), View.OnClickListener {

    private lateinit var prefs : SharedPreferences
    val TOKEN: String = "TOKEN"
    private var tokenId: String? = null
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val meViewModel : MeViewModel by activityViewModels()
    private lateinit var profileModifyData: ProfileModifyData
    private val profileModVieModel: ProfileModifyViewModel by activityViewModels()
    private lateinit var dsactv : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        tokenId = prefs.getString(TOKEN, null)
        if(prefs.getString("pmtc", "") == "") prefs.edit().putString("pmtc", "").apply()
        if(prefs.getInt("default_settings_at_creation_time", -1) == -1) prefs.edit().putInt("default_settings_at_creation_time", 0).apply()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        dsactv = profile_settings_default_settings_at_creation_time

        when(prefs.getInt("default_settings_at_creation_time", -1)){
            0 -> profile_settings_default_settings_at_creation_time.text = getString(R.string.only_me)
            1 -> profile_settings_default_settings_at_creation_time.text = getString(R.string.public_label)
        }

        profileModifyData = ProfileModifyData(requireContext())
        prefs.stringLiveData("UserInfoDTO", Gson().toJson(profileModifyData.loadProfileModifyModel())).observe(viewLifecycleOwner, Observer {
            val type: Type = object : TypeToken<UpdateUserInfoDTO?>() {}.type
            val profilModifyModel : UpdateUserInfoDTO? = Gson().fromJson<UpdateUserInfoDTO>(it, type)
            when (profilModifyModel?.status) {
                STATUS.PUBLIC.ordinal -> profile_settings_switch_account_status.isChecked = false
                STATUS.PRIVATE.ordinal  -> profile_settings_switch_account_status.isChecked = true
                null -> profile_settings_switch_account_status.isChecked = false
            }

            when (profilModifyModel?.dateFormat) {
                STATUS.PUBLIC.ordinal -> profile_setting_date_format_tv.text = getString(R.string.date)
                STATUS.PRIVATE.ordinal -> profile_setting_date_format_tv.text = getString(R.string.countdown)
                null -> profile_setting_date_format_tv.hint =
                    getString(R.string.timenote_date_format)
            }

            if(prefs.getString("pmtc", "") != Gson().toJson(profileModifyData.loadProfileModifyModel())){
                meViewModel.modifyProfile(tokenId!!, UpdateUserInfoDTO(
                    profilModifyModel?.givenName, profilModifyModel?.familyName, profilModifyModel?.picture,
                    profilModifyModel?.location, profilModifyModel?.birthday, profilModifyModel?.description,
                    profilModifyModel?.gender, profilModifyModel?.status!!, profilModifyModel.dateFormat, profilModifyModel.socialMedias
                )!!).observe(viewLifecycleOwner, Observer {

                })
            }

            prefs.edit().putString("pmtc", Gson().toJson(profileModifyData.loadProfileModifyModel())).apply()
        })

        profile_settings_change_password.setOnClickListener(this)
        profile_settings_notification_manager.setOnClickListener(this)
        profile_settings_edit_personnal_infos.setOnClickListener(this)
        profile_settings_date_format.setOnClickListener(this)
        profile_settings_timenote_format.setOnClickListener(this)
        profile_settings_disconnect.setOnClickListener(this)
        profile_settings_asked_sent.setOnClickListener(this)
        profile_settings_awaiting.setOnClickListener(this)
        profile_settings_switch_account_status.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) profileModifyData.setStatusAccount(1)
            else profileModifyData.setStatusAccount(0)
        }
    }

    override fun onClick(v: View?) {
        when(v) {
            profile_settings_notification_manager -> findNavController().navigate(SettingsDirections.actionSettingsToNotificationManager())
            profile_settings_edit_personnal_infos -> findNavController().navigate(SettingsDirections.actionSettingsToProfilModify(false, null))
            profile_settings_date_format -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.timenote_date_format)
                listItems(
                    null,
                    listOf(getString(R.string.date), getString(R.string.countdown))
                ) { dialog, index, text ->
                    profileModifyData.setFormatTimenote(index)
                }
                lifecycleOwner(this@Settings)
            }
            profile_settings_timenote_format -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.timenote_visibility_format)
                listItems(
                    null,
                    listOf(getString(R.string.only_me), getString(R.string.public_label))
                ) { dialog, index, text ->
                    when (index) {
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
            profile_settings_change_password -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.change_password)
                input(hintRes = R.string.actual_password) { _, oldPassword ->
                    MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(R.string.change_password)
                        input(hintRes = R.string.new_password) { _, newPassword ->
                            MaterialDialog(
                                requireContext(),
                                BottomSheet(LayoutMode.WRAP_CONTENT)
                            ).show {
                                title(R.string.change_password)
                                input(hintRes = R.string.new_password_again) { _, newPasswordAgain ->
                                    if (newPassword == newPasswordAgain) {

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
            profile_settings_disconnect ->{
                prefs.edit().putString("nearby",  Gson().toJson(NearbyRequestBody(Location(0.0, 0.0, Address("","", "","")), 10, listOf(), "2020-10-12T15:51:53.448Z", Price(0, ""), 2))).apply()
                prefs.edit().putString(TOKEN, null).apply()
                loginViewModel.markAsUnauthenticated()}
            profile_settings_asked_sent -> findNavController().navigate(
                SettingsDirections.actionSettingsToFollowPage().setFollowers(3))
            profile_settings_awaiting -> findNavController().navigate(SettingsDirections.actionSettingsToFollowPage().setFollowers(2))
        }
    }
}
