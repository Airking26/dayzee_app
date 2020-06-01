package com.timenoteco.timenote.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.dialog.plus.ui.DatePickerDialog
import com.dialog.plus.ui.DialogPlus
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

        profile_modify_gender.setOnClickListener(this)
        profile_modify_birthday.setOnClickListener(this)
        profile_modify_account_status.setOnClickListener(this)
        profile_modify_format_timenote.setOnClickListener(this)
        profile_modify_done_btn.setOnClickListener {
        }

        //profile_modify_done_btn.setOnClickListener {view.findNavController().navigate(ProfilModifyDirections.actionProfilModifyToProfile())}
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) { override fun handleOnBackPressed() { view?.findNavController()?.popBackStack(R.id.profile, false) } }
        //requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onClick(v: View?) {
        when(v){
            profile_modify_gender -> createDialogPicker(R.string.gender, listOf("Male", "Female", "None"))
            profile_modify_birthday -> DialogPlusBuilder().blurBackground().setHeaderBgColor(R.color.colorPrimary).setHeaderTextColor(android.R.color.white)
                .buildDatePickerDialog(2020) { pickedYear, pickedMonth, pickedDay -> TODO("Not yet implemented") }.show(childFragmentManager, "")
            profile_modify_account_status -> createDialogPicker(R.string.account_status, listOf("Private", "Public"))
            profile_modify_format_timenote -> createDialogPicker(R.string.timenote_date_format, listOf("X", "Y"))
        }
    }

    private fun createDialogPicker(title: Int, choices: List<String>) {
        DialogPlusBuilder().setTitle(resources.getString(title))
            .setHeaderBgColor(R.color.colorPrimary).setHeaderTextColor(android.R.color.white)
            .hideCloseIcon().blurBackground().buildMultiOptionsDialog(
                choices, object : MultiOptionsDialog.ActionListener() {
                    override fun onActionClicked(clickedOption: String?, position: Int) {

                    }

                }).show(childFragmentManager, "")
    }
}