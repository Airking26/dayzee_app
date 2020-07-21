package com.timenoteco.timenote.view.homeFlow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ItemTimenoteAdapter
import com.timenoteco.timenote.common.BaseThroughFragment
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.Timenote
import com.timenoteco.timenote.model.statusTimenote
import kotlinx.android.synthetic.main.fragment_home.*

class Home : BaseThroughFragment(), ItemTimenoteAdapter.TimenoteRecentClicked, TimenoteOptionsListener {

    private var timenotes: List<Timenote> = listOf()
    private lateinit var timenoteAdapter: ItemTimenoteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_home)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        timenotes = listOf(
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
                "In 23 days",
                12L,
                "www.google.com",
                statusTimenote.FREE
            ), Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("",
                    "",
                    ""),
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
                "In 23 days",
                12L,
                "www.google.com",
                statusTimenote.PAID
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
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                statusTimenote.NOANSWER
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
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                statusTimenote.PAID
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
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                statusTimenote.FREE
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
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                statusTimenote.NOANSWER
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
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                statusTimenote.FREE
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
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                statusTimenote.NOANSWER
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
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                statusTimenote.NOANSWER
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
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                statusTimenote.NOANSWER
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
                "10\nAug",
                "15:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                statusTimenote.NOANSWER

            )
        )
        timenoteAdapter = ItemTimenoteAdapter(timenotes, timenotes, true, this, this, this as Fragment)

        home_rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timenoteAdapter
        }
    }

    override fun onCommentClicked() {
        findNavController().navigate(HomeDirections.actionHomeToComments())
    }


    override fun onPlusClicked() {
    }

    override fun onPictureClicked() {
        findNavController().navigate(HomeDirections.actionHomeToProfile(true))
    }

    override fun onHideToOthersClicked() {

    }

    override fun onTimenoteRecentClicked() {
        findNavController().navigate(HomeDirections.actionHomeToDetailedTimenote())
    }

    override fun onSeeMoreClicked() {
        findNavController().navigate(HomeDirections.actionHomeToDetailedTimenote())
    }

    override fun onReportClicked() {
    }

    override fun onEditClicked() {
        findNavController().navigate(HomeDirections.actionHomeToCreateTimenote())
    }

    override fun onAlarmClicked() {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            dateTimePicker { dialog, datetime ->

            }
            lifecycleOwner(this@Home)
        }
    }

    override fun onDeleteClicked() {
    }

    override fun onDuplicateClicked() {
        findNavController().navigate(HomeDirections.actionHomeToCreateTimenote())
    }

    override fun onAddressClicked() {
        findNavController().navigate(HomeDirections.actionHomeToTimenoteAddress())
    }
}
