package com.timenoteco.timenote.view.profileFlow

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ItemProfileEventAdapter
import com.timenoteco.timenote.common.BaseThroughFragment
import com.timenoteco.timenote.model.Event
import com.timenoteco.timenote.model.Timenote
import kotlinx.android.synthetic.main.fragment_profile.*

class Profile : BaseThroughFragment(), View.OnClickListener {

    private lateinit var eventAdapter: ItemProfileEventAdapter
    private var events: MutableList<Event> = mutableListOf()
    private var timenotes: List<Timenote> = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?  =
        getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_profile)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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

        events = mutableListOf(
            Event("Beach Party",
                "34 Olhio Street",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "Dans 25 jours"), Event("Beach Party",
                "34 Olhio Street",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "Dans 25 jours"), Event("Beach Party",
                "34 Olhio Street",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "Dans 25 jours"), Event("Beach Party",
                "34 Olhio Street",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "Dans 25 jours"), Event("Beach Party",
                "34 Olhio Street",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "Dans 25 jours"), Event("Beach Party",
                "34 Olhio Street",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "Dans 25 jours"), Event("Beach Party",
                "34 Olhio Street",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "Dans 25 jours"), Event("Beach Party",
                "34 Olhio Street",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "Dans 25 jours"), Event("Beach Party",
                "34 Olhio Street",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "Dans 25 jours")
        )
        timenotes = listOf(
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party",
                true,
                "2020",
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days"
            ),
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party",
                true,
                "2020",
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days"
            ),
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party",
                true,
                "2020",
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days"
            ),
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party",
                true,
                "2020",
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days"
            ),
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party",
                true,
                "2020",
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days"
            ),
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party",
                true,
                "2020",
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days"
            ),
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party",
                true,
                "2020",
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days"
            ),
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party",
                true,
                "2020",
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days"
            ),
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
                "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party#Beach#Sunset#Love#Summer#Dance#Party",
                true,
                "2020",
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days"
            )
        )


        eventAdapter = ItemProfileEventAdapter(timenotes)

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
                events.removeAt(position)
                eventAdapter.notifyDataSetChanged()
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(profile_rv)
    }

    override fun onClick(v: View?) {
        when(v){
            profile_modify_btn -> findNavController().navigate(ProfileDirections.actionProfileToProfilModify())
            profile_calendar_btn -> findNavController().navigate(ProfileDirections.actionProfileToProfileCalendar())
            profile_settings_btn -> findNavController().navigate(ProfileDirections.actionProfileToSettings())
            profile_notif_btn -> findNavController().navigate(ProfileDirections.actionProfileToNotifications())
            profile_view_type_btn -> {
                val type = eventAdapter.switchViewType()
                if(type == 1) profile_view_type_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_view_list_black_24dp))
                else profile_view_type_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_view_module_black_24dp))
            }
            profile_arrow_forward_btn -> profile_arrow_forward_btn.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
        }
    }

}
