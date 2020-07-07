package com.timenoteco.timenote.view.createTimenoteFlow

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.OrientationHelper.HORIZONTAL
import com.bumptech.glide.Glide
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ScreenSlidePagerAdapter
import com.timenoteco.timenote.listeners.BackToHomeListener
import com.timenoteco.timenote.viewModel.CreationTimenoteViewModel
import kotlinx.android.synthetic.main.fragment_preview_timenote_created.*

class PreviewTimenoteCreated : Fragment(), View.OnClickListener {

    private lateinit var backToHomeListener: BackToHomeListener
    private val creationTimenoteViewModel: CreationTimenoteViewModel by activityViewModels()
    private lateinit var screenSlidePagerAdapter: ScreenSlidePagerAdapter

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
                screenSlidePagerAdapter = ScreenSlidePagerAdapter(this, it.pic, true)
                preview_created_timenote_vp.adapter = screenSlidePagerAdapter
                //preview_created_timenote_indicator.orientation = LinearLayout.VERTICAL
                preview_created_timenote_indicator.setViewPager(preview_created_timenote_vp)
                screenSlidePagerAdapter.registerAdapterDataObserver(preview_created_timenote_indicator.adapterDataObserver)
            } else {
                if(!it.color.isNullOrBlank()) preview_created_timenote_vp.setBackgroundColor(Color.parseColor(it.color))
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
