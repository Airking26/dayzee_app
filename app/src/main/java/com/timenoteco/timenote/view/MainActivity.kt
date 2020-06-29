package com.timenoteco.timenote.view

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.timenoteco.timenote.R
import com.timenoteco.timenote.common.setupWithNavController
import com.timenoteco.timenote.listeners.BackToHomeListener
import com.timenoteco.timenote.viewModel.LoginViewModel
import com.timenoteco.timenote.viewModel.LoginViewModel.AuthenticationState
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BackToHomeListener {

    private lateinit var viewModel: LoginViewModel
    private var currentNavController: LiveData<NavController>? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        viewModel.authenticationState.observe(this, Observer {
            when (it) {
                AuthenticationState.AUTHENTICATED -> setupController(true)
                AuthenticationState.UNAUTHENTICATED -> findNavController(R.id.nav_host_fragment_main).navigate(R.id.login)
                AuthenticationState.INVALID_AUTHENTICATION -> Log.d("", "")
                AuthenticationState.UNAUTHENTICATED_CHOOSED -> setupController(false)
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupController(true)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupController(finished: Boolean) {

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

        bottomNavView.itemIconTintList = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bottomNavView.menu[2].iconTintList = null
            bottomNavView.menu[4].iconTintList = null
            bottomNavView.menu[2].iconTintMode = null
            bottomNavView.menu[4].iconTintMode = null
        }

        bottomNavView.menu[2].icon = resources.getDrawable(R.drawable.logo)
        if(!finished) bottomNavView.selectedItemId = R.id.navigation_graph_tab_2

        Glide
            .with(this)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .load("https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792")
            .apply(RequestOptions.circleCropTransform())
            .into(object: CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bottomNavView.menu[4].icon = BitmapDrawable(resources, resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })

        controller.observe(this, Observer {
            it.addOnDestinationChangedListener { navController, destination, arguments ->
                when (destination.id) {
                    R.id.login -> {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        bottomNavView.visibility = View.GONE
                    }
                    R.id.search -> {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        bottomNavView.visibility = View.VISIBLE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    }
                    R.id.nearBy -> {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        bottomNavView.visibility = View.VISIBLE
                    }
                    R.id.nearbyFilters -> {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        bottomNavView.visibility = View.VISIBLE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    }
                    R.id.profile -> {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        bottomNavView.visibility = View.VISIBLE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    }
                    R.id.profileCalendar -> {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        bottomNavView.visibility = View.GONE
                    }
                    R.id.settings -> {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        bottomNavView.visibility = View.GONE
                    }
                    R.id.home -> {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        bottomNavView.visibility = View.VISIBLE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    }
                    R.id.createTimenote -> {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        bottomNavView.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    }
                    R.id.comments -> {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.STATUS_BAR_VISIBLE
                        bottomNavView.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    }
                }
            }

        })

        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onBackHome() {
        bottomNavView.selectedItemId = R.id.navigation_graph_tab_1
    }


}
