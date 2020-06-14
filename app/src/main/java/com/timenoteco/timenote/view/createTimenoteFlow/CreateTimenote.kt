package com.timenoteco.timenote.view.createTimenoteFlow

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
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
            from_label -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                dateTimePicker { dialog, datetime ->

                }
                lifecycleOwner(this@CreateTimenote)
            }
        }
    }
}
