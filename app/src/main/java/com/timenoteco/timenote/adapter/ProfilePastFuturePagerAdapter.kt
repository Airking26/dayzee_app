package com.timenoteco.timenote.adapter

import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.timenoteco.timenote.view.profileFlow.ProfileFutureEvents


class ProfilePastFuturePagerAdapter(fragment: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragment, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> ProfileFutureEvents()
            1 -> ProfileFutureEvents()
            else -> ProfileFutureEvents()
        }
    }

}