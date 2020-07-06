package com.timenoteco.timenote.adapter

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter.POSITION_NONE
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.timenoteco.timenote.common.ScreenSlideTimenoteImageFragment


class ScreenSlidePagerAdapter(var fa: Fragment, var images: MutableList<Bitmap>?) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = images?.size!!

    override fun createFragment(position: Int): Fragment {
        return ScreenSlideTimenoteImageFragment.newInstance(position, images?.get(position), fa)
    }

    override fun getItemViewType(position: Int): Int {
        return POSITION_NONE
    }
}