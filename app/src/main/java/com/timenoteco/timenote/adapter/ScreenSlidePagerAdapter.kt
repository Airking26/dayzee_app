package com.timenoteco.timenote.adapter

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.timenoteco.timenote.common.ScreenSlideTimenoteImageFragment

class ScreenSlidePagerAdapter(fa: FragmentActivity, var images: MutableList<Bitmap>?) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = images?.size!!

    override fun createFragment(position: Int): Fragment {
        return ScreenSlideTimenoteImageFragment.newInstance(position, images?.get(position))
    }

}