package com.timenoteco.timenote.view.profileFlow

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ProfileEventPagerAdapter
import com.timenoteco.timenote.common.BaseThroughFragment
import com.timenoteco.timenote.common.intLiveData
import com.timenoteco.timenote.common.stringLiveData
import com.timenoteco.timenote.listeners.OnRemoveFilterBarListener
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.view.profileFlow.settingsDirectory.SettingsDirections
import com.timenoteco.timenote.viewModel.LoginViewModel
import com.timenoteco.timenote.viewModel.MeViewModel
import com.timenoteco.timenote.viewModel.SwitchToNotifViewModel
import com.timenoteco.timenote.viewModel.SwitchToPreviewDetailedTimenoteViewModel
import com.timenoteco.timenote.webService.ProfileModifyData
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.BranchEvent
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.fragment_my_profile.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class MyProfile : BaseThroughFragment(), View.OnClickListener, OnRemoveFilterBarListener {

    private var userInfoDTO: UserInfoDTO? = null
    private var profileEventPagerAdapter: ProfileEventPagerAdapter? = null
    private lateinit var profileModifyData: ProfileModifyData
    private val loginViewModel : LoginViewModel by activityViewModels()
    private val switchToNotifViewModel : SwitchToNotifViewModel by activityViewModels()
    private val switchToDetailedTimenote : SwitchToPreviewDetailedTimenoteViewModel by activityViewModels()
    private val meViewModel : MeViewModel by activityViewModels()
    private var showFilterBar: Boolean = false
    private lateinit var prefs: SharedPreferences
    private lateinit var notifications: MutableList<Notification>
    private var tokenId : String? = null
    private var showFilterBarFutureEvents = false
    private var showFilterBarPastEvents = false
    private var locaPref: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
        locaPref = prefs.getInt("locaPref", -1)
        switchToNotifViewModel.getSwitchNotifLiveData().observe(requireActivity(), androidx.lifecycle.Observer { if(it) findNavController().navigate(MyProfileDirections.actionMyProfileToNotifications()) })

        loginViewModel.getAuthenticationState().observe(requireActivity(), androidx.lifecycle.Observer {
            when (it) {
                LoginViewModel.AuthenticationState.DISCONNECTED -> findNavController().navigate(SettingsDirections.actionGlobalNavigation())
                LoginViewModel.AuthenticationState.UNAUTHENTICATED -> findNavController().navigate(MyProfileDirections.actionGlobalNavigation())
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    tokenId = prefs.getString(accessToken, null)
                    if(arguments == null || arguments?.isEmpty!!) findNavController().popBackStack(R.id.myProfile, false)
                    else arguments.let { bundle ->
                        if(!bundle?.getString("type").isNullOrBlank())
                            findNavController().navigate(MyProfileDirections.actionMyProfileToNotifications())
                        else
                            findNavController().popBackStack(R.id.myProfile, false)
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        when(loginViewModel.getAuthenticationState().value){
            LoginViewModel.AuthenticationState.GUEST -> loginViewModel.markAsUnauthenticated()
        }

        prefs.stringLiveData("notifications", Gson().toJson(prefs.getString("notifications", null))).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val typeNotification: Type = object : TypeToken<MutableList<Notification?>>() {}.type
            notifications = Gson().fromJson<MutableList<Notification>>(it, typeNotification) ?: mutableListOf()
            if(notifications.any { n -> !n.read }){
                profile_notif_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_notification_rouge))
            } else {
                profile_notif_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_notifications_ok))
            }
        })

        switchToDetailedTimenote.getswitchToPreviewDetailedTimenoteViewModel().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it) {
                findNavController().navigate(MyProfileDirections.actionGlobalDetailedTimenote(4, switchToDetailedTimenote.getTimenoteInfoDTO()))
                switchToDetailedTimenote.switchToPreviewDetailedTimenoteViewModel(false)
            }
        })
        profile_tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0 -> {
                        showFilterBarPastEvents = if(!showFilterBarPastEvents){
                            profileEventPagerAdapter?.setShowFilterBar(true, 0, true)
                            true
                        } else {
                            profileEventPagerAdapter?.setShowFilterBar(false, 0, false)
                            false
                        }
                    }
                    1 -> {
                        showFilterBarFutureEvents = if(!showFilterBarFutureEvents){
                            profileEventPagerAdapter?.setShowFilterBar(true, 1, true)
                            true
                        } else {
                            profileEventPagerAdapter?.setShowFilterBar(false, 1, false)
                            false
                        }
                    }
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //profileEventPagerAdapter?.setShowFilterBar(true, tab?.position!!, false,  !args.isNotMine)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                profileEventPagerAdapter?.setShowFilterBar(false, tab?.position!!, false)
                showFilterBarPastEvents = false
                showFilterBarFutureEvents = false
                when (tab?.position) {
                    0 -> {
                        profile_tablayout.getTabAt(1)?.icon = resources.getDrawable(R.drawable.ic_futur_ok)
                        profile_tablayout.getTabAt(0)?.icon = resources.getDrawable(R.drawable.ic_passe_plein_grad_ok)
                    }
                    1 -> {
                        profile_tablayout.getTabAt(1)?.icon = resources.getDrawable(R.drawable.ic_futur_plein_grad)
                        profile_tablayout.getTabAt(0)?.icon = resources.getDrawable(R.drawable.ic_passe_ok)
                    }
                }
            }

        })
        profile_tablayout.setSelectedTabIndicatorColor(resources.getColor(android.R.color.darker_gray))
        TabLayoutMediator(profile_tablayout, profile_vp) { _, _ -> }.attach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_my_profile)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(!tokenId.isNullOrBlank()) {

            profile_name_toolbar.setOnClickListener {

            }

            val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
            userInfoDTO = Gson().fromJson<UserInfoDTO>(prefs.getString("UserInfoDTO", ""), typeUserInfo)

            val simpleDateFormatDayName = SimpleDateFormat("EEE.", Locale.getDefault())
            val simpleDateFormatDayNumber = SimpleDateFormat("dd", Locale.getDefault())
            profile_day_name_calendar.text = simpleDateFormatDayName.format(System.currentTimeMillis())
            profile_day_number_calendar.text = simpleDateFormatDayNumber.format(System.currentTimeMillis())

            Glide
                .with(this)
                .load(userInfoDTO?.picture)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.circle_pic)
                .into(profile_pic_imageview)

            profile_name_toolbar.text = userInfoDTO?.userName
            if(userInfoDTO?.description.isNullOrBlank()) profile_desc.visibility = View.GONE else {
                profile_desc.visibility = View.VISIBLE
                profile_desc.text = userInfoDTO?.description
            }

            if(userInfoDTO?.givenName.isNullOrBlank()) profile_name.visibility = View.GONE else {
                profile_name.visibility = View.VISIBLE
                profile_name.text = userInfoDTO?.givenName
            }


                prefs.intLiveData("followers", userInfoDTO?.followers!!).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    profile_nbr_followers.text = it.toString()
                })
                prefs.intLiveData("following", userInfoDTO?.following!!).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    profile_nbr_following.text = it.toString()
                })

                meViewModel.getMyProfile(tokenId!!).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    if(it.code() == 401){
                        loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner, androidx.lifecycle.Observer { newAccessToken ->
                            tokenId = newAccessToken
                            meViewModel.getMyProfile(tokenId!!).observe(viewLifecycleOwner, androidx.lifecycle.Observer { response ->
                                if(response.isSuccessful) {
                                    prefs.edit().putInt("followers", response.body()?.followers!!).apply()
                                    prefs.edit().putInt("following", response.body()?.following!!).apply()

                                    profile_nbr_followers.text = response.body()?.followers?.toString()
                                    profile_nbr_following.text = response.body()?.following?.toString()
                                    profileModifyData.setNbrFollowers(response.body()?.followers!!)
                                    profileModifyData.setNbrFollowing(response.body()?.following!!)
                                }
                            })
                        })
                    }
                    if(it.isSuccessful) {
                        prefs.edit().putInt("followers", it.body()?.followers!!).apply()
                        prefs.edit().putInt("following", it.body()?.following!!).apply()

                        profile_nbr_followers.text = it.body()?.followers?.toString()
                        profile_nbr_following.text = it.body()?.following?.toString()
                        profileModifyData.setNbrFollowers(it.body()?.followers!!)
                        profileModifyData.setNbrFollowing(it.body()?.following!!)
                    }
                })

                profile_modify_btn.visibility = View.VISIBLE
                profileModifyData = ProfileModifyData(requireContext())

                prefs.intLiveData("locaPref", -1)
                    .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                        if (userInfoDTO?.location == null || it == -1 || it == 0) profile_location.visibility =
                            View.GONE
                        else if (it == 1 && userInfoDTO?.location?.address?.city == null) profile_location.visibility =
                            View.GONE
                        else if (it == 2 && userInfoDTO?.location?.address?.address == null) profile_location.visibility =
                            View.GONE
                        else {
                            profile_location.visibility = View.VISIBLE
                            if (it == 1 && userInfoDTO?.location?.address?.city != null) profile_location.text =
                                userInfoDTO?.location?.address?.city
                            else if (it == 2 && userInfoDTO?.location?.address?.address != null) profile_location.text =
                                userInfoDTO?.location?.address?.address
                        }
                    })


            profileEventPagerAdapter = ProfileEventPagerAdapter(childFragmentManager, lifecycle, showFilterBar, this, 1, userInfoDTO?.id!!, true)
            profile_vp?.apply {
                adapter = profileEventPagerAdapter
                isUserInputEnabled = false
                isSaveEnabled = false
                post {
                    profile_vp?.setCurrentItem(1, false)
                }
            }

            profile_name_toolbar.setOnClickListener {
                if(profileEventPagerAdapter != null)
                    profileEventPagerAdapter?.scrollToTop()
            }

            profile_modify_btn.setOnClickListener(this)
                profile_settings_btn.setOnClickListener(this)
                profile_followers_label.setOnClickListener(this)
                profile_nbr_followers.setOnClickListener(this)
                profile_nbr_following.setOnClickListener(this)
                profile_following_label.setOnClickListener(this)
                profile_infos.setOnClickListener(this)
                profile_calendar_btn.setOnClickListener(this)
            profile_notif_btn.setOnClickListener(this)
            profile_location.setOnClickListener(this)


        }
    }


    override fun onStop() {
        profile_tablayout.clearOnTabSelectedListeners()
        super.onStop()
    }


    private fun share() {
        val linkProperties: LinkProperties = LinkProperties().setChannel("whatsapp").setFeature("sharing")

        val branchUniversalObject = if(!userInfoDTO?.picture.isNullOrEmpty()) BranchUniversalObject()
            .setTitle(userInfoDTO?.userName!!)
            .setContentDescription(userInfoDTO?.givenName)
            .setContentImageUrl(userInfoDTO?.picture!!)
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setContentMetadata(ContentMetadata().addCustomMetadata("userInfoDTO", Gson().toJson(userInfoDTO)))
        else BranchUniversalObject()
            .setTitle(userInfoDTO?.userName!!)
            .setContentDescription(userInfoDTO?.givenName)
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setContentMetadata(ContentMetadata().addCustomMetadata("userInfoDTO", Gson().toJson(userInfoDTO)))

        branchUniversalObject.generateShortUrl(requireContext(), linkProperties) { url, error ->
            BranchEvent("branch_url_created").logEvent(requireContext())
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_TEXT, String.format("Dayzee : %s at %s", userInfoDTO?.userName, url))
            startActivityForResult(i, 111)
        }


    }

    override fun onClick(v: View?) {
        when(v){
            profile_modify_btn -> findNavController().navigate(MyProfileDirections.actionGlobalProfilModify(false, null))
            profile_calendar_btn -> findNavController().navigate(MyProfileDirections.actionGlobalProfileCalendar(userInfoDTO?.id!!))
            profile_settings_btn -> findNavController().navigate(MyProfileDirections.actionMyProfileToMenu())
            profile_notif_btn -> findNavController().navigate(MyProfileDirections.actionMyProfileToNotifications())
            profile_followers_label -> findNavController().navigate(MyProfileDirections.actionGlobalFollowPage(userInfoDTO?.id!!, true, 4).setFollowers(1))
            profile_following_label -> findNavController().navigate(MyProfileDirections.actionGlobalFollowPage(userInfoDTO?.id!!, true, 4).setFollowers(0))
            profile_nbr_followers -> findNavController().navigate(MyProfileDirections.actionGlobalFollowPage(userInfoDTO?.id!!, true, 4).setFollowers(1))
            profile_nbr_following -> findNavController().navigate(MyProfileDirections.actionGlobalFollowPage(userInfoDTO?.id!!, true, 4).setFollowers(0))
            profile_infos -> share()
            profile_location -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.location)
                listItems(items = listOf(getString(R.string.no_location), getString(R.string.city), getString(
                                    R.string.address))) { dialog, index, text ->
                    when(index){
                        0 -> prefs.edit().putInt("locaPref", index).apply()
                        1 -> prefs.edit().putInt("locaPref", index).apply()
                        2 -> prefs.edit().putInt("locaPref", index).apply()
                    }
                }
            }
        }
    }

    override fun onHideFilterBarClicked(position:Int?) {
        profileEventPagerAdapter?.setShowFilterBar(false, position, null)
    }

}
