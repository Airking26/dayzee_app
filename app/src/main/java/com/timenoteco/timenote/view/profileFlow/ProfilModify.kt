package com.timenoteco.timenote.view.profileFlow

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BasicGridItem
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dialog.plus.ui.DialogPlusBuilder
import com.dialog.plus.ui.MultiOptionsDialog
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profil_modify.*


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
        profile_modify_name_appearance.setOnClickListener(this)
        profile_modify_name.setOnClickListener(this)
        profile_modify_from.setOnClickListener(this)
        profile_modify_youtube_channel.setOnClickListener(this)
        profile_modify_description.setOnClickListener(this)
        profile_modify_done_btn.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v){
            profile_modify_gender -> createDialogBottomSheet(R.string.gender, listOf("Male", "Female", "None"))
            profile_modify_birthday -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                datePicker { dialog, datetime ->

                }
            }
            profile_modify_account_status -> createDialogBottomSheet(R.string.account_status, listOf("Private", "Public"))
            profile_modify_format_timenote -> createDialogBottomSheet(R.string.timenote_date_format, listOf("X", "Y"))
            profile_modify_name_appearance -> createInputDialog(R.string.name_you_want_to_appear)
            profile_modify_name -> createInputDialog(R.string.your_name)
            profile_modify_from -> createInputDialog(R.string.from)
            profile_modify_youtube_channel -> createInputDialog(R.string.youtube_channel)
            profile_modify_description -> createInputDialog(R.string.describe_yourself)
            profile_modify_done_btn -> findNavController().navigate(ProfilModifyDirections.actionProfilModifyToProfile())
        }
    }


    private fun createDialogBottomSheet(title: Int, choices: List<String>) {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(null, resources.getString(title))
            listItems(null, choices) { dialog, index, text ->

            }
            lifecycleOwner(this@ProfilModify)
        }
    }

    private fun createInputDialog(title: Int){
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(null, resources.getString(title))
            input(inputType = InputType.TYPE_CLASS_TEXT){ _, text ->

            }
            positiveButton(R.string.done)
            lifecycleOwner(this@ProfilModify)
        }
    }
}