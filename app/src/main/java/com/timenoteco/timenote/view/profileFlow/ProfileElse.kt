package com.timenoteco.timenote.view.profileFlow

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import com.timenoteco.timenote.listeners.OnRemoveFilterBarListener
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.viewModel.FollowViewModel
import com.timenoteco.timenote.viewModel.LoginViewModel
import com.timenoteco.timenote.viewModel.ProfileViewModel
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.BranchEvent
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.fragment_my_profile.*
import kotlinx.android.synthetic.main.fragment_profile_else.*
import kotlinx.android.synthetic.main.fragment_profile_else.profile_account_private
import kotlinx.android.synthetic.main.fragment_profile_else.profile_calendar_btn
import kotlinx.android.synthetic.main.fragment_profile_else.profile_day_name_calendar
import kotlinx.android.synthetic.main.fragment_profile_else.profile_day_number_calendar
import kotlinx.android.synthetic.main.fragment_profile_else.profile_desc
import kotlinx.android.synthetic.main.fragment_profile_else.profile_followers_label
import kotlinx.android.synthetic.main.fragment_profile_else.profile_following_label
import kotlinx.android.synthetic.main.fragment_profile_else.profile_infos
import kotlinx.android.synthetic.main.fragment_profile_else.profile_name
import kotlinx.android.synthetic.main.fragment_profile_else.profile_name_toolbar
import kotlinx.android.synthetic.main.fragment_profile_else.profile_nbr_followers
import kotlinx.android.synthetic.main.fragment_profile_else.profile_nbr_following
import kotlinx.android.synthetic.main.fragment_profile_else.profile_notif_btn
import kotlinx.android.synthetic.main.fragment_profile_else.profile_pic_imageview
import kotlinx.android.synthetic.main.fragment_profile_else.profile_settings_btn
import kotlinx.android.synthetic.main.fragment_profile_else.profile_tablayout
import kotlinx.android.synthetic.main.fragment_profile_else.profile_vp
import kotlinx.android.synthetic.main.fragment_profile_else.scrollable
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class ProfileElse : BaseThroughFragment(), View.OnClickListener, OnRemoveFilterBarListener {

    private lateinit var meInfoDTO: UserInfoDTO
    private var userInfoDTO: UserInfoDTO? = null
    private var stateSwitchUrl: String? = null
    private var profileEventPagerAdapter: ProfileEventPagerAdapter? = null
    private var showFilterBar: Boolean = false
    private val args : ProfileElseArgs by navArgs()
    private lateinit var prefs: SharedPreferences
    private var tokenId : String? = null
    private var isFollowed = false
    private val loginViewModel : LoginViewModel by activityViewModels()
    private val followViewModel: FollowViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private var showFilterBarFutureEvents = false
    private var showFilterBarPastEvents = false
    private var locaPref: Int = -1
    private var isDetailShown : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
        locaPref = prefs.getInt("locaPref", -1)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_profile_else)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
        meInfoDTO = Gson().fromJson<UserInfoDTO>(prefs.getString("UserInfoDTO", ""), typeUserInfo)
        userInfoDTO = args.userInfoDTO

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

        if(userInfoDTO?.certified!!) profile_name_toolbar.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_certification, 0)
        else profile_name_toolbar.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)

        profile_name_toolbar.text = userInfoDTO?.userName
        if(userInfoDTO?.description.isNullOrBlank()) profile_desc.visibility = View.GONE else {
                profile_desc.visibility = View.VISIBLE
                profile_desc.text = userInfoDTO?.description
            }

        if(userInfoDTO?.givenName.isNullOrBlank()) profile_name.visibility = View.GONE else {
                profile_name.visibility = View.VISIBLE
                profile_name.text = userInfoDTO?.givenName
            }
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
                    isDetailShown = false
                    profile_infos.setImageDrawable(resources.getDrawable(R.drawable.ic_icons8_contacts))
                }

                profile_nbr_followers.text = userInfoDTO?.followers?.toString()
                profile_nbr_following.text = userInfoDTO?.following?.toString()

            if (userInfoDTO?.isInFollowers!!) {
                profile_follow_btn.apply {
                    setBorderColor(resources.getColor(android.R.color.darker_gray))
                    setBorderWidth(1)
                    setText(resources.getString(R.string.unfollow))
                    setBackgroundColor(resources.getColor(android.R.color.transparent))
                    setTextColor(resources.getColor(android.R.color.darker_gray))
                }
                isFollowed = true
            }

            if(userInfoDTO?.status == STATUS.PRIVATE.ordinal && !userInfoDTO?.isInFollowers!!){
                scrollable.visibility = View.GONE
                profile_account_private.visibility = View.VISIBLE
            }
            else {
                profileEventPagerAdapter = ProfileEventPagerAdapter(childFragmentManager, lifecycle, showFilterBar, this, args.from, userInfoDTO?.id!!, false)
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
                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                    override fun onTabUnselected(tab: TabLayout.Tab?) {}

                    override fun onTabSelected(tab: TabLayout.Tab?) {
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

                TabLayoutMediator(profile_tablayout, profile_vp) { tab, position -> }.attach()
            }

            if ((userInfoDTO?.status == STATUS.PRIVATE.ordinal && userInfoDTO?.isInFollowers!!)||(userInfoDTO?.status == STATUS.PUBLIC.ordinal)){
                profile_name_toolbar.setOnClickListener {
                    if(profileEventPagerAdapter != null)
                        profileEventPagerAdapter?.scrollToTop()
                }
                profile_settings_btn.setOnClickListener(this)
                profile_followers_label.setOnClickListener(this)
                profile_nbr_followers.setOnClickListener(this)
                profile_nbr_following.setOnClickListener(this)
                profile_following_label.setOnClickListener(this)
                profile_infos.setOnClickListener(this)
                profile_calendar_btn.setOnClickListener(this)
            }
        profile_follow_btn.setOnClickListener(this)
        profile_notif_btn.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v){
            profile_calendar_btn -> findNavController().navigate(ProfileElseDirections.actionGlobalProfileCalendar(userInfoDTO?.id!!))
            profile_settings_btn -> {
                val listItems = if(meInfoDTO.isAdmin!! && !userInfoDTO?.certified!!) mutableListOf(
                    getString(R.string.certify),
                    getString(R.string.details),
                    getString(R.string.share_to),
                    getString(R.string.report)
                ) else mutableListOf(
                    getString(R.string.details),
                    getString(R.string.share_to),
                    getString(R.string.report)
                )
                MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    title(text = userInfoDTO?.userName)
                    listItems(items = listItems) { _, _, text ->
                        when (text.toString()) {
                            context.getString(R.string.certify) -> profileViewModel.certifyProfile(tokenId!!, userInfoDTO?.id!!).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                                if(it.code() == 401){
                                    loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner, androidx.lifecycle.Observer {newAccessToken ->
                                        tokenId = newAccessToken
                                        profileViewModel.certifyProfile(tokenId!!, userInfoDTO?.id!!).observe(viewLifecycleOwner, androidx.lifecycle.Observer {rsp ->
                                            if(rsp.isSuccessful) Toast.makeText(
                                                requireContext(),
                                                "Certified",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        })
                                    })
                                }

                                if(it.isSuccessful) Toast.makeText(
                                    requireContext(),
                                    "Certified",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            })
                            context.getString(R.string.details) -> findNavController().navigate(
                                ProfileElseDirections.actionGlobalProfilModify(true, userInfoDTO)
                            )
                            context.getString(R.string.report) -> Toast.makeText(
                                requireContext(),
                                "Reported",
                                Toast.LENGTH_SHORT
                            ).show()
                            context.getString(R.string.share_to) -> share(userInfoDTO!!)
                        }
                    }
                }
            }
            profile_notif_btn -> findNavController().popBackStack()
            profile_followers_label -> findNavController().navigate(ProfileElseDirections.actionGlobalFollowPage(userInfoDTO?.id!!, false, args.from).setFollowers(1))
            profile_following_label -> findNavController().navigate(ProfileElseDirections.actionGlobalFollowPage(userInfoDTO?.id!!, false, args.from).setFollowers(0))
            profile_nbr_followers -> findNavController().navigate(ProfileElseDirections.actionGlobalFollowPage(userInfoDTO?.id!!, false, args.from).setFollowers(1))
            profile_nbr_following -> findNavController().navigate(ProfileElseDirections.actionGlobalFollowPage(userInfoDTO?.id!!, false, args.from).setFollowers(0))
            profile_infos -> {
                if(stateSwitchUrl.isNullOrBlank()) {
                    findNavController().navigate(ProfileElseDirections.actionGlobalProfilModify(true, userInfoDTO))
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
                if(it.code() == 400) Toast.makeText(
                    requireContext(),
                    "Already Asked",
                    Toast.LENGTH_SHORT
                )
                    .show()
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
    }

}
