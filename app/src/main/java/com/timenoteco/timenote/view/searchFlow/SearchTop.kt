package com.timenoteco.timenote.view.searchFlow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.SuggestionAdapter
import com.timenoteco.timenote.common.BaseThroughFragment
import com.timenoteco.timenote.model.UserSuggested
import kotlinx.android.synthetic.main.fragment_search_top.*

class SearchTop: BaseThroughFragment(), SuggestionAdapter.SuggestionItemListener {

    private lateinit var topAdapter: SuggestionAdapter
    private var tops: Map<String, List<UserSuggested>> = mapOf()

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
        topAdapter = SuggestionAdapter(tops, this)
        search_top_rv.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = topAdapter
        }
    }

    override fun onItemSelected() {
    }
}