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
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.AccountPicker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.BuildConfig
import com.timenoteco.timenote.R
import com.timenoteco.timenote.common.SynchronizeCalendars
import com.timenoteco.timenote.model.UserInfoDTO
import kotlinx.android.synthetic.main.fragment_menu.*
import java.lang.reflect.Type
import java.util.*


class Menu : Fragment(), View.OnClickListener {

    private val IS_EMAIL_LINKED = "is_email_linked"
    private val GMAIL = "gmail"
    private lateinit var userInfoDTO: UserInfoDTO
    private lateinit var prefs : SharedPreferences
    private val PERMISSIONS_CALENDAR = arrayOf(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR
    )
    private val PERMISSION_CALENDAR_CODE = 12


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
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
            menu_preferences_cv -> {
                if(!hasPermissions(PERMISSIONS_CALENDAR)) requestPermissions(PERMISSIONS_CALENDAR, PERMISSION_CALENDAR_CODE)
                else {
                    SynchronizeCalendars().syncCalendars(requireContext())
                    SynchronizeCalendars().readCalendar(requireContext(), 0, 0)
                }
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
        } else if( requestCode == PERMISSION_CALENDAR_CODE){
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
            SynchronizeCalendars().syncCalendars(requireContext())
            SynchronizeCalendars().readCalendar(requireContext(), 0, 0)
        }
    }
}