package com.timenoteco.timenote.view.profileFlow

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
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
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
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
import com.asksira.bsimagepicker.BSImagePicker
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.theartofdev.edmodo.cropper.CropImageView
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.WebSearchAdapter
import com.timenoteco.timenote.androidView.input
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.common.stringLiveData
import com.timenoteco.timenote.model.AWSFile
import com.timenoteco.timenote.model.ProfilModifyModel
import com.timenoteco.timenote.view.createTimenoteFlow.CreateTimenoteDirections
import com.timenoteco.timenote.viewModel.ProfileModifyViewModel
import com.timenoteco.timenote.viewModel.WebSearchViewModel
import com.timenoteco.timenote.webService.ProfileModifyData
import kotlinx.android.synthetic.main.cropview.view.*
import kotlinx.android.synthetic.main.cropview_circle.view.*
import kotlinx.android.synthetic.main.fragment_profil_modify.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.reflect.Type
import java.net.URL
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class ProfilModify: Fragment(), View.OnClickListener, BSImagePicker.OnSingleImageSelectedListener,
    WebSearchAdapter.ImageChoosedListener, WebSearchAdapter.MoreImagesClicked, BSImagePicker.ImageLoaderDelegate, BSImagePicker.OnSelectImageCancelledListener{

    private var imagesUrl: String = ""
    val amazonClient = AmazonS3Client(
        BasicAWSCredentials(
            "AKIA5JWTNYVYJQIE5GWS",
            "pVf9Wxd/rK4r81FsOsNDaaOJIKE5AGbq96Lh4RB9"
        )
    )
    private lateinit var prefs : SharedPreferences
    private lateinit var profileModifyPicIv : ImageView
    private lateinit var profileModifyPb: ProgressBar
    private lateinit var profileModifyData: ProfileModifyData
    private val DATE_FORMAT = "dd MMMM yyyy"
    private var dateFormat : SimpleDateFormat
    private val webSearchViewModel : WebSearchViewModel by activityViewModels()
    val TOKEN: String = "TOKEN"
    private var images: AWSFile? = null
    private var tokenId: String? = null
    private lateinit var utils: Utils
    private val profileModVieModel: ProfileModifyViewModel by activityViewModels()

    init {
        dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AWSMobileClient.getInstance().initialize(requireContext()).execute()
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        tokenId = prefs.getString(TOKEN, null)
        if(prefs.getString("pmtc", "") == "")
        prefs.edit().putString("pmtc", "").apply()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_profil_modify, container, false)

    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        utils = Utils()
        profileModifyPb = profile_modify_pb
        profileModifyPicIv = profile_modify_pic_imageview
        Glide
            .with(this)
            .load("https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792")
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .apply(RequestOptions.circleCropTransform())
            .into(profile_modify_pic_imageview)

        setListeners()
        setProfilModifyViewModel()

        profile_modify_youtube_switch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) profileModifyData.setStateSwitch(0)
        }

        profile_modify_facebook_switch.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked) profileModifyData.setStateSwitch(1)
        }

        profile_modify_insta_switch.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked) profileModifyData.setStateSwitch(2)
        }

        profile_modify_whatsapp_switch.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked) profileModifyData.setStateSwitch(3)
        }

        profile_modify_linkedin_switch.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked) profileModifyData.setStateSwitch(4)
        }
    }

    private fun setProfilModifyViewModel() {
        profileModifyData = ProfileModifyData(requireContext())
        prefs.stringLiveData("profile", Gson().toJson(profileModifyData.loadProfileModifyModel())).observe(viewLifecycleOwner, Observer {
            val type: Type = object : TypeToken<ProfilModifyModel?>() {}.type
            val profilModifyModel : ProfilModifyModel? = Gson().fromJson<ProfilModifyModel>(prefs.getString("profile", ""), type)
            if (profilModifyModel?.nameAppearance.isNullOrBlank()) profile_modify_name_appearance.hint =
                getString(R.string.name_you_want_to_appear) else profile_modify_name_appearance.text =
                profilModifyModel?.nameAppearance
            if (profilModifyModel?.name.isNullOrBlank()) profile_modify_name.hint =
                getString(R.string.your_name) else profile_modify_name.text = profilModifyModel?.name
            if (profilModifyModel?.location.isNullOrBlank()) profile_modify_from.hint =
                getString(R.string.from) else profile_modify_from.text = profilModifyModel?.location
            if (profilModifyModel?.birthday.isNullOrBlank()) profile_modify_birthday.hint =
                getString(R.string.birthday) else profile_modify_birthday.text = profilModifyModel?.birthday
            when (profilModifyModel?.gender) {
                0 -> profile_modify_gender.text = getString(R.string.man)
                1 -> profile_modify_gender.text = getString(R.string.woman)
                2 -> profile_modify_gender.text = getString(R.string.others)
                null -> profile_modify_gender.hint = getString(R.string.gender)
            }
            when (profilModifyModel?.statusAccount) {
                0 -> profile_modify_account_status.text = getString(R.string.public_label)
                1 -> profile_modify_account_status.text = getString(R.string.private_label)
                null -> profile_modify_account_status.hint = getString(R.string.account_status)
            }
            when (profilModifyModel?.formatTimenote) {
                0 -> profile_modify_format_timenote.text = getString(R.string.date)
                1 -> profile_modify_format_timenote.text = getString(R.string.countdown)
                null -> profile_modify_format_timenote.hint =
                    getString(R.string.timenote_date_format)
            }
            if (profilModifyModel?.youtubeLink.isNullOrBlank()) profile_modify_youtube_channel.hint =
                getString(R.string.youtube_channel) else profile_modify_youtube_channel.text = profilModifyModel?.youtubeLink

            if(profilModifyModel?.facebookLink.isNullOrBlank()) profile_modify_facebook.hint = getString(R.string.facebook)
            else profile_modify_facebook.text = profilModifyModel?.facebookLink

            if(profilModifyModel?.whatsappLink.isNullOrBlank()) profile_modify_whatsapp.hint = getString(R.string.whatsapp)
            else profile_modify_whatsapp.text = profilModifyModel?.whatsappLink

            if(profilModifyModel?.instaLink.isNullOrBlank()) profile_modify_instagram.hint = getString(R.string.instagram)
            else profile_modify_instagram.text = profilModifyModel?.instaLink

            if(profilModifyModel?.linkedinLink.isNullOrBlank()) profile_modify_linkedin.hint = getString(R.string.linkedin)
            else profile_modify_linkedin.text = profilModifyModel?.linkedinLink

            when(profilModifyModel?.stateSwitch){
                0 -> setStateSwitch(profile_modify_youtube_switch, profile_modify_facebook_switch, profile_modify_insta_switch, profile_modify_whatsapp_switch, profile_modify_linkedin_switch)
                1 -> setStateSwitch( profile_modify_facebook_switch, profile_modify_youtube_switch, profile_modify_insta_switch, profile_modify_whatsapp_switch, profile_modify_linkedin_switch)
                2 -> setStateSwitch( profile_modify_insta_switch, profile_modify_facebook_switch, profile_modify_youtube_switch, profile_modify_whatsapp_switch, profile_modify_linkedin_switch)
                3 -> setStateSwitch( profile_modify_whatsapp_switch, profile_modify_insta_switch, profile_modify_facebook_switch, profile_modify_youtube_switch, profile_modify_linkedin_switch)
                4 -> setStateSwitch(profile_modify_linkedin_switch, profile_modify_whatsapp_switch, profile_modify_insta_switch, profile_modify_facebook_switch, profile_modify_youtube_switch)
                else -> {
                    profile_modify_youtube_switch.isChecked = false
                    profile_modify_facebook_switch.isChecked = false
                    profile_modify_insta_switch.isChecked = false
                    profile_modify_whatsapp_switch.isChecked = false
                    profile_modify_linkedin_switch.isChecked = false
                }
            }


            if (profilModifyModel?.description.isNullOrBlank()) profile_modify_description.hint =
                getString(R.string.describe_yourself) else profile_modify_description.text =
                profilModifyModel?.description


            if(prefs.getString("pmtc", "") != Gson().toJson(profileModifyData.loadProfileModifyModel())){
                profileModVieModel.modifyProfile(tokenId!!, profileModifyData.loadProfileModifyModel()!!).observe(viewLifecycleOwner, Observer {

                })
            }

            prefs.edit().putString("pmtc", Gson().toJson(profileModifyData.loadProfileModifyModel())).apply()
        })
    }

    private fun setPicProfile(bitmap: Bitmap) {
        Glide
            .with(this)
            .load(bitmap)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .apply(RequestOptions.circleCropTransform())
            .into(profile_modify_pic_imageview)
    }

    private fun setListeners() {
        profile_modify_gender.setOnClickListener(this)
        profile_modify_birthday.setOnClickListener(this)
        profile_modify_account_status.setOnClickListener(this)
        profile_modify_format_timenote.setOnClickListener(this)
        profile_modify_name_appearance.setOnClickListener(this)
        profile_modify_name.setOnClickListener(this)
        profile_modify_from.setOnClickListener(this)
        profile_modify_youtube_channel.setOnClickListener(this)
        profile_modify_facebook.setOnClickListener(this)
        profile_modify_instagram.setOnClickListener(this)
        profile_modify_whatsapp.setOnClickListener(this)
        profile_modify_linkedin.setOnClickListener(this)
        profile_modify_description.setOnClickListener(this)
        profile_modify_done_btn.setOnClickListener(this)
        profile_modify_pic_imageview.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            profile_modify_gender -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.gender)
                listItems(null, listOf(getString(R.string.man), getString(R.string.woman), getString(R.string.others) )) { dialog, index, text ->
                    profileModifyData.setGender(index)
                }
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_birthday -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                datePicker() { dialog, datetime ->
                    profileModifyData.setBirthday(dateFormat.format(datetime.time.time))
                }
            }
            profile_modify_account_status -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.account_status)
                listItems(null, listOf(getString(R.string.public_label), getString(R.string.private_label))) { dialog, index, text ->
                    profileModifyData.setStatusAccount(index)
                }
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_format_timenote -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.timenote_date_format)
                listItems(null, listOf("X", "Y")) { dialog, index, text ->
                    profileModifyData.setFormatTimenote(index)
                }
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_name_appearance -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.name_you_want_to_appear)
                input(inputType = InputType.TYPE_CLASS_TEXT, prefill = profileModifyData.loadProfileModifyModel()?.nameAppearance){ _, text ->
                    profileModifyData.setNameAppearance(text.toString())
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_name -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.your_name)
                input(inputType = InputType.TYPE_CLASS_TEXT, prefill = profileModifyData.loadProfileModifyModel()?.name
                ){ _, text ->
                    profileModifyData.setName(text.toString())
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_from -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.from)
                input(inputType = InputType.TYPE_CLASS_TEXT, prefill = profileModifyData.loadProfileModifyModel()?.location
                ){ _, text ->
                    prefs
                    profileModifyData.setLocation(text.toString())
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_youtube_channel -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.youtube_channel)
                input(inputType = InputType.TYPE_CLASS_TEXT, prefill = profileModifyData.loadProfileModifyModel()?.youtubeLink
                ){ _, text ->
                    profileModifyData.setYoutubeLink(text.toString())
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_facebook -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.facebook)
            input(inputType = InputType.TYPE_CLASS_TEXT, prefill = profileModifyData.loadProfileModifyModel()?.facebookLink
            ){ _, text ->
                profileModifyData.setFacebookLink(text.toString())
            }
                    positiveButton(R.string.done)
                    lifecycleOwner(this@ProfilModify)
            }
            profile_modify_instagram -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.instagram)
            input(inputType = InputType.TYPE_CLASS_TEXT, prefill = profileModifyData.loadProfileModifyModel()?.instaLink
            ){ _, text ->
                profileModifyData.setInstaLink(text.toString())
            }
                    positiveButton(R.string.done)
                    lifecycleOwner(this@ProfilModify)
        }
            profile_modify_whatsapp -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.whatsapp)
            input(inputType = InputType.TYPE_CLASS_TEXT, prefill = profileModifyData.loadProfileModifyModel()?.whatsappLink
            ){ _, text ->
                profileModifyData.setWhatsappLink(text.toString())
            }
                    positiveButton(R.string.done)
                    lifecycleOwner(this@ProfilModify)
        }
            profile_modify_linkedin -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.linkedin)
            input(inputType = InputType.TYPE_CLASS_TEXT, prefill = profileModifyData.loadProfileModifyModel()?.linkedinLink
            ){ _, text ->
                profileModifyData.setLinkedinLink(text.toString())
            }
                    positiveButton(R.string.done)
                    lifecycleOwner(this@ProfilModify)
        }

            profile_modify_description -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.describe_yourself)
                input(inputType = InputType.TYPE_CLASS_TEXT, prefill = profileModifyData.loadProfileModifyModel()?.description
                ){ _, text ->
                    profileModifyData.setDescription(text.toString())
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_pic_imageview -> {
                picturePickerUser()
            }
            profile_modify_done_btn -> findNavController().popBackStack()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == 2){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                picturePickerUser()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        picturePickerUser()
    }

    fun pushPic(file: File, bitmap: Bitmap){
        amazonClient.setRegion(Region.getRegion(Regions.EU_WEST_3))
        val transferUtiliy = TransferUtility(amazonClient, requireContext())
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
                    imagesUrl = amazonClient.getResourceUrl("timenote-dev-images", key).toString()

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

    private fun setStateSwitch(switchActive: Switch, switchInactive1: Switch, switchInactive2: Switch, switchInactive3: Switch, switchInactive4: Switch){
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
            listItems(items = listOf(getString(R.string.take_a_photo), getString(R.string.search_on_web), getString(R.string.trim), getString(R.string.delete))) { _, index, text ->
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    when (index) {
                        0 -> utils.createPictureSingleBS(childFragmentManager, "single")
                        1 -> utils.createWebSearchDialog(requireContext(), webSearchViewModel, this@ProfilModify, null, null)
                        2 -> cropImage(images)
                        3 -> {
                            images = null

                        }
                    }
                } else requestPermissions(PERMISSIONS_STORAGE, 2)
            }
            lifecycleOwner(this@ProfilModify)
        }
    }

    private fun cropImage(awsFile: AWSFile?) {
        val a = awsFile
        var cropView: CropImageView? = null
        val dialog = MaterialDialog(requireContext()).show {
            customView(R.layout.cropview_circle)
            title(R.string.resize)
            positiveButton(R.string.done) {
                profileModifyPb.visibility = View.GONE
                profileModifyPicIv.visibility = View.VISIBLE
                setPicProfile(cropView?.croppedImage!!)
                awsFile?.bitmap = cropView?.croppedImage
            }
            lifecycleOwner(this@ProfilModify)
        }
        cropView = dialog.getCustomView().crop_view_circle as CropImageView
        cropView.setImageBitmap(awsFile?.bitmap)
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
        utils.createPictureSingleBS(childFragmentManager, "single")
    }

    override fun onSingleImageSelected(uri: Uri?, tag: String?) {
        images = AWSFile(uri, MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri))
        setPicProfile(MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri))
    }

    fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = requireActivity().managedQuery(uri, projection, null, null, null)
        requireActivity().startManagingCursor(cursor)
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
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
        webSearchViewModel.search(query, requireContext(), (position).toLong())
    }

    override fun loadImage(imageUri: Uri?, ivImage: ImageView?) {
        Glide.with(this).load(imageUri).into(ivImage!!)
    }

    override fun onCancelled(isMultiSelecting: Boolean, tag: String?) {
    }
}