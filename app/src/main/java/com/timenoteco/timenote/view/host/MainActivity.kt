package com.timenoteco.timenote.view.host

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.navArgs
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.timenoteco.timenote.R
import com.timenoteco.timenote.garbage.setupWithNavController
import com.timenoteco.timenote.view.LoginArgs

class MainActivity : AppCompatActivity(){

    private var isFromLoginPage: Boolean = false
    private val args: LoginArgs by navArgs()
    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBottomNavigationBar()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    private fun displayMainOrLoginScreen() {
        if (intent.extras != null)
            isFromLoginPage = args.isFromLoginPage
    }

    private fun setupBottomNavigationBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavView)

        val navGraphIds = listOf(R.navigation.navigation_graph_tab_home, R.navigation.navigation_graph_tab_nearby, R.navigation.navigation_graph_tab_search, R.navigation.navigation_graph_tab_profile)

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment_main,
            intent = intent
        )

        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }
}
