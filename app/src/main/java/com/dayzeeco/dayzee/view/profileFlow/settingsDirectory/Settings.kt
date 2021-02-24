package com.dayzeeco.dayzee.view.profileFlow.settingsDirectory

import android.accounts.AccountManager
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.work.*
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.androidView.dialog.input
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.common.booleanLiveData
import com.dayzeeco.dayzee.common.stringLiveData
import com.dayzeeco.dayzee.listeners.RefreshPicBottomNavListener
import com.dayzeeco.dayzee.model.*
import com.dayzeeco.dayzee.viewModel.LoginViewModel
import com.dayzeeco.dayzee.viewModel.MeViewModel
import com.dayzeeco.dayzee.viewModel.TimenoteViewModel
import com.dayzeeco.dayzee.webService.ProfileModifyData
import com.dayzeeco.dayzee.worker.SynchronizeGoogleCalendarWorker
import com.dayzeeco.dayzee.worker.token_id
import com.dayzeeco.dayzee.worker.user_id
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class Settings : Fragment(), View.OnClickListener {

    private val REQUEST_ACCOUNT_PICKER = 1000
    private val REQUEST_AUTHORIZATION = 1001
    private val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    private lateinit var service: Calendar
    private lateinit var credential: GoogleAccountCredential
    private val transport = AndroidHttp.newCompatibleTransport()
    private val GMAIL = "gmail"

    private val utils = Utils()
    private val timenoteViewModel: TimenoteViewModel by activityViewModels()
    private val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"
    private val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()
    private lateinit var prefs : SharedPreferences
    private var tokenId: String? = null
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val meViewModel : MeViewModel by activityViewModels()
    private lateinit var profileModifyData: ProfileModifyData
    private lateinit var dsactv : TextView
    private lateinit var userInfoDTO: UserInfoDTO
    private lateinit var onRefreshPicBottomNavListener: RefreshPicBottomNavListener
    private lateinit var map : MutableMap<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        tokenId = prefs.getString(accessToken, null)

        if(prefs.getString("pmtc", "") == "") prefs.edit().putString("pmtc", "").apply()
        if(prefs.getInt("default_settings_at_creation_time", -1) == -1) prefs.edit().putInt("default_settings_at_creation_time", 0).apply()
        val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
        userInfoDTO = Gson().fromJson<UserInfoDTO>(prefs.getString("UserInfoDTO", ""), typeUserInfo)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onRefreshPicBottomNavListener = context as RefreshPicBottomNavListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        map = Gson().fromJson(prefs.getString("mapEventIdToTimenote", null), object : TypeToken<MutableMap<String, String>>() {}.type) ?: mutableMapOf()
        credential = GoogleAccountCredential.usingOAuth2(requireContext(), listOf(CalendarScopes.CALENDAR_READONLY))
            .setBackOff(ExponentialBackOff())
            .setSelectedAccountName(prefs.getString(GMAIL, null))

        service = Calendar.Builder(
            transport, jsonFactory, credential)
            .setApplicationName("TestGoogleCalendar")
            .build()

        dsactv = profile_settings_default_settings_at_creation_time

        when(prefs.getInt("default_settings_at_creation_time", 1)){
            0 -> profile_settings_default_settings_at_creation_time.text = getString(R.string.only_me)
            1 -> profile_settings_default_settings_at_creation_time.text = getString(R.string.public_label)
        }

        profileModifyData = ProfileModifyData(requireContext())
        settings_switch_account_synchro_google.isChecked = prefs.getBoolean("googleCalendarSynchronized", false)

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
                )).observe(viewLifecycleOwner, Observer { usrInfoDTO ->
                    if(usrInfoDTO.code() == 401){
                        loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer { newAccessToken ->
                            tokenId = newAccessToken
                            meViewModel.modifyProfile(tokenId!!, UpdateUserInfoDTO(
                                profilModifyModel.givenName, profilModifyModel.familyName, profilModifyModel.picture,
                                profilModifyModel.location, profilModifyModel.birthday, profilModifyModel.description,
                                profilModifyModel.gender, profilModifyModel.status, profilModifyModel.dateFormat, profilModifyModel.socialMedias
                            ))
                        })
                    }
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
        profile_settings_switch_account_status.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) profileModifyData.setStatusAccount(1)
            else profileModifyData.setStatusAccount(0)
        }
        settings_switch_account_synchro_google.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("googleCalendarSynchronized", isChecked).apply()
            settings_switch_account_synchro_google.isChecked = isChecked
            if(isChecked) chooseAccount()
            else WorkManager.getInstance(requireContext()).cancelUniqueWork("synchronizeGoogleCalendarWorker")
        }
    }

    override fun onClick(v: View?) {
        when(v) {
            profile_settings_notification_manager -> findNavController().navigate(SettingsDirections.actionSettingsToNotificationManager())
            profile_settings_edit_personnal_infos -> findNavController().navigate(SettingsDirections.actionGlobalProfilModify(false, null))
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
            profile_settings_change_password -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.change_password)
                message(R.string.cant_start_with_password)
                input(hintRes = R.string.new_password, inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD) { _, newPassword ->
                    MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(R.string.change_password)
                        message(R.string.cant_start_with_password)
                        input(hintRes = R.string.new_password_again, inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD) { _, newPasswordAgain ->
                            if (newPassword.toString() == newPasswordAgain.toString()) {
                                meViewModel.changePassword(tokenId!!, newPasswordAgain.toString()).observe(viewLifecycleOwner, Observer { rsp ->
                                    if (rsp.code() == 401) {
                                        loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer { newToken ->
                                                tokenId = newToken
                                                meViewModel.changePassword(tokenId!!, newPasswordAgain.toString()).observe(viewLifecycleOwner, Observer { resp ->
                                                    if (resp.isSuccessful) {
                                                        Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show()
                                                    } else Toast.makeText(requireContext(), "Error. Please try again", Toast.LENGTH_SHORT).show()
                                                })
                                            })
                                    }

                                    if (rsp.isSuccessful) {
                                        Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show()
                                    } else Toast.makeText(requireContext(), "Error. Please try again", Toast.LENGTH_SHORT).show()
                                })
                            }
                        }
                        lifecycleOwner(this@Settings)
                    }
                }
                lifecycleOwner(this@Settings)
            }
            profile_settings_disconnect -> {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO){
                        prefs.edit().putString("notifications", null).apply()
                        onRefreshPicBottomNavListener.onrefreshPicBottomNav(null)
                        FirebaseInstanceId.getInstance().deleteInstanceId()
                        prefs.edit().putString(accessToken, null).apply()
                        prefs.edit().putString("UserInfoDTO", null).apply()
                        loginViewModel.markAsDisconnected()
                    }
                }
            }
            profile_settings_asked_sent -> findNavController().navigate(
                SettingsDirections.actionGlobalFollowPage(userInfoDTO.id!!, false, 0).setFollowers(3))
            profile_settings_awaiting -> findNavController().navigate(SettingsDirections.actionGlobalFollowPage(userInfoDTO.id!!, false, 0).setFollowers(2))
        }
    }

    private fun refreshResults() {
        if (credential.selectedAccountName == null) {
            chooseAccount()
        } else {
            var li : Events? = null
            try {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO){
                        try {
                            li = service.events()?.list("primary")?.setMaxResults(Integer.MAX_VALUE)?.setTimeMin(DateTime( System.currentTimeMillis()))?.setOrderBy("startTime")?.setSingleEvents(true)?.execute()
                        } catch (e : UserRecoverableAuthIOException){
                            startActivityForResult(e.intent, REQUEST_AUTHORIZATION);
                        }
                    }
                }.invokeOnCompletion {
                    if(li != null && li?.items!= null && li?.items?.size!! > 0) loopThroughCalendar(li?.items?.iterator()!!)
                }
            } catch (e : UserRecoverableAuthIOException) {
                startActivityForResult(e.intent, REQUEST_AUTHORIZATION);
            }

        }
    }

    private fun loopThroughCalendar(mEventsList: Iterator<Event>) {
        if (mEventsList.hasNext()) {
            val item = mEventsList.next()
            if(!map.keys.contains(item.id)){
                val creationTimenoteDTO = CreationTimenoteDTO(
                    userInfoDTO.id!!,
                    listOf(),
                    item.summary,
                    item.description,
                    listOf("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/toDL.jpg"),
                    null,
                    if(item.location != null) utils.getLocaFromAddress(requireActivity().applicationContext, item.location) else null,
                    null,
                    SimpleDateFormat(ISO, Locale.getDefault()).format(if(item.start.date == null) item.start.dateTime.value else item.start.date.value),
                    SimpleDateFormat(ISO, Locale.getDefault()).format(if(item.end.date == null) item.end.dateTime.value else item.end.date.value),
                    listOf(),
                    null,
                    Price(0, ""),
                    listOf(userInfoDTO.id!!),
                    null
                )

                timenoteViewModel.createTimenote(tokenId!!, creationTimenoteDTO).observe(viewLifecycleOwner, androidx.lifecycle.Observer { rsp ->
                    if (rsp.isSuccessful) {
                        println(creationTimenoteDTO.title)
                        map[item.id] = rsp.body()?.id!!
                        loopThroughCalendar(mEventsList.iterator())
                    } else if (rsp.code() == 401) {
                        loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner, androidx.lifecycle.Observer { newToken ->
                            tokenId = newToken
                            timenoteViewModel.createTimenote(tokenId!!, creationTimenoteDTO).observe(viewLifecycleOwner, androidx.lifecycle.Observer { sdRsp ->
                                if (sdRsp.isSuccessful) {
                                    println(creationTimenoteDTO.title)
                                    map[item.id] = rsp.body()?.id!!
                                    loopThroughCalendar(mEventsList.iterator())
                                }
                            })
                        })
                    }
                })
            } else {
                loopThroughCalendar(mEventsList.iterator())
            }
        } else {
            prefs.edit().putString("mapEventIdToTimenote", Gson().toJson(map)).apply()
            val data = workDataOf(user_id to userInfoDTO.id, token_id to tokenId)
            val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).setRequiresCharging(true).build()
            val worker = PeriodicWorkRequestBuilder<SynchronizeGoogleCalendarWorker>(6, TimeUnit.HOURS).setConstraints(constraints).setInputData(data).build()
            WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork("synchronizeGoogleCalendarWorker", ExistingPeriodicWorkPolicy.REPLACE ,worker)
        }
    }

    private fun chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER)
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(requireContext())
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
            return false
        } else if (connectionStatusCode != ConnectionResult.SUCCESS) {
            return false
        }
        return true
    }

    private fun showGooglePlayServicesAvailabilityErrorDialog(
        connectionStatusCode: Int) {
        ThreadUtils.runOnUiThread {
            val dialog: Dialog = GooglePlayServicesUtil.getErrorDialog(
                connectionStatusCode,
                requireActivity(),
                REQUEST_GOOGLE_PLAY_SERVICES
            )
            dialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_GOOGLE_PLAY_SERVICES -> if (resultCode == Activity.RESULT_OK) {
                refreshResults()
            } else {
                isGooglePlayServicesAvailable()
            }
            REQUEST_ACCOUNT_PICKER -> if (resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
                val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                if (accountName != null) {
                    credential.selectedAccountName = accountName
                    prefs.edit().putString(GMAIL, accountName).apply()
                    refreshResults()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
            REQUEST_AUTHORIZATION -> if (resultCode == Activity.RESULT_OK) {
                refreshResults()
            } else {
                chooseAccount()
            }
        }
    }
}
