package com.timenoteco.timenote.view.createTimenoteFlow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.fragment_preview_timenote_created.*
import kotlinx.android.synthetic.main.fragment_profile.*

class PreviewTimenoteCreated : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
         inflater.inflate(R.layout.fragment_preview_timenote_created, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        preview_created_timenote_toolbar.elevation = 5.0f
        Glide
            .with(this)
            .load("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg")
            .centerCrop()
            .into(preview_created_timenote_pic_imageview)
    }


}
