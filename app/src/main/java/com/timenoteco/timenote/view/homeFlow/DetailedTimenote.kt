package com.timenoteco.timenote.view.homeFlow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.fragment_detailed_fragment.*

class DetailedTimenote : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
         inflater.inflate(R.layout.fragment_detailed_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        detailed_timenote_comment.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            detailed_timenote_comment -> findNavController().navigate(DetailedTimenoteDirections.actionDetailedTimenoteToComments())
        }
    }

}