package com.timenoteco.timenote.view.createTimenoteFlow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.fragment_tab_create_timenote.*
import kotlinx.android.synthetic.main.fragment_tab_home.*

class TabCreateTimenote: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_tab_create_timenote, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHostFragment = childFragmentManager.findFragmentById(R.id.tab_create_timenote_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfig = AppBarConfiguration(navController.graph)
        tab_create_timenote_toolbar.setupWithNavController(navController, appBarConfig)
    }

}