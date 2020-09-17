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
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.TimenoteComparator
import com.timenoteco.timenote.adapter.TimenotePagingAdapter
import com.timenoteco.timenote.adapter.UsersPagingAdapter
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.TimenoteInfoDTO
import com.timenoteco.timenote.model.UserInfoDTO
import com.timenoteco.timenote.viewModel.FollowViewModel
import com.timenoteco.timenote.viewModel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search_people.*
import kotlinx.android.synthetic.main.fragment_search_tag.*
import kotlinx.android.synthetic.main.users_participating.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchTag : Fragment(), TimenoteOptionsListener, UsersPagingAdapter.SearchPeopleListener {

    private val searchViewModel : SearchViewModel by activityViewModels()
    private val followViewModel : FollowViewModel by activityViewModels()
    private val utils = Utils()
    private lateinit var prefs : SharedPreferences
    private var tokenId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString("TOKEN", null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search_tag, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val userAdapter = TimenotePagingAdapter(TimenoteComparator, this, this, true, utils)
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


    override fun onShareClicked(infoDTO: TimenoteInfoDTO) {
        val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            customView(R.layout.users_participating)
            lifecycleOwner(this@SearchTag)
        }

        val recyclerview = dial.getCustomView().users_participating_rv
        val userAdapter = UsersPagingAdapter(UsersPagingAdapter.UserComparator, infoDTO, this)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.adapter = userAdapter
        lifecycleScope.launch{
            followViewModel.getUsers(tokenId!!, false).collectLatest {
                userAdapter.submitData(it)
            }
        }
    }


    override fun onAlarmClicked(timenoteInfoDTO: TimenoteInfoDTO) {
    }

    override fun onDeleteClicked() {
    }

    override fun onDuplicateClicked(timenoteInfoDTO: TimenoteInfoDTO) {
    }

    override fun onAddressClicked() {
    }

    override fun onSeeMoreClicked(event: TimenoteInfoDTO) {
    }

    override fun onCommentClicked(event: TimenoteInfoDTO) {
    }

    override fun onPlusClicked(timenoteInfoDTO: TimenoteInfoDTO) {
    }

    override fun onPictureClicked() {
    }

    override fun onHideToOthersClicked() {
    }

    override fun onMaskThisUser() {
    }

    override fun onDoubleClick() {
    }

    override fun onSeeParticipants(timenoteInfoDTO: TimenoteInfoDTO) {
    }

    override fun onSearchClicked(userInfoDTO: UserInfoDTO, infoDTO: TimenoteInfoDTO?) {
    }
}