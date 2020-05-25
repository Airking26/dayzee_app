package com.timenoteco.timenote.view.host

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.navArgs
import androidx.navigation.ui.NavigationUI
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ViewPagerAdapter
import com.timenoteco.timenote.view.LoginArgs
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var isFromLoginPage: Boolean = false
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private val args: LoginArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        displayMainOrLoginScreen()
    }

    private fun displayMainOrLoginScreen() {
        if (intent.extras != null)
            isFromLoginPage = args.isFromLoginPage

        if (isFromLoginPage) {
            setContentView(R.layout.activity_main)
            viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
            viewPager.adapter = viewPagerAdapter

            navigateBottomNavView()

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    selectPage(position)
                }
            })
        } else {
            setContentView(R.layout.activity_login)
        }
    }

    private fun navigateBottomNavView() {
        bottomNavView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profileId -> {
                    viewPager.currentItem = 0
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nearById -> {
                    viewPager.currentItem = 1
                    return@OnNavigationItemSelectedListener true
                }
                R.id.searchId -> {
                    viewPager.currentItem = 2
                    return@OnNavigationItemSelectedListener true
                }
                R.id.settingsId -> {
                    viewPager.currentItem = 3
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    private fun selectPage(position: Int) {
        when (position) {
            0 -> bottomNavView.menu.findItem(R.id.profileId).isChecked = true
            1 -> bottomNavView.menu.findItem(R.id.nearById).isChecked = true
            2 -> bottomNavView.menu.findItem(R.id.searchId).isChecked = true
            3 -> bottomNavView.menu.findItem(R.id.settingsId).isChecked = true
        }
        viewPager.currentItem = position
    }
}
