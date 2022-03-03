package com.dayzeeco.dayzee.view.profileFlow.settingsDirectory

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.androidView.dialog.input
import com.dayzeeco.dayzee.common.*
import com.dayzeeco.dayzee.listeners.RefreshPicBottomNavListener
import com.dayzeeco.dayzee.listeners.SynchronizeWithGoogleCalendarListener
import com.dayzeeco.dayzee.model.*
import com.dayzeeco.dayzee.viewModel.LoginViewModel
import com.dayzeeco.dayzee.viewModel.MeViewModel
import com.dayzeeco.dayzee.viewModel.TimenoteViewModel
import com.dayzeeco.dayzee.webService.ProfileModifyData
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Settings : Fragment(), View.OnClickListener, SynchronizeWithGoogleCalendarListener {

    private var sizeAlreadyImported: Int = 0
    private var totalEventsCount: Int? = 0
    private val REQUEST_ACCOUNT_PICKER = 1000
    private val REQUEST_AUTHORIZATION = 1001
    private val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    val callbackId = 42

    private lateinit var service: Calendar
    private lateinit var credential: GoogleAccountCredential
    private val transport = AndroidHttp.newCompatibleTransport()
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
    private var totalImported: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        tokenId = prefs.getString(accessToken, null)

        if(prefs.getString(pmtc, "") == "") prefs.edit().putString(pmtc, "").apply()
        if(prefs.getInt(default_settings_at_creation_time, -1) == -1) prefs.edit().putInt(
            default_settings_at_creation_time,
            0
        ).apply()
        if(prefs.getInt(format_date_default, -1) == -1) prefs.edit().putInt(format_date_default, 0).apply()
        val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
        userInfoDTO = Gson().fromJson(prefs.getString(user_info_dto, ""), typeUserInfo)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onRefreshPicBottomNavListener = context as RefreshPicBottomNavListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        map = Gson().fromJson(
            prefs.getString(map_event_id_to_timenote, null),
            object : TypeToken<MutableMap<String, String>>() {}.type
        ) ?: mutableMapOf()
        sizeAlreadyImported = map.size
        credential = GoogleAccountCredential.usingOAuth2(
            requireContext(),
            listOf(CalendarScopes.CALENDAR_READONLY)
        )
            .setBackOff(ExponentialBackOff())
            .setSelectedAccountName(prefs.getString(gmail, null))

        service = Calendar.Builder(
            transport, jsonFactory, credential
        )
            .setApplicationName(getString(R.string.app_name))
            .build()

        dsactv = profile_settings_default_settings_at_creation_time

        when(prefs.getInt(default_settings_at_creation_time, 1)){
            0 -> profile_settings_default_settings_at_creation_time.text =
                getString(R.string.only_me)
            1 -> profile_settings_default_settings_at_creation_time.text =
                getString(R.string.public_label)
        }

        profileModifyData = ProfileModifyData(requireContext())
        //settings_switch_account_synchro_google.isChecked = prefs.getBoolean(google_calendar_synchronized, false)

        prefs.stringLiveData(
            user_info_dto,
            Gson().toJson(profileModifyData.loadProfileModifyModel())
        ).observe(viewLifecycleOwner, Observer {
            val type: Type = object : TypeToken<UpdateUserInfoDTO?>() {}.type
            val profilModifyModel: UpdateUserInfoDTO? = Gson().fromJson<UpdateUserInfoDTO>(
                it,
                type
            )
            when (profilModifyModel?.status) {
                STATUS.PUBLIC.ordinal -> profile_settings_switch_account_status.isChecked = false
                STATUS.PRIVATE.ordinal -> profile_settings_switch_account_status.isChecked = true
                null -> profile_settings_switch_account_status.isChecked = false
            }

            when (profilModifyModel?.dateFormat) {
                STATUS.PUBLIC.ordinal -> profile_setting_date_format_tv.text =
                    getString(R.string.date)
                STATUS.PRIVATE.ordinal -> profile_setting_date_format_tv.text =
                    getString(R.string.countdown)
                null -> profile_setting_date_format_tv.hint =
                    getString(R.string.timenote_date_format)
            }

            if (prefs.getString(
                    pmtc,
                    ""
                ) != Gson().toJson(profileModifyData.loadProfileModifyModel())
            ) {
                meViewModel.modifyProfile(
                    tokenId!!, UpdateUserInfoDTO(
                        profilModifyModel?.givenName,
                        profilModifyModel?.familyName,
                        profilModifyModel?.picture,
                        profilModifyModel?.location,
                        profilModifyModel?.birthday,
                        profilModifyModel?.description,
                        profilModifyModel?.gender,
                        profilModifyModel?.status!!,
                        profilModifyModel.dateFormat,
                        profilModifyModel.socialMedias
                    )
                ).observe(viewLifecycleOwner, { usrInfoDTO ->
                    if (usrInfoDTO.code() == 401) {
                        loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner,
                            { newAccessToken ->
                                tokenId = newAccessToken
                                meViewModel.modifyProfile(
                                    tokenId!!, UpdateUserInfoDTO(
                                        profilModifyModel.givenName,
                                        profilModifyModel.familyName,
                                        profilModifyModel.picture,
                                        profilModifyModel.location,
                                        profilModifyModel.birthday,
                                        profilModifyModel.description,
                                        profilModifyModel.gender,
                                        profilModifyModel.status,
                                        profilModifyModel.dateFormat,
                                        profilModifyModel.socialMedias
                                    )
                                )
                            })
                    }
                })
            }

            prefs.edit().putString(pmtc, Gson().toJson(profileModifyData.loadProfileModifyModel()))
                .apply()
        })

        profile_settings_change_password.setOnClickListener(this)
        profile_settings_notification_manager.setOnClickListener(this)
        profile_settings_edit_personnal_infos.setOnClickListener(this)
        profile_settings_date_format.setOnClickListener(this)
        profile_settings_timenote_format.setOnClickListener(this)
        profile_settings_disconnect.setOnClickListener(this)
        profile_settings_asked_sent.setOnClickListener(this)
        profile_settings_awaiting.setOnClickListener(this)
        settings_synchro_cl.setOnClickListener(this)
        profile_settings_users_hidden.setOnClickListener(this)
        profile_settings_dayzee_hidden.setOnClickListener(this)
        profile_settings_switch_account_status.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) profileModifyData.setStatusAccount(1)
            else profileModifyData.setStatusAccount(0)
        }

    }

    override fun onClick(v: View?) {
        when(v) {
            settings_synchro_cl -> checkPermission(callbackId, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
            profile_settings_notification_manager -> findNavController().navigate(SettingsDirections.actionSettingsToNotificationManager())
            profile_settings_edit_personnal_infos -> findNavController().navigate(
                SettingsDirections.actionGlobalProfilModify(
                    false,
                    null
                )
            )
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
                    prefs.edit().putInt(format_date_default, index).apply()
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
                            prefs.edit().putInt(default_settings_at_creation_time, index).apply()
                        }
                        1 -> {
                            dsactv.text = text
                            prefs.edit().putInt(default_settings_at_creation_time, index).apply()
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
                input(
                    hintRes = R.string.current_password,
                    inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                ) { _, oldPassword ->
                    MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(R.string.change_password)
                        input(
                            hintRes = R.string.new_password,
                            inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                        ) { _, newPassword ->
                            MaterialDialog(
                                requireContext(),
                                BottomSheet(LayoutMode.WRAP_CONTENT)
                            ).show {
                                title(R.string.change_password)
                                input(
                                    hintRes = R.string.new_password_again,
                                    inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                                ) { _, newPasswordAgain ->
                                    if (newPassword.toString() == newPasswordAgain.toString()) {
                                        loginViewModel.modifyCurrentPassword(
                                            tokenId!!,
                                            userInfoDTO.userName!!,
                                            oldPassword.toString(),
                                            newPassword.toString()
                                        ).observe(viewLifecycleOwner, { rsp ->
                                            if (rsp.code() == 401) {
                                                loginViewModel.refreshToken(prefs).observe(
                                                    viewLifecycleOwner,
                                                    { newToken ->
                                                        tokenId = newToken
                                                        loginViewModel.modifyCurrentPassword(
                                                            tokenId!!,
                                                            userInfoDTO.userName!!,
                                                            oldPassword.toString(),
                                                            newPasswordAgain.toString()
                                                        ).observe(
                                                            viewLifecycleOwner,
                                                            { resp ->
                                                                if (resp.isSuccessful) {
                                                                    Toast.makeText(
                                                                        requireContext(),
                                                                        getString(R.string.password_changed_successfully),
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                } else Toast.makeText(
                                                                    requireContext(),
                                                                    getString(R.string.error_try_again),
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            })
                                                    })
                                            }

                                            if (rsp.isSuccessful) {
                                                Toast.makeText(
                                                    requireContext(),
                                                    getString(R.string.password_changed_successfully),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else Toast.makeText(
                                                requireContext(),
                                                getString(R.string.error_try_again),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        })
                                    } else Toast.makeText(
                                        requireContext(),
                                        getString(R.string.not_same_value),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }

                        }
                        lifecycleOwner(this@Settings)
                    }
                }
                lifecycleOwner(this@Settings)
            }
            profile_settings_disconnect -> {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        prefs.edit().clear().apply()
                        prefs.edit().putBoolean(already_signed_in, true).apply()
                        onRefreshPicBottomNavListener.onrefreshPicBottomNav(null)
                        FirebaseInstanceId.getInstance().deleteInstanceId()
                        loginViewModel.markAsDisconnected()
                    }
                }
            }
            profile_settings_asked_sent -> findNavController().navigate(
                SettingsDirections.actionGlobalFollowPage(userInfoDTO.id!!, false, 0)
                    .setFollowers(3)
            )
            profile_settings_awaiting -> findNavController().navigate(
                SettingsDirections.actionGlobalFollowPage(
                    userInfoDTO.id!!,
                    false,
                    0
                ).setFollowers(2)
            )
            profile_settings_users_hidden -> findNavController().navigate(SettingsDirections.actionSettingsToUsersHidden())
            profile_settings_dayzee_hidden -> findNavController().navigate(SettingsDirections.actionSettingsToEventsHidden())
        }
    }

    private fun checkPermission(callbackId: Int, vararg permissionsId: String) {
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALENDAR) != PERMISSION_GRANTED)
            requestPermissions(permissionsId, callbackId)
        else chooseAccount()

    }



    private fun loopThroughCal(mEventsList: Iterator<CalendarEvent>) {
        if (mEventsList.hasNext()) {
            val item = mEventsList.next()
            if(!map.keys.contains(item.id.toString())){
                val creationTimenoteDTO = CreationTimenoteDTO(
                    userInfoDTO.id!!,
                    listOf(userInfoDTO.id!!),
                    item.title,
                    item.description,
                    listOf("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/toDL.jpg"),
                    null,
                    utils.getLocaFromAddress(
                        requireActivity().applicationContext,
                        item.location
                    ),
                    null,
                    SimpleDateFormat(
                        ISO,
                        Locale.getDefault()
                    ).format(if (item.allDay) item.begin.time - TimeZone.getDefault().getOffset(System.currentTimeMillis()) else item.begin.time),
                    SimpleDateFormat(
                        ISO,
                        Locale.getDefault()
                    ).format(if (item.allDay) item.end.time - TimeZone.getDefault().getOffset(System.currentTimeMillis()) else item.end.time),
                    listOf(),
                    null,
                    Price(0.0, ""),
                    listOf(),
                    null
                )

                timenoteViewModel.createTimenote(tokenId!!, creationTimenoteDTO).observe(
                    viewLifecycleOwner,
                    { rsp ->
                        when {
                            rsp.isSuccessful -> {
                                map[item.id.toString()] = rsp.body()?.id!!
                                totalImported++
                                loopThroughCal(mEventsList.iterator())
                            }
                            rsp.code() == 400 -> loopThroughCal(mEventsList.iterator())
                            rsp.code() == 401 -> {
                                loginViewModel.refreshToken(prefs).observe(
                                    viewLifecycleOwner,
                                    { newToken ->
                                        tokenId = newToken
                                        timenoteViewModel.createTimenote(tokenId!!, creationTimenoteDTO)
                                            .observe(
                                                viewLifecycleOwner,
                                                { sdRsp ->
                                                    if (sdRsp.isSuccessful) {
                                                        totalImported++
                                                        map[item.id.toString()] = rsp.body()?.id!!
                                                        loopThroughCal(mEventsList.iterator())
                                                    } else if (sdRsp.code() == 400) loopThroughCal(
                                                        mEventsList.iterator()
                                                    )
                                                })
                                    })
                            }
                        }
                    })
            } else {
                loopThroughCal(mEventsList.iterator())
            }
        } else {
            settings_switch_account_synchro_google_pb.visibility = View.GONE
            Toast.makeText(
                requireContext(), if (sizeAlreadyImported == 0) String.format(
                    resources.getString(
                        R.string.total_events_retrieve_google_calendar,
                        totalImported,
                        totalEventsCount,
                        sizeAlreadyImported
                    )
                ).split("(")[0] else String.format(
                    resources.getString(
                        R.string.total_events_retrieve_google_calendar,
                        totalImported,
                        totalEventsCount,
                        sizeAlreadyImported
                    )
                ), Toast.LENGTH_LONG
            ).show()
            prefs.edit().putString(map_event_id_to_timenote, Gson().toJson(map)).apply()
        }
    }

    private fun chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            callbackId ->
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    chooseAccount()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.calendar_permission), Toast.LENGTH_SHORT).show()
                }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ACCOUNT_PICKER -> if (resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
                val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                if (accountName != null) {
                    credential.selectedAccountName = accountName
                    prefs.edit().putString(gmail, accountName).apply()
                    var m = SynchronizeCalendars(this)
                    m.determineCalendar(requireContext(), accountName)
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                settings_switch_account_synchro_google_pb.visibility = View.GONE
            }

        }
    }

    override fun onSynchronize(mEventsList: ArrayList<CalendarEvent>) {
        settings_switch_account_synchro_google_pb.visibility = View.VISIBLE
        totalEventsCount = mEventsList.size
        loopThroughCal(mEventsList.listIterator())
    }












    private fun isGooglePlayServicesAvailable(): Boolean {
        val connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(
            requireContext()
        )
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
            return false
        } else if (connectionStatusCode != ConnectionResult.SUCCESS) {
            return false
        }
        return true
    }

    private fun showGooglePlayServicesAvailabilityErrorDialog(
        connectionStatusCode: Int
    ) {
        ThreadUtils.runOnUiThread {
            val dialog: Dialog = GooglePlayServicesUtil.getErrorDialog(
                connectionStatusCode,
                requireActivity(),
                REQUEST_GOOGLE_PLAY_SERVICES
            )
            dialog.show()
        }
    }

    private fun refreshResults() {
        if (credential.selectedAccountName == null) {
            chooseAccount()
        } else {
            var li : Events? = null
            try {
                settings_switch_account_synchro_google_pb.visibility = View.VISIBLE
                lifecycleScope.launch {
                    kotlin.runCatching{
                        try {
                            var fli = service.events().list("primary")?.execute()
                            li = service.events()?.list("primary")?.setMaxResults(Integer.MAX_VALUE)?.setTimeMin(
                                DateTime(
                                    System.currentTimeMillis()
                                )
                            )?.setOrderBy("startTime")?.setSingleEvents(true)?.execute()
                            var m = ""
                        } catch (e: UserRecoverableAuthIOException){
                            startActivityForResult(e.intent, REQUEST_AUTHORIZATION);
                        }
                    }.getOrElse { exception -> print(exception) }
                }.invokeOnCompletion {
                    totalEventsCount = li?.items?.size
                    if(li != null && li?.items!= null && li?.items?.size!! > 0) loopThroughCalendar(
                        li?.items?.iterator()!!
                    )
                    else {
                        Toast.makeText(
                            requireContext(), if (sizeAlreadyImported == 0) String.format(
                                resources.getString(
                                    R.string.total_events_retrieve_google_calendar,
                                    totalImported,
                                    totalEventsCount,
                                    sizeAlreadyImported
                                )
                            ).split("(")[0] else String.format(
                                resources.getString(
                                    R.string.total_events_retrieve_google_calendar,
                                    totalImported,
                                    totalEventsCount,
                                    sizeAlreadyImported
                                )
                            ), Toast.LENGTH_LONG
                        ).show()
                        settings_switch_account_synchro_google_pb.visibility = View.GONE
                    }

                }
            } catch (e: UserRecoverableAuthIOException) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
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
                    item.summary ?: "",
                    item.description ?: "",
                    listOf("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/toDL.jpg"),
                    null,
                    if (item.location != null) utils.getLocaFromAddress(
                        requireActivity().applicationContext,
                        item.location
                    ) else null,
                    null,
                    SimpleDateFormat(
                        ISO,
                        Locale.getDefault()
                    ).format(if (item.start.date == null) item.start.dateTime.value else item.start.date.value),
                    SimpleDateFormat(
                        ISO,
                        Locale.getDefault()
                    ).format(if (item.end.date == null) item.end.dateTime.value else item.end.date.value),
                    listOf(),
                    null,
                    Price(0.0, ""),
                    listOf(userInfoDTO.id!!),
                    null
                )

                timenoteViewModel.createTimenote(tokenId!!, creationTimenoteDTO).observe(
                    viewLifecycleOwner,
                    { rsp ->
                        if (rsp.isSuccessful) {
                            map[item.id] = rsp.body()?.id!!
                            totalImported++
                            loopThroughCalendar(mEventsList.iterator())
                        } else if (rsp.code() == 400) loopThroughCalendar(mEventsList.iterator())
                        else if (rsp.code() == 401) {
                            loginViewModel.refreshToken(prefs).observe(
                                viewLifecycleOwner,
                                androidx.lifecycle.Observer { newToken ->
                                    tokenId = newToken
                                    timenoteViewModel.createTimenote(tokenId!!, creationTimenoteDTO)
                                        .observe(
                                            viewLifecycleOwner,
                                            androidx.lifecycle.Observer { sdRsp ->
                                                if (sdRsp.isSuccessful) {
                                                    totalImported++
                                                    map[item.id] = rsp.body()?.id!!
                                                    loopThroughCalendar(mEventsList.iterator())
                                                } else if (sdRsp.code() == 400) loopThroughCalendar(
                                                    mEventsList.iterator()
                                                )
                                            })
                                })
                        }
                    })
            } else {
                loopThroughCalendar(mEventsList.iterator())
            }
        } else {
            settings_switch_account_synchro_google_pb.visibility = View.GONE
            Toast.makeText(
                requireContext(), if (sizeAlreadyImported == 0) String.format(
                    resources.getString(
                        R.string.total_events_retrieve_google_calendar,
                        totalImported,
                        totalEventsCount,
                        sizeAlreadyImported
                    )
                ).split("(")[0] else String.format(
                    resources.getString(
                        R.string.total_events_retrieve_google_calendar,
                        totalImported,
                        totalEventsCount,
                        sizeAlreadyImported
                    )
                ), Toast.LENGTH_LONG
            ).show()
            prefs.edit().putString(map_event_id_to_timenote, Gson().toJson(map)).apply()
            /*val data = workDataOf(user_id to userInfoDTO.id, token_id to tokenId)
            val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).setRequiresCharging(true).build()
            val worker = PeriodicWorkRequestBuilder<SynchronizeGoogleCalendarWorker>(6, TimeUnit.HOURS).setConstraints(constraints).setInputData(data).build()
            WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                synchronize_google_calendar_worker, ExistingPeriodicWorkPolicy.REPLACE ,worker)*/
        }
    }
}
