package com.timenoteco.timenote.view.profileFlow.menuDirectory

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.UserInfoDTO
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.fragment_profile.*
import java.lang.reflect.Type

class Menu : Fragment(), View.OnClickListener {

    val TOKEN: String = "TOKEN"
    private var tokenId: String? = null
    private lateinit var prefs : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(TOKEN, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_menu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
        val userInfoDTO = Gson().fromJson<UserInfoDTO>(prefs.getString("UserInfoDTO", ""), typeUserInfo)

        menu_settings_cv.setOnClickListener(this)
        menu_preferences_cv.setOnClickListener(this)
        menu_profile_cv.setOnClickListener(this)
        menu_invite_friends_cv.setOnClickListener(this)
        profile_menu_modify.setOnClickListener(this)

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
            menu_profile_cv -> findNavController().navigate(MenuDirections.actionMenuToProfilePreview())
            menu_preferences_cv -> findNavController().navigate(MenuDirections.actionMenuToPreferenceCategory())
            menu_invite_friends_cv -> {
                val PERMISSIONS_STORAGE = arrayOf(
                    Manifest.permission.READ_CONTACTS)
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                        findNavController().navigate(MenuDirections.actionMenuToContacts())
                    } else {
                        requestPermissions(PERMISSIONS_STORAGE, 10)
                    }
            }
            profile_menu_modify -> findNavController().navigate(MenuDirections.actionMenuToProfilModify())
        }
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
        }
    }
}