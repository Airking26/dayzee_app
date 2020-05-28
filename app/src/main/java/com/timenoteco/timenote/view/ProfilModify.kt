package com.timenoteco.timenote.view

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
import com.dialog.plus.ui.DatePickerDialog
import com.dialog.plus.ui.DialogPlus
import com.dialog.plus.ui.DialogPlusBuilder
import com.dialog.plus.ui.MultiOptionsDialog
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.fragment_profil_modify.*


class ProfilModify: Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_profil_modify, container, false)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        profile_modify_gender.setOnClickListener(this)
        profile_modify_birthday.setOnClickListener(this)

        //profile_modify_done_btn.setOnClickListener {view.findNavController().navigate(ProfilModifyDirections.actionProfilModifyToProfile())}
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    view?.findNavController()?.popBackStack(R.id.profile, false)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onClick(v: View?) {
        when(v){
            profile_modify_gender -> DialogPlusBuilder().setTitle(resources.getString(R.string.gender)).setHeaderBgColor(R.color.colorPrimary).
                    setHeaderTextColor(android.R.color.white).hideCloseIcon().blurBackground().buildMultiOptionsDialog(
                listOf("Male", "Female", "None"), object: MultiOptionsDialog.ActionListener() {
                override fun onActionClicked(clickedOption: String?, position: Int) {
                }

            }).show(childFragmentManager, "")
            profile_modify_birthday -> DialogPlusBuilder().blurBackground().setHeaderBgColor(R.color.colorPrimary).setHeaderTextColor(android.R.color.white)
                .buildDatePickerDialog(2020) { pickedYear, pickedMonth, pickedDay -> TODO("Not yet implemented") }.show(childFragmentManager, "")
        }
    }
}