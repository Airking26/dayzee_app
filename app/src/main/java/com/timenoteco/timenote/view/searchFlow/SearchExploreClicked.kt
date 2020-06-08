package com.timenoteco.timenote.view.searchFlow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.SuggestionAdapter
import com.timenoteco.timenote.adapter.SuggestionItemAdapter
import com.timenoteco.timenote.model.UserSuggested
import kotlinx.android.synthetic.main.fragment_search_explore_clicked.*

class SearchExploreClicked: Fragment(), SuggestionAdapter.SuggestionItemListener {

    private var suggestions: List<UserSuggested> = listOf()
    private lateinit var suggestionItemAdapter: SuggestionItemAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search_explore_clicked, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        suggestions = listOf(UserSuggested(
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
                false))

        suggestionItemAdapter = SuggestionItemAdapter(suggestions, this)
        search_explore_clicked_rv.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = suggestionItemAdapter
        }
    }

    override fun onItemSelected() {

    }
}