package com.timenoteco.timenote.view.createTimenoteFlow

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ScreenSlideCreationTimenotePagerAdapter
import com.timenoteco.timenote.listeners.BackToHomeListener
import com.timenoteco.timenote.model.StatusTimenote
import com.timenoteco.timenote.viewModel.CreationTimenoteViewModel
import kotlinx.android.synthetic.main.fragment_preview_timenote_created.*

class PreviewTimenoteCreated : Fragment(), View.OnClickListener {

    private lateinit var backToHomeListener: BackToHomeListener
    private val creationTimenoteViewModel: CreationTimenoteViewModel by activityViewModels()
    private lateinit var screenSlideCreationTimenotePagerAdapter: ScreenSlideCreationTimenotePagerAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backToHomeListener = context as BackToHomeListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
         inflater.inflate(R.layout.fragment_preview_timenote_created, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        preview_created_timenote_done_btn.setOnClickListener(this)
        creationTimenoteViewModel.getCreateTimeNoteLiveData().observe(viewLifecycleOwner, Observer {
            if(it.status == StatusTimenote.FREE){
                if(it.url.isNullOrBlank()) preview_created_timenote_buy.visibility = View.GONE
                else {
                    preview_created_timenote_buy.visibility =View.VISIBLE
                }
            } else if(it.status == StatusTimenote.PAID){
                preview_created_timenote_buy.visibility = View.VISIBLE
                preview_created_timenote_buy.text = """${it.price.toString()}$"""
            } else {
                preview_created_timenote_buy.visibility =View.GONE
            }
            if(it.pic?.size == 1) preview_created_timenote_indicator.visibility = View.GONE
            if(it.pic != null) {
                screenSlideCreationTimenotePagerAdapter = ScreenSlideCreationTimenotePagerAdapter(this, it.pic, true)
                preview_created_timenote_vp.adapter = screenSlideCreationTimenotePagerAdapter
                preview_created_timenote_indicator.setViewPager(preview_created_timenote_vp)
                screenSlideCreationTimenotePagerAdapter.registerAdapterDataObserver(preview_created_timenote_indicator.adapterDataObserver)
            } else {
                if(!it.color.isNullOrBlank()) preview_created_timenote_vp.setBackgroundColor(Color.parseColor(it.color))
            }
            /*if(it.format == 0){
                preview_created_timenote_date_ll.setPadding(((resources.displayMetrics.density * 38) + 0.5f).toInt(), 0, 0, ((resources.displayMetrics.density * 8) + 0.5f).toInt())
            } else preview_created_timenote_date_ll.setPadding(((resources.displayMetrics.density * 18) + 0.5f).toInt(), 0, 0, ((resources.displayMetrics.density * 8) + 0.5f).toInt())*/


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
