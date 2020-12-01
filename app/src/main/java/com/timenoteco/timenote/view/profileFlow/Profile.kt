package com.timenoteco.timenote.view.profileFlow

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
import com.timenoteco.timenote.viewModel.FollowViewModel
import com.timenoteco.timenote.viewModel.LoginViewModel
import com.timenoteco.timenote.viewModel.MeViewModel
import com.timenoteco.timenote.viewModel.StringViewModel
import com.timenoteco.timenote.webService.ProfileModifyData
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.BranchEvent
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.fragment_profile.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class Profile : BaseThroughFragment(), View.OnClickListener, OnRemoveFilterBarListener {

    private var userInfoDTO: UserInfoDTO? = null
    private var stateSwitchUrl: String? = null
    private var isFollowed = false
    private var profileEventPagerAdapter: ProfileEventPagerAdapter? = null
    private lateinit var profileModifyData: ProfileModifyData
    private val loginViewModel : LoginViewModel by activityViewModels()
    private val followViewModel: FollowViewModel by activityViewModels()
    private val stringViewModel : StringViewModel by activityViewModels()
    private val meViewModel : MeViewModel by activityViewModels()
    private var showFilterBar: Boolean = false
    private val args : ProfileArgs by navArgs()
    private lateinit var prefs: SharedPreferences
    private var tokenId : String? = null
    private var locaPref: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
        locaPref = prefs.getInt("locaPref", -1)
        stringViewModel.getSwitchNotifLiveData().observe(requireActivity(), androidx.lifecycle.Observer { if(it) findNavController().navigate(ProfileDirections.actionProfileToNotifications()) })
        //if(!tokenId.isNullOrBlank()) loginViewModel.markAsAuthenticated() else findNavController().navigate(ProfileDirections.actionProfileToNavigation())
        loginViewModel.getAuthenticationState().observe(requireActivity(), androidx.lifecycle.Observer {
            when (it) {
                LoginViewModel.AuthenticationState.UNAUTHENTICATED -> findNavController().navigate(ProfileDirections.actionProfileToNavigation())
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    tokenId = prefs.getString(accessToken, null)
                    if(arguments?.isEmpty!!)
                        findNavController().popBackStack(R.id.profile, false)
                    else arguments.let { bundle ->
                        if(!bundle?.getString("type").isNullOrBlank())
                            findNavController().navigate(ProfileDirections.actionProfileToNotifications())
                        else
                            findNavController().popBackStack(R.id.profile, false)
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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_profile)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(!tokenId.isNullOrBlank()) {

            val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
            userInfoDTO = if(args.isNotMine) args.userInfoDTO else Gson().fromJson<UserInfoDTO>(prefs.getString("UserInfoDTO", ""), typeUserInfo)

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


            if(!args.isNotMine) {
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
                prefs.stringLiveData(
                    "UserInfoDTO",
                    Gson().toJson(profileModifyData.loadProfileModifyModel())
                ).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    val type: Type = object : TypeToken<UpdateUserInfoDTO?>() {}.type
                    val profilModifyModel: UpdateUserInfoDTO? =
                        Gson().fromJson<UpdateUserInfoDTO>(it, type)

                    if (profilModifyModel?.socialMedias?.youtube?.enabled!!) {
                        if (!profilModifyModel.socialMedias.youtube.url.isBlank()) {
                            profile_infos.setImageDrawable(resources.getDrawable(R.drawable.ic_youtube_colored))
                            stateSwitchUrl = profilModifyModel.socialMedias.youtube.url
                        }
                    } else if (profilModifyModel.socialMedias.facebook.enabled) {
                        if (!profilModifyModel.socialMedias.facebook.url.isBlank()) {
                            profile_infos.setImageDrawable(resources.getDrawable(R.drawable.ic_facebook_colored))
                            stateSwitchUrl = profilModifyModel.socialMedias.facebook.url
                        }
                    } else if (profilModifyModel.socialMedias.instagram.enabled) {
                        if (!profilModifyModel.socialMedias.instagram.url.isBlank()) {
                            profile_infos.setImageDrawable(resources.getDrawable(R.drawable.ic_insta_colored))
                            stateSwitchUrl = profilModifyModel.socialMedias.instagram.url
                        }
                    } else if (profilModifyModel.socialMedias.whatsApp.enabled) {
                        if (!profilModifyModel.socialMedias.whatsApp.url.isBlank()) {
                            profile_infos.setImageDrawable(resources.getDrawable(R.drawable.ic_whatsapp))
                            stateSwitchUrl = profilModifyModel.socialMedias.whatsApp.url
                        }
                    } else if (profilModifyModel.socialMedias.linkedIn.enabled) {
                        if (!profilModifyModel.socialMedias.linkedIn.url.isBlank()) {
                            profile_infos.setImageDrawable(resources.getDrawable(R.drawable.ic_linkedin_colored))
                            stateSwitchUrl = profilModifyModel.socialMedias.linkedIn.url
                        }
                    } else {
                        profile_infos.setImageDrawable(resources.getDrawable(R.drawable.ic_icons8_contacts))
                    }
                })
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
            } else {
                profile_location.visibility = View.GONE
                profile_modify_btn.visibility = View.INVISIBLE
                profile_follow_btn.visibility = View.VISIBLE
                profile_settings_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_more_vert_black_profile_24dp))
                profile_notif_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_back_thin))
                profile_follow_btn.setOnClickListener(this)

                if (userInfoDTO?.socialMedias?.youtube?.enabled!!) {
                    if (!userInfoDTO?.socialMedias?.youtube?.url?.isBlank()!!) {
                        profile_infos.setImageDrawable(resources.getDrawable(R.drawable.ic_youtube_colored))
                        stateSwitchUrl = userInfoDTO?.socialMedias?.youtube?.url
                    }
                } else if (userInfoDTO?.socialMedias?.facebook?.enabled!!) {
                    if (!userInfoDTO?.socialMedias?.facebook?.url?.isBlank()!!) {
                        profile_infos.setImageDrawable(resources.getDrawable(R.drawable.ic_facebook_colored))
                        stateSwitchUrl = userInfoDTO?.socialMedias?.facebook?.url
                    }
                } else if (userInfoDTO?.socialMedias?.instagram?.enabled!!) {
                    if (!userInfoDTO?.socialMedias?.instagram?.url?.isBlank()!!) {
                        profile_infos.setImageDrawable(resources.getDrawable(R.drawable.ic_insta_colored))
                        stateSwitchUrl = userInfoDTO?.socialMedias?.instagram?.url
                    }
                } else if (userInfoDTO?.socialMedias?.whatsApp?.enabled!!) {
                    if (!userInfoDTO?.socialMedias?.whatsApp?.url?.isBlank()!!) {
                        profile_infos.setImageDrawable(resources.getDrawable(R.drawable.ic_whatsapp))
                        stateSwitchUrl = userInfoDTO?.socialMedias?.whatsApp?.url
                    }
                } else if (userInfoDTO?.socialMedias?.linkedIn?.enabled!!) {
                    if (!userInfoDTO?.socialMedias?.linkedIn?.url?.isBlank()!!) {
                        profile_infos.setImageDrawable(resources.getDrawable(R.drawable.ic_linkedin_colored))
                        stateSwitchUrl = userInfoDTO?.socialMedias?.linkedIn?.url
                    }
                } else {
                    profile_infos.setImageDrawable(resources.getDrawable(R.drawable.ic_icons8_contacts))
                }

                profile_nbr_followers.text = userInfoDTO?.followers?.toString()
                profile_nbr_following.text = userInfoDTO?.following?.toString()


            }

            if (userInfoDTO?.isInFollowers!! && args.isNotMine) {
                profile_modify_btn.visibility = View.INVISIBLE
                profile_follow_btn.apply {
                    setBorderColor(resources.getColor(android.R.color.darker_gray))
                    setBorderWidth(1)
                    setText(resources.getString(R.string.unfollow))
                    setBackgroundColor(resources.getColor(android.R.color.transparent))
                    setTextColor(resources.getColor(android.R.color.darker_gray))
                }
            }

            if(userInfoDTO?.status == STATUS.PRIVATE.ordinal && !userInfoDTO?.isInFollowers!! && args.isNotMine){
                scrollable.visibility = View.GONE
                profile_account_private.visibility = View.VISIBLE
            }
            else {
                profileEventPagerAdapter = ProfileEventPagerAdapter(childFragmentManager, lifecycle, showFilterBar, this, args.from, userInfoDTO?.id!!)
                profile_vp?.apply {
                    adapter = profileEventPagerAdapter
                    isUserInputEnabled = false
                    isSaveEnabled = false
                    post {
                        profile_vp?.setCurrentItem(1, false)
                    }
                }

                profile_tablayout.setSelectedTabIndicatorColor(resources.getColor(android.R.color.darker_gray))
                profile_tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabReselected(tab: TabLayout.Tab?) {
                        profileEventPagerAdapter?.setShowFilterBar(true, tab?.position!!, true, !args.isNotMine)
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                        profileEventPagerAdapter?.setShowFilterBar(true, tab?.position!!, false,  !args.isNotMine)
                    }

                    override fun onTabSelected(tab: TabLayout.Tab?) {
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

                TabLayoutMediator(profile_tablayout, profile_vp) { tab, position -> }.attach()
            }

            profile_modify_btn.setOnClickListener(this)
            if(!args.isNotMine) profile_calendar_btn.setOnClickListener(this)
            profile_settings_btn.setOnClickListener(this)
            profile_notif_btn.setOnClickListener(this)
            profile_location.setOnClickListener(this)
            profile_followers_label.setOnClickListener(this)
            profile_nbr_followers.setOnClickListener(this)
            profile_nbr_following.setOnClickListener(this)
            profile_following_label.setOnClickListener(this)
            profile_infos.setOnClickListener(this)

        }

    }

    override fun onClick(v: View?) {
        when(v){
            profile_modify_btn -> findNavController().navigate(ProfileDirections.actionProfileToProfilModify(false, null))
            profile_calendar_btn -> findNavController().navigate(ProfileDirections.actionProfileToProfileCalendar(userInfoDTO?.id!!))
            profile_settings_btn -> {
                if(args.isNotMine) MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    title(text = userInfoDTO?.userName)
                    val listItems: MutableList<String> = mutableListOf(context.getString(R.string.share_to) , context.getString(R.string.report))
                    listItems (items = listItems){ _, _, text ->
                        when(text.toString()){
                            context.getString(R.string.report) -> Toast.makeText(
                                requireContext(),
                                "Reported",
                                Toast.LENGTH_SHORT
                            ).show()
                            context.getString(R.string.share_to) -> share(userInfoDTO!!)
                        }
                    }
                }
                else findNavController().navigate(ProfileDirections.actionProfileToMenu())
            }
            profile_notif_btn -> {
                if(args.isNotMine)findNavController().popBackStack()
                else findNavController().navigate(ProfileDirections.actionProfileToNotifications())
            }
            profile_followers_label -> findNavController().navigate(ProfileDirections.actionProfileToFollowPage(userInfoDTO?.id!!).setFollowers(1))
            profile_following_label -> findNavController().navigate(ProfileDirections.actionProfileToFollowPage(userInfoDTO?.id!!).setFollowers(0))
            profile_nbr_followers -> findNavController().navigate(ProfileDirections.actionProfileToFollowPage(userInfoDTO?.id!!).setFollowers(1))
            profile_nbr_following -> findNavController().navigate(ProfileDirections.actionProfileToFollowPage(userInfoDTO?.id!!).setFollowers(0))
            profile_infos -> {
                if(stateSwitchUrl.isNullOrBlank()) {
                    if(args.isNotMine) findNavController().navigate(ProfileDirections.actionProfileToProfilModify(args.isNotMine, args.userInfoDTO))
                    else findNavController().navigate(ProfileDirections.actionProfileToProfilModify(args.isNotMine, null))
                }
                else {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(stateSwitchUrl)
                    try {
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException){
                        Toast.makeText(
                            requireContext(),
                            "No app found to handle the url",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            profile_follow_btn -> {
                if (!isFollowed) {
                    if (userInfoDTO?.status == STATUS.PUBLIC.ordinal) {
                        followPublicUser()
                    } else if (!isFollowed && (userInfoDTO?.status == STATUS.PRIVATE.ordinal)) {
                        followPrivateUser()
                    }
                } else if(isFollowed){
                    unfollowUser()
                }
            }
            profile_location -> if(!args.isNotMine) MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
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

    private fun share(userInfoDTO: UserInfoDTO) {

        val linkProperties: LinkProperties = LinkProperties().setChannel("whatsapp").setFeature("sharing")

        val branchUniversalObject = if(!userInfoDTO.picture.isNullOrEmpty()) BranchUniversalObject()
            .setTitle(userInfoDTO.userName!!)
            .setContentDescription(userInfoDTO.givenName ?: "")
            .setContentImageUrl(userInfoDTO.picture!!)
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setContentMetadata(ContentMetadata().addCustomMetadata("timenoteInfoDTO", Gson().toJson(userInfoDTO)))
        else BranchUniversalObject()
            .setTitle(userInfoDTO.userName!!)
            .setContentDescription(userInfoDTO.givenName ?: "")
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setContentMetadata(ContentMetadata().addCustomMetadata("timenoteInfoDTO", Gson().toJson(userInfoDTO)))

        branchUniversalObject.generateShortUrl(requireContext(), linkProperties) { url, error ->
            BranchEvent("branch_url_created").logEvent(requireContext())
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_TEXT, String.format("Dayzee : %s at %s", userInfoDTO.userName, url))
            startActivityForResult(i, 111)
        }


    }

    private fun unfollowUser() {
        followViewModel.unfollowUser(tokenId!!, userInfoDTO?.id!!).observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                if(it.code() == 401){
                    loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner, androidx.lifecycle.Observer { newAccessToken ->
                        tokenId = newAccessToken
                        unfollowUser()
                    })
                }
                if(it.isSuccessful) {
                    prefs.edit().putInt("following", prefs.getInt("following", 0) - 1).apply()
                    profile_follow_btn.apply {
                        setBorderColor(resources.getColor(android.R.color.transparent))
                        setBorderWidth(0)
                        setText(resources.getString(R.string.follow))
                        setBackgroundColor(resources.getColor(R.color.colorYellow))
                        setTextColor(resources.getColor(R.color.colorBackground))
                    }

                    isFollowed = false
                }
            })
    }

    private fun followPrivateUser() {
        followViewModel.followPrivateUser(tokenId!!, userInfoDTO?.id!!)
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if(it.code() == 401){
                    loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner, androidx.lifecycle.Observer { newAccessToken ->
                        tokenId = newAccessToken
                        followPrivateUser()
                    })
                }
                if(it.isSuccessful) {
                    profile_follow_btn.apply {
                        setBorderColor(resources.getColor(android.R.color.darker_gray))
                        setBorderWidth(1)
                        setText(resources.getString(R.string.unfollow))
                        setBackgroundColor(resources.getColor(android.R.color.transparent))
                        setTextColor(resources.getColor(android.R.color.darker_gray))
                    }

                    isFollowed = true
                }

            })
    }

    private fun followPublicUser() {
        followViewModel.followPublicUser(tokenId!!, userInfoDTO?.id!!).observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                if(it.code() == 401) loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner, androidx.lifecycle.Observer { newAccessToken ->
                    tokenId = newAccessToken
                    followPublicUser()
                })
                if(it.isSuccessful) {
                    prefs.edit().putInt("following", prefs.getInt("following", 0) + 1).apply()
                    profile_follow_btn.apply {
                        setBorderColor(resources.getColor(android.R.color.darker_gray))
                        setBorderWidth(1)
                        setText(resources.getString(R.string.unfollow))
                        setBackgroundColor(resources.getColor(android.R.color.transparent))
                        setTextColor(resources.getColor(android.R.color.darker_gray))
                    }

                    isFollowed = true

                }
            })
    }

    override fun onHideFilterBarClicked(position:Int?) {
        profileEventPagerAdapter?.setShowFilterBar(false, position, null, !args.isNotMine)
    }

}
