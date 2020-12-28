package com.timenoteco.timenote.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.timenoteco.timenote.view.profileFlow.ProfileEvents


class ProfileEventPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, b: Boolean, fragment: Fragment, from: Int, id: String, onMyProfile: Boolean): FragmentStateAdapter(fragmentManager, lifecycle) {

    private val profileFutureEvents: ProfileEvents = ProfileEvents.newInstance(b, fragment, from, id, true, onMyProfile)
    private val profilePastEvents: ProfileEvents = ProfileEvents.newInstance(b, fragment, from, id, false, onMyProfile)

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> profilePastEvents
            1 -> profileFutureEvents
            else -> profileFutureEvents
        }
    }

    fun setShowFilterBar(showFilterBar: Boolean, position: Int?, isReselected: Boolean?){
            if(isReselected != null && isReselected){
                if(position == 0) profilePastEvents.setShowFilterBar(showFilterBar)
                else profileFutureEvents.setShowFilterBar(showFilterBar)
            } else if(isReselected != null && !isReselected){
                if(position == 0)profilePastEvents.setShowFilterBar(showFilterBar)
                else profileFutureEvents.setShowFilterBar(showFilterBar)
            }
    }

}