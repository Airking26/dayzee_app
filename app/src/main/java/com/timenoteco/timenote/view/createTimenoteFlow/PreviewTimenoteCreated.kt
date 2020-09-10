package com.timenoteco.timenote.view.createTimenoteFlow

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ScreenSlideCreationTimenotePagerAdapter
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.listeners.BackToHomeListener
import com.timenoteco.timenote.listeners.ExitCreationTimenote
import com.timenoteco.timenote.model.AWSFile
import com.timenoteco.timenote.viewModel.CreationTimenoteViewModel
import kotlinx.android.synthetic.main.fragment_preview_timenote_created.*

class PreviewTimenoteCreated : Fragment(), View.OnClickListener {

    private lateinit var listener : ExitCreationTimenote
    private val creationTimenoteViewModel: CreationTimenoteViewModel by activityViewModels()
    private lateinit var screenSlideCreationTimenotePagerAdapter: ScreenSlideCreationTimenotePagerAdapter
    private var mutableList : MutableList<AWSFile> = mutableListOf()
    private val args : PreviewTimenoteCreatedArgs by navArgs()
    private val utils: Utils = Utils()

    override fun onAttach(context: Context) {
        super.onAttach(context)
         listener = context as ExitCreationTimenote
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_preview_timenote_created, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        preview_created_timenote_done_btn.setOnClickListener(this)
        creationTimenoteViewModel.getCreateTimeNoteLiveData().observe(viewLifecycleOwner, Observer {
            if(it.price == 0){
                if(it.url.isNullOrBlank()) preview_created_timenote_buy.visibility = View.GONE
                else {
                    preview_created_timenote_buy.visibility =View.VISIBLE
                }
            } else if(it.price > 0){
                preview_created_timenote_buy.visibility = View.VISIBLE
                preview_created_timenote_buy.text = """${it.price.toString()}$"""
            } else {
                preview_created_timenote_buy.visibility =View.GONE
            }
            if(it.pictures?.size == 1) preview_created_timenote_indicator.visibility = View.GONE
            if(it.pictures != null) {
                for(pic in it.pictures!!){
                    mutableList.add(AWSFile(Uri.parse(pic), null))
                }
                screenSlideCreationTimenotePagerAdapter = ScreenSlideCreationTimenotePagerAdapter(this, mutableList, true)
                preview_created_timenote_vp.adapter = screenSlideCreationTimenotePagerAdapter
                preview_created_timenote_indicator.setViewPager(preview_created_timenote_vp)
                screenSlideCreationTimenotePagerAdapter.registerAdapterDataObserver(preview_created_timenote_indicator.adapterDataObserver)
            } else {
                if(!it.colorHex.isNullOrBlank()) preview_created_timenote_vp.setBackgroundColor(Color.parseColor(it.colorHex))
            }

            preview_created_timenote_title.text = it.title
           if(!it.startingAt.isBlank()) preview_created_timenote_year.text = utils.setYear(it.startingAt)
            if(!it.startingAt.isBlank() && !it.endingAt.isBlank()) preview_created_timenote_day_month.text = utils.setFormatedStartDate(it.startingAt, it.endingAt)
            if(!it.startingAt.isBlank() && !it.endingAt.isBlank())preview_created_timenote_time.text = utils.setFormatedEndDate(it.startingAt, it.endingAt)
           if(it.location != null) preview_created_timenote_place.text = it.location?.address?.address
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
        listener.onDone(args.from)
    }


}