package com.timenoteco.timenote.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter.POSITION_NONE
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.timenoteco.timenote.view.homeFlow.ScreenSlideTimenoteImageFragment


class ScreenSlideTimenotePagerAdapter(var fa: Fragment, var images: MutableList<String>?, val hideIcons: Boolean) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = images?.size!!

    override fun createFragment(position: Int): Fragment {
        return ScreenSlideTimenoteImageFragment.newInstance(position, images?.get(position), fa, hideIcons)
    }

    override fun getItemViewType(position: Int): Int {
        return POSITION_NONE
    }
}