package com.timenoteco.timenote.view.host

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.multidex.MultiDex
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.navArgs
import androidx.navigation.plusAssign
import androidx.navigation.ui.NavigationUI
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ViewPagerAdapter
import com.timenoteco.timenote.garbage.KeepStateNavigator
import com.timenoteco.timenote.garbage.setupWithNavController
import com.timenoteco.timenote.view.LoginArgs
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_tab_profile.*

class MainActivity : AppCompatActivity() {

    private var currentNavController: LiveData<NavController>? = null
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
                R.id.navigation_graph_tab_1 -> {
                    viewPager.currentItem = 0
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_graph_tab_2 -> {
                    viewPager.currentItem = 1
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_graph_tab_3 -> {
                    viewPager.currentItem = 2
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_graph_tab_4 -> {
                    viewPager.currentItem = 3
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    private fun selectPage(position: Int) {
        when (position) {
            0 -> bottomNavView.menu.findItem(R.id.navigation_graph_tab_1).isChecked = true
            1 -> bottomNavView.menu.findItem(R.id.navigation_graph_tab_2).isChecked = true
            2 -> bottomNavView.menu.findItem(R.id.navigation_graph_tab_3).isChecked = true
            3 -> bottomNavView.menu.findItem(R.id.navigation_graph_tab_4).isChecked = true
        }
        viewPager.currentItem = position
    }

    override fun onSupportNavigateUp() =
        currentNavController?.value?.navigateUp() ?: false
}
