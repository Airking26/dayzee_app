package com.dayzeeco.dayzee.view.searchFlow

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.TimenoteLoadStateAdapter
import com.dayzeeco.dayzee.adapter.UsersPagingAdapter
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.viewModel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search_people.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.Type

class SearchPeople: Fragment(), UsersPagingAdapter.SearchPeopleListener {

    private val searchViewModel : SearchViewModel by activityViewModels()
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search_people, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
        val userInfoDTOPref = Gson().fromJson<UserInfoDTO>(prefs.getString("UserInfoDTO", ""), typeUserInfo)

        var userAdapter = UsersPagingAdapter(
            UsersPagingAdapter.UserComparator,
            null,
            this,
            null,
            null
        )
        search_people_rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter =  userAdapter!!.withLoadStateFooter(
                footer = TimenoteLoadStateAdapter{ userAdapter!!.retry() }
            )
        }

        searchViewModel.getUserSearchLiveData().observe(viewLifecycleOwner, Observer {
                lifecycleScope.launch {
                    it.collectLatest {
                        userAdapter.submitData(it.filter { userInfoDTO -> userInfoDTO.id != userInfoDTOPref.id })
                        userAdapter.notifyDataSetChanged()
                    }
                }
            })

        searchViewModel.getSearchIsEmptyLiveData().observe(viewLifecycleOwner, Observer {
            if(it) {
                lifecycleScope.launch {
                    userAdapter.submitData(PagingData.empty())
                }
            }
        })
    }

    override fun onSearchClicked(userInfoDTO: UserInfoDTO) {
        findNavController().navigate(SearchDirections.actionGlobalProfileElse(2).setUserInfoDTO(userInfoDTO))
    }

    override fun onUnfollow(id: String) {

    }

    override fun onRemove(id: String) {
    }
}