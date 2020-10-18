package com.timenoteco.timenote.view.searchFlow

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.WebSearchAdapter
import com.timenoteco.timenote.androidView.input
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.common.stringLiveData
import com.timenoteco.timenote.listeners.RefreshPicBottomNavListener
import com.timenoteco.timenote.model.STATUS
import com.timenoteco.timenote.model.UpdateUserInfoDTO
import com.timenoteco.timenote.model.UserInfoDTO
import com.timenoteco.timenote.viewModel.MeViewModel
import com.timenoteco.timenote.viewModel.ProfileModifyViewModel
import com.timenoteco.timenote.viewModel.WebSearchViewModel
import com.timenoteco.timenote.webService.ProfileModifyData
import com.zhihu.matisse.Matisse
import kotlinx.android.synthetic.main.fragment_profil_modify.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.reflect.Type
import java.net.URL
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class ProfilModifySearch: Fragment(), View.OnClickListener {

    private lateinit var prefs : SharedPreferences
    private lateinit var profileModifyPicIv : ImageView
    private lateinit var profileModifyPb: ProgressBar
    private lateinit var profileModifyData: ProfileModifyData
    private var dateFormat : SimpleDateFormat
    private val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private val args : ProfilModifySearchArgs by navArgs()
    val TOKEN: String = "TOKEN"
    private var tokenId: String? = null
    private lateinit var utils: Utils
    private lateinit var onRefreshPicBottomNavListener: RefreshPicBottomNavListener

    init {
        dateFormat = SimpleDateFormat(ISO, Locale.getDefault())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AWSMobileClient.getInstance().initialize(requireContext()).execute()
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        tokenId = prefs.getString(TOKEN, null)
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
            inflater.inflate(R.layout.fragment_profil_modify, container, false)

    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        utils = Utils()
        profileModifyPb = profile_modify_pb
        profileModifyPicIv = profile_modify_pic_imageview

        profileModifyPicIv.visibility = View.VISIBLE
        profileModifyPb.visibility = View.GONE

        setListeners(args.isNotMine)
        setSwitchs(args.isNotMine)
        if(args.isNotMine) setUserInfoDTO(args.userInfoDTO)
    }

    private fun setSwitchs(isNotMine: Boolean) {

        if(isNotMine){
            profile_from_switch.visibility = View.GONE
            profile_modify_youtube_switch.visibility = View.GONE
            profile_modify_facebook_switch.visibility = View.GONE
            profile_modify_insta_switch.visibility = View.GONE
            profile_modify_whatsapp_switch.visibility = View.GONE
            profile_modify_linkedin_switch.visibility = View.GONE
        } else {
            profile_from_switch.visibility = View.VISIBLE
            profile_modify_youtube_switch.visibility = View.VISIBLE
            profile_modify_facebook_switch.visibility = View.VISIBLE
            profile_modify_insta_switch.visibility = View.VISIBLE
            profile_modify_whatsapp_switch.visibility = View.VISIBLE
            profile_modify_linkedin_switch.visibility = View.VISIBLE

            profile_from_switch.isChecked =
                prefs.getInt("locaPref", -1) == 1 || prefs.getInt("locaPref", -1) == 2

            profile_from_switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) prefs.edit().putInt("locaPref", 1).apply()
                else prefs.edit().putInt("locaPref", 0).apply()
            }

            profile_modify_youtube_switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) profileModifyData.setStateSwitch(0)
            }

            profile_modify_facebook_switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) profileModifyData.setStateSwitch(1)
            }

            profile_modify_insta_switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) profileModifyData.setStateSwitch(2)
            }

            profile_modify_whatsapp_switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) profileModifyData.setStateSwitch(3)
            }

            profile_modify_linkedin_switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) profileModifyData.setStateSwitch(4)
            }
        }


    }

    private fun setUserInfoDTO(profilModifyModel: UserInfoDTO?) {
        Glide
            .with(this)
            .load(profilModifyModel?.picture)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.circle_pic)
            .into(profile_modify_pic_imageview)
        profile_modify_name_appearance.text = profilModifyModel?.userName

        if (profilModifyModel?.givenName.isNullOrBlank()) profile_modify_name.hint =
            getString(R.string.your_name) else profile_modify_name.text =
            profilModifyModel?.givenName
        if (profilModifyModel?.location?.address?.address.isNullOrBlank()) profile_modify_from.hint =
            getString(R.string.from) else profile_modify_from.text =
            profilModifyModel?.location?.address?.address.plus(
                ", "
            ).plus(profilModifyModel?.location?.address?.city).plus(" ")
                .plus(profilModifyModel?.location?.address?.country)
        if (profilModifyModel?.birthday.isNullOrBlank()) profile_modify_birthday.hint =
            getString(R.string.birthday) else profile_modify_birthday.text =
            profilModifyModel?.birthday
        when (profilModifyModel?.gender) {
            getString(R.string.man) -> profile_modify_gender.text = getString(R.string.man)
            getString(R.string.woman) -> profile_modify_gender.text =
                getString(R.string.woman)
            getString(R.string.other) -> profile_modify_gender.text =
                getString(R.string.other)
            null -> profile_modify_gender.hint = getString(R.string.gender)
        }
        when (profilModifyModel?.status) {
            0 -> profile_modify_account_status.text =
                getString(R.string.public_label)
            1 -> profile_modify_account_status.text =
                getString(
                    R.string.private_label
                )
            null -> profile_modify_account_status.hint = getString(R.string.account_status)
        }
        when (profilModifyModel?.dateFormat) {
            0 -> profile_modify_format_timenote.text =
                getString(R.string.date)
            1 -> profile_modify_format_timenote.text =
                getString(R.string.countdown)
            null -> profile_modify_format_timenote.hint =
                getString(R.string.timenote_date_format)
        }
        if (profilModifyModel?.socialMedias?.youtube?.url.isNullOrBlank()) profile_modify_youtube_channel.hint =
            getString(R.string.youtube_channel) else profile_modify_youtube_channel.text =
            profilModifyModel?.socialMedias?.youtube?.url

        if (profilModifyModel?.socialMedias?.facebook?.url.isNullOrBlank()) profile_modify_facebook.hint =
            getString(
                R.string.facebook
            )
        else profile_modify_facebook.text = profilModifyModel?.socialMedias?.facebook?.url

        if (profilModifyModel?.socialMedias?.whatsApp?.url.isNullOrBlank()) profile_modify_whatsapp.hint =
            getString(
                R.string.whatsapp
            )
        else profile_modify_whatsapp.text = profilModifyModel?.socialMedias?.whatsApp?.url

        if (profilModifyModel?.socialMedias?.instagram?.url.isNullOrBlank()) profile_modify_instagram.hint =
            getString(
                R.string.instagram
            )
        else profile_modify_instagram.text = profilModifyModel?.socialMedias?.instagram?.url

        if (profilModifyModel?.socialMedias?.linkedIn?.url.isNullOrBlank()) profile_modify_linkedin.hint =
            getString(
                R.string.linkedin
            )
        else profile_modify_linkedin.text = profilModifyModel?.socialMedias?.linkedIn?.url

        if (profilModifyModel?.description.isNullOrBlank()) profile_modify_description.hint =
            getString(R.string.describe_yourself) else profile_modify_description.text =
            profilModifyModel?.description
    }

    private fun setListeners(isNotMine: Boolean) {

        if(!isNotMine){
            profile_modify_gender.setOnClickListener(this)
            profile_modify_birthday.setOnClickListener(this)
            profile_modify_account_status.setOnClickListener(this)
            profile_modify_format_timenote.setOnClickListener(this)
            profile_modify_name_appearance.setOnClickListener(this)
            profile_modify_name.setOnClickListener(this)
            profile_modify_from.setOnClickListener(this)
            profile_modify_description.setOnClickListener(this)
            profile_modify_done_btn.setOnClickListener(this)
            profile_modify_pic_imageview.setOnClickListener(this)
        } else {
            profile_modify_done_btn.visibility = View.GONE
        }

        profile_modify_youtube_channel.setOnClickListener(this)
        profile_modify_facebook.setOnClickListener(this)
        profile_modify_instagram.setOnClickListener(this)
        profile_modify_whatsapp.setOnClickListener(this)
        profile_modify_linkedin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            profile_modify_gender -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.gender)
                listItems(
                    null, listOf(
                        getString(R.string.man), getString(R.string.woman), getString(
                            R.string.other
                        )
                    )
                ) { dialog, index, text ->
                    profileModifyData.setGender(text.toString())
                }
                lifecycleOwner(this@ProfilModifySearch)
            }
            profile_modify_birthday -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                datePicker() { dialog, datetime ->
                    profileModifyData.setBirthday(dateFormat.format(datetime.time.time))
                }
            }
            profile_modify_account_status -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.account_status)
                listItems(
                    null, listOf(
                        getString(R.string.public_label),
                        getString(R.string.private_label)
                    )
                ) { dialog, index, text ->
                    profileModifyData.setStatusAccount(index)
                }
                lifecycleOwner(this@ProfilModifySearch)
            }
            profile_modify_format_timenote -> MaterialDialog(
                requireContext(), BottomSheet(
                    LayoutMode.WRAP_CONTENT
                )
            ).show {
                title(R.string.timenote_date_format)
                listItems(
                    null,
                    listOf(getString(R.string.date), getString(R.string.countdown))
                ) { dialog, index, text ->
                    when (index) {
                        0 -> profileModifyData.setFormatTimenote(index)
                        1 -> profileModifyData.setFormatTimenote(index)
                    }

                }
                lifecycleOwner(this@ProfilModifySearch)
            }
            profile_modify_name -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.your_name)
                input(
                    inputType = InputType.TYPE_CLASS_TEXT,
                    prefill = profileModifyData.loadProfileModifyModel()?.givenName
                ) { _, text ->
                    profileModifyData.setName(text.toString())
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModifySearch)
            }
            profile_modify_from -> {}
            profile_modify_youtube_channel -> {
                if(!args.isNotMine) {
                    MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(R.string.youtube_channel)
                        input(
                            inputType = InputType.TYPE_CLASS_TEXT,
                            prefill = profileModifyData.loadProfileModifyModel()?.socialMedias?.youtube?.url
                        ) { _, text ->
                            profileModifyData.setYoutubeLink(text.toString())
                        }
                        positiveButton(R.string.done)
                        lifecycleOwner(this@ProfilModifySearch)
                    }
                } else {

                }
            }
            profile_modify_facebook -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.facebook)
                input(
                    inputType = InputType.TYPE_CLASS_TEXT,
                    prefill = profileModifyData.loadProfileModifyModel()?.socialMedias?.facebook?.url
                ) { _, text ->
                    profileModifyData.setFacebookLink(text.toString())
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModifySearch)
            }
            profile_modify_instagram -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.instagram)
                input(
                    inputType = InputType.TYPE_CLASS_TEXT,
                    prefill = profileModifyData.loadProfileModifyModel()?.socialMedias?.instagram?.url
                ) { _, text ->
                    profileModifyData.setInstaLink(text.toString())
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModifySearch)
            }
            profile_modify_whatsapp -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.whatsapp)
                input(
                    inputType = InputType.TYPE_CLASS_TEXT,
                    prefill = profileModifyData.loadProfileModifyModel()?.socialMedias?.whatsApp?.url
                ) { _, text ->
                    profileModifyData.setWhatsappLink(text.toString())
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModifySearch)
            }
            profile_modify_linkedin -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.linkedin)
                input(
                    inputType = InputType.TYPE_CLASS_TEXT,
                    prefill = profileModifyData.loadProfileModifyModel()?.socialMedias?.linkedIn?.url
                ) { _, text ->
                    profileModifyData.setLinkedinLink(text.toString())
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModifySearch)
            }

            profile_modify_description -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.describe_yourself)
                input(
                    inputType = InputType.TYPE_CLASS_TEXT,
                    prefill = profileModifyData.loadProfileModifyModel()?.description
                ) { _, text ->
                    profileModifyData.setDescription(text.toString())
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModifySearch)
            }
            profile_modify_pic_imageview -> {
            }
            profile_modify_done_btn -> findNavController().popBackStack()
        }
    }
}