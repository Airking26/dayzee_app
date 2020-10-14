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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.SuggestionAdapter
import com.timenoteco.timenote.adapter.SuggestionItemAdapter
import com.timenoteco.timenote.adapter.UsersShareWithPagingAdapter
import com.timenoteco.timenote.model.Category
import com.timenoteco.timenote.model.UserInfoDTO
import com.timenoteco.timenote.model.UserSuggested
import com.timenoteco.timenote.viewModel.FollowViewModel
import com.timenoteco.timenote.viewModel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search_explore_clicked.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchExploreClicked: Fragment(), SuggestionAdapter.SuggestionItemListener,
    SuggestionAdapter.SuggestionItemPicListener, UsersShareWithPagingAdapter.AddToSend,
    UsersShareWithPagingAdapter.SearchPeopleListener {

    private val followViewModel : FollowViewModel by activityViewModels()
    private val searchViewModel : SearchViewModel by activityViewModels()
    private lateinit var prefs: SharedPreferences
    private val args : SearchExploreClickedArgs by navArgs()
    val TOKEN: String = "TOKEN"
    private var tokenId: String? = null
    private lateinit var userByCategoryAdapter : UsersShareWithPagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(TOKEN, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search_explore_clicked, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        search_explore_clicked_title_toolbar.text = args.category?.subcategory

        userByCategoryAdapter = UsersShareWithPagingAdapter(UsersShareWithPagingAdapter.UserComparator, this, this)
        search_explore_clicked_rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userByCategoryAdapter
        }

        lifecycleScope.launch {
            searchViewModel.searchBasedOnCategory(tokenId!!, args.category!!).collectLatest {
                userByCategoryAdapter.submitData(it)
            }
        }
    }

    override fun onItemSelected(follow: Boolean) {
        //followViewModel.followPublicUser("", 0).observe(viewLifecycleOwner, Observer {})
    }

    override fun onPicClicked() {
        //findNavController().navigate(SearchExploreClickedDirections.actionSearchExploreClickedToProfileSearch())
    }

    override fun onAdd(userInfoDTO: UserInfoDTO) {

    }

    override fun onRemove(userInfoDTO: UserInfoDTO) {
    }

    override fun onSearchClicked(userInfoDTO: UserInfoDTO) {

    }
}