package com.timenoteco.timenote.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.navArgs
import com.timenoteco.timenote.R
import com.timenoteco.timenote.common.setupWithNavController
import com.timenoteco.timenote.view.loginFlow.LoginArgs
import kotlinx.android.synthetic.main.activity_main.*

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
        val navGraphIds = listOf(R.navigation.navigation_graph_tab_home, R.navigation.navigation_graph_tab_nearby,
            R.navigation.navigation_graph_tab_search, R.navigation.navigation_graph_tab_profile, R.navigation.navigation_graph_tab_create_timenote)

        val controller = bottomNavView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment_main,
            intent = intent
        )

        controller.observe(this, Observer {
            it.addOnDestinationChangedListener { _, destination, _ ->
                when(destination.id){
                    R.id.search -> bottomNavView.visibility = View.GONE
                    R.id.nearBy -> bottomNavView.visibility = View.VISIBLE
                    R.id.profile -> bottomNavView.visibility = View.VISIBLE
                    R.id.home -> bottomNavView.visibility = View.VISIBLE
                    R.id.createTimenote -> bottomNavView.visibility = View.VISIBLE

                }
            }
        })

        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }
}
