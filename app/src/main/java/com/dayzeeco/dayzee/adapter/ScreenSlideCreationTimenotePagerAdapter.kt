package com.dayzeeco.dayzee.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter.POSITION_NONE
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dayzeeco.dayzee.view.createTimenoteFlow.ScreenSlideCreationTimenoteImageFragment
import java.io.File


class ScreenSlideCreationTimenotePagerAdapter(var fa: Fragment, var images: MutableList<File>?, val hideIcons: Boolean, val fromDuplicateOrEdit : Boolean, var pictures: List<String>?) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int =
        if(images.isNullOrEmpty()) pictures?.size!! else images?.size!!

    override fun createFragment(position: Int): Fragment = ScreenSlideCreationTimenoteImageFragment.newInstance(position, if(images?.isNullOrEmpty()!!) "" else transformFileToUri(images!![position]), fa, hideIcons, fromDuplicateOrEdit, if(pictures.isNullOrEmpty()) "" else pictures?.get(position))

    override fun getItemViewType(position: Int): Int = POSITION_NONE

    private fun transformFileToUri(file: File): String{
        return "file://${file.absolutePath}"
    }
}