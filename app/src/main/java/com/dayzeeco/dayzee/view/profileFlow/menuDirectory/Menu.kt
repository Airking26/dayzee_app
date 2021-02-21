package com.dayzeeco.dayzee.view.profileFlow.menuDirectory

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.AccountPicker
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.dayzeeco.dayzee.BuildConfig
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.SynchronizeCalendars
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.listeners.SynchronizeWithGoogleCalendarListener
import com.dayzeeco.dayzee.model.*
import com.dayzeeco.dayzee.viewModel.LoginViewModel
import com.dayzeeco.dayzee.viewModel.TimenoteViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.JsonFactory
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*


class Menu : Fragment(), View.OnClickListener, SynchronizeWithGoogleCalendarListener {

    private val REQUEST_ACCOUNT_PICKER = 1000
    private val REQUEST_AUTHORIZATION = 1001
    private val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    private lateinit var service: Calendar
    private lateinit var credential: GoogleAccountCredential
    private val transport = AndroidHttp.newCompatibleTransport()
    private val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()

    private val utils = Utils()
    private val timenoteViewModel: TimenoteViewModel by activityViewModels()
    private val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"
    private val GMAIL = "gmail"
    private lateinit var userInfoDTO: UserInfoDTO
    private lateinit var prefs : SharedPreferences
    private val PERMISSION_CALENDAR_CODE = 12
    private var tokenId: String? = null
    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_menu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
        userInfoDTO = Gson().fromJson<UserInfoDTO>(prefs.getString("UserInfoDTO", ""), typeUserInfo)

        credential = GoogleAccountCredential.usingOAuth2(requireContext(), listOf(CalendarScopes.CALENDAR_READONLY))
            .setBackOff(ExponentialBackOff())
            .setSelectedAccountName(prefs.getString(GMAIL, null))

        service = Calendar.Builder(
            transport, jsonFactory, credential)
            .setApplicationName("TestGoogleCalendar")
            .build()

        menu_settings_cv.setOnClickListener(this)
        menu_preferences_cv.setOnClickListener(this)
        menu_profile_cv.setOnClickListener(this)
        menu_invite_friends_cv.setOnClickListener(this)
        menu_synchro_cv.setOnClickListener(this)

        Glide
            .with(this)
            .load(userInfoDTO.picture)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.circle_pic)
            .into(profile_menu_iv)

        profile_menu_name.text = userInfoDTO.userName
        if(userInfoDTO.location != null) profile_menu_location.text = userInfoDTO.location?.address?.city else profile_menu_location.visibility = View.GONE
    }

    override fun onClick(v: View?) {
        when(v){
            menu_settings_cv -> findNavController().navigate(MenuDirections.actionMenuToSettings())
            menu_profile_cv -> findNavController().navigate(MenuDirections.actionGlobalProfileElse(4).setUserInfoDTO(userInfoDTO))
            menu_synchro_cv -> {

                sync_pb.visibility =View.VISIBLE
                menu_synchro_iv.visibility = View.INVISIBLE
                refreshResults()


                /*startActivityForResult(cre.newChooseAccountIntent(), 13)
                if(!hasPermissions(PERMISSIONS_CALENDAR)) requestPermissions(PERMISSIONS_CALENDAR, PERMISSION_CALENDAR_CODE)
                else {
                    val intent = AccountPicker.newChooseAccountIntent(null, null, null, false, null, null, null, null)
                    startActivityForResult(intent, 13)
                }*/
            }
            menu_invite_friends_cv -> {
                try {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Dayzee")
                    var shareMessage = "\nLet me recommend you this application\n\n"
                    shareMessage = """${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}""".trimIndent()
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                    startActivity(Intent.createChooser(shareIntent, "choose one"))
                } catch (e: Exception) {
                    //e.toString();
                }
            }
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        return true
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
        } else if(requestCode == PERMISSION_CALENDAR_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                val intent = AccountPicker.newChooseAccountIntent(null, null, null, false, null, null, null, null)
                startActivityForResult(intent, 13)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_GOOGLE_PLAY_SERVICES -> if (resultCode == RESULT_OK) {
                refreshResults()
            } else {
                isGooglePlayServicesAvailable()
            }
            REQUEST_ACCOUNT_PICKER -> if (resultCode == RESULT_OK && data != null && data.extras != null) {
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

        /*if(requestCode == 13 && resultCode == RESULT_OK){
            val mEmail = Objects.requireNonNull<Intent>(data).getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().putString(GMAIL, mEmail).apply()
            PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().putBoolean(IS_EMAIL_LINKED, true).apply()
            sync = SynchronizeCalendars(this)
            sync.syncCalendars(requireContext())
            sync.readCalendar(requireContext(), 0, 0)
        }*/
    }

    private fun refreshResults() {
        if (credential.selectedAccountName == null) {
            chooseAccount()
        } else {
            try {
                Thread(Runnable {
                    try {
                        val li = service.events()?.list("primary")?.setMaxResults(10)?.setTimeMin(DateTime( System.currentTimeMillis()))?.setOrderBy("startTime")?.setSingleEvents(true)?.execute()
                        val ite = li?.items
                        val mm = ""
                    } catch (e : UserRecoverableAuthIOException){
                        startActivityForResult(e.intent, REQUEST_AUTHORIZATION);
                    }
                }).start()
            } catch (e : UserRecoverableAuthIOException) {
                startActivityForResult(e.intent, REQUEST_AUTHORIZATION);
            }

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
        runOnUiThread {
            val dialog: Dialog = GooglePlayServicesUtil.getErrorDialog(
                connectionStatusCode,
                requireActivity(),
                REQUEST_GOOGLE_PLAY_SERVICES)
            dialog.show()
        }
    }

    override fun onSynchronize(mEventsList: ArrayList<CalendarEvent>) {

        val map: MutableMap<Long, String> = Gson().fromJson(prefs.getString("mapEventIdToTimenote", null), object : TypeToken<MutableMap<Long, String>>() {}.type) ?: mutableMapOf()
        loopThroughCalendar(mEventsList.iterator(), map)

        /*mEventsList.forEach {
                val creationTimenoteDTO = CreationTimenoteDTO(
                    userInfoDTO.id!!,
                    listOf(),
                    it.title,
                    it.description,
                    listOf("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/toDL.jpg"),
                    null,
                    utils.getLocaFromFromAddress(requireContext(), it.location),
                    null,
                    SimpleDateFormat(ISO, Locale.getDefault()).format(it.begin.time),
                    SimpleDateFormat(ISO, Locale.getDefault()).format(it.end.time),
                    listOf(),
                    null,
                    Price(0, ""),
                    listOf(userInfoDTO.id!!),
                    null
                )

                timenoteViewModel.createTimenote(tokenId!!, creationTimenoteDTO).observe(viewLifecycleOwner, androidx.lifecycle.Observer {rsp ->
                    if(rsp.isSuccessful) {
                        println(creationTimenoteDTO.title)
                        sync_pb.visibility =View.GONE
                        menu_setting_iv.visibility = View.VISIBLE
                    }
                    else if(rsp.code() == 401){
                        loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner, androidx.lifecycle.Observer { newToken ->
                            tokenId = newToken
                            timenoteViewModel.createTimenote(tokenId!!, creationTimenoteDTO).observe(viewLifecycleOwner, androidx.lifecycle.Observer { sdRsp ->
                                if(sdRsp.isSuccessful) {
                                    println(creationTimenoteDTO.title)
                                    sync_pb.visibility =View.GONE
                                    menu_setting_iv.visibility = View.VISIBLE
                                }
                            })
                        })
                    }
                })

            }*/
    }

    private fun loopThroughCalendar(mEventsList: Iterator<CalendarEvent>, map: MutableMap<Long, String>) {
        if (mEventsList.hasNext()) {
            val item = mEventsList.next()
            if(!map.keys.contains(item.id)){
                val creationTimenoteDTO = CreationTimenoteDTO(
                    userInfoDTO.id!!,
                    listOf(),
                    item.title,
                    item.description,
                    listOf("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/toDL.jpg"),
                    null,
                    utils.getLocaFromFromAddress(requireContext(), item.location),
                    null,
                    SimpleDateFormat(ISO, Locale.getDefault()).format(item.begin.time),
                    SimpleDateFormat(ISO, Locale.getDefault()).format(item.end.time),
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
                        loopThroughCalendar(mEventsList.iterator(), map)
                    } else if (rsp.code() == 401) {
                        loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner, androidx.lifecycle.Observer { newToken ->
                            tokenId = newToken
                            timenoteViewModel.createTimenote(tokenId!!, creationTimenoteDTO).observe(viewLifecycleOwner, androidx.lifecycle.Observer { sdRsp ->
                                if (sdRsp.isSuccessful) {
                                    println(creationTimenoteDTO.title)
                                    map[item.id] = rsp.body()?.id!!
                                    loopThroughCalendar(mEventsList.iterator(), map)
                                }
                            })
                        })
                    }
                })
            } else {
                loopThroughCalendar(mEventsList.iterator(), map)
            }
        } else {
            sync_pb.visibility = View.GONE
            menu_synchro_iv.visibility = View.VISIBLE
            prefs.edit().putString("mapEventIdToTimenote", Gson().toJson(map)).apply()
            Toast.makeText(requireContext(), getString(R.string.synchro_ok), Toast.LENGTH_SHORT).show()
        }
    }
}