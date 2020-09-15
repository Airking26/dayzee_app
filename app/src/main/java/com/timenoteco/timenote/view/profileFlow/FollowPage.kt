package com.timenoteco.timenote.view.profileFlow

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.UsersPagingAdapter
import com.timenoteco.timenote.model.UserInfoDTO
import com.timenoteco.timenote.viewModel.FollowViewModel
import com.timenoteco.timenote.viewModel.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_follow_page.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FollowPage : Fragment(), UsersPagingAdapter.SearchPeopleListener {

    private val followViewModel : FollowViewModel by activityViewModels()
    private lateinit var usersPagingAdapter: UsersPagingAdapter
    private lateinit var prefs: SharedPreferences
    private val args : FollowPageArgs by navArgs()
    val TOKEN: String = "TOKEN"
    private var tokenId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(TOKEN, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_follow_page, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        usersPagingAdapter = UsersPagingAdapter(UsersPagingAdapter.UserComparator, this)
        users_rv.layoutManager = LinearLayoutManager(requireContext())
        users_rv.adapter = usersPagingAdapter
        lifecycleScope.launch{
            followViewModel.getUsers(tokenId!!, followers = args.followers).collectLatest {
                usersPagingAdapter.submitData(it)
            }
        }
    }

    override fun onSearchClicked(userInfoDTO: UserInfoDTO) {

    }
}