package com.timenoteco.timenote.view.profileFlow

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dialog.plus.ui.DialogPlusBuilder
import com.dialog.plus.ui.MultiOptionsDialog
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profil_modify.*
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfilModify: Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return if(rootView == null){
            inflater.inflate(R.layout.fragment_profil_modify, container, false)
        } else rootView
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Glide
            .with(this)
            .load("https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792")
            .apply(RequestOptions.circleCropTransform())
            .into(profile_modify_pic_imageview)

        profile_modify_gender.setOnClickListener(this)
        profile_modify_birthday.setOnClickListener(this)
        profile_modify_account_status.setOnClickListener(this)
        profile_modify_format_timenote.setOnClickListener(this)
        profile_modify_done_btn.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v){
            profile_modify_gender -> createDialogPicker(R.string.gender, listOf("Male", "Female", "None"))
            profile_modify_birthday -> DialogPlusBuilder().blurBackground().setHeaderBgColor(R.color.colorPrimary).setHeaderTextColor(android.R.color.white)
                .buildDatePickerDialog(2020) { pickedYear, pickedMonth, pickedDay -> TODO("Not yet implemented") }.show(childFragmentManager, "")
            profile_modify_account_status -> createDialogPicker(R.string.account_status, listOf("Private", "Public"))
            profile_modify_format_timenote -> createDialogPicker(R.string.timenote_date_format, listOf("X", "Y"))
            profile_modify_done_btn -> findNavController().navigate(ProfilModifyDirections.actionProfilModifyToProfile())
        }
    }

    private fun createDialogPicker(title: Int, choices: List<String>) {
        DialogPlusBuilder().setTitle(resources.getString(title))
            .hideCloseIcon().blurBackground().setPrimaryDrawable(R.drawable.too).buildMultiOptionsDialog(
                choices, object : MultiOptionsDialog.ActionListener() {
                    override fun onActionClicked(clickedOption: String?, position: Int) {

                    }

                }).show(childFragmentManager, "")
    }
}