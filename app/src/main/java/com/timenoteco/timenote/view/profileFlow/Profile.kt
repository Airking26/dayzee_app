package com.timenoteco.timenote.view.profileFlow

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.renderscript.RenderScript
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ProfilePastFuturePagerAdapter
import com.timenoteco.timenote.androidView.blurry.RSBlurProcessor
import com.timenoteco.timenote.common.BaseThroughFragment
import com.timenoteco.timenote.listeners.OnRemoveFilterBarListener
import com.timenoteco.timenote.model.StatusTimenote
import com.timenoteco.timenote.model.Timenote
import kotlinx.android.synthetic.main.fragment_profile.*
import java.text.SimpleDateFormat
import java.util.*


class Profile : BaseThroughFragment(), View.OnClickListener, OnRemoveFilterBarListener {

    private var profilePastFuturePagerAdapter: ProfilePastFuturePagerAdapter? = null
    private var showFilterBar: Boolean = false
    private val args : ProfileArgs by navArgs()
    private var timenotes: MutableList<Timenote> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_profile)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val simpleDateFormatDayName= SimpleDateFormat("EEE.", Locale.getDefault())
        val simpleDateFormatDayNumber = SimpleDateFormat("dd", Locale.getDefault())

        profile_day_name_calendar.text = simpleDateFormatDayName.format(System.currentTimeMillis())
        profile_day_number_calendar.text = simpleDateFormatDayNumber.format(System.currentTimeMillis())

        timenotes = mutableListOf(
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10\nAug",
                "15:30 PM",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.FREE,
                0
            ), Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10Aug.\n15:45",
                "11Aug.\n12:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.PAID,
                1
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10\nAug",
                "15:30 PM",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.FREE,
                0
            ), Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10Aug.\n15:45",
                "11Aug.\n12:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.PAID,
                1
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10\nAug",
                "15:30 PM",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.FREE,
                0
            ), Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10Aug.\n15:45",
                "11Aug.\n12:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.PAID,
                1
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10\nAug",
                "15:30 PM",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.FREE,
                0
            ), Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10Aug.\n15:45",
                "11Aug.\n12:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.PAID,
                1
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10\nAug",
                "15:30 PM",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.FREE,
                0
            ), Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10Aug.\n15:45",
                "11Aug.\n12:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.PAID,
                1
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10\nAug",
                "15:30 PM",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.FREE,
                0
            ), Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10Aug.\n15:45",
                "11Aug.\n12:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.PAID,
                1
            )
        )

        if(args.whereFrom){
            profile_modify_btn.visibility = View.INVISIBLE
            profile_follow_btn.visibility = View.VISIBLE
            profile_settings_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_info_24))
            profile_notif_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_share_black_24dp))
        }

        Glide
            .with(this)
            .load("https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792")
            .apply(RequestOptions.circleCropTransform())
            .into(profile_pic_imageview)

        profilePastFuturePagerAdapter = ProfilePastFuturePagerAdapter(childFragmentManager, lifecycle, showFilterBar, this)
        profile_vp?.apply {
            adapter = profilePastFuturePagerAdapter
            isUserInputEnabled = false
            isSaveEnabled = false
            post {
                profile_vp?.currentItem = 1
            }
        }

        profile_tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
                profilePastFuturePagerAdapter?.setShowFilterBar(true, tab?.position!!, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                profilePastFuturePagerAdapter?.setShowFilterBar(true, tab?.position!!, false)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.d(TAG, "onTabSelected: ")
            }

        })

        TabLayoutMediator(profile_tablayout, profile_vp){ tab, position -> }.attach()

        profile_tablayout.getTabAt(1)?.icon = resources.getDrawable(R.drawable.ic_baseline_keyboard_arrow_right_24)
        profile_tablayout.getTabAt(0)?.icon = resources.getDrawable(R.drawable.ic_baseline_keyboard_arrow_left_24)

        profile_modify_btn.setOnClickListener(this)
        profile_calendar_btn.setOnClickListener(this)
        profile_settings_btn.setOnClickListener(this)
        profile_notif_btn.setOnClickListener(this)
        profile_location.setOnClickListener(this)
        profile_followers_label.setOnClickListener(this)
        profile_nbr_followers.setOnClickListener(this)
        profile_nbr_following.setOnClickListener(this)
        profile_following_label.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v){
            profile_modify_btn -> findNavController().navigate(ProfileDirections.actionProfileToProfilModify())
            profile_calendar_btn -> findNavController().navigate(ProfileDirections.actionProfileToProfileCalendar())
            profile_settings_btn -> findNavController().navigate(ProfileDirections.actionProfileToMenu())
            profile_notif_btn -> findNavController().navigate(ProfileDirections.actionProfileToNotifications())
            profile_followers_label -> findNavController().navigate(ProfileDirections.actionProfileToFollowPage())
            profile_following_label -> findNavController().navigate(ProfileDirections.actionProfileToFollowPage())
            profile_nbr_followers -> findNavController().navigate(ProfileDirections.actionProfileToFollowPage())
            profile_nbr_following -> findNavController().navigate(ProfileDirections.actionProfileToFollowPage())
            profile_location -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.location)
                listItems(items = listOf(getString(R.string.no_location), getString(R.string.city), getString(
                                    R.string.address))) { dialog, index, text ->  }
            }
        }
    }

    override fun onHideFilterBarClicked(position:Int?) {
        profilePastFuturePagerAdapter?.setShowFilterBar(false, position, null)
    }

}
