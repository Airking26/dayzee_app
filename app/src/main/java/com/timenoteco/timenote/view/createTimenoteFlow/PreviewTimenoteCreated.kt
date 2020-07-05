package com.timenoteco.timenote.view.createTimenoteFlow

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.timenoteco.timenote.R
import com.timenoteco.timenote.listeners.BackToHomeListener
import com.timenoteco.timenote.viewModel.CreationTimenoteViewModel
import kotlinx.android.synthetic.main.fragment_preview_timenote_created.*

class PreviewTimenoteCreated : Fragment(), View.OnClickListener {

    private lateinit var backToHomeListener: BackToHomeListener
    private val creationTimenoteViewModel: CreationTimenoteViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backToHomeListener = context as BackToHomeListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
         inflater.inflate(R.layout.fragment_preview_timenote_created, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        preview_created_timenote_done_btn.setOnClickListener(this)
        creationTimenoteViewModel.getCreateTimeNoteLiveData().observe(viewLifecycleOwner, Observer {
            if(it.pic != null) {
                Glide
                    .with(this)
                    .load(it.pic)
                    .centerCrop()
                    .into(preview_created_timenote_pic_imageview)
            } else {
                if(!it.color.isNullOrBlank()) preview_created_timenote_pic_imageview.setBackgroundColor(Color.parseColor(it.color))
            }

            preview_created_timenote_title.text = it.title
            preview_created_timenote_year.text = it.year
            preview_created_timenote_day_month.text = it.formatedStartDate
            preview_created_timenote_time.text = it.formatedEndDate
            preview_created_timenote_place.text = it.place
        })



    }

    override fun onClick(v: View?) {
        when(v){
            preview_created_timenote_done_btn -> {
                clearPopAndBackHome()
            }
        }
    }

    private fun clearPopAndBackHome() {
        creationTimenoteViewModel.clear()
        findNavController().popBackStack()
        backToHomeListener.onBackHome()
    }


}
