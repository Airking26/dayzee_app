package com.dayzeeco.dayzee.view.searchFlow

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.SuggestionAdapter
import com.dayzeeco.dayzee.model.SubCategoryRated
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.model.accessToken
import com.dayzeeco.dayzee.viewModel.FollowViewModel
import com.dayzeeco.dayzee.viewModel.LoginViewModel
import com.dayzeeco.dayzee.viewModel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search_top.*

class SearchTop: Fragment(), SuggestionAdapter.SuggestionItemListener,
    SuggestionAdapter.SuggestionItemPicListener {


    private lateinit var topAdapter: SuggestionAdapter
    private var tops: MutableMap<SubCategoryRated, List<UserInfoDTO>> = mutableMapOf()
    private val followViewModel : FollowViewModel by activityViewModels()
    private val searchViewModel : SearchViewModel by activityViewModels()
    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var prefs: SharedPreferences
    private var tokenId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search_top, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        topAdapter = SuggestionAdapter(tops, this, this)
        search_top_rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = topAdapter
        }

        searchViewModel.getTop(tokenId!!).observe(viewLifecycleOwner, { response ->
            response.body()?.forEach {
                if(it.rating > 0 && it.users.isNotEmpty()) tops[SubCategoryRated(it.category, it.rating)] = if(it.users.size > it.rating) it.users.subList(0, it.rating) else it.users }
            search_top_pb.visibility = View.GONE
            topAdapter.notifyDataSetChanged()
        })

    }

    override fun onItemSelected(follow: Boolean, userInfoDTO: UserInfoDTO) {
        followViewModel.followPublicUser(tokenId!!, userInfoDTO.id!!).observe(viewLifecycleOwner, {
            if(it.isSuccessful) topAdapter.notifyDataSetChanged()
            if(it.code() == 401) {
                loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner){ newAccessToken ->
                    tokenId = newAccessToken
                    followViewModel.followPublicUser(tokenId!!, userInfoDTO?.id!!).observe(viewLifecycleOwner){ rsp ->
                        if(rsp.isSuccessful) topAdapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    override fun onPicClicked(userInfoDTO: UserInfoDTO) {
        findNavController().navigate(SearchDirections.actionGlobalProfileElse(2).setUserInfoDTO(userInfoDTO))
    }
}