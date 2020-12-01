package com.timenoteco.timenote.view.profileFlow.menuDirectory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ProfileEventPagerAdapter
import com.timenoteco.timenote.listeners.OnRemoveFilterBarListener
import kotlinx.android.synthetic.main.fragment_profile.*
import java.text.SimpleDateFormat
import java.util.*

class ProfilePreview: Fragment(), OnRemoveFilterBarListener {

    private var profileEventPagerAdapter: ProfileEventPagerAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_profile_preview, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val simpleDateFormatDayName= SimpleDateFormat("EEE.", Locale.getDefault())
        val simpleDateFormatDayNumber = SimpleDateFormat("dd", Locale.getDefault())

        profile_day_name_calendar.text = simpleDateFormatDayName.format(System.currentTimeMillis())
        profile_day_number_calendar.text = simpleDateFormatDayNumber.format(System.currentTimeMillis())

        Glide
            .with(this)
            .load("https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792")
            .apply(RequestOptions.circleCropTransform())
            .into(profile_pic_imageview)

        scrollable.visibility = View.GONE
        profile_account_private.visibility = View.VISIBLE

       // profilePastFuturePagerAdapter = ProfilePastFuturePagerAdapter(childFragmentManager, lifecycle, false, this, 4)
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

    override fun onHideFilterBarClicked(position: Int?) {

    }
}