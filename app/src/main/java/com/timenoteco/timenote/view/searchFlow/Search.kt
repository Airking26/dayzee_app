package com.timenoteco.timenote.view.searchFlow

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.mancj.materialsearchbar.MaterialSearchBar
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.SearchViewTopExplorePagerAdapter
import com.timenoteco.timenote.adapter.SearchViewPeopleTagPagerAdapter
import com.timenoteco.timenote.common.BaseThroughFragment
import com.timenoteco.timenote.view.profileFlow.ProfileDirections
import com.timenoteco.timenote.viewModel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_search.*

class Search : BaseThroughFragment() {

    private var tabText0 : String = "Top"
    private var tabText1 : String = "Explore"
    private lateinit var viewTopExplorePagerAdapter: SearchViewTopExplorePagerAdapter
    private lateinit var viewPeopleTagPagerAdapter: SearchViewPeopleTagPagerAdapter
    private val loginViewModel : LoginViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel.getAuthenticationState().observe(requireActivity(), androidx.lifecycle.Observer {
            when (it) {
                LoginViewModel.AuthenticationState.UNAUTHENTICATED -> findNavController().navigate(SearchDirections.actionSearchToNavigation())
                LoginViewModel.AuthenticationState.AUTHENTICATED -> findNavController().popBackStack(R.id.search, false)
                LoginViewModel.AuthenticationState.GUEST -> findNavController().popBackStack(R.id.search, false)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        when(loginViewModel.getAuthenticationState().value){
            LoginViewModel.AuthenticationState.GUEST -> loginViewModel.markAsUnauthenticated()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        return getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_search)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewTopExplorePagerAdapter = SearchViewTopExplorePagerAdapter(childFragmentManager, lifecycle)
        viewPeopleTagPagerAdapter = SearchViewPeopleTagPagerAdapter(childFragmentManager, lifecycle)
        search_viewpager.apply {
            adapter = viewTopExplorePagerAdapter
            isUserInputEnabled = false
            isSaveEnabled = false
        }

        TabLayoutMediator(search_tablayout, search_viewpager){ tab, position ->
            when(position){
                0 -> tab.text = tabText0
                1 -> tab.text = tabText1
            }
        }.attach()

        searchBar.addTextChangeListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        searchBar.setCardViewElevation(0)
        searchBar.setOnSearchActionListener(object: MaterialSearchBar.OnSearchActionListener{
            override fun onButtonClicked(buttonCode: Int) {
                Toast.makeText(context, "onButtonClicked", Toast.LENGTH_SHORT).show()
            }

                override fun onSearchStateChanged(enabled: Boolean) {
                    if(enabled) {
                        search_tablayout.getTabAt(0)?.text = "People"
                        search_tablayout.getTabAt(1)?.text = "Tags"
                        search_viewpager.adapter = viewPeopleTagPagerAdapter
                    } else {
                        search_tablayout.getTabAt(0)?.text = "Top"
                        search_tablayout.getTabAt(1)?.text = "Explore"
                        search_viewpager.adapter = viewTopExplorePagerAdapter
                    }
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                Toast.makeText(context, "onSearchConfirmed", Toast.LENGTH_SHORT).show()
            }

        })
    }

}
