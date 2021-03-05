package com.dayzeeco.dayzee.view.loginFlow

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.SuggestionAdapter
import com.dayzeeco.dayzee.common.accessToken
import com.dayzeeco.dayzee.common.stringLiveData
import com.dayzeeco.dayzee.model.SubCategoryRated
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.view.searchFlow.SearchDirections
import com.dayzeeco.dayzee.viewModel.FollowViewModel
import com.dayzeeco.dayzee.viewModel.LoginViewModel
import com.dayzeeco.dayzee.viewModel.SearchViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_preference_suggestion.*
import kotlinx.android.synthetic.main.fragment_search_top.*
import java.lang.reflect.Type
import kotlin.math.log


class PreferenceSuggestion : Fragment(), View.OnClickListener,
    SuggestionAdapter.SuggestionItemListener, SuggestionAdapter.SuggestionItemPicListener {

    private val args : PreferenceCategoryArgs by navArgs()
    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var topAdapter: SuggestionAdapter
    private var tops: MutableMap<SubCategoryRated, List<UserInfoDTO>> = mutableMapOf()
    private lateinit var prefs: SharedPreferences
    private var tokenId: String? = null
    private val searchViewModel : SearchViewModel by activityViewModels()
    private val followViewModel : FollowViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_preference_suggestion, container, false)
        view.isFocusableInTouchMode = true
        view.requestFocus();
        view.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN){
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    false
                }
            }
            true
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        suggestion_ok_btn.setOnClickListener(this)

        topAdapter = SuggestionAdapter(tops, this, this)
        suggestion_card_rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = topAdapter
        }

        searchViewModel.getTop(tokenId!!).observe(viewLifecycleOwner, { response ->
            if(response.isSuccessful) {
                response.body()?.forEach { if(it.rating > 0 && it.users.isNotEmpty()) tops[SubCategoryRated(it.category, it.rating)] = if(it.users.size > it.rating) it.users.subList(0, it.rating) else it.users }
                topAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onClick(v: View?) {
        when(v){
            suggestion_ok_btn -> {
                if(args.isInLogin) loginViewModel.markAsAuthenticated()
                else findNavController().popBackStack(R.id.myProfile, false)
            }
        }
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
        if(args.isInLogin) findNavController().navigate(SearchDirections.actionGlobalProfileElse(4).setUserInfoDTO(userInfoDTO))
        else findNavController().navigate(SearchDirections.actionGlobalProfileElse(2).setUserInfoDTO(userInfoDTO))
    }

}
