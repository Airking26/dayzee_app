package com.dayzeeco.dayzee.view.profileFlow

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.ProfileEventPagerAdapter
import com.dayzeeco.dayzee.common.*
import com.dayzeeco.dayzee.listeners.BackToHomeListener
import com.dayzeeco.dayzee.listeners.OnRemoveFilterBarListener
import com.dayzeeco.dayzee.listeners.RefreshPicBottomNavListener
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.view.profileFlow.settingsDirectory.SettingsDirections
import com.dayzeeco.dayzee.viewModel.*
import com.dayzeeco.dayzee.webService.ProfileModifyData
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.BranchEvent
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.fragment_my_profile.*
import kotlinx.android.synthetic.main.fragment_profil_modify.*
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
    private val notificationViewModel : NotificationViewModel by activityViewModels()
    private var showFilterBar: Boolean = false
    private lateinit var prefs: SharedPreferences
    private var tokenId : String? = null
    private var showFilterBarFutureEvents = false
    private var showFilterBarPastEvents = false
    private lateinit var onRefreshPicBottomNavListener: RefreshPicBottomNavListener
    private var locaPref: Int = -1
    private lateinit var onBackHome : BackToHomeListener
    private val utils: Utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
        locaPref = prefs.getInt(location_pref, -1)
        switchToNotifViewModel.getSwitchNotifLiveData().observe(requireActivity()
        ) { if (it) findNavController().navigate(MyProfileDirections.actionMyProfileToNotifications()) }
        loginViewModel.getAuthenticationState().observe(requireActivity()) {
            when (it) {
                LoginViewModel.AuthenticationState.GUEST -> findNavController().popBackStack(
                    R.id.myProfile,
                    false
                )
                LoginViewModel.AuthenticationState.DISCONNECTED -> findNavController().navigate(
                    SettingsDirections.actionGlobalNavigation()
                )
                LoginViewModel.AuthenticationState.UNAUTHENTICATED -> findNavController().navigate(
                    MyProfileDirections.actionGlobalNavigation()
                )
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    tokenId = prefs.getString(accessToken, null)
                    if (arguments == null || arguments?.isEmpty!!) {
                        findNavController().popBackStack(R.id.myProfile, false)
                    } else arguments.let { bundle ->
                        if (!bundle?.getString(type).isNullOrBlank())
                            view?.post { findNavController().navigate(MyProfileDirections.actionMyProfileToNotifications()) }
                        else
                            findNavController().popBackStack(R.id.myProfile, false)
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onRefreshPicBottomNavListener = context as RefreshPicBottomNavListener
        onBackHome = context as BackToHomeListener
    }

    override fun onResume() {
        super.onResume()

        when(loginViewModel.getAuthenticationState().value){
            LoginViewModel.AuthenticationState.GUEST -> onBackHome.onBackHome()
            LoginViewModel.AuthenticationState.UNAUTHENTICATED -> onBackHome.onBackHome()
        }
        if (!prefs.getString(accessToken, null).isNullOrBlank()) {
            onRefreshPicBottomNavListener.onrefreshPicBottomNav(userInfoDTO?.picture, userInfoDTO?.userName)

            notificationViewModel.checkUnreadNotifications(tokenId!!, userInfoDTO?.id!!).observe(
                viewLifecycleOwner
            ) { resp ->
                if (resp.code() == 401) {
                    loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner
                    ) { refreshedToken ->
                        tokenId = refreshedToken
                        notificationViewModel.checkUnreadNotifications(
                            tokenId!!,
                            userInfoDTO?.id!!
                        ).observe(
                            viewLifecycleOwner
                        ) { secondResp ->
                            if (secondResp.isSuccessful) {
                                if (secondResp.body()!!) {
                                    profile_notif_btn.setImageDrawable(
                                        resources.getDrawable(
                                            R.drawable.ic_notification_rouge
                                        )
                                    )
                                } else profile_notif_btn.setImageDrawable(
                                    resources.getDrawable(
                                        R.drawable.ic_notifications_ok
                                    )
                                )

                            }
                        }
                    }
                } else if (resp.isSuccessful) {
                    if (resp.body()!!) {
                        profile_notif_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_notification_rouge))
                    } else profile_notif_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_notifications_ok))
                }

            }

            switchToDetailedTimenote.getswitchToPreviewDetailedTimenoteViewModel().observe(
                viewLifecycleOwner
            ) {
                if (it) {
                    findNavController().navigate(
                        MyProfileDirections.actionGlobalDetailedTimenote(
                            4,
                            switchToDetailedTimenote.getTimenoteInfoDTO()
                        )
                    )
                    switchToDetailedTimenote.switchToPreviewDetailedTimenoteViewModel(false)
                }
            }
            profile_tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> {
                            showFilterBarPastEvents = if (!showFilterBarPastEvents) {
                                profileEventPagerAdapter?.setShowFilterBar(true, 0, true)
                                true
                            } else {
                                profileEventPagerAdapter?.setShowFilterBar(false, 0, false)
                                false
                            }
                        }
                        1 -> {
                            showFilterBarFutureEvents = if (!showFilterBarFutureEvents) {
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
                            profile_tablayout.getTabAt(1)?.icon =
                                resources.getDrawable(R.drawable.ic_futur_ok)
                            profile_tablayout.getTabAt(0)?.icon =
                                resources.getDrawable(R.drawable.ic_passe_plein_grad_ok)
                        }
                        1 -> {
                            profile_tablayout.getTabAt(1)?.icon =
                                resources.getDrawable(R.drawable.ic_futur_plein_grad)
                            profile_tablayout.getTabAt(0)?.icon =
                                resources.getDrawable(R.drawable.ic_passe_ok)
                        }
                    }
                }

            })
            profile_tablayout.setSelectedTabIndicatorColor(resources.getColor(android.R.color.darker_gray))
            if(profile_vp.adapter != null)
                TabLayoutMediator(profile_tablayout, profile_vp) { _, _ -> }.attach()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return getPersistentView(
            inflater,
            container,
            savedInstanceState,
            R.layout.fragment_my_profile
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(!prefs.getString(accessToken, null).isNullOrBlank()) {
            profile_name_toolbar.setOnClickListener {}

            val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
            userInfoDTO = Gson().fromJson<UserInfoDTO>(
                prefs.getString(user_info_dto, ""),
                typeUserInfo
            )

            val simpleDateFormatDayName = SimpleDateFormat("EEE.", Locale.getDefault())
            val simpleDateFormatDayNumber = SimpleDateFormat("dd", Locale.getDefault())
            profile_day_name_calendar.text = simpleDateFormatDayName.format(System.currentTimeMillis())
            profile_day_number_calendar.text = simpleDateFormatDayNumber.format(System.currentTimeMillis())

            if (userInfoDTO?.picture.isNullOrBlank()){
                profile_pic_imageview.setImageDrawable(utils.determineLetterLogo(userInfoDTO?.userName!!, requireContext()))
            } else {
                if(userInfoDTO?.isPictureNft!!) profile_pic_imageview.vertices = 6
                else profile_pic_imageview.vertices = 0
                Glide
                    .with(this)
                    .load(userInfoDTO?.picture)
                    .into(profile_pic_imageview)
            }
            profile_name_toolbar.text = userInfoDTO?.userName

            if(userInfoDTO?.certified!!) profile_name_toolbar.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_certified_other,
                0
            )
            else profile_name_toolbar.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

            if(userInfoDTO?.description.isNullOrBlank()) profile_desc.visibility = View.GONE else {
                profile_desc.visibility = View.VISIBLE
                profile_desc.text = userInfoDTO?.description
                if(userInfoDTO?.givenName.isNullOrBlank()){
                    setMargins(profile_desc, 0, 16, 0, 0)
                } else setMargins(profile_desc, 0, 0, 0, 0)
            }

            if(userInfoDTO?.givenName.isNullOrBlank()) profile_name.visibility = View.GONE else {
                profile_name.visibility = View.VISIBLE
                profile_name.text = userInfoDTO?.givenName
            }



                prefs.intLiveData(followers, userInfoDTO?.followers!!).observe(viewLifecycleOwner) {
                    profile_nbr_followers.text = it.toString()
                }
            prefs.intLiveData(following, userInfoDTO?.following!!).observe(viewLifecycleOwner) {
                profile_nbr_following.text = it.toString()
            }

            meViewModel.getMyProfile(tokenId!!).observe(viewLifecycleOwner) {
                    if (it.code() == 401) {
                        loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner
                        ) { newAccessToken ->
                            tokenId = newAccessToken
                            meViewModel.getMyProfile(tokenId!!).observe(viewLifecycleOwner
                            ) { response ->
                                if (response.isSuccessful) {
                                    prefs.edit().putInt(
                                        followers,
                                        response.body()?.followers!!
                                    ).apply()
                                    prefs.edit().putInt(
                                        following,
                                        response.body()?.following!!
                                    ).apply()

                                    profile_nbr_followers.text =
                                        response.body()?.followers?.toString()
                                    profile_nbr_following.text =
                                        response.body()?.following?.toString()
                                    profileModifyData.setNbrFollowers(response.body()?.followers!!)
                                    profileModifyData.setNbrFollowing(response.body()?.following!!)
                                }
                            }
                        }
                    }
                    if (it.isSuccessful) {
                        prefs.edit().putInt(followers, it.body()?.followers!!).apply()
                        prefs.edit().putInt(following, it.body()?.following!!).apply()

                        profile_nbr_followers.text = it.body()?.followers?.toString()
                        profile_nbr_following.text = it.body()?.following?.toString()
                        profileModifyData.setNbrFollowers(it.body()?.followers!!)
                        profileModifyData.setNbrFollowing(it.body()?.following!!)
                    }
                }

            profile_modify_btn.visibility = View.VISIBLE
                profileModifyData = ProfileModifyData(requireContext())

                prefs.intLiveData(location_pref, -1)
                    .observe(viewLifecycleOwner) {
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
                    }


            profileEventPagerAdapter = ProfileEventPagerAdapter(
                childFragmentManager,
                lifecycle,
                showFilterBar,
                this,
                1,
                userInfoDTO?.id!!,
                true
            )
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

    private fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is MarginLayoutParams) {
            val p = view.layoutParams as MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
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
            .setContentMetadata(
                ContentMetadata().addCustomMetadata(
                    user_info_dto, Gson().toJson(
                        userInfoDTO
                    )
                )
            )
        else BranchUniversalObject()
            .setTitle(userInfoDTO?.userName!!)
            .setContentDescription(userInfoDTO?.givenName)
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setContentMetadata(
                ContentMetadata().addCustomMetadata(
                    user_info_dto, Gson().toJson(
                        userInfoDTO
                    )
                )
            )

        branchUniversalObject.generateShortUrl(requireContext(), linkProperties) { url, error ->
            BranchEvent("branch_url_created").logEvent(requireContext())
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(
                Intent.EXTRA_TEXT, String.format(
                    resources.getString(R.string.profile_externe),
                    userInfoDTO?.userName,
                    userInfoDTO?.userName,
                    url
                )
            )
            startActivityForResult(i, 111)
        }


    }

    override fun onClick(v: View?) {
        when(v){
            profile_modify_btn -> findNavController().navigate(MyProfileDirections.actionGlobalProfilModify(false, null))
            profile_calendar_btn -> findNavController().navigate(
                MyProfileDirections.actionGlobalProfileCalendar(
                    userInfoDTO?.id!!
                )
            )
            profile_settings_btn -> findNavController().navigate(MyProfileDirections.actionMyProfileToMenu())
            profile_notif_btn -> findNavController().navigate(MyProfileDirections.actionMyProfileToNotifications())
            profile_followers_label -> findNavController().navigate(
                MyProfileDirections.actionGlobalFollowPage(
                    userInfoDTO?.id!!,
                    true,
                    4
                ).setFollowers(1)
            )
            profile_following_label -> findNavController().navigate(
                MyProfileDirections.actionGlobalFollowPage(
                    userInfoDTO?.id!!,
                    true,
                    4
                ).setFollowers(0)
            )
            profile_nbr_followers -> findNavController().navigate(
                MyProfileDirections.actionGlobalFollowPage(
                    userInfoDTO?.id!!,
                    true,
                    4
                ).setFollowers(1)
            )
            profile_nbr_following -> findNavController().navigate(
                MyProfileDirections.actionGlobalFollowPage(
                    userInfoDTO?.id!!,
                    true,
                    4
                ).setFollowers(0)
            )
            profile_infos -> share()
            profile_location -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.location)
                listItems(
                    items = listOf(
                        getString(R.string.no_location), getString(R.string.city), getString(
                            R.string.address
                        )
                    )
                ) { dialog, index, text ->
                    when (index) {
                        0 -> prefs.edit().putInt(location_pref, index).apply()
                        1 -> prefs.edit().putInt(location_pref, index).apply()
                        2 -> prefs.edit().putInt(location_pref, index).apply()
                    }
                }
            }
        }
    }

    override fun onHideFilterBarClicked(position: Int?) {
        profileEventPagerAdapter?.setShowFilterBar(false, position, null)
    }

}
