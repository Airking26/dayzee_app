package com.timenoteco.timenote.view.homeFlow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.viewModel.TimenoteViewModel
import kotlinx.android.synthetic.main.fragment_detailed_fragment.*
import kotlinx.android.synthetic.main.item_timenote.view.*

class DetailedTimenote : Fragment(), View.OnClickListener {

    private val timenoteViewModel: TimenoteViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
         inflater.inflate(R.layout.fragment_detailed_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        timenoteViewModel.getSpecificTimenote("").observe(viewLifecycleOwner, Observer {
        })

        Glide
            .with(this)
            .load("https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792")
            .apply(RequestOptions.circleCropTransform())
            .into(detailed_timenote_pic_user)

        detailed_timenote_comment.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            detailed_timenote_comment -> findNavController().navigate(DetailedTimenoteDirections.actionDetailedTimenoteToComments())
        }
    }

}