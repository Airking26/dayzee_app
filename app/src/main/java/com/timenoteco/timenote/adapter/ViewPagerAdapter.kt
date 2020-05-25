package com.timenoteco.timenote.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.timenoteco.timenote.view.TabHome
import com.timenoteco.timenote.view.TabNearby
import com.timenoteco.timenote.view.TabSearch
import com.timenoteco.timenote.view.TabSettings

class ViewPagerAdapter(fm: FragmentManager?, lifecycle: Lifecycle) : FragmentStateAdapter(fm!!, lifecycle) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> TabHome()
            1 -> TabNearby()
            2 -> TabSearch()
            3 -> TabSettings()
            else -> TabHome()
        }
    }
}