package com.timenoteco.timenote.view.createTimenoteFlow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.fragment_create_timenote.*

class CreateTimenote : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_create_timenote, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        create_timenote_next_btn.setOnClickListener { findNavController().navigate(CreateTimenoteDirections.actionCreateTimenoteToPreviewTimenoteCreated()) }
    }
}
