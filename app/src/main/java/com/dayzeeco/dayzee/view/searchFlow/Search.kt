package com.dayzeeco.dayzee.view.searchFlow

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.tabs.TabLayoutMediator
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.SearchViewTopExplorePagerAdapter
import com.dayzeeco.dayzee.adapter.SearchViewPeopleTagPagerAdapter
import com.dayzeeco.dayzee.androidView.materialsearchbar.MaterialSearchBar
import com.dayzeeco.dayzee.common.BaseThroughFragment
import com.dayzeeco.dayzee.common.accessToken
import com.dayzeeco.dayzee.listeners.RefreshPicBottomNavListener
import com.dayzeeco.dayzee.viewModel.LoginViewModel
import com.dayzeeco.dayzee.viewModel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search.*

class Search : BaseThroughFragment() {

    private var isEnabled: Boolean = false
    private lateinit var handler: Handler
    private val TRIGGER_AUTO_COMPLETE = 200
    private val AUTO_COMPLETE_DELAY: Long = 200
    private var tabText0 : String = "Top"
    private var tabText1 : String = "Explore"
    private lateinit var viewTopExplorePagerAdapter: SearchViewTopExplorePagerAdapter
    private lateinit var viewPeopleTagPagerAdapter: SearchViewPeopleTagPagerAdapter
    private val loginViewModel : LoginViewModel by activityViewModels()
    private lateinit var prefs: SharedPreferences
    private val searchViewModel : SearchViewModel by activityViewModels()
    private var tokenId : String? = null
    private lateinit var onRefreshPicBottomNavListener: RefreshPicBottomNavListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
        loginViewModel.getAuthenticationState().observe(requireActivity(), androidx.lifecycle.Observer {
            when (it) {
                LoginViewModel.AuthenticationState.UNAUTHENTICATED -> findNavController().navigate(SearchDirections.actionGlobalNavigation())
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    tokenId = prefs.getString(accessToken, null)
                    findNavController().popBackStack(R.id.search, false)
                }
                LoginViewModel.AuthenticationState.GUEST -> findNavController().popBackStack(R.id.search, false)
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onRefreshPicBottomNavListener = context as RefreshPicBottomNavListener
    }

    override fun onResume() {
        super.onResume()
        when(loginViewModel.getAuthenticationState().value){
            LoginViewModel.AuthenticationState.GUEST -> loginViewModel.markAsUnauthenticated()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        return getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_search)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(!tokenId.isNullOrBlank()) {

            searchBar.lastSuggestions = prefs.getStringSet("lastSuggestions", setOf<String>())?.toMutableList()

            viewTopExplorePagerAdapter =
                SearchViewTopExplorePagerAdapter(childFragmentManager, lifecycle)
            viewPeopleTagPagerAdapter =
                SearchViewPeopleTagPagerAdapter(childFragmentManager, lifecycle)
            search_viewpager.apply {
                adapter = viewTopExplorePagerAdapter
                isUserInputEnabled = false
                isSaveEnabled = false
            }

            search_tablayout.setSelectedTabIndicatorColor(resources.getColor(android.R.color.darker_gray))
            TabLayoutMediator(search_tablayout, search_viewpager) { tab, position ->
                when (position) {
                    0 -> tab.text = tabText0
                    1 -> tab.text = tabText1
                }
            }.attach()

            handler = Handler { msg ->
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(searchBar.text)) {
                        searchViewModel.setSearchIsEmpty(false)
                        searchViewModel.searchChanged(tokenId!!, searchBar.text, prefs)
                    } else {
                        searchViewModel.setSearchIsEmpty(true)
                    }
                }
                false
            }

            searchBar.addTextChangeListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    handler.removeMessages(TRIGGER_AUTO_COMPLETE)
                    handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY)
                }

            })


            if(isEnabled){
                search_tablayout.getTabAt(0)?.text = getString(R.string.people)
                search_tablayout.getTabAt(1)?.text = getString(R.string.tags)
                search_viewpager.adapter = viewPeopleTagPagerAdapter
            } else {
                search_tablayout.getTabAt(0)?.text = getString(R.string.top)
                search_tablayout.getTabAt(1)?.text = getString(R.string.explore)
                search_viewpager.adapter = viewTopExplorePagerAdapter
            }

            searchBar.setCardViewElevation(0)
            searchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
                override fun onButtonClicked(buttonCode: Int) {
                }

                override fun onSearchStateChanged(enabled: Boolean) {
                    isEnabled = enabled
                    if (enabled) {
                        search_tablayout.getTabAt(0)?.text = getString(R.string.people)
                        search_tablayout.getTabAt(1)?.text = getString(R.string.tags)
                        search_viewpager.adapter = viewPeopleTagPagerAdapter
                    } else {
                        search_tablayout.getTabAt(0)?.text = getString(R.string.top)
                        search_tablayout.getTabAt(1)?.text = getString(R.string.explore)
                        search_viewpager.adapter = viewTopExplorePagerAdapter
                    }
                }

                override fun onSearchConfirmed(text: CharSequence?) {
                }

            })
        }
    }

    override fun onStop() {
        super.onStop()
        prefs.edit().putStringSet("lastSuggestions", (searchBar.lastSuggestions as MutableList<String>).toSet()).apply()
    }

}
