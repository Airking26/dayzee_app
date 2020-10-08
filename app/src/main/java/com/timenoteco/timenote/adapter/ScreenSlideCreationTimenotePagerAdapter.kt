package com.timenoteco.timenote.adapter

import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter.POSITION_NONE
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.timenoteco.timenote.model.AWSFile
import com.timenoteco.timenote.view.createTimenoteFlow.ScreenSlideCreationTimenoteImageFragment


class ScreenSlideCreationTimenotePagerAdapter(var fa: Fragment, var images: MutableList<String>?, val hideIcons: Boolean, val fromDuplicateOrEdit : Boolean, var pictures: List<String>?) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int =
        if(images.isNullOrEmpty()) pictures?.size!! else images?.size!!

    override fun createFragment(position: Int): Fragment
        = ScreenSlideCreationTimenoteImageFragment.newInstance(position, if(images?.isNullOrEmpty()!!) "" else images?.get(position), fa, hideIcons, fromDuplicateOrEdit, if(pictures.isNullOrEmpty()) "" else pictures?.get(position))

    override fun getItemViewType(position: Int): Int = POSITION_NONE
}