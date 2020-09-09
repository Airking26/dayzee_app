package com.timenoteco.timenote.view.searchFlow

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.UsersPagingAdapter
import com.timenoteco.timenote.viewModel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search_people.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchPeople: Fragment() {

    private val searchViewModel : SearchViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search_people, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val userAdapter = UsersPagingAdapter(UsersPagingAdapter.UserComparator)
        search_people_rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }
            searchViewModel.getUserSearchLiveData().observe(viewLifecycleOwner, Observer {
                lifecycleScope.launch {
                    it.collectLatest {
                        userAdapter.submitData(it)
                    }
                }
            })
    }
}