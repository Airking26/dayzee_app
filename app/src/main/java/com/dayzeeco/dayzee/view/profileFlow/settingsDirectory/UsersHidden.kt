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
import com.dayzeeco.dayzee.adapter.TimenoteRecentLoadStateAdapter
import com.dayzeeco.dayzee.adapter.UsersHiddenPagingAdapter
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.common.accessToken
import com.dayzeeco.dayzee.common.user_info_dto
import com.dayzeeco.dayzee.model.TimenoteHiddedCreationDTO
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.viewModel.LoginViewModel
import com.dayzeeco.dayzee.viewModel.TimenoteHiddedViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_hidden_users.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.Type

class UsersHidden: Fragment(), UsersHiddenPagingAdapter.HideUnhide,
    UsersHiddenPagingAdapter.SearchPeopleListener {

    private lateinit var usersHiddenPagingAdapter: UsersHiddenPagingAdapter
    private val hiddenViewModel: TimenoteHiddedViewModel by activityViewModels()
    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var prefs : SharedPreferences
    private var tokenId: String? = null
    private lateinit var userInfoDTOMe: UserInfoDTO

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
        inflater.inflate(R.layout.fragment_hidden_users, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usersHiddenPagingAdapter = UsersHiddenPagingAdapter(UsersHiddenPagingAdapter.UserComparator, this, this, false, Utils())
        lifecycleScope.launch {
            hiddenViewModel.getUsersHidden(tokenId!!, prefs).collectLatest {
                usersHiddenPagingAdapter.submitData(it)
            }
        }

        users_hidden_rv?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = usersHiddenPagingAdapter.withLoadStateFooter(footer = TimenoteRecentLoadStateAdapter{usersHiddenPagingAdapter.retry()})
        }


    }

    override fun onSwitch(userInfoDTO: UserInfoDTO, unHide: Boolean) {
        if(unHide) hiddenViewModel.removeUserFromHiddens(tokenId!!, userInfoDTO.id!!).observe(viewLifecycleOwner){
            if(it.code() == 401){
                loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner) { newToken ->
                    tokenId = newToken
                    hiddenViewModel.removeUserFromHiddens(tokenId!!, userInfoDTO.id!!).observe(viewLifecycleOwner){}
                }
            }
        } else hiddenViewModel.hideEventOrUSer(tokenId!!, TimenoteHiddedCreationDTO(userInfoDTOMe.id!!, null, userInfoDTO.id!!)).observe(viewLifecycleOwner){
            if(it.code() == 401){
                loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner) { newToken ->
                    tokenId = newToken
                    hiddenViewModel.hideEventOrUSer(tokenId!!, TimenoteHiddedCreationDTO(userInfoDTOMe.id!!, null, userInfoDTO.id!!)).observe(viewLifecycleOwner){}
                }
            }
        }
    }

    override fun onSearchClicked(userInfoDTO: UserInfoDTO, isTagged: Boolean) {
        findNavController().navigate(UsersHiddenDirections.actionGlobalProfileElse(4).setUserInfoDTO(userInfoDTO))
    }
}