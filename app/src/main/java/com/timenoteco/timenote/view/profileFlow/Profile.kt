package com.timenoteco.timenote.view.profileFlow

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayoutMediator
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ItemProfileEventAdapter
import com.timenoteco.timenote.adapter.ProfilePastFuturePagerAdapter
import com.timenoteco.timenote.common.BaseThroughFragment
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.Timenote
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_search.*

class Profile : BaseThroughFragment(), View.OnClickListener, TimenoteOptionsListener {

    private val args : ProfileArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_profile)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if(args.whereFrom){
            profile_modify_btn.visibility = View.INVISIBLE
            profile_follow_btn.visibility = View.VISIBLE
            profile_message_btn.visibility = View.VISIBLE
            profile_settings_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_info_24))
            profile_notif_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_share_black_24dp))
        }

        Glide
            .with(this)
            .load("https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792")
            .apply(RequestOptions.circleCropTransform())
            .into(profile_pic_imageview)

        val profilePastFuturePagerAdapter = ProfilePastFuturePagerAdapter(this)
        profile_vp.adapter = profilePastFuturePagerAdapter
        TabLayoutMediator(profile_tablayout, profile_vp){ tab, position ->
            when(position){
            }
        }.attach()

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
            profile_settings_btn -> findNavController().navigate(ProfileDirections.actionProfileToSettings())
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

    override fun onReportClicked() {
    }

    override fun onEditClicked() {
    }

    override fun onAlarmClicked() {
    }

    override fun onDeleteClicked() {
    }

    override fun onDuplicateClicked() {
    }

    override fun onAddressClicked() {
    }

    override fun onSeeMoreClicked() {
    }

    override fun onCommentClicked() {
    }

    override fun onPlusClicked() {
    }

    override fun onPictureClicked() {
    }

    override fun onHideToOthersClicked() {

    }

}
