package com.timenoteco.timenote.view.createTimenoteFlow

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.listeners.BackToHomeListener
import kotlinx.android.synthetic.main.fragment_preference_sub_category.*
import kotlinx.android.synthetic.main.fragment_preview_timenote_created.*
import kotlinx.android.synthetic.main.fragment_profile.*

class PreviewTimenoteCreated : Fragment(), View.OnClickListener {

    private lateinit var backToHomeListener: BackToHomeListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backToHomeListener = context as BackToHomeListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
         inflater.inflate(R.layout.fragment_preview_timenote_created, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        preview_created_timenote_done_btn.setOnClickListener(this)

        Glide
            .with(this)
            .load("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg")
            .centerCrop()
            .into(preview_created_timenote_pic_imageview)
    }

    override fun onClick(v: View?) {
        when(v){
            preview_created_timenote_done_btn -> {
                clearPopAndBackHome()
            }
        }
    }

    private fun clearPopAndBackHome() {
        findNavController().popBackStack()
        backToHomeListener.onBackHome()
    }


}
