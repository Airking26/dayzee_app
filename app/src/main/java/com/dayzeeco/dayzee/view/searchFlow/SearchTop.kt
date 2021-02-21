package com.dayzeeco.dayzee.view.searchFlow

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.SuggestionAdapter
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.model.accessToken
import com.dayzeeco.dayzee.viewModel.FollowViewModel
import com.dayzeeco.dayzee.viewModel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search_top.*

class SearchTop: Fragment(), SuggestionAdapter.SuggestionItemListener,
    SuggestionAdapter.SuggestionItemPicListener {


    private lateinit var topAdapter: SuggestionAdapter
    private var tops: MutableMap<String, List<UserInfoDTO>> = mutableMapOf()
    private val followViewModel : FollowViewModel by activityViewModels()
    private val searchViewModel : SearchViewModel by activityViewModels()
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
            layoutManager = LinearLayoutManager(view.context)
            adapter = topAdapter
        }

        searchViewModel.getTop(tokenId!!).observe(viewLifecycleOwner, Observer { response ->
            response.body()?.groupBy { it.category.subcategory }?.entries?.map { (name, group) -> tops.put(name, group.map { it.users[0] }) }
        })

    }

    override fun onItemSelected(follow: Boolean, userInfoDTO: UserInfoDTO) {
        //followViewModel.followPublicUser("", 0).observe(viewLifecycleOwner, Observer {})
    }

    override fun onPicClicked(userInfoDTO: UserInfoDTO) {
        //findNavController().navigate(SearchDirections.actionSearchToProfileSearch())
    }
}