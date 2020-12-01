package com.timenoteco.timenote.view.searchFlow

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.play.core.internal.k
import com.google.android.play.core.internal.v
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.SearchExploreCategoryAdapter
import com.timenoteco.timenote.adapter.UsersShareWithPagingAdapter
import com.timenoteco.timenote.common.BaseThroughFragment
import com.timenoteco.timenote.model.Category
import com.timenoteco.timenote.model.accessToken
import com.timenoteco.timenote.viewModel.LoginViewModel
import com.timenoteco.timenote.viewModel.PreferencesViewModel
import com.timenoteco.timenote.viewModel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search_explore.*

class SearchExplore : Fragment(), SearchExploreCategoryAdapter.SearchSubCategoryListener{

    private lateinit var searchExploreAdapter : SearchExploreCategoryAdapter
    private var explores: MutableMap<String, MutableList<String>> = mutableMapOf()
    private lateinit var prefs: SharedPreferences
    private var tokenId: String? = null
    private val prefrenceViewModel : PreferencesViewModel by activityViewModels()
    private val authViewModel : LoginViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search_explore, container, false)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        searchExploreAdapter = SearchExploreCategoryAdapter(explores, this)
        search_explore_root_rv.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = searchExploreAdapter
        }

        prefrenceViewModel.getCategories().observe(viewLifecycleOwner, Observer { response ->
            if(response.code() == 401){
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer {newAccessToken ->
                    tokenId = newAccessToken
                    prefrenceViewModel.getCategories().observe(viewLifecycleOwner, Observer {lc ->
                        if(lc.isSuccessful){
                            response.body()?.groupBy { it.category }?.entries?.map { (name, group) -> explores.put(name, group.map { it.subcategory }.toMutableList()) }
                            searchExploreAdapter.notifyDataSetChanged()
                        }
                    })
                })
            }
            if(response.isSuccessful){
            response.body()?.groupBy { it.category }?.entries?.map { (name, group) -> explores.put(name, group.map { it.subcategory }.toMutableList()) }
                searchExploreAdapter.notifyDataSetChanged()
            }
        })

    }

    override fun onSubCategorySelected(category: Category) {
        findNavController().navigate(SearchDirections.actionSearchToSearchExploreClicked(category))
    }
}