package com.timenoteco.timenote.view.searchFlow

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
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ProfileEventPagerAdapter
import com.timenoteco.timenote.common.BaseThroughFragment
import com.timenoteco.timenote.listeners.OnRemoveFilterBarListener
import com.timenoteco.timenote.model.STATUS
import com.timenoteco.timenote.model.UserInfoDTO
import com.timenoteco.timenote.model.accessToken
import com.timenoteco.timenote.view.profileFlow.ProfileDirections
import com.timenoteco.timenote.viewModel.FollowViewModel
import com.timenoteco.timenote.viewModel.LoginViewModel
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.BranchEvent
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.fragment_profile.*
import java.text.SimpleDateFormat
import java.util.*

class ProfileSearch : BaseThroughFragment(), View.OnClickListener, OnRemoveFilterBarListener {

    private var stateSwitchUrl: String? = null
    private var profileEventPagerAdapter: ProfileEventPagerAdapter? = null
    private var isFollowed = false
    private val args : ProfileSearchArgs by navArgs()
    private val loginViewModel : LoginViewModel by activityViewModels()
    private val followViewModel : FollowViewModel by activityViewModels()
    private var showFilterBar: Boolean = false
    private lateinit var prefs: SharedPreferences
    private var tokenId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_profile)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val simpleDateFormatDayName= SimpleDateFormat("EEE.", Locale.getDefault())
        val simpleDateFormatDayNumber = SimpleDateFormat("dd", Locale.getDefault())

        profile_location.visibility = View.GONE
        profile_day_name_calendar.text = simpleDateFormatDayName.format(System.currentTimeMillis())
        profile_day_number_calendar.text = simpleDateFormatDayNumber.format(System.currentTimeMillis())

            if(args.userInfoDTO?.socialMedias?.youtube?.enabled!!) {
                if(!args.userInfoDTO?.socialMedias?.youtube?.url?.isBlank()!!){
                    profile_infos.setImageDrawable(resources.getDrawable(R.drawable.ic_youtube_colored))
                    stateSwitchUrl = args.userInfoDTO?.socialMedias?.youtube?.url
                }
            }
            else if(args.userInfoDTO?.socialMedias?.facebook?.enabled!!) {
                if(!args.userInfoDTO?.socialMedias?.facebook?.url?.isBlank()!!){
                    profile_infos.setImageDrawable(resources.getDrawable(R.drawable.ic_facebook_colored))
                    stateSwitchUrl = args.userInfoDTO?.socialMedias?.facebook?.url
                }
            }
            else if(args.userInfoDTO?.socialMedias?.instagram?.enabled!!) {
                if(!args.userInfoDTO?.socialMedias?.instagram?.url?.isBlank()!!){
                    profile_infos.setImageDrawable(resources.getDrawable(R.drawable.ic_insta_colored))
                    stateSwitchUrl = args.userInfoDTO?.socialMedias?.instagram?.url
                }
            }
            else if(args.userInfoDTO?.socialMedias?.whatsApp?.enabled!!) {
                if(!args.userInfoDTO?.socialMedias?.whatsApp?.url?.isBlank()!!){
                    profile_infos.setImageDrawable(resources.getDrawable(R.drawable.ic_whatsapp))
                    stateSwitchUrl = args.userInfoDTO?.socialMedias?.whatsApp?.url
                }
            }
            else if(args.userInfoDTO?.socialMedias?.linkedIn?.enabled!!){
                if(!args.userInfoDTO?.socialMedias?.linkedIn?.url?.isBlank()!!){
                    profile_infos.setImageDrawable(resources.getDrawable(R.drawable.ic_linkedin_colored))
                    stateSwitchUrl = args.userInfoDTO?.socialMedias?.linkedIn?.url
                }
            }
            else {
                profile_infos.setImageDrawable(resources.getDrawable(R.drawable.ic_icons8_contacts))
            }

        profile_modify_btn.visibility = View.INVISIBLE
        profile_follow_btn.visibility = View.VISIBLE
        profile_settings_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_more_vert_black_profile_24dp))
        profile_notif_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_back_thin))
        profile_follow_btn.setOnClickListener(this)
        isFollowed = args.userInfoDTO?.isInFollowers!!
        if(args.userInfoDTO?.isInFollowers!!) {
            profile_modify_btn.visibility = View.INVISIBLE
            profile_follow_btn.apply {
                setBorderColor(resources.getColor(android.R.color.darker_gray))
                setBorderWidth(1)
                setText(resources.getString(R.string.unfollow))
                setBackgroundColor(resources.getColor(android.R.color.transparent))
                setTextColor(resources.getColor(android.R.color.darker_gray))
            }
        }

        profile_name_toolbar.text = args.userInfoDTO?.userName
        profile_nbr_followers.text = args.userInfoDTO?.followers.toString()
        profile_nbr_following.text = args.userInfoDTO?.following.toString()

        if(args.userInfoDTO?.description.isNullOrBlank()) profile_desc.visibility = View.GONE else {
            profile_desc.visibility = View.VISIBLE
            profile_desc.text = args.userInfoDTO?.description
        }

        if(args.userInfoDTO?.givenName.isNullOrBlank()) profile_name.visibility = View.GONE else {
            profile_name.visibility = View.VISIBLE
            profile_name.text = args.userInfoDTO?.givenName
        }

        Glide
            .with(this)
            .load(args.userInfoDTO?.picture)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.circle_pic)
            .into(profile_pic_imageview)

        if(args.userInfoDTO?.status == STATUS.PRIVATE.ordinal && !args.userInfoDTO?.isInFollowers!!){
            scrollable.visibility = View.GONE
            profile_account_private.visibility = View.VISIBLE
        } else {
            profileEventPagerAdapter = ProfileEventPagerAdapter(childFragmentManager, lifecycle, showFilterBar, this, 2, args.userInfoDTO?.id!!)
            profile_vp?.apply {
                adapter = profileEventPagerAdapter
                isUserInputEnabled = false
                isSaveEnabled = false
                post {
                    profile_vp?.currentItem = 1
                }
            }

            profile_tablayout.setSelectedTabIndicatorColor(resources.getColor(android.R.color.darker_gray))
            profile_tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                override fun onTabReselected(tab: TabLayout.Tab?) {
                    profileEventPagerAdapter?.setShowFilterBar(true, tab?.position!!, true, false)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    profileEventPagerAdapter?.setShowFilterBar(true, tab?.position!!, false, false)
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when(tab?.position){
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

            TabLayoutMediator(profile_tablayout, profile_vp){ tab, position -> }.attach()
        }


        if(args.userInfoDTO?.isInFollowers!!){
            profile_calendar_btn.setOnClickListener(this)
            profile_settings_btn.setOnClickListener(this)
            profile_location.setOnClickListener(this)
            profile_followers_label.setOnClickListener(this)
            profile_nbr_followers.setOnClickListener(this)
            profile_nbr_following.setOnClickListener(this)
            profile_following_label.setOnClickListener(this)
            profile_infos.setOnClickListener(this)
        }
        profile_notif_btn.setOnClickListener(this)
        profile_modify_btn.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        when(v){
            profile_modify_btn -> {
            }
            profile_calendar_btn -> findNavController().navigate(ProfileSearchDirections.actionProfileSearchToProfileCalendarSearch(args.userInfoDTO?.id!!))
            profile_notif_btn -> {
                findNavController().popBackStack()
            }
            profile_settings_btn ->
                MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    title(text = args.userInfoDTO?.userName)
                    val listItems: MutableList<String> = mutableListOf(context.getString(R.string.share_to) , context.getString(R.string.report))
                    listItems (items = listItems){ _, _, text ->
                        when(text.toString()){
                            context.getString(R.string.report) -> Toast.makeText(
                                requireContext(),
                                "Reported",
                                Toast.LENGTH_SHORT
                            ).show()
                            context.getString(R.string.share_to) -> share(args.userInfoDTO!!)
                        }
                    }
                }
                profile_followers_label -> findNavController().navigate(ProfileSearchDirections.actionProfileSearchToFollowPageSearch(args.userInfoDTO?.id!!, true).setFollowers(1))
            profile_following_label -> findNavController().navigate(ProfileSearchDirections.actionProfileSearchToFollowPageSearch(args.userInfoDTO?.id!!, true).setFollowers(0))
            profile_nbr_followers -> findNavController().navigate(ProfileSearchDirections.actionProfileSearchToFollowPageSearch(args.userInfoDTO?.id!!, true).setFollowers(1))
            profile_nbr_following -> findNavController().navigate(ProfileSearchDirections.actionProfileSearchToFollowPageSearch(args.userInfoDTO?.id!!, true).setFollowers(0))
            profile_follow_btn -> {
                if (!isFollowed) {
                    if (args.userInfoDTO?.status == STATUS.PUBLIC.ordinal) {
                        followPublicUser()
                    } else if (!isFollowed && (args.userInfoDTO?.status == STATUS.PRIVATE.ordinal)) {
                        followPrivateUser()
                    }
                } else if(isFollowed){
                    unfollowUser()
                }
            }            profile_location -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.location)
                listItems(items = listOf(getString(R.string.no_location), getString(R.string.city), getString(
                                    R.string.address))) { dialog, index, text ->
                    when(index){
                        0 -> loginViewModel.markAsUnauthenticated()
                    }
                }
            }
            profile_infos -> {
                if(stateSwitchUrl.isNullOrBlank()) {
                    findNavController().navigate(ProfileSearchDirections.actionProfileSearchToProfilModifySearch(true, args.userInfoDTO))
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
        followViewModel.unfollowUser(tokenId!!, args.userInfoDTO?.id!!).observe(
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
        followViewModel.followPrivateUser(tokenId!!, args.userInfoDTO?.id!!)
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
        followViewModel.followPublicUser(tokenId!!, args.userInfoDTO?.id!!).observe(
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
        profileEventPagerAdapter?.setShowFilterBar(false, position, null, false)
    }

}
