package com.dayzeeco.dayzee.view.profileFlow

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
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
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.WebSearchAdapter
import com.dayzeeco.dayzee.androidView.dialog.input
import com.dayzeeco.dayzee.androidView.instaLike.GlideEngine
import com.dayzeeco.dayzee.common.*
import com.dayzeeco.dayzee.listeners.RefreshPicBottomNavListener
import com.dayzeeco.dayzee.model.STATUS
import com.dayzeeco.dayzee.model.UpdateUserInfoDTO
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.viewModel.LoginViewModel
import com.dayzeeco.dayzee.viewModel.MeViewModel
import com.dayzeeco.dayzee.viewModel.ProfileModifyViewModel
import com.dayzeeco.dayzee.viewModel.WebSearchViewModel
import com.dayzeeco.dayzee.webService.ProfileModifyData
import com.dayzeeco.picture_library.config.PictureMimeType
import com.dayzeeco.picture_library.entity.LocalMedia
import com.dayzeeco.picture_library.instagram.InsGallery
import com.dayzeeco.picture_library.listener.OnResultCallbackListener
import com.google.android.material.switchmaterial.SwitchMaterial
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.BranchEvent
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.fragment_create_timenote.*
import kotlinx.android.synthetic.main.fragment_profil_modify.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.reflect.Type
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class ProfilModify: Fragment(), View.OnClickListener,
    WebSearchAdapter.ImageChoosedListener, WebSearchAdapter.MoreImagesClicked{

    val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private lateinit var nameTv : TextView
    private lateinit var descTv: TextView
    private lateinit var profilModifyModel: UserInfoDTO
    private var imagesUrl: String = ""
    private lateinit var am : AmazonS3Client
    private lateinit var prefs : SharedPreferences
    private val AUTOCOMPLETE_REQUEST_CODE: Int = 12
    private lateinit var profileModifyPicIv : ImageView
    private lateinit var profileModifyPb: ProgressBar
    private lateinit var profileModifyData: ProfileModifyData
    private var dateFormat : SimpleDateFormat
    private val ISO = "dd.MM.yyyy"
    private val webSearchViewModel : WebSearchViewModel by activityViewModels()
    private val args : ProfilModifyArgs by navArgs()
    private var tokenId: String? = null
    private lateinit var utils: Utils
    private lateinit var onRefreshPicBottomNavListener: RefreshPicBottomNavListener
    private val profileModViewModel: ProfileModifyViewModel by activityViewModels()
    private val meViewModel : MeViewModel by activityViewModels()
    private val authViewModel : LoginViewModel by activityViewModels()
    private var placesList: List<Place.Field> = listOf(
        Place.Field.ID,
        Place.Field.NAME,
        Place.Field.ADDRESS,
        Place.Field.LAT_LNG
    )
    private lateinit var placesClient: PlacesClient

    init {
        dateFormat = SimpleDateFormat(ISO, Locale.getDefault())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AWSMobileClient.getInstance().initialize(requireContext()).execute()
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        tokenId = prefs.getString(accessToken, null)
        if(prefs.getString(pmtc, "") == "")
        prefs.edit().putString(pmtc, "").apply()
        Places.initialize(requireContext(), getString(R.string.api_web_key))
        placesClient = Places.createClient(requireContext())
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
        nameTv = profile_modify_name
        descTv = profile_modify_description
        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> { InsGallery.setCurrentTheme(InsGallery.THEME_STYLE_DARK) }
            Configuration.UI_MODE_NIGHT_NO -> {InsGallery.setCurrentTheme(InsGallery.THEME_STYLE_DEFAULT)}
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {InsGallery.setCurrentTheme(InsGallery.THEME_STYLE_DARK_BLUE)}
        }

        am = AmazonS3Client(
            CognitoCachingCredentialsProvider(
            requireContext(),
            identity_pool_id, // ID du groupe d'identités
            Regions.US_EAST_1 // Région
        )
        )
        utils = Utils()
        profileModifyPb = profile_modify_pb
        profileModifyPicIv = profile_modify_pic_imageview

        profileModifyPicIv.visibility = View.VISIBLE
        profileModifyPb.visibility = View.GONE

        setListeners(args.isNotMine)
        setSwitchs(args.isNotMine)
        if(args.isNotMine){
            setUserInfoDTO(args.userInfoDTO)
        } else setProfilModifyViewModel()
    }

    private fun setSwitchs(isNotMine: Boolean) {

        if(isNotMine){
            profile_from_switch.visibility = View.GONE
            profile_modify_youtube_switch.visibility = View.GONE
            profile_modify_facebook_switch.visibility = View.GONE
            profile_modify_insta_switch.visibility = View.GONE
            profile_modify_whatsapp_switch.visibility = View.GONE
            profile_modify_linkedin_switch.visibility = View.GONE
            profile_modify_twitter_switch.visibility = View.GONE
            profile_modify_discord_switch.visibility = View.GONE
            profile_modify_telegram_switch.visibility = View.GONE
            if(profile_modify_youtube_channel.text == requireContext().resources.getString(R.string.youtube_channel) || profile_modify_youtube_channel.hint == requireContext().resources.getString(R.string.youtube_channel) || profile_modify_youtube_channel.text.isNullOrBlank() || profile_modify_youtube_channel.text.isNullOrEmpty()){
                youtube_cl.visibility = View.GONE
            }
            if(profile_modify_facebook.text == requireContext().resources.getString(R.string.facebook) || profile_modify_facebook.hint == requireContext().resources.getString(R.string.facebook) || profile_modify_facebook.text.isNullOrBlank() || profile_modify_facebook.text.isNullOrEmpty()){
                facebook_cl.visibility = View.GONE
            }
            if(profile_modify_instagram.text == requireContext().resources.getString(R.string.instagram) || profile_modify_instagram.hint == requireContext().resources.getString(R.string.instagram) || profile_modify_instagram.text.isNullOrBlank() || profile_modify_instagram.text.isNullOrEmpty()){
                instagram_cl.visibility = View.GONE
            }
            if(profile_modify_whatsapp.text == requireContext().resources.getString(R.string.whatsapp) || profile_modify_whatsapp.hint == requireContext().resources.getString(R.string.whatsapp) || profile_modify_whatsapp.text.isNullOrBlank() || profile_modify_whatsapp.text.isNullOrEmpty()){
                whatsapp_cl.visibility = View.GONE
            }
            if(profile_modify_linkedin.text == requireContext().resources.getString(R.string.linkedin) || profile_modify_linkedin.hint == requireContext().resources.getString(R.string.linkedin) || profile_modify_linkedin.text.isNullOrBlank() || profile_modify_linkedin.text.isNullOrEmpty()){
                linkedin_cl.visibility = View.GONE
            }
            if(profile_modify_twitter.text == requireContext().resources.getString(R.string.twitter) || profile_modify_twitter.hint == requireContext().resources.getString(R.string.twitter) || profile_modify_twitter.text.isNullOrBlank() || profile_modify_twitter.text.isNullOrEmpty()){
                twitter_cl.visibility = View.GONE
            }
            if(profile_modify_discord.text == requireContext().resources.getString(R.string.discord) || profile_modify_discord.hint == requireContext().resources.getString(R.string.discord) || profile_modify_discord.text.isNullOrBlank() || profile_modify_discord.text.isNullOrEmpty()){
                discord_cl.visibility = View.GONE
            }
            if(profile_modify_telegram.text == requireContext().resources.getString(R.string.telegram) || profile_modify_telegram.hint == requireContext().resources.getString(R.string.telegram) || profile_modify_telegram.text.isNullOrBlank() || profile_modify_telegram.text.isNullOrEmpty()){
                telegram_cl.visibility = View.GONE
            }
        } else {
            profile_from_switch.visibility = View.VISIBLE
            profile_modify_youtube_switch.visibility = View.VISIBLE
            profile_modify_facebook_switch.visibility = View.VISIBLE
            profile_modify_insta_switch.visibility = View.VISIBLE
            profile_modify_whatsapp_switch.visibility = View.VISIBLE
            profile_modify_linkedin_switch.visibility = View.VISIBLE
            profile_modify_twitter_switch.visibility = View.VISIBLE
            profile_modify_discord_switch.visibility = View.VISIBLE
            profile_modify_telegram_switch.visibility = View.VISIBLE
            profile_from_switch.isChecked =
                prefs.getInt(location_pref, -1) == 1 || prefs.getInt(location_pref, -1) == 2

            profile_from_switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) prefs.edit().putInt(location_pref, 1).apply()
                else prefs.edit().putInt(location_pref, 0).apply()
            }

            profile_modify_youtube_switch.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked && profileModifyData.loadProfileModifyModel()?.socialMedias?.youtube?.url?.isNotEmpty()!!) profileModifyData.setStateSwitch(0)
                 else profile_modify_youtube_switch.isChecked = false
            }

            profile_modify_facebook_switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked  && profileModifyData.loadProfileModifyModel()?.socialMedias?.facebook?.url?.isNotEmpty()!!) profileModifyData.setStateSwitch(1)
                else profile_modify_facebook_switch.isChecked = false
            }

            profile_modify_insta_switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked  && profileModifyData.loadProfileModifyModel()?.socialMedias?.instagram?.url?.isNotEmpty()!!) profileModifyData.setStateSwitch(2)
                else profile_modify_insta_switch.isChecked = false
            }

            profile_modify_whatsapp_switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked  && profileModifyData.loadProfileModifyModel()?.socialMedias?.whatsApp?.url?.isNotEmpty()!!) profileModifyData.setStateSwitch(3)
                else profile_modify_whatsapp_switch.isChecked = false
            }

            profile_modify_linkedin_switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked  && profileModifyData.loadProfileModifyModel()?.socialMedias?.linkedIn?.url?.isNotEmpty()!!) profileModifyData.setStateSwitch(4)
                else profile_modify_linkedin_switch.isChecked = false
            }

            profile_modify_twitter_switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked  && profileModifyData.loadProfileModifyModel()?.socialMedias?.twitter?.url?.isNotEmpty()!!) profileModifyData.setStateSwitch(5)
                else profile_modify_twitter_switch.isChecked = false
            }

            profile_modify_discord_switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked  && profileModifyData.loadProfileModifyModel()?.socialMedias?.discord?.url?.isNotEmpty()!!) profileModifyData.setStateSwitch(6)
                else profile_modify_discord_switch.isChecked = false
            }

            profile_modify_telegram_switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked  && profileModifyData.loadProfileModifyModel()?.socialMedias?.telegram?.url?.isNotEmpty()!!) profileModifyData.setStateSwitch(7)
                else profile_modify_telegram_switch.isChecked = false
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setProfilModifyViewModel() {
        profileModifyData = ProfileModifyData(requireContext())
        prefs.stringLiveData(
            user_info_dto,
            Gson().toJson(profileModifyData.loadProfileModifyModel())
        ).observe(
            viewLifecycleOwner,
            {
                val type: Type = object : TypeToken<UserInfoDTO?>() {}.type
                profilModifyModel = Gson().fromJson(it, type)
                setUserInfoDTO(profilModifyModel)

                when {
                    profilModifyModel.socialMedias.youtube.enabled -> setStateSwitch(
                        profile_modify_youtube_switch,
                        profile_modify_facebook_switch,
                        profile_modify_insta_switch,
                        profile_modify_whatsapp_switch,
                        profile_modify_linkedin_switch,
                        profile_modify_twitter_switch,
                        profile_modify_discord_switch,
                        profile_modify_telegram_switch
                    )
                    profilModifyModel.socialMedias.facebook.enabled -> setStateSwitch(
                        profile_modify_facebook_switch,
                        profile_modify_youtube_switch,
                        profile_modify_insta_switch,
                        profile_modify_whatsapp_switch,
                        profile_modify_linkedin_switch,
                        profile_modify_twitter_switch,
                        profile_modify_discord_switch,
                        profile_modify_telegram_switch
                    )
                    profilModifyModel.socialMedias.instagram.enabled -> setStateSwitch(
                        profile_modify_insta_switch,
                        profile_modify_facebook_switch,
                        profile_modify_youtube_switch,
                        profile_modify_whatsapp_switch,
                        profile_modify_linkedin_switch,
                        profile_modify_twitter_switch,
                        profile_modify_discord_switch,
                        profile_modify_telegram_switch
                    )
                    profilModifyModel.socialMedias.whatsApp.enabled -> setStateSwitch(
                        profile_modify_whatsapp_switch,
                        profile_modify_insta_switch,
                        profile_modify_facebook_switch,
                        profile_modify_youtube_switch,
                        profile_modify_linkedin_switch,
                        profile_modify_twitter_switch,
                        profile_modify_discord_switch,
                        profile_modify_telegram_switch
                    )
                    profilModifyModel.socialMedias.linkedIn.enabled -> setStateSwitch(
                        profile_modify_linkedin_switch,
                        profile_modify_whatsapp_switch,
                        profile_modify_insta_switch,
                        profile_modify_facebook_switch,
                        profile_modify_youtube_switch,
                        profile_modify_twitter_switch,
                        profile_modify_discord_switch,
                        profile_modify_telegram_switch
                    )
                    profilModifyModel.socialMedias.twitter.enabled -> setStateSwitch(
                        profile_modify_twitter_switch,
                        profile_modify_linkedin_switch,
                        profile_modify_whatsapp_switch,
                        profile_modify_insta_switch,
                        profile_modify_facebook_switch,
                        profile_modify_youtube_switch,
                        profile_modify_discord_switch,
                        profile_modify_telegram_switch
                    )
                    profilModifyModel.socialMedias.discord.enabled -> setStateSwitch(
                        profile_modify_discord_switch,
                        profile_modify_twitter_switch,
                        profile_modify_linkedin_switch,
                        profile_modify_whatsapp_switch,
                        profile_modify_insta_switch,
                        profile_modify_facebook_switch,
                        profile_modify_youtube_switch,
                        profile_modify_telegram_switch
                    )
                    profilModifyModel.socialMedias.telegram.enabled -> setStateSwitch(
                        profile_modify_telegram_switch,
                        profile_modify_discord_switch,
                        profile_modify_twitter_switch,
                        profile_modify_linkedin_switch,
                        profile_modify_whatsapp_switch,
                        profile_modify_insta_switch,
                        profile_modify_facebook_switch,
                        profile_modify_youtube_switch,
                    )
                    else -> {
                        profile_modify_youtube_switch.isChecked = false
                        profile_modify_facebook_switch.isChecked = false
                        profile_modify_insta_switch.isChecked = false
                        profile_modify_whatsapp_switch.isChecked = false
                        profile_modify_linkedin_switch.isChecked = false
                        profile_modify_twitter_switch.isChecked = false
                        profile_modify_discord_switch.isChecked = false
                        profile_modify_telegram_switch.isChecked = false
                    }
                }

                if (prefs.getString(pmtc, "") != Gson().toJson(profileModifyData.loadProfileModifyModel())) {
                    modifyProfil(profilModifyModel)
                }

                prefs.edit().putString(
                    pmtc,
                    Gson().toJson(profileModifyData.loadProfileModifyModel())
                ).apply()
            })
    }

    private fun modifyProfil(profilModifyModel: UserInfoDTO) {
        meViewModel.modifyProfile(
            tokenId!!, UpdateUserInfoDTO(
                profilModifyModel.givenName,
                profilModifyModel.familyName,
                profilModifyModel.picture,
                profilModifyModel.location,
                profilModifyModel.birthday,
                profilModifyModel.description,
                profilModifyModel.gender,
                if (profilModifyModel.status == 0) STATUS.PUBLIC.ordinal else STATUS.PRIVATE.ordinal,
                if (profilModifyModel.dateFormat == 0) STATUS.PUBLIC.ordinal else STATUS.PRIVATE.ordinal,
                profilModifyModel.socialMedias
            )
        ).observe(viewLifecycleOwner, { usr ->
            if(usr.code() == 401){
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, { newAccesssToken ->
                    tokenId = newAccesssToken
                    modifyProfil(profilModifyModel)
                })
            }
            if(usr.isSuccessful) {
                onRefreshPicBottomNavListener.onrefreshPicBottomNav(usr.body()?.picture)
                prefs.edit().putString(user_info_dto, Gson().toJson(usr.body())).apply()
                prefs.edit().putInt(followers, usr.body()?.followers!!).apply()
                prefs.edit().putInt(following, usr.body()?.following!!).apply()
            }
        })
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
        if (profilModifyModel?.givenName.isNullOrBlank()) {
            nameTv.hint = getString(R.string.your_name)
            nameTv.text = null
        } else nameTv.text = profilModifyModel?.givenName
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
        if (profilModifyModel?.socialMedias?.youtube?.url.isNullOrBlank()) {
            profile_modify_youtube_channel.hint = getString(R.string.youtube_channel)
            profile_modify_youtube_channel.text = ""
            profile_modify_youtube_switch.isChecked = false
        } else profile_modify_youtube_channel.text =
            profilModifyModel?.socialMedias?.youtube?.url

        if (profilModifyModel?.socialMedias?.facebook?.url.isNullOrBlank()) {
            profile_modify_facebook.hint = getString(
                    R.string.facebook
                )
            profile_modify_facebook.text = ""
            profile_modify_facebook_switch.isChecked = false
        }
        else profile_modify_facebook.text = profilModifyModel?.socialMedias?.facebook?.url

        if (profilModifyModel?.socialMedias?.whatsApp?.url.isNullOrBlank()) {
            profile_modify_whatsapp.hint = getString(
                    R.string.whatsapp
                )
            profile_modify_whatsapp.text = ""
            profile_modify_whatsapp_switch.isChecked = false
        }
        else profile_modify_whatsapp.text = profilModifyModel?.socialMedias?.whatsApp?.url

        if (profilModifyModel?.socialMedias?.instagram?.url.isNullOrBlank()) {
            profile_modify_instagram.hint = getString(
                    R.string.instagram
                )
            profile_modify_instagram.text = ""
            profile_modify_insta_switch.isChecked = false
        }
        else profile_modify_instagram.text = profilModifyModel?.socialMedias?.instagram?.url

        if (profilModifyModel?.socialMedias?.linkedIn?.url.isNullOrBlank()) {
            profile_modify_linkedin.hint = getString(
                    R.string.linkedin
                )
            profile_modify_linkedin.text = ""
            profile_modify_linkedin_switch.isChecked = false
        }
        else profile_modify_linkedin.text = profilModifyModel?.socialMedias?.linkedIn?.url

        if (profilModifyModel?.socialMedias?.twitter?.url.isNullOrBlank()) {
            profile_modify_twitter.hint = getString(
                R.string.twitter
            )
            profile_modify_twitter.text = ""
            profile_modify_twitter_switch.isChecked = false
        }
        else profile_modify_twitter.text = profilModifyModel?.socialMedias?.twitter?.url

        if (profilModifyModel?.socialMedias?.discord?.url.isNullOrBlank()) {
            profile_modify_discord.hint = getString(
                R.string.discord
            )
            profile_modify_discord.text = ""
            profile_modify_discord_switch.isChecked = false
        }
        else profile_modify_discord.text = profilModifyModel?.socialMedias?.discord?.url

        if (profilModifyModel?.socialMedias?.telegram?.url.isNullOrBlank()) {
            profile_modify_telegram.hint = getString(
                R.string.telegram
            )
            profile_modify_telegram.text = ""
            profile_modify_telegram_switch.isChecked = false
        }
        else profile_modify_telegram.text = profilModifyModel?.socialMedias?.telegram?.url

        if (profilModifyModel?.description.isNullOrBlank()) {descTv.hint = getString(R.string.describe_yourself)
        descTv.text =  ""}else descTv.text =
            profilModifyModel?.description
    }

    private fun setListeners(isNotMine: Boolean) {

        if(!isNotMine){
            profile_modify_gender.setOnClickListener(this)
            profile_modify_birthday.setOnClickListener(this)
            profile_modify_account_status.setOnClickListener(this)
            profile_modify_format_timenote.setOnClickListener(this)
            profile_modify_name_appearance.setOnClickListener(this)
            nameTv.setOnClickListener(this)
            profile_modify_from.setOnClickListener(this)
            descTv.setOnClickListener(this)
            profile_modify_done_btn.setOnClickListener(this)
            profile_modify_pic_imageview.setOnClickListener(this)
            profile_modify_share_btn.setOnClickListener(this)
        } else {
            profile_modify_done_btn.visibility = View.GONE
        }

        profile_modify_youtube_channel.setOnClickListener(this)
        profile_modify_facebook.setOnClickListener(this)
        profile_modify_instagram.setOnClickListener(this)
        profile_modify_whatsapp.setOnClickListener(this)
        profile_modify_linkedin.setOnClickListener(this)
        profile_modify_twitter.setOnClickListener(this)
        profile_modify_discord.setOnClickListener(this)
        profile_modify_telegram.setOnClickListener(this)
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
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_birthday -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                datePicker(maxDate = Calendar.getInstance()) { _, datetime ->
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
                lifecycleOwner(this@ProfilModify)
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
                lifecycleOwner(this@ProfilModify)
            }
            nameTv -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.your_name)
                input(
                    allowEmpty = true,
                    inputType = InputType.TYPE_CLASS_TEXT,
                    prefill = profileModifyData.loadProfileModifyModel()?.givenName
                ) { _, text ->
                    profileModifyData.setName(text.toString())
                    //if(text.toString().isEmpty()) nameTv.text = null
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModify)
            }
            descTv -> {
                MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    title(R.string.describe_yourself)
                    input(allowEmpty = true, inputType = InputType.TYPE_CLASS_TEXT, prefill = profileModifyData.loadProfileModifyModel()?.description, maxLength = 120) { _, text ->
                        profileModifyData.setDescription(text.toString())
                        if(text.toString().isEmpty()) descTv.text = ""
                    }
                    positiveButton(R.string.done)
                    lifecycleOwner(this@ProfilModify)
                }
            }
            profile_modify_from -> startActivityForResult(
                Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, placesList
                ).build(requireContext()), AUTOCOMPLETE_REQUEST_CODE
            )
            profile_modify_youtube_channel -> {
                if(!args.isNotMine) {
                    MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(R.string.youtube_channel)
                        input(
                            allowEmpty = true,
                            inputType = InputType.TYPE_CLASS_TEXT,
                            prefill = profileModifyData.loadProfileModifyModel()?.socialMedias?.youtube?.url
                        ) { _, text ->
                            if(text.isEmpty()) profileModifyData.setYoutubeLink("")
                            else profileModifyData.setYoutubeLink(text.toString())
                        }
                        positiveButton(R.string.done)
                        lifecycleOwner(this@ProfilModify)
                    }
                } else {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(profileModifyData.loadProfileModifyModel()?.socialMedias?.youtube?.url)
                    try {
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException){
                        Toast.makeText(
                            requireContext(),
                            "No app found to handle the url",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            profile_modify_facebook -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.facebook)
                input(
                    allowEmpty = true,
                    inputType = InputType.TYPE_CLASS_TEXT,
                    prefill = profileModifyData.loadProfileModifyModel()?.socialMedias?.facebook?.url
                ) { _, text ->
                    if(text.isEmpty()) profileModifyData.setFacebookLink("")
                    else profileModifyData.setFacebookLink(text.toString())
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_instagram -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.instagram)
                input(
                    allowEmpty = true,
                    inputType = InputType.TYPE_CLASS_TEXT,
                    prefill = profileModifyData.loadProfileModifyModel()?.socialMedias?.instagram?.url
                ) { _, text ->
                    if(text.isEmpty()) profileModifyData.setInstaLink("")
                    else profileModifyData.setInstaLink(text.toString())
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_whatsapp -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.whatsapp)
                input(
                    allowEmpty = true,
                    inputType = InputType.TYPE_CLASS_TEXT,
                    prefill = profileModifyData.loadProfileModifyModel()?.socialMedias?.whatsApp?.url
                ) { _, text ->
                    if(text.isEmpty()) profileModifyData.setWhatsappLink("")
                    else profileModifyData.setWhatsappLink(text.toString())
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_linkedin -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.linkedin)
                input(
                    allowEmpty = true,
                    inputType = InputType.TYPE_CLASS_TEXT,
                    prefill = profileModifyData.loadProfileModifyModel()?.socialMedias?.linkedIn?.url
                ) { _, text ->
                    if(text.isEmpty()) profileModifyData.setLinkedinLink("")
                    else profileModifyData.setLinkedinLink(text.toString())
                }

                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_twitter -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.twitter)
                input(
                    allowEmpty = true,
                    inputType = InputType.TYPE_CLASS_TEXT,
                    prefill = profileModifyData.loadProfileModifyModel()?.socialMedias?.twitter?.url
                ) { _, text ->
                    if(text.isEmpty()) profileModifyData.setTwitterLink("")
                    else profileModifyData.setTwitterLink(text.toString())
                }

                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_discord -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.discord)
                input(
                    allowEmpty = true,
                    inputType = InputType.TYPE_CLASS_TEXT,
                    prefill = profileModifyData.loadProfileModifyModel()?.socialMedias?.discord?.url
                ) { _, text ->
                    if(text.isEmpty()) profileModifyData.setDiscordLink("")
                    else profileModifyData.setDiscordLink(text.toString())
                }

                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_telegram -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.telegram)
                input(
                    allowEmpty = true,
                    inputType = InputType.TYPE_CLASS_TEXT,
                    prefill = profileModifyData.loadProfileModifyModel()?.socialMedias?.telegram?.url
                ) { _, text ->
                    if(text.isEmpty()) profileModifyData.setTelegramLink("")
                    else profileModifyData.setTelegramLink(text.toString())
                }

                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModify)
            }


            profile_modify_pic_imageview -> {
                openGallery()
            }
            profile_modify_share_btn -> share()
            profile_modify_done_btn -> findNavController().popBackStack()
        }
    }

    private fun openGallery(){
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED) {

            profileModifyPicIv.visibility = View.GONE
            profileModifyPb.visibility = View.VISIBLE

            InsGallery
                .openGallery(
                    requireActivity(),
                    GlideEngine.createGlideEngine(),
                    object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: MutableList<LocalMedia>?) {
                            for (media in result!!) {
                                var path: String =
                                    if (media.isCut && !media.isCompressed) {
                                        media.cutPath
                                    } else if (media.isCompressed || media.isCut && media.isCompressed) {
                                        media.compressPath
                                    } else if (PictureMimeType.isHasVideo(media.mimeType) && !TextUtils.isEmpty(
                                            media.coverPath
                                        )
                                    ) {
                                        media.coverPath
                                    } else {
                                        media.path
                                    }
                                ImageCompressor.compressBitmap(requireContext(), File(path)) {
                                    pushPic(it)
                                }
                            }
                        }

                        override fun onCancel() {

                        }

                    }, 1)
        } else requestPermissions(PERMISSIONS_STORAGE, 2)
    }

    private fun share() {

        val linkProperties: LinkProperties = LinkProperties().setChannel("whatsapp").setFeature("sharing")

        val branchUniversalObject = if(!profilModifyModel.picture.isNullOrEmpty()) BranchUniversalObject()
            .setTitle(profilModifyModel.userName!!)
            .setContentDescription(profilModifyModel.givenName)
            .setContentImageUrl(profilModifyModel.picture!!)
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setContentMetadata(ContentMetadata().addCustomMetadata(user_info_dto, Gson().toJson(profilModifyModel)))
        else BranchUniversalObject()
            .setTitle(profilModifyModel.userName!!)
            .setContentDescription(profilModifyModel.givenName)
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setContentMetadata(ContentMetadata().addCustomMetadata(user_info_dto, Gson().toJson(profilModifyModel)))

        branchUniversalObject.generateShortUrl(requireContext(), linkProperties) { url, error ->
            BranchEvent("branch_url_created").logEvent(requireContext())
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_TEXT, String.format(resources.getString(R.string.profile_externe), profilModifyModel.userName, profilModifyModel.userName , url))
            startActivityForResult(i, 111)
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 2){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery()
            }
        }
    }

    private fun pushPic(file: File){
        val transferUtiliy = TransferUtility(am, requireContext())
        val key = "timenote/${UUID.randomUUID().mostSignificantBits}"
        val transferObserver = transferUtiliy.upload(
            bucket_dayzee_dev_image, key,
            file, CannedAccessControlList.Private
        )
        transferObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState?) {
                Log.d(ContentValues.TAG, "onStateChanged: ${state?.name}")
                if (state == TransferState.COMPLETED) {
                    profileModifyPb.visibility = View.GONE
                    profileModifyPicIv.visibility = View.VISIBLE
                    imagesUrl = am.getResourceUrl(bucket_dayzee_dev_image, key).toString()
                    profileModifyData.setPicture(imagesUrl)
                }

            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                Log.d(ContentValues.TAG, "onProgressChanged: ")
            }

            override fun onError(id: Int, ex: java.lang.Exception?) {
                Log.d(ContentValues.TAG, "onError: ${ex?.message}")
                Toast.makeText(requireContext(), ex?.message, Toast.LENGTH_LONG).show()
            }

        })

    }

    private fun setStateSwitch(
        switchActive: SwitchMaterial,
        switchInactive1: SwitchMaterial,
        switchInactive2: SwitchMaterial,
        switchInactive3: SwitchMaterial,
        switchInactive4: SwitchMaterial,
        switchInactive5: SwitchMaterial,
        switchInactive6: SwitchMaterial,
        switchInactive7: SwitchMaterial
    ){
        switchActive.isChecked = true
        switchInactive1.isChecked = false
        switchInactive2.isChecked = false
        switchInactive3.isChecked = false
        switchInactive4.isChecked = false
        switchInactive5.isChecked = false
        switchInactive6.isChecked = false
        switchInactive7.isChecked = false
    }


    private fun saveTemporary(image: Bitmap, dialog: MaterialDialog){
        val outputDir = requireContext().cacheDir
        val name = "IMG_${System.currentTimeMillis()}"
        val outputFile = File.createTempFile(name, ".jpg", outputDir)
        val outputStream = FileOutputStream(outputFile)
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
        ImageCompressor.compressBitmap(requireContext(), outputFile){
            pushPic(it)
            dialog.dismiss()
        }
    }

    override fun onImageSelectedFromWeb(bitmap: String, dialog: MaterialDialog) {
        webSearchViewModel.getBitmap().removeObservers(viewLifecycleOwner)
        webSearchViewModel.decodeSampledBitmapFromResource(URL(bitmap), Rect(), 100, 100)
        webSearchViewModel.getBitmap().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                saveTemporary(it, dialog)
                webSearchViewModel.clearBitmap()
                webSearchViewModel.getBitmap().removeObservers(viewLifecycleOwner)
            }
        })
    }

    override fun onMoreImagesClicked(position: Int, query: String) {
        webSearchViewModel.search(query, requireContext(), (position).toLong(), getString(R.string.api_web_key)) }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        profileModViewModel.fetchLocation(place.id!!).observe(
                            viewLifecycleOwner, {
                                val location = utils.setLocation(it.body()!!, false, null)
                                profileModifyData.setLocation(location)
                            })
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(ContentValues.TAG, status.statusMessage!!)
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        } else if(requestCode == 2){
            if (resultCode == Activity.RESULT_OK){
                openGallery()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}