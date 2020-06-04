package com.timenoteco.timenote.view.homeFlow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ItemTimenoteRegularAdapter
import com.timenoteco.timenote.model.Timenote
import kotlinx.android.synthetic.main.fragment_home.*

class Home : Fragment() {

    private var timenotes: List<Timenote> = listOf()
    private lateinit var timenoteAdapter: ItemTimenoteRegularAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        timenotes = listOf(
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party",
                true
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party",
                true
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party",
                true
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party",
                true
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party",
                true
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party",
                true
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party",
                true
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party",
                true
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party",
                true
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party",
                true
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party",
                true
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love#Summer#Dance#Party",
                true
            )
        )
        timenoteAdapter = ItemTimenoteRegularAdapter(timenotes)
        home_rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timenoteAdapter
        }

    }
}
