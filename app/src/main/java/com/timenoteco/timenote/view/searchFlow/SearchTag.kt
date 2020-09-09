package com.timenoteco.timenote.view.searchFlow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.TimenoteComparator
import com.timenoteco.timenote.adapter.TimenotePagingAdapter
import com.timenoteco.timenote.adapter.UsersPagingAdapter
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.viewModel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search_people.*
import kotlinx.android.synthetic.main.fragment_search_tag.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchTag : Fragment(), TimenoteOptionsListener {

    private val searchViewModel : SearchViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search_tag, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val userAdapter = TimenotePagingAdapter(TimenoteComparator, this, this, true)
            search_tag_rv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = userAdapter
            }
            searchViewModel.getTagSearchLiveData().observe(viewLifecycleOwner, Observer {
                lifecycleScope.launch {
                    it.collectLatest {
                        userAdapter.submitData(it)
                    }
                }
            })
        }

    override fun onReportClicked() {

    }

    override fun onEditClicked() {
    }

    override fun onAlarmClicked() {
    }

    override fun onDeleteClicked() {
    }

    override fun onDuplicateClicked() {
    }

    override fun onAddressClicked() {
    }

    override fun onSeeMoreClicked() {
    }

    override fun onCommentClicked() {
    }

    override fun onPlusClicked() {
    }

    override fun onPictureClicked() {
    }

    override fun onHideToOthersClicked() {
    }

    override fun onMaskThisUser() {
    }

    override fun onDoubleClick() {
    }

    override fun onSeeParticipants() {
    }
}