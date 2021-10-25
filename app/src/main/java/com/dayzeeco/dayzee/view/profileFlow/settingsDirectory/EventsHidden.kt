package com.dayzeeco.dayzee.view.profileFlow.settingsDirectory

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.EventsHiddenPagingAdapter
import com.dayzeeco.dayzee.adapter.TimenoteComparator
import com.dayzeeco.dayzee.adapter.TimenoteRecentLoadStateAdapter
import com.dayzeeco.dayzee.adapter.UsersHiddenPagingAdapter
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.common.accessToken
import com.dayzeeco.dayzee.common.user_info_dto
import com.dayzeeco.dayzee.model.TimenoteHiddedCreationDTO
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.paging.HiddedEventsPagingSource
import com.dayzeeco.dayzee.viewModel.LoginViewModel
import com.dayzeeco.dayzee.viewModel.TimenoteHiddedViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_hidden_events.*
import kotlinx.android.synthetic.main.fragment_hidden_users.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.Type

class EventsHidden: Fragment(), EventsHiddenPagingAdapter.GoToEvent,
    EventsHiddenPagingAdapter.HideUnhideEvent {

    private val hiddenViewModel: TimenoteHiddedViewModel by activityViewModels()
    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var prefs : SharedPreferences
    private var tokenId: String? = null
    private lateinit var eventsHiddenPagingAdapter: EventsHiddenPagingAdapter
    private lateinit var userInfoDTOMe: UserInfoDTO
    private val utils : Utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        tokenId = prefs.getString(accessToken, null)
        val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
        userInfoDTOMe = Gson().fromJson(prefs.getString(user_info_dto, ""), typeUserInfo)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_hidden_events, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventsHiddenPagingAdapter = EventsHiddenPagingAdapter(TimenoteComparator, this, this, false, utils)
        lifecycleScope.launch {
            hiddenViewModel.getEventsHidden(tokenId!!, prefs).collectLatest {
                eventsHiddenPagingAdapter.submitData(it)
            }
        }

        events_hidden_rv?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventsHiddenPagingAdapter.withLoadStateFooter(footer = TimenoteRecentLoadStateAdapter{eventsHiddenPagingAdapter.retry()})
        }


    }

    override fun onEventClicked(timenote: TimenoteInfoDTO, isTagged: Boolean) {
        findNavController().navigate(EventsHiddenDirections.actionGlobalDetailedTimenote(4, timenote))
    }

    override fun onSwitch(timenote: TimenoteInfoDTO, unHide: Boolean) {
        if(unHide) hiddenViewModel.removeEventFromHiddens(tokenId!!, timenote.id).observe(viewLifecycleOwner){
            if(it.code() == 401){
                loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner) { newToken ->
                    tokenId = newToken
                    hiddenViewModel.removeEventFromHiddens(tokenId!!, timenote.id).observe(viewLifecycleOwner){}
                }
            }
        } else hiddenViewModel.hideEventOrUSer(tokenId!!, TimenoteHiddedCreationDTO(userInfoDTOMe.id!!, timenote.id, null)).observe(viewLifecycleOwner){
            if(it.code() == 401){
                loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner) { newToken ->
                    tokenId = newToken
                    hiddenViewModel.hideEventOrUSer(tokenId!!, TimenoteHiddedCreationDTO(userInfoDTOMe.id!!, timenote.id, null)).observe(viewLifecycleOwner){}
                }
            }
        }    }
}