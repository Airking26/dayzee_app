package com.dayzeeco.dayzee.view.searchFlow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.dayzeeco.dayzee.R
import kotlinx.android.synthetic.main.fragment_tab_nearby.*
import kotlinx.android.synthetic.main.fragment_tab_search.*

class TabSearch: Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_tab_search, container, false)

}