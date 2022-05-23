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
import androidx.navigation.fragment.navArgs
import androidx.paging.ExperimentalPagingApi
import androidx.paging.filter
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.UsersShareWithPagingAdapter
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.common.accessToken
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.viewModel.FollowViewModel
import com.dayzeeco.dayzee.viewModel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search_explore_clicked.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchExploreClicked: Fragment(), UsersShareWithPagingAdapter.AddToSend,
    UsersShareWithPagingAdapter.SearchPeopleListener {

    private val followViewModel : FollowViewModel by activityViewModels()
    private val searchViewModel : SearchViewModel by activityViewModels()
    private lateinit var prefs: SharedPreferences
    private val args : SearchExploreClickedArgs by navArgs()
    private var tokenId: String? = null
    private lateinit var userByCategoryAdapter : UsersShareWithPagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search_explore_clicked, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        search_explore_clicked_title_toolbar.text = args.category?.subcategory

        userByCategoryAdapter = UsersShareWithPagingAdapter(
            UsersShareWithPagingAdapter.UserComparator,
            this,
            this,
            null,
            null,
            null,
            false
            , Utils()
        )
        search_explore_clicked_rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userByCategoryAdapter
        }

        lifecycleScope.launch {
            searchViewModel.searchBasedOnCategory(tokenId!!, args.category!!).collectLatest {
                userByCategoryAdapter.submitData(it.filter { userInfoDTO -> !userInfoDTO.isInFollowers })

            }
        }
    }

    /*override fun onItemSelected(follow: Boolean, userInfoDTO: UserInfoDTO) {
        if(follow) {
            followViewModel.followPublicUser(tokenId!!, userInfoDTO.id!!)
                .observe(viewLifecycleOwner, Observer {
                    if(it.isSuccessful) ""
                })
        } else {
            followViewModel.unfollowUser(tokenId!!, userInfoDTO.id!!).observe(viewLifecycleOwner, Observer {
                if(it.isSuccessful) ""
            })
        }
    }

    override fun onPicClicked(userInfoDTO: UserInfoDTO) {
        findNavController().navigate(SearchExploreClickedDirections.actionSearchExploreClickedToProfileSearch(userInfoDTO))
    }*/

    override fun onAdd(userInfoDTO: UserInfoDTO, createGroup: Int?) {
        followViewModel.followPublicUser(tokenId!!, userInfoDTO.id!!)
            .observe(viewLifecycleOwner) {
                if (it.isSuccessful) userByCategoryAdapter.refresh()
            }
    }

    override fun onRemove(userInfoDTO: UserInfoDTO, createGroup: Int?) {
        followViewModel.unfollowUser(tokenId!!, userInfoDTO.id!!).observe(viewLifecycleOwner, Observer {
            if(it.isSuccessful) ""
        })
    }

    override fun onSearchClicked(userInfoDTO: UserInfoDTO, isTagged: Boolean) {
        findNavController().navigate(SearchExploreClickedDirections.actionGlobalProfileElse(2).setUserInfoDTO(userInfoDTO))
    }
}