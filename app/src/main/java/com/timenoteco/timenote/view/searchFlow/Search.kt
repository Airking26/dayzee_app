package com.timenoteco.timenote.view.searchFlow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.paulrybitskyi.persistentsearchview.adapters.model.SuggestionItem
import com.paulrybitskyi.persistentsearchview.listeners.OnSuggestionChangeListener
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.SearchViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_search.*

class Search : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        search_viewpager.adapter = SearchViewPagerAdapter(this)

        TabLayoutMediator(search_tablayout, search_viewpager){ tab, position ->
            when(position){
                0 -> tab.text = "Top"
                1 -> tab.text = "Explore"
            }
        }.attach()

       /* with(persistent_searchview) {
            setOnLeftBtnClickListener {
                // Handle the left button click
            }
            setOnClearInputBtnClickListener {
                // Handle the clear input button click
            }


            setOnSearchConfirmedListener { searchView, query ->

            }

            setOnSearchQueryChangeListener { searchView, oldQuery, newQuery ->
                // Handle a search query change. This is the place where you'd
                // want load new suggestions based on the newQuery parameter.
            }

            setOnSuggestionChangeListener(object : OnSuggestionChangeListener {

                override fun onSuggestionPicked(suggestion: SuggestionItem) {
                    // Handle a suggestion pick event. This is the place where you'd
                    // want to perform a search against your data provider.
                }

                override fun onSuggestionRemoved(suggestion: SuggestionItem) {
                    // Handle a suggestion remove event. This is the place where
                    // you'd want to remove the suggestion from your data provider.
                }

            })
        }*/
    }

}
