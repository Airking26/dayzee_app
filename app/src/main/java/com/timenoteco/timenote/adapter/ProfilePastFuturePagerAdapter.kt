package com.timenoteco.timenote.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.timenoteco.timenote.view.profileFlow.ProfileFutureEvents
import com.timenoteco.timenote.view.profileFlow.ProfilePastEvents


class ProfilePastFuturePagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle,b: Boolean, fragment: Fragment, from: Int): FragmentStateAdapter(fragmentManager, lifecycle) {

    private val profileFutureEvents: ProfileFutureEvents = ProfileFutureEvents.newInstance(b, fragment, from)
    private val profilePastEvents: ProfilePastEvents = ProfilePastEvents.newIstance(b, fragment)

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> profilePastEvents
            1 -> profileFutureEvents
            else -> profileFutureEvents
        }
    }

    fun setShowFilterBar(b: Boolean, position: Int?, isReslected: Boolean?){
        if(isReslected != null && isReslected){
            if(position == 0) profilePastEvents.setShowFilterBar(b)
            else profileFutureEvents.setShowFilterBar(b)
        } else if(isReslected != null && !isReslected){
            if(position == 1){
                profilePastEvents.setShowFilterBar(b)
                profileFutureEvents.setShowFilterBar(b)
            }
        } else {
            if(position == 0) profilePastEvents.setShowFilterBar(b)
            else profileFutureEvents.setShowFilterBar(b)
        }

    }


}