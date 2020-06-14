package com.timenoteco.timenote.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ActionProvider
import android.view.View
import androidx.core.view.get
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.timenoteco.timenote.R
import com.timenoteco.timenote.common.setupWithNavController
import com.timenoteco.timenote.viewModel.LoginViewModel
import com.timenoteco.timenote.viewModel.LoginViewModel.AuthenticationState
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        viewModel.authenticationState.observe(this, Observer {
            when (it) {
                AuthenticationState.AUTHENTICATED -> setupController(true)
                AuthenticationState.UNAUTHENTICATED -> findNavController(R.id.nav_host_fragment_main).navigate(
                    R.id.login
                )
                AuthenticationState.INVALID_AUTHENTICATION -> Log.d("", "")
                AuthenticationState.UNAUTHENTICATED_CHOOSED -> setupController(false)
            }
        })
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupController(true)
    }

    private fun setupController(finished: Boolean) {
        var x = finished
        val navGraphIds: List<Int> =
            listOf(
                R.navigation.navigation_graph_tab_home,
                R.navigation.navigation_graph_tab_nearby,
                R.navigation.navigation_graph_tab_search,
                R.navigation.navigation_graph_tab_profile,
                R.navigation.navigation_graph_tab_create_timenote
            )

        val controller = bottomNavView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment_main,
            intent = intent
        )

        controller.observe(this, Observer {
            it.addOnDestinationChangedListener { navController, destination, arguments ->
                if(!x) {
                    bottomNavView.selectedItemId = R.id.navigation_graph_tab_2
                    x = !x
                }
                when (destination.id) {
                    R.id.login -> bottomNavView.visibility = View.GONE
                    R.id.search -> bottomNavView.visibility = View.VISIBLE
                    R.id.nearBy -> bottomNavView.visibility = View.VISIBLE
                    R.id.profile -> bottomNavView.visibility = View.VISIBLE
                    R.id.home -> bottomNavView.visibility = View.VISIBLE
                    R.id.createTimenote -> bottomNavView.visibility = View.GONE
                    R.id.comments -> bottomNavView.visibility = View.GONE
                }
            }

        })

        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }
}
