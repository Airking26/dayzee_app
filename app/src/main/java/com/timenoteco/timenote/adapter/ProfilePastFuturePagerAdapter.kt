package com.timenoteco.timenote.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.timenoteco.timenote.view.profileFlow.ProfileFutureEvents

class ProfilePastFuturePagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> ProfileFutureEvents()
            1 -> ProfileFutureEvents()
            else -> ProfileFutureEvents()
        }
    }
}