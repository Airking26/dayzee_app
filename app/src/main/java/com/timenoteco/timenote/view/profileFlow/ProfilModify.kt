package com.timenoteco.timenote.view.profileFlow

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BasicGridItem
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dialog.plus.ui.DialogPlusBuilder
import com.dialog.plus.ui.MultiOptionsDialog
import com.takusemba.cropme.CropLayout
import com.takusemba.cropme.OnCropListener
import com.timenoteco.timenote.R
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.viewModel.ProfileModifyViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.cropview.view.*
import kotlinx.android.synthetic.main.cropview_circle.view.*
import kotlinx.android.synthetic.main.fragment_create_timenote.*
import kotlinx.android.synthetic.main.fragment_profil_modify.*
import java.text.SimpleDateFormat
import java.util.*


class ProfilModify: Fragment(), View.OnClickListener, OnCropListener {

    private val profileModifyViewModel: ProfileModifyViewModel by activityViewModels()
    private val DATE_FORMAT = "dd MMMM yyyy"
    private var dateFormat : SimpleDateFormat

    init {
        dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_profil_modify, container, false)

    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setListeners()
        setPicProfile()
        setProfilModifyViewModel()

    }

    private fun setProfilModifyViewModel() {
        profileModifyViewModel.getProfileModify().observe(viewLifecycleOwner, Observer {
            if (it.nameAppearance.isNullOrBlank()) profile_modify_name_appearance.hint =
                getString(R.string.name_you_want_to_appear) else profile_modify_name_appearance.text =
                it.nameAppearance
            if (it.name.isNullOrBlank()) profile_modify_name.hint =
                getString(R.string.your_name) else profile_modify_name.text = it.name
            if (it.location.isNullOrBlank()) profile_modify_from.hint =
                getString(R.string.from) else profile_modify_from.text = it.location
            if (it.birthday.isNullOrBlank()) profile_modify_birthday.hint =
                getString(R.string.birthday) else profile_modify_birthday.text = it.birthday
            when (it.gender) {
                0 -> profile_modify_gender.text = getString(R.string.man)
                1 -> profile_modify_gender.text = getString(R.string.woman)
                2 -> profile_modify_gender.text = getString(R.string.others)
                null -> profile_modify_gender.hint = getString(R.string.gender)
            }
            when (it.statusAccount) {
                0 -> profile_modify_account_status.text = getString(R.string.public_label)
                1 -> profile_modify_account_status.text = getString(R.string.private_label)
                null -> profile_modify_account_status.hint = getString(R.string.account_status)
            }
            when (it.formatTimenote) {
                0 -> profile_modify_format_timenote.text = "getString(R.string.public)"
                1 -> profile_modify_format_timenote.text = "getString(R.string.private)"
                null -> profile_modify_format_timenote.hint =
                    getString(R.string.timenote_date_format)
            }
            if (it.link.isNullOrBlank()) profile_modify_youtube_channel.hint =
                getString(R.string.youtube_channel) else profile_modify_youtube_channel.text =
                it.link
            if (it.description.isNullOrBlank()) profile_modify_description.hint =
                getString(R.string.describe_yourself) else profile_modify_description.text =
                it.description


        })
    }

    private fun setPicProfile() {
        Glide
            .with(this)
            .load("https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792")
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
        profile_modify_description.setOnClickListener(this)
        profile_modify_done_btn.setOnClickListener(this)
        profile_modify_pic_imageview.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            profile_modify_gender -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.gender)
                listItems(null, listOf(getString(R.string.man), getString(R.string.woman), getString(R.string.others) )) { dialog, index, text ->
                    profileModifyViewModel.setGender(index)
                }
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_birthday -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                datePicker() { dialog, datetime ->
                    profileModifyViewModel.setBirthday(dateFormat.format(datetime.time.time))
                }
            }
            profile_modify_account_status -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.account_status)
                listItems(null, listOf(getString(R.string.public_label), getString(R.string.private_label))) { dialog, index, text ->
                    profileModifyViewModel.setStatusAccount(index)
                }
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_format_timenote -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.timenote_date_format)
                listItems(null, listOf("X", "Y")) { dialog, index, text ->
                    profileModifyViewModel.setFormatTimenote(index)
                }
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_name_appearance -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.name_you_want_to_appear)
                input(inputType = InputType.TYPE_CLASS_TEXT, prefill = profileModifyViewModel.getProfileModify().value?.nameAppearance){ _, text ->
                    profileModifyViewModel.setNameAppearance(text.toString())
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_name -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.your_name)
                input(inputType = InputType.TYPE_CLASS_TEXT, prefill = profileModifyViewModel.getProfileModify().value?.name){ _, text ->
                    profileModifyViewModel.setName(text.toString())
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_from -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.from)
                input(inputType = InputType.TYPE_CLASS_TEXT, prefill = profileModifyViewModel.getProfileModify().value?.location){ _, text ->
                    profileModifyViewModel.setLocation(text.toString())
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_youtube_channel -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.youtube_channel)
                input(inputType = InputType.TYPE_CLASS_TEXT, prefill = profileModifyViewModel.getProfileModify().value?.link){ _, text ->
                    profileModifyViewModel.setLink(text.toString())
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_description -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.describe_yourself)
                input(inputType = InputType.TYPE_CLASS_TEXT, prefill = profileModifyViewModel.getProfileModify().value?.description){ _, text ->
                    profileModifyViewModel.setDescription(text.toString())
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@ProfilModify)
            }
            profile_modify_pic_imageview -> Utils().picChooserMaterialDialog(requireContext(), resources, profile_modify_pic_imageview, profile_modify_pb, this@ProfilModify)
            profile_modify_done_btn -> findNavController().navigate(ProfilModifyDirections.actionProfilModifyToProfile())
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == 2){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Utils().picChooserMaterialDialog(requireContext(), resources, profile_modify_pic_imageview, profile_modify_pb, this@ProfilModify)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            0 -> {
                if(resultCode == Activity.RESULT_OK && data != null){
                    val selectedImage: Bitmap = data.extras?.get("data") as Bitmap
                    cropImage(selectedImage)
                } else {
                    profile_modify_pb.visibility =View.GONE
                    profile_modify_pic_imageview.visibility = View.VISIBLE
                }
            }
            1 -> {
                if(resultCode == Activity.RESULT_OK && data != null){
                    val selectedImage : Uri? = data.data
                    val filePathColumn: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
                    if(selectedImage != null){
                        val cursor: Cursor? = requireActivity().contentResolver.query(selectedImage, filePathColumn, null, null, null)
                        if(cursor != null){
                            cursor.moveToFirst()
                            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                            val picturePath: String = cursor.getString(columnIndex)
                            val bitmap = BitmapFactory.decodeFile(picturePath)
                            cursor.close()
                            cropImage(bitmap)
                        }
                    }
                } else {
                    profile_modify_pb.visibility =View.GONE
                    profile_modify_pic_imageview.visibility = View.VISIBLE
                }
            }
        }

    }


    private fun cropImage(bitmap: Bitmap) {
        var cropView: CropLayout? = null
        val dialog = MaterialDialog(requireContext()).show {
            customView(R.layout.cropview_circle)
            title(R.string.resize)
            positiveButton(R.string.done) {
                val c = cropView
                cropView?.addOnCropListener(this@ProfilModify)
            }
            lifecycleOwner(this@ProfilModify)

        }

        cropView = dialog.getCustomView().crop_view_circle as CropLayout
        cropView.setBitmap(bitmap)
        cropView.isOffFrame()
        cropView.crop()
    }

    override fun onFailure(e: Exception) {
        e.message
    }

    override fun onSuccess(bitmap: Bitmap) {
        profile_modify_pb.visibility =View.GONE
        profile_modify_pic_imageview.visibility = View.VISIBLE
        profile_modify_pic_imageview.setImageBitmap(bitmap)
    }
}