package com.timenoteco.timenote.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.*
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.auth.BasicSessionCredentials
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.R
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.common.setupWithNavController
import com.timenoteco.timenote.listeners.BackToHomeListener
import com.timenoteco.timenote.listeners.ExitCreationTimenote
import com.timenoteco.timenote.listeners.RefreshPicBottomNavListener
import com.timenoteco.timenote.listeners.ShowBarListener
import com.timenoteco.timenote.model.FCMDTO
import com.timenoteco.timenote.model.TimenoteInfoDTO
import com.timenoteco.timenote.model.UserInfoDTO
import com.timenoteco.timenote.view.homeFlow.DetailedTimenoteArgs
import com.timenoteco.timenote.view.homeFlow.DetailedTimenoteDirections
import com.timenoteco.timenote.view.homeFlow.Home
import com.timenoteco.timenote.view.homeFlow.HomeDirections
import com.timenoteco.timenote.view.searchFlow.ProfileSearchDirections
import com.timenoteco.timenote.viewModel.LoginViewModel
import com.timenoteco.timenote.viewModel.MeViewModel
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.BranchError
import io.branch.referral.util.BranchEvent
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.reflect.Type

class MainActivity : AppCompatActivity(), BackToHomeListener, Home.OnGoToNearby, ShowBarListener, ExitCreationTimenote, RefreshPicBottomNavListener {

    private lateinit var control: NavController
    private val CHANNEL_ID: String = "dayzee_channel"
    private var currentNavController: LiveData<NavController>? = null
    private val utils = Utils()
    private lateinit var prefs : SharedPreferences
    private val meViewModel : MeViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        setupController()
    }

    override fun onStart() {
        super.onStart()
        Branch.sessionBuilder(this).withCallback { referringParams, error ->
            if (error == null) {
                if (referringParams?.getBoolean("+clicked_branch_link")!!) {
                    val typeTimenoteInfo: Type = object : TypeToken<TimenoteInfoDTO?>() {}.type
                    val timenoteInfoDTO = Gson().fromJson<TimenoteInfoDTO>(referringParams.getString("timenoteInfoDTO"), typeTimenoteInfo)
                    control.navigate(HomeDirections.actionGlobalDetailedTimenote(1, timenoteInfoDTO))
                }
            }
        }.withData(this.intent?.data).init()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
        Branch.sessionBuilder(this).withCallback{ referringParams, error ->
            if (error == null) {
                Log.i("BRANCH SDK START", referringParams.toString())
                if (referringParams?.getBoolean("+clicked_branch_link")!!) {
                    val typeTimenoteInfo: Type = object : TypeToken<TimenoteInfoDTO?>() {}.type
                    val timenoteInfoDTO = Gson().fromJson<TimenoteInfoDTO>(referringParams.getString("timenoteInfoDTO"), typeTimenoteInfo)
                    control.navigate(HomeDirections.actionGlobalDetailedTimenote(1, timenoteInfoDTO))
                }
            } else {
                Log.e("BRANCH SDK START", error.message)
            }
        }.reInit()
    }

    @SuppressLint("StringFormatInvalid")
    fun retrieveCurrentRegistrationToken(){
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val token = task.result?.token
                    if (prefs.getString("TOKEN", null) != null)
                        meViewModel.putFCMToken(prefs.getString("TOKEN", null)!!, FCMDTO(token!!))
                    return@OnCompleteListener
                }
            })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupController()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupController() {

        createNotificationChannel()
        val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
        val userInfoDTO = Gson().fromJson<UserInfoDTO>(prefs.getString("UserInfoDTO", ""), typeUserInfo)

        retrieveCurrentRegistrationToken()
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }

        val navGraphIds: List<Int> =
            listOf(
                R.navigation.navigation_graph_tab_home,
                R.navigation.navigation_graph_tab_search,
                R.navigation.navigation_graph_tab_profile,
                R.navigation.navigation_graph_tab_nearby,
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
            bottomNavView.menu[4].iconTintList = null
            bottomNavView.menu[4].iconTintMode = null
        }

        setPicBottomNav(userInfoDTO)

        controller.observe(this, Observer {
            it.addOnDestinationChangedListener { navController, destination, arguments ->

                control = navController
                val u = destination

                when (destination.id) {
                    R.id.login -> {
                        utils.hideStatusBar(this)
                        bottomNavView.visibility = View.GONE
                    }
                    R.id.preferenceCategory -> {
                        utils.showStatusBar(this)
                        bottomNavView.visibility = View.GONE
                    }
                    R.id.preferenceSubCategory -> {
                        utils.showStatusBar(this)
                        bottomNavView.visibility = View.GONE
                    }
                    R.id.preferenceSuggestion -> {
                        utils.showStatusBar(this)
                        bottomNavView.visibility = View.GONE
                    }
                    R.id.search -> {
                        utils.showStatusBar(this)
                        bottomNavView.visibility = View.VISIBLE
                    }
                    R.id.nearBy -> {
                        utils.hideStatusBar(this)
                        bottomNavView.visibility = View.VISIBLE
                    }
                    R.id.nearbyFilters -> {
                        bottomNavView.visibility = View.VISIBLE
                        utils.showStatusBar(this)
                    }
                    R.id.profile -> {
                        utils.showStatusBar(this)
                        bottomNavView.visibility = View.VISIBLE
                    }
                    R.id.createTimenoteSearch ->{
                        utils.showStatusBar(this)
                        bottomNavView.visibility = View.GONE
                    }
                    R.id.detailedTimenoteSearch -> {
                        utils.showStatusBar(this)
                        bottomNavView.visibility = View.GONE
                    }
                    R.id.profileSearch -> {
                        utils.showStatusBar(this)
                        bottomNavView.visibility = View.VISIBLE
                    }
                    R.id.profileCalendarSearch -> {
                        utils.hideStatusBar(this)
                        bottomNavView.visibility = View.GONE
                    }
                    R.id.profileCalendar -> {
                        utils.hideStatusBar(this)
                        bottomNavView.visibility = View.GONE
                    }
                    R.id.settings -> {
                        utils.showStatusBar(this)
                        bottomNavView.visibility = View.GONE
                    }
                    R.id.home -> {
                        bottomNavView.visibility = View.VISIBLE
                        utils.showStatusBar(this)
                    }
                    R.id.createTimenote -> {
                        utils.showStatusBar(this)
                        bottomNavView.visibility = View.GONE
                    }
                    R.id.profilModify -> {
                        bottomNavView.visibility = View.VISIBLE
                        utils.showStatusBar(this)
                    }
                    R.id.detailedTimenote -> {
                        bottomNavView.visibility = View.GONE
                        utils.showStatusBar(this)
                    }
                }
            }

        })
        currentNavController = controller
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setPicBottomNav(userInfoDTO: UserInfoDTO?) {
        if (userInfoDTO != null || !userInfoDTO?.picture.isNullOrBlank()) {
            Glide
                .with(this)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .load(userInfoDTO?.picture)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.circle_pic)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        bottomNavView.menu[4].icon = BitmapDrawable(resources, resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        } else bottomNavView.menu[4].icon = getDrawable(R.drawable.circle_pic)
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onBackHome() {
        bottomNavView.selectedItemId = R.id.navigation_graph_tab_1
    }

    override fun onGuestMode() {
        bottomNavView.selectedItemId = R.id.navigation_graph_tab_2
    }

    override fun onBarAskedToShow() {
        bottomNavView.visibility = View.VISIBLE
    }

    override fun onDone(from: Int) {
        when(from){
            0 -> bottomNavView.selectedItemId = R.id.navigation_graph_tab_1
            1 -> bottomNavView.selectedItemId = R.id.navigation_graph_tab_1
            2 -> bottomNavView.selectedItemId = R.id.navigation_graph_tab_3
            3 -> bottomNavView.selectedItemId = R.id.navigation_graph_tab_2
            4 -> bottomNavView.selectedItemId = R.id.navigation_graph_tab_4

        }
    }

    override fun onrefreshPicBottomNav(userInfoDTO: UserInfoDTO?) {
        setPicBottomNav(userInfoDTO)
    }

}
