package com.dayzeeco.dayzee.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dayzeeco.dayzee.view.homeFlow.TabHome
import com.dayzeeco.dayzee.view.nearByFlow.TabNearby
import com.dayzeeco.dayzee.view.profileFlow.TabProfile
import com.dayzeeco.dayzee.view.searchFlow.*

class SearchViewPeopleTagPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val searchPeople = SearchPeople()
    private val searchTag = SearchTag()

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
                0 -> searchPeople
                1 -> searchTag
                else -> searchPeople
        }
    }

}