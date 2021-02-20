package com.timenoteco.timenote.view.profileFlow.menuDirectory

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity.RESULT_OK
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
import com.timenoteco.timenote.BuildConfig
import com.timenoteco.timenote.R
import com.timenoteco.timenote.common.SynchronizeCalendars
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.listeners.SynchronizeWithGoogleCalendarListener
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.viewModel.LoginViewModel
import com.timenoteco.timenote.viewModel.TimenoteViewModel
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

    private val utils = Utils()
    private val timenoteViewModel: TimenoteViewModel by activityViewModels()
    private lateinit var sync : SynchronizeCalendars
    private val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"
    private val IS_EMAIL_LINKED = "is_email_linked"
    private val GMAIL = "gmail"
    private lateinit var userInfoDTO: UserInfoDTO
    private lateinit var prefs : SharedPreferences
    private val PERMISSIONS_CALENDAR = arrayOf(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR
    )
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
                val cre = GoogleAccountCredential.usingOAuth2(requireContext(), listOf(CalendarScopes.CALENDAR_READONLY)).setBackOff(ExponentialBackOff()).setSelectedAccountName("samuel2629@gmail.com")
                val ser = Calendar.Builder(AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), cre).setApplicationName("Dayzee").build()
                //startActivityForResult(cre.newChooseAccountIntent(), 13)


                lifecycleScope.launch {
                    withContext(Dispatchers.IO){
                        val li = ser.events().list("primary").setMaxResults(10).setTimeMin(DateTime( System.currentTimeMillis())).setOrderBy("startTime").setSingleEvents(true).execute()
                        val ite = li.items
                    }

                }

                /*sync_pb.visibility =View.VISIBLE
                menu_synchro_iv.visibility = View.INVISIBLE
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

                /*val PERMISSIONS_STORAGE = arrayOf(
                    Manifest.permission.READ_CONTACTS)
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                        findNavController().navigate(MenuDirections.actionMenuToContacts())
                    } else {
                        requestPermissions(PERMISSIONS_STORAGE, 10)
                    }*/
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
        if(requestCode == 13 && resultCode == RESULT_OK){
            val mEmail = Objects.requireNonNull<Intent>(data).getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().putString(GMAIL, mEmail).apply()
            PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().putBoolean(IS_EMAIL_LINKED, true).apply()
            sync = SynchronizeCalendars(this)
            sync.syncCalendars(requireContext())
            sync.readCalendar(requireContext(), 0, 0)
        }
    }

    override fun onSynchronize(mEventsList: ArrayList<CalendarEvent>) {
        val cre = GoogleAccountCredential.usingOAuth2(requireContext(), listOf(CalendarScopes.CALENDAR_READONLY)).setBackOff(ExponentialBackOff()).setSelectedAccountName(prefs.getString("account_name", null))
        val ser = Calendar.Builder(AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), cre).setApplicationName("Dayzee").build()

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