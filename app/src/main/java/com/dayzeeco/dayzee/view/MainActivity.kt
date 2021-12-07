package com.dayzeeco.dayzee.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.*
import com.dayzeeco.dayzee.listeners.*
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.view.homeFlow.Home
import com.dayzeeco.dayzee.view.homeFlow.HomeDirections
import com.dayzeeco.dayzee.view.loginFlow.SignupDirections
import com.dayzeeco.dayzee.viewModel.SwitchToNotifViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.branch.referral.Branch
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.Type


class MainActivity : AppCompatActivity(), BackToHomeListener, Home.OnGoToNearby, ShowBarListener, ExitCreationTimenote, RefreshPicBottomNavListener, GoToProfile, GoToTop {

    private lateinit var control: NavController
    private var currentNavController: LiveData<NavController>? = null
    private val utils = Utils()
    private lateinit var receiver: BroadcastReceiver
    private lateinit var prefs : SharedPreferences

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        registerNotificationReceiver()
        setupController()
    }


    private fun registerNotificationReceiver() {
        val filter = IntentFilter()
        filter.addAction("NotificationOnClickListener")
        this.receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val type = intent?.getIntExtra("type", 0)
                if(type != 1){
                    if(type == 0 || type == 6) control.navigate(HomeDirections.actionGlobalDetailedTimenote(1, intent.getParcelableExtra("event")))
                    else control.navigate(HomeDirections.actionGlobalProfileElse(1).setUserInfoDTO(intent?.getParcelableExtra("user")))
                }
            }
        }
        super.registerReceiver(this.receiver, filter)
    }

    override fun onStart() {
        super.onStart()
        Branch.sessionBuilder(this).withCallback { referringParams, error ->
            if (error == null) {
                if (referringParams?.getBoolean("+clicked_branch_link")!!) {
                    when {
                        referringParams.has(timenote_info_dto) -> {
                            val typeTimenoteInfo: Type = object : TypeToken<TimenoteInfoDTO?>() {}.type
                            val timenoteInfoDTO = Gson().fromJson<TimenoteInfoDTO>(
                                referringParams.getString(timenote_info_dto),
                                typeTimenoteInfo
                            )
                            control.navigate(
                                HomeDirections.actionGlobalDetailedTimenote(
                                    1,
                                    timenoteInfoDTO
                                )
                            )
                        }
                        referringParams.has(user_info_dto) -> {
                            val typeUserInfoDTO: Type = object : TypeToken<UserInfoDTO?>() {}.type
                            val userInfoDTO = Gson().fromJson<UserInfoDTO>(
                                referringParams.getString(
                                    user_info_dto
                                ), typeUserInfoDTO
                            )
                            goToProfile()
                            control.navigate(
                                HomeDirections.actionGlobalProfileElse(1).setUserInfoDTO(
                                    userInfoDTO
                                )
                            )
                        }
                        referringParams.has("accessToken") -> {
                            val at = referringParams.getString("accessToken")
                            if(prefs.getString(accessTokenReferringParam, null) != at && prefs.getBoolean(
                                    already_signed_in, false)) {
                                prefs.edit().putString(accessTokenReferringParam, at).apply()
                                control.navigate(SignupDirections.actionGlobalChangePassword(at))
                            }

                        }
                    }
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
                    when {
                        referringParams.has(timenote_info_dto) -> {
                            val typeTimenoteInfo: Type = object : TypeToken<TimenoteInfoDTO?>() {}.type
                            val timenoteInfoDTO = Gson().fromJson<TimenoteInfoDTO>(
                                referringParams.getString(
                                    timenote_info_dto
                                ), typeTimenoteInfo
                            )
                            control.navigate(
                                HomeDirections.actionGlobalDetailedTimenote(
                                    1,
                                    timenoteInfoDTO
                                )
                            )
                        }
                        referringParams.has(user_info_dto) -> {
                            val typeUserInfoDTO: Type = object : TypeToken<UserInfoDTO?>() {}.type
                            val userInfoDTO = Gson().fromJson<UserInfoDTO>(
                                referringParams.getString(user_info_dto),
                                typeUserInfoDTO
                            )
                            control.navigate(
                                HomeDirections.actionGlobalProfileElse(1).setUserInfoDTO(
                                    userInfoDTO
                                )
                            )
                            goToProfile()
                        }
                        referringParams.has("accessToken") -> {
                            val at = referringParams.getString("accessToken")
                            if(prefs.getString(accessTokenReferringParam, null) != at && prefs.getBoolean(
                                    already_signed_in, false)){
                                prefs.edit().putString(accessTokenReferringParam, at).apply()
                                control.navigate(SignupDirections.actionGlobalChangePassword(at))
                            }
                        }
                    }
                }
            } else {
                Log.e("BRANCH SDK START", error.message)
            }
        }.reInit()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupController()
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setupController()
        }
        if(!intent.getStringExtra(type).isNullOrBlank()){
            goToProfile()
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return SwitchToNotifViewModel() as T
                }
            })[SwitchToNotifViewModel::class.java].switchNotif(true)
        } else if(intent.hasExtra("USER")){
            control.navigate(
                HomeDirections.actionGlobalProfileElse(1).setUserInfoDTO(
                    intent.getParcelableExtra(
                        "USER"
                    )
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupController() {
        createNotificationChannel()
        val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
        val userInfoDTO = Gson().fromJson<UserInfoDTO>(
            prefs.getString(user_info_dto, ""),
            typeUserInfo
        )

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

        if(userInfoDTO != null) setPicBottomNav(userInfoDTO.picture)

        controller.observe(this, Observer {
            it.addOnDestinationChangedListener { navController, destination, _ ->

                control = navController
                when (destination.id) {
                    R.id.signup -> {
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
                    R.id.myProfile -> {
                        utils.showStatusBar(this)
                        bottomNavView.visibility = View.VISIBLE
                    }
                    R.id.profileElse -> {
                        utils.showStatusBar(this)
                        bottomNavView.visibility = View.VISIBLE
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
            val channel = NotificationChannel(channel_id, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setPicBottomNav(picture: String?) {
        if (!picture.isNullOrBlank()) {
            Glide
                .with(this)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .load(picture)
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
        return currentNavController?.value?.navigateUp() ?: true
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

    override fun onrefreshPicBottomNav(picture: String?) {
        setPicBottomNav(picture)
    }

    override fun goToProfile() {
        bottomNavView.selectedItemId = R.id.navigation_graph_tab_4
    }

    override fun goToTop() {
        bottomNavView.selectedItemId = R.id.navigation_graph_tab_3
    }

}
