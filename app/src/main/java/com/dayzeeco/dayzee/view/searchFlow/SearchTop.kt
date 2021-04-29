package com.dayzeeco.dayzee.view.searchFlow

import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Log.DEBUG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dayzeeco.dayzee.BuildConfig.DEBUG
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.SuggestionAdapter
import com.dayzeeco.dayzee.adapter.UsersTopPagingAdapter
import com.dayzeeco.dayzee.common.accessToken
import com.dayzeeco.dayzee.common.list_subcategory_rated
import com.dayzeeco.dayzee.common.stringLiveData
import com.dayzeeco.dayzee.model.SubCategoryRated
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.viewModel.FollowViewModel
import com.dayzeeco.dayzee.viewModel.LoginViewModel
import com.dayzeeco.dayzee.viewModel.SearchViewModel
import com.dayzeeco.dayzee.webService.repo.DayzeeRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_search_top.*
import kotlinx.coroutines.launch
import java.lang.reflect.Type

class SearchTop: Fragment(), SuggestionAdapter.SuggestionItemListener,
    SuggestionAdapter.SuggestionItemPicListener {


    private lateinit var topAdapter: SuggestionAdapter
    private val followViewModel : FollowViewModel by activityViewModels()
    private val searchViewModel : SearchViewModel by activityViewModels()
    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var preferencesCategoryRated: MutableList<SubCategoryRated>
    private lateinit var prefs: SharedPreferences
    private var tokenId: String? = null
    private val searchService = DayzeeRepository().getSearchService()
    private var mapSCRtoLUI: MutableMap<SubCategoryRated, List<UserInfoDTO>?> = mutableMapOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search_top, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        prefs.stringLiveData(
            list_subcategory_rated, Gson().toJson(prefs.getString(
            list_subcategory_rated, null))).observe(viewLifecycleOwner, {
            val typeSubCat: Type = object : TypeToken<MutableList<SubCategoryRated?>>() {}.type
            preferencesCategoryRated = Gson().fromJson(it, typeSubCat) ?: mutableListOf()
            getTop()
        })
    }

    private fun getTop(){
                lifecycleScope.launch {
                    val m : MutableMap<SubCategoryRated, List<UserInfoDTO>?> = mutableMapOf()
                    preferencesCategoryRated.forEach { scr ->
                        Log.d(TAG, scr.category.subcategory)
                        if (scr.rating > 0) {
                            var i = 0
                            var req =
                                searchService.searchBasedOnCategory(
                                    "Bearer " + tokenId!!,
                                    scr.category,
                                    i
                                )
                            if (req.code() == 401) {
                                loginViewModel.refreshToken(prefs)
                                    .observe(viewLifecycleOwner, { nat ->
                                        tokenId = nat
                                        getTop()
                                    })

                            } else {
                                val body =
                                    req.body()?.filter { userInfoDTO -> !userInfoDTO.isInFollowers }
                                if (body?.size != 0) {
                                    if (body?.size!! > scr.rating) {
                                        m[scr] = body.subList(0, scr.rating)
                                    } else {
                                        var continueBrowsing = true
                                        do {
                                            req = searchService.searchBasedOnCategory(
                                                "Bearer " + tokenId,
                                                scr.category,
                                                i++
                                            )
                                            if (req.body()?.size!! > 0) {
                                                body.toMutableList().addAll(
                                                    req.body()
                                                        ?.filter { userInfoDTO -> !userInfoDTO.isInFollowers }!!
                                                )
                                                if (body.size > scr.rating) {
                                                    continueBrowsing = false
                                                }
                                            } else {
                                                continueBrowsing = false
                                            }

                                        } while (req.body()?.size != 0 || continueBrowsing)
                                        m[scr] = body
                                    }
                                }
                            }
                        }
                    }
                    search_top_pb.visibility = View.GONE
                    topAdapter = SuggestionAdapter(m.toList().sortedByDescending { it.first.rating }.toMap(), this@SearchTop, this@SearchTop, lifecycleScope, searchViewModel, tokenId)
                    search_top_rv.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = topAdapter
                    }
        }
    }

    override fun onItemSelected(follow: Boolean, userInfoDTO: UserInfoDTO) {
        followViewModel.followPublicUser(tokenId!!, userInfoDTO.id!!).observe(viewLifecycleOwner, {
            if(it.code() == 401) {
                loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner){ newAccessToken ->
                    tokenId = newAccessToken
                    followViewModel.followPublicUser(tokenId!!, userInfoDTO.id!!).observe(viewLifecycleOwner){ rsp ->

                    }
                }
            }
        })
    }

    override fun onPicClicked(userInfoDTO: UserInfoDTO) {
        findNavController().navigate(SearchDirections.actionGlobalProfileElse(2).setUserInfoDTO(userInfoDTO))
    }
}