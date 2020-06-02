package com.timenoteco.timenote.view.nearByFlow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.fragment_tab_nearby.*

class TabNearby: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tab_nearby, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.tab_nearby_nav_host_fragment) as NavHostFragment? ?: return
        val navController = navHostFragment.navController
        val appBarConfig = AppBarConfiguration(navController.graph)

        tab_nearby_toolbar.setupWithNavController(navController, appBarConfig)
    }
}