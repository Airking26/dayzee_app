package com.timenoteco.timenote.view.createTimenoteFlow

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.noowenz.customdatetimepicker.CustomDateTimePicker
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.fragment_create_timenote.*
import java.util.*

class CreateTimenote : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_create_timenote, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        create_timenote_next_btn.setOnClickListener(this)
        from_label.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            create_timenote_next_btn ->findNavController().navigate(CreateTimenoteDirections.actionCreateTimenoteToPreviewTimenoteCreated())
            from_label -> CustomDateTimePicker(requireActivity(), object: CustomDateTimePicker.ICustomDateTimeListener{
                override fun onCancel() {}
                override fun onSet(dialog: Dialog, calendarSelected: Calendar, dateSelected: Date, year: Int,
                                   monthFullName: String, monthShortName: String, monthNumber: Int, day: Int,
                                   weekDayFullName: String, weekDayShortName: String, hour24: Int, hour12: Int,
                                   min: Int, sec: Int, AM_PM: String) {

                }

            }).apply {
                setDate(Calendar.getInstance())
                showDialog()
            }
        }
    }
}
