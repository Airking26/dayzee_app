package com.timenoteco.timenote.garbage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_pager.*


/*class Pager : Fragment() {

    lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPagerAdapter = ViewPagerAdapter(parentFragmentManager, lifecycle)
        viewPager.adapter = viewPagerAdapter
        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectPage(position)
            }
        })

        bottomNavView.setOnNavigationItemSelectedListener {
            selectPageBottomNavView(it.itemId)
        }
    }

    private fun selectPageBottomNavView(itemId: Int): Boolean {
        when(itemId){
            R.id.profileId -> selectPage(0)
            R.id.nearById -> selectPage(1)
        }
        return true
    }

    private fun selectPage(position: Int) {
        when (position) {
            0 -> bottomNavView.menu.findItem(R.id.profileId).isChecked = true
            1 -> bottomNavView.menu.findItem(R.id.nearById).isChecked = true
        }
        viewPager.currentItem = position
    }

}
*/