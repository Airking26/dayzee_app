package com.timenoteco.timenote.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.timenoteco.timenote.view.homeFlow.TabHome
import com.timenoteco.timenote.view.nearByFlow.TabNearby
import com.timenoteco.timenote.view.searchFlow.TabSearch
import com.timenoteco.timenote.view.profileFlow.TabProfile
import com.timenoteco.timenote.view.searchFlow.SearchExplore
import com.timenoteco.timenote.view.searchFlow.SearchTop

class SearchViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> SearchTop()
            1 -> SearchExplore()
            else -> SearchTop()
        }
    }
}