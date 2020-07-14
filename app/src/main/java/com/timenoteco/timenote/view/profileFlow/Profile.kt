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
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ItemProfileEventAdapter
import com.timenoteco.timenote.common.BaseThroughFragment
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.Timenote
import kotlinx.android.synthetic.main.fragment_profile.*

class Profile : BaseThroughFragment(), View.OnClickListener, TimenoteOptionsListener {

    private lateinit var itemTouchHelper: ItemTouchHelper
    private val args : ProfileArgs by navArgs()
    private lateinit var eventAdapter: ItemProfileEventAdapter
    private var timenotes: MutableList<Timenote> = mutableListOf()

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

        profile_modify_btn.setOnClickListener(this)
        profile_calendar_btn.setOnClickListener(this)
        profile_settings_btn.setOnClickListener(this)
        profile_view_type_btn.setOnClickListener(this)
        profile_filter_btn.setOnClickListener(this)
        profile_notif_btn.setOnClickListener(this)
        profile_arrow_forward_btn.setOnClickListener(this)
        profile_arrow_back_btn.setOnClickListener(this)
        profile_filter_btn.setOnClickListener(this)
        profile_location.setOnClickListener(this)
        profile_followers_label.setOnClickListener(this)
        profile_nbr_followers.setOnClickListener(this)
        profile_nbr_following.setOnClickListener(this)
        profile_following_label.setOnClickListener(this)

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
                "15:30",
                "Beach Party",
                "In 23 days"
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
                "15:30",
                "Beach Party",
                "In 23 days"
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
                "15:30",
                "Beach Party",
                "In 23 days"
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
                "15:30",
                "Beach Party",
                "In 23 days"
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
                "15:30",
                "Beach Party",
                "In 23 days"
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
                "15:30",
                "Beach Party",
                "In 23 days"
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
                "15:30",
                "Beach Party",
                "In 23 days"
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
                "15:30",
                "Beach Party",
                "In 23 days"
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
                "15:30",
                "Beach Party",
                "In 23 days"
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
                "15:30",
                "Beach Party",
                "In 23 days"
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
                "15:30",
                "Beach Party",
                "In 23 days"
            )
        )

        eventAdapter = ItemProfileEventAdapter(timenotes, this as Fragment, this)

        profile_rv.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = eventAdapter
        }

        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                Toast.makeText(context, "on Move", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                Toast.makeText(context, "on Swiped ", Toast.LENGTH_SHORT).show()
                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.adapterPosition
                timenotes.removeAt(position)
                eventAdapter.notifyDataSetChanged()
            }
        }

        itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
    }

    override fun onClick(v: View?) {
        when(v){
            profile_modify_btn -> findNavController().navigate(ProfileDirections.actionProfileToProfilModify())
            profile_calendar_btn -> findNavController().navigate(ProfileDirections.actionProfileToProfileCalendar())
            profile_settings_btn -> findNavController().navigate(ProfileDirections.actionProfileToSettings())
            profile_notif_btn -> findNavController().navigate(ProfileDirections.actionProfileToNotifications())
            profile_filter_btn -> findNavController().navigate(ProfileDirections.actionProfileToFilterProfile())
            profile_followers_label -> findNavController().navigate(ProfileDirections.actionProfileToFollowPage())
            profile_following_label -> findNavController().navigate(ProfileDirections.actionProfileToFollowPage())
            profile_nbr_followers -> findNavController().navigate(ProfileDirections.actionProfileToFollowPage())
            profile_nbr_following -> findNavController().navigate(ProfileDirections.actionProfileToFollowPage())
            profile_view_type_btn -> {
                var viewType = eventAdapter.switchViewType()
                if(viewType == 1) {
                    profile_view_type_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_view_list_black_24dp))
                    profile_rv.setBackgroundColor(resources.getColor(android.R.color.white))
                    itemTouchHelper.attachToRecyclerView(null)
                }
                else {
                    profile_view_type_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_view_module_black_24dp))
                    itemTouchHelper.attachToRecyclerView(profile_rv)
                }
            }
            profile_arrow_forward_btn -> profile_rv.setBackgroundColor(resources.getColor(R.color.colorBackgroundForward))
            profile_arrow_back_btn -> profile_rv.setBackgroundColor(resources.getColor(R.color.colorBackgroundBackward))
            profile_arrow_forward_btn -> profile_arrow_forward_btn.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
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
