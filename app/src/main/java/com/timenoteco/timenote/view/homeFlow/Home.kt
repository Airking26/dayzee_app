package com.timenoteco.timenote.view.homeFlow

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ItemTimenoteAdapter
import com.timenoteco.timenote.common.BaseThroughFragment
import com.timenoteco.timenote.model.Timenote
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_timenote.*

class Home : BaseThroughFragment(), ItemTimenoteAdapter.CommentListener, ItemTimenoteAdapter.PlusListener,
    ItemTimenoteAdapter.PictureProfileListener, ItemTimenoteAdapter.TimenoteRecentClicked {

    private var timenotes: List<Timenote> = listOf()
    private lateinit var timenoteAdapter: ItemTimenoteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_home)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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
        timenoteAdapter = ItemTimenoteAdapter(timenotes, timenotes, true, this, this, this, this)

        home_rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timenoteAdapter
        }
    }

    override fun onCommentClicked() {
        findNavController().navigate(HomeDirections.actionHomeToComments())
    }


    override fun onPlusClicked() {
        findNavController().navigate(HomeDirections.actionHomeToDetailedTimenote())
    }

    override fun onPictureClicked() {
        findNavController().navigate(HomeDirections.actionHomeToProfile(true))
    }

    override fun onTimenoteRecentClicked() {
        findNavController().navigate(HomeDirections.actionHomeToDetailedTimenote())
    }
}
