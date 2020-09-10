package com.timenoteco.timenote.view.searchFlow

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.SuggestionAdapter
import com.timenoteco.timenote.common.BaseThroughFragment
import com.timenoteco.timenote.model.UserSuggested
import com.timenoteco.timenote.viewModel.FollowViewModel
import com.timenoteco.timenote.viewModel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_search_top.*
import me.samlss.broccoli.Broccoli

class SearchTop: Fragment(), SuggestionAdapter.SuggestionItemListener,
    SuggestionAdapter.SuggestionItemPicListener {


    private val broccoli = Broccoli()
    private lateinit var topAdapter: SuggestionAdapter
    private var tops: Map<String, List<UserSuggested>> = mapOf()
    private val followViewModel : FollowViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search_top, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tops = mapOf(
            "Football" to listOf(UserSuggested(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
            "Ligue 1",
            false),UserSuggested(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
            "Ligue 1",
            false),UserSuggested(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
            "Ligue 1",
            false),UserSuggested(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
            "Ligue 1",
            false)),
            "Music" to listOf(UserSuggested(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "Techno",
                false),UserSuggested(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "Techno",
                false),UserSuggested(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                "Techno",
                false)))
        topAdapter = SuggestionAdapter(tops, this, this, broccoli)
        search_top_rv.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = topAdapter
        }

    }

    override fun onItemSelected() {
        //followViewModel.followPublicUser("", 0).observe(viewLifecycleOwner, Observer {})
    }

    override fun onPicClicked() {
        //findNavController().navigate(SearchDirections.actionSearchToProfileSearch())
    }
}