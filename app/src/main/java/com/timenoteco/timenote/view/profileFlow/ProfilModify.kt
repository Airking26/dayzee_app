package com.timenoteco.timenote.view.profileFlow

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
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
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.WebSearchAdapter
import com.timenoteco.timenote.androidView.dialog.input
import com.timenoteco.timenote.androidView.matisse.Matisse
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.common.stringLiveData
import com.timenoteco.timenote.listeners.RefreshPicBottomNavListener
import com.timenoteco.timenote.model.STATUS
import com.timenoteco.timenote.model.UpdateUserInfoDTO
import com.timenoteco.timenote.model.UserInfoDTO
import com.timenoteco.timenote.model.accessToken
import com.timenoteco.timenote.viewModel.LoginViewModel
import com.timenoteco.timenote.viewModel.MeViewModel
import com.timenoteco.timenote.viewModel.ProfileModifyViewModel
import com.timenoteco.timenote.viewModel.WebSearchViewModel
import com.timenoteco.timenote.webService.ProfileModifyData
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.BranchEvent
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.fragment_profil_modify.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.reflect.Type
import java.net.URL
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class ProfilModify: Fragment(), View.OnClickListener,
    WebSearchAdapter.ImageChoosedListener, WebSearchAdapter.MoreImagesClicked{

    private lateinit var profilModifyModel: UserInfoDTO
    private var imagesUrl: String = ""
    private lateinit var am : AmazonS3Client
    private lateinit var prefs : SharedPreferences
    private val AUTOCOMPLETE_REQUEST_CODE: Int = 12
    private lateinit var profileModifyPicIv : ImageView
    private lateinit var profileModifyPb: ProgressBar
    private lateinit var profileModifyData: ProfileModifyData
    private var dateFormat : SimpleDateFormat
    private val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private val webSearchViewModel : WebSearchViewModel by activityViewModels()
    private val args : ProfilModifyArgs by navArgs()
    private var images: String? = null
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
        if(prefs.getString("pmtc", "") == "")
        prefs.edit().putString("pmtc", "").apply()
        Places.initialize(requireContext(), "AIzaSyBhM9HQo1fzDlwkIVqobfmrRmEMCWTU1CA")
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
        am = AmazonS3Client(
            CognitoCachingCredentialsProvider(
            requireContext(),
            "us-east-1:a1e54ce4-a26d-44b1-83ea-9ca1d0d7903a", // ID du groupe d'identités
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setProfilModifyViewModel() {
        profileModifyData = ProfileModifyData(requireContext())
        prefs.stringLiveData(
            "UserInfoDTO",
            Gson().toJson(profileModifyData.loadProfileModifyModel())
        ).observe(
            viewLifecycleOwner,
            Observer {
                val type: Type = object : TypeToken<UserInfoDTO?>() {}.type
                profilModifyModel = Gson().fromJson<UserInfoDTO>(it, type)
                setUserInfoDTO(profilModifyModel)

                if (profilModifyModel.socialMedias.youtube.enabled)
                    setStateSwitch(
                        profile_modify_youtube_switch,
                        profile_modify_facebook_switch,
                        profile_modify_insta_switch,
                        profile_modify_whatsapp_switch,
                        profile_modify_linkedin_switch
                    )
                else if (profilModifyModel.socialMedias.facebook.enabled)
                    setStateSwitch(
                        profile_modify_facebook_switch,
                        profile_modify_youtube_switch,
                        profile_modify_insta_switch,
                        profile_modify_whatsapp_switch,
                        profile_modify_linkedin_switch
                    )
                else if (profilModifyModel.socialMedias.instagram.enabled)
                    setStateSwitch(
                        profile_modify_insta_switch,
                        profile_modify_facebook_switch,
                        profile_modify_youtube_switch,
                        profile_modify_whatsapp_switch,
                        profile_modify_linkedin_switch
                    )
                else if (profilModifyModel.socialMedias.whatsApp.enabled)
                    setStateSwitch(
                        profile_modify_whatsapp_switch,
                        profile_modify_insta_switch,
                        profile_modify_facebook_switch,
                        profile_modify_youtube_switch,
                        profile_modify_linkedin_switch
                    )
                else if (profilModifyModel.socialMedias.linkedIn.enabled)
                    setStateSwitch(
                        profile_modify_linkedin_switch,
                        profile_modify_whatsapp_switch,
                        profile_modify_insta_switch,
                        profile_modify_facebook_switch,
                        profile_modify_youtube_switch
                    )
                else {
                    profile_modify_youtube_switch.isChecked = false
                    profile_modify_facebook_switch.isChecked = false
                    profile_modify_insta_switch.isChecked = false
                    profile_modify_whatsapp_switch.isChecked = false
                    profile_modify_linkedin_switch.isChecked = false
                }

                if (prefs.getString("pmtc", "") != Gson().toJson(profileModifyData.loadProfileModifyModel())) {
                    modifyProfil(profilModifyModel)
                }

                prefs.edit().putString(
                    "pmtc",
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
        ).observe(viewLifecycleOwner, Observer { usr ->
            if(usr.code() == 401){
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer { newAccesssToken ->
                    tokenId = newAccesssToken
                    modifyProfil(profilModifyModel)
                })
            }
            if(usr.isSuccessful) {
                onRefreshPicBottomNavListener.onrefreshPicBottomNav(usr.body())
                prefs.edit().putString("UserInfoDTO", Gson().toJson(usr.body())).apply()
                prefs.edit().putInt("followers", usr.body()?.followers!!).apply()
                prefs.edit().putInt("following", usr.body()?.following!!).apply()
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
            profile_modify_share_btn.setOnClickListener(this)
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
                lifecycleOwner(this@ProfilModify)
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
                            inputType = InputType.TYPE_CLASS_TEXT,
                            prefill = profileModifyData.loadProfileModifyModel()?.socialMedias?.youtube?.url
                        ) { _, text ->
                            profileModifyData.setYoutubeLink(text.toString())
                        }
                        positiveButton(R.string.done)
                        lifecycleOwner(this@ProfilModify)
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
                lifecycleOwner(this@ProfilModify)
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
                lifecycleOwner(this@ProfilModify)
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
                lifecycleOwner(this@ProfilModify)
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
                lifecycleOwner(this@ProfilModify)
            }

            profile_modify_description -> {
                val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.describe_yourself)
                input(allowEmpty = true, inputType = InputType.TYPE_CLASS_TEXT, prefill = profileModifyData.loadProfileModifyModel()?.description, maxLength = 120) { _, text ->
                    profileModifyData.setDescription(text.toString())
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModify)
            }
            }
            profile_modify_pic_imageview -> {
                picturePickerUser()
            }
            profile_modify_share_btn -> share()
            profile_modify_done_btn -> findNavController().popBackStack()
        }
    }


    private fun share() {

        val linkProperties: LinkProperties = LinkProperties().setChannel("whatsapp").setFeature("sharing")

        val branchUniversalObject = if(!profilModifyModel.picture.isNullOrEmpty()) BranchUniversalObject()
            .setTitle(profilModifyModel.userName!!)
            .setContentDescription(profilModifyModel.givenName)
            .setContentImageUrl(profilModifyModel.picture!!)
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setContentMetadata(ContentMetadata().addCustomMetadata("userInfoDTO", Gson().toJson(profilModifyModel)))
        else BranchUniversalObject()
            .setTitle(profilModifyModel.userName!!)
            .setContentDescription(profilModifyModel.givenName)
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setContentMetadata(ContentMetadata().addCustomMetadata("userInfoDTO", Gson().toJson(profilModifyModel)))

        branchUniversalObject.generateShortUrl(requireContext(), linkProperties) { url, error ->
            BranchEvent("branch_url_created").logEvent(requireContext())
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_TEXT, String.format("Dayzee : %s at %s", profilModifyModel.userName, url))
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
                picturePickerUser()
            }
        }
    }

    fun pushPic(file: File, bitmap: Bitmap){
        val transferUtiliy = TransferUtility(am, requireContext())
        compressFile(file, bitmap)
        val key = "timenote/${UUID.randomUUID().mostSignificantBits}"
        val transferObserver = transferUtiliy.upload(
            "timenote-dev-images", key,
            file, CannedAccessControlList.Private
        )
        transferObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState?) {
                Log.d(ContentValues.TAG, "onStateChanged: ${state?.name}")
                if (state == TransferState.COMPLETED) {
                    profileModifyPb.visibility = View.GONE
                    profileModifyPicIv.visibility = View.VISIBLE
                    imagesUrl = am.getResourceUrl("timenote-dev-images", key).toString()
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

    private fun compressFile(imageFile: File, image: Bitmap) {
        try {
            val fOut: OutputStream = FileOutputStream(imageFile)
            image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            fOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setStateSwitch(
        switchActive: Switch,
        switchInactive1: Switch,
        switchInactive2: Switch,
        switchInactive3: Switch,
        switchInactive4: Switch
    ){
        switchActive.isChecked = true
        switchInactive1.isChecked = false
        switchInactive2.isChecked = false
        switchInactive3.isChecked = false
        switchInactive4.isChecked = false
    }

    fun picturePickerUser() {
        val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.take_add_a_picture)
            listItems(
                items = listOf(
                    getString(R.string.add_a_picture), getString(R.string.search_on_web), getString(R.string.delete)
                )
            ) { _, index, text ->
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED) {
                    when (index) {
                        0 -> {
                            utils.createImagePicker(this@ProfilModify, requireContext())
                            profileModifyPicIv.visibility = View.GONE
                            profileModifyPb.visibility = View.VISIBLE
                        }
                        1 -> utils.createWebSearchDialog(
                            requireContext(),
                            webSearchViewModel,
                            this@ProfilModify,
                            profileModifyPicIv,
                            profileModifyPb
                        )
                        2 -> {
                            images = null

                        }
                    }
                } else requestPermissions(PERMISSIONS_STORAGE, 2)
            }
            lifecycleOwner(this@ProfilModify)
        }
    }

    private fun saveImage(image: Bitmap, dialog: MaterialDialog): String? {
        var savedImagePath: String? = null
        val imageFileName = "JPEG_${Timestamp(System.currentTimeMillis())}.jpg"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + "/TIMENOTE_PICTURES"
        )
        var success = true
        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }
        if (success) {
            val imageFile = File(storageDir, imageFileName)
            savedImagePath = imageFile.absolutePath
            compressFile(imageFile, image)

            galleryAddPic(savedImagePath, dialog)
        }


        return savedImagePath
    }

    private fun galleryAddPic(imagePath: String?, dialog: MaterialDialog) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(imagePath!!)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        requireActivity().sendBroadcast(mediaScanIntent)
        dialog.dismiss()
        utils.createImagePicker(this, requireContext())
    }

    override fun onImageSelectedFromWeb(bitmap: String, dialog: MaterialDialog) {
        webSearchViewModel.getBitmap().removeObservers(viewLifecycleOwner)
        webSearchViewModel.decodeSampledBitmapFromResource(URL(bitmap), Rect(), 100, 100)
        webSearchViewModel.getBitmap().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                saveImage(it, dialog)
                webSearchViewModel.clearBitmap()
                webSearchViewModel.getBitmap().removeObservers(viewLifecycleOwner)
            }
        })
    }

    override fun onMoreImagesClicked(position: Int, query: String) {
        webSearchViewModel.search(query, requireContext(), (position).toLong()) }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        profileModViewModel.fetchLocation(place.id!!).observe(
                            viewLifecycleOwner,
                            Observer {
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
                picturePickerUser()
            }
        } else if(requestCode == 112 && resultCode == Activity.RESULT_OK){
            pushPic(File(Matisse.obtainPathResult(data)[0]), MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, Matisse.obtainResult(data)[0]))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}