package com.dayzeeco.dayzee.view.profileFlow.menuDirectory

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dayzeeco.dayzee.BuildConfig
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.common.accessToken
import com.dayzeeco.dayzee.common.gmail
import com.dayzeeco.dayzee.common.user_info_dto
import com.dayzeeco.dayzee.model.*
import com.google.android.gms.common.AccountPicker
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.fragment_my_profile.*
import java.lang.reflect.Type
import java.util.*


class Menu : Fragment(), View.OnClickListener {

    private lateinit var service: Calendar
    private val utils = Utils()
    private lateinit var credential: GoogleAccountCredential
    private val transport = AndroidHttp.newCompatibleTransport()
    private val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()

    private lateinit var userInfoDTO: UserInfoDTO
    private lateinit var prefs : SharedPreferences
    private val PERMISSION_CALENDAR_CODE = 12
    private var tokenId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_menu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
        userInfoDTO = Gson().fromJson(prefs.getString(user_info_dto, ""), typeUserInfo)

        credential = GoogleAccountCredential.usingOAuth2(
            requireContext(),
            listOf(CalendarScopes.CALENDAR_READONLY)
        )
            .setBackOff(ExponentialBackOff())
            .setSelectedAccountName(prefs.getString(gmail, null))

        service = Calendar.Builder(
            transport, jsonFactory, credential
        )
            .setApplicationName("Dayzee")
            .build()

        menu_settings_cv.setOnClickListener(this)
        menu_preferences_cv.setOnClickListener(this)
        menu_profile_cv.setOnClickListener(this)
        menu_invite_friends_cv.setOnClickListener(this)
        menu_synchro_wallet_cv.setOnClickListener(this)

        if (userInfoDTO.picture.isNullOrBlank()){
            profile_menu_iv.setImageDrawable(utils.determineLetterLogo(userInfoDTO.userName!!, requireContext()))
        } else {
            if(userInfoDTO.isPictureNft!!) profile_menu_iv.vertices = 6
            else profile_menu_iv.vertices = 0
            Glide
                .with(this)
                .load(userInfoDTO.picture)
                .into(profile_menu_iv)
        }

        profile_menu_name.text = userInfoDTO.userName
        if(userInfoDTO.location != null) profile_menu_location.text = userInfoDTO.location?.address?.city else profile_menu_location.visibility = View.GONE
    }

    override fun onClick(v: View?) {
        when(v){
            menu_settings_cv -> findNavController().navigate(MenuDirections.actionMenuToSettings())
            menu_profile_cv -> findNavController().navigate(
                MenuDirections.actionGlobalProfileElse(4).setUserInfoDTO(
                    userInfoDTO
                )
            )
            menu_invite_friends_cv -> {
                try {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Dayzee")
                    var shareMessage = "\nLet me recommend you this application\n\n"
                    shareMessage =
                        """${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}""".trimIndent()
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                    startActivity(Intent.createChooser(shareIntent, "choose one"))
                } catch (e: Exception) {
                    //e.toString();
                }
            }
            menu_preferences_cv -> findNavController().navigate(MenuDirections.actionMenuToPreferenceCategory())
            menu_synchro_wallet_cv -> {
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
               //findNavController().navigate(MenuDirections.actionMenuToContacts())
            }
        } else if(requestCode == PERMISSION_CALENDAR_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                val intent = AccountPicker.newChooseAccountIntent(
                    null,
                    null,
                    null,
                    false,
                    null,
                    null,
                    null,
                    null
                )
                startActivityForResult(intent, 13)
            }
        }
    }

}