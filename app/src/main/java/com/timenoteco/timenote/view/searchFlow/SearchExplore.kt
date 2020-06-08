package com.timenoteco.timenote.view.searchFlow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.SearchExploreCategoryAdapter
import kotlinx.android.synthetic.main.fragment_search_explore.*

class SearchExplore : Fragment(), SearchExploreCategoryAdapter.SearchSubCategoryListener,
    SearchExploreCategoryAdapter.SearchCategoryListener {

    private lateinit var searchExploreAdapter : SearchExploreCategoryAdapter
    private var explores: Map<String, List<String>> = mapOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search_explore, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        explores = mapOf("Sport" to listOf("Football", "Tennis"),
        "Religion" to listOf(), "Music" to listOf("Techno", "Classique", "Jazz"), "Livre" to listOf(), "Cinema" to listOf())
        searchExploreAdapter = SearchExploreCategoryAdapter(explores, this, this)
        search_explore_root_rv.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = searchExploreAdapter
        }
    }

    override fun onSubCategorySelected() {

    }

    override fun onCategorySelected() {
        findNavController().navigate(R.id.action_global_searchExploreClicked)
    }
}