package com.dayzeeco.dayzee.view.searchFlow

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.SearchExploreCategoryAdapter
import com.dayzeeco.dayzee.common.accessToken
import com.dayzeeco.dayzee.model.Category
import com.dayzeeco.dayzee.viewModel.LoginViewModel
import com.dayzeeco.dayzee.viewModel.PreferencesViewModel
import kotlinx.android.synthetic.main.fragment_search_explore.*

class SearchExplore : Fragment(), SearchExploreCategoryAdapter.SearchSubCategoryListener{

    private lateinit var searchExploreAdapter : SearchExploreCategoryAdapter
    private var explores: MutableMap<String, List<Category>?>? = mutableMapOf()
    private lateinit var prefs: SharedPreferences
    private var tokenId: String? = null
    private val prefrenceViewModel : PreferencesViewModel by activityViewModels()
    private val authViewModel : LoginViewModel by activityViewModels()
    private var alreadyCreated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
        searchExploreAdapter = SearchExploreCategoryAdapter(explores, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search_explore, container, false)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            search_explore_root_rv.apply {
                layoutManager = LinearLayoutManager(view.context)
                adapter = searchExploreAdapter
            }

        if(!alreadyCreated) {
            prefrenceViewModel.getCategories().observe(viewLifecycleOwner, { response ->
                if (response.code() == 401) {
                    authViewModel.refreshToken(prefs)
                        .observe(viewLifecycleOwner, { newAccessToken ->
                            tokenId = newAccessToken
                            prefrenceViewModel.getCategories().observe(viewLifecycleOwner, { lc ->
                                if (lc.isSuccessful) {
                                    explores?.putAll(response.body()?.groupBy { it.category }!!)
                                    searchExploreAdapter.notifyDataSetChanged()
                                    search_explore_pb.visibility = View.GONE
                                    alreadyCreated = true
                                }
                            })
                        })
                }
                if (response.isSuccessful) {
                    explores?.putAll(response.body()?.groupBy { it.category }!!)
                    searchExploreAdapter.notifyDataSetChanged()
                    search_explore_pb.visibility = View.GONE
                    alreadyCreated = true
                }
            })
        }
        else search_explore_pb.visibility = View.GONE

    }

    override fun onSubCategorySelected(category: Category) {
        findNavController().navigate(SearchDirections.actionSearchToSearchExploreClicked(category))
    }
}