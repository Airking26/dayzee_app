package com.timenoteco.timenote.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.fragment_tab_settings.*

class TabProfile: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tab_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.tab_settings_nav_host_fragment) as NavHostFragment? ?: return
        val navController = navHostFragment.navController
        val appBarConfig = AppBarConfiguration(navController.graph)

        tab_settings_toolbar.setupWithNavController(navController, appBarConfig)
    }
}