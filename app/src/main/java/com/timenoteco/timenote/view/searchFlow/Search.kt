package com.timenoteco.timenote.view.searchFlow

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.tabs.TabLayoutMediator
import com.mancj.materialsearchbar.MaterialSearchBar
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.SearchViewTopExplorePagerAdapter
import com.timenoteco.timenote.adapter.SearchViewPeopleTagPagerAdapter
import kotlinx.android.synthetic.main.fragment_search.*

class Search : Fragment() {

    private var tabText0 : String = "Top"
    private var tabText1 : String = "Explore"
    private lateinit var viewTopExplorePagerAdapter: SearchViewTopExplorePagerAdapter
    private lateinit var viewPeopleTagPagerAdapter: SearchViewPeopleTagPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewTopExplorePagerAdapter = SearchViewTopExplorePagerAdapter(this)
        viewPeopleTagPagerAdapter = SearchViewPeopleTagPagerAdapter(this)
        search_viewpager.adapter = viewTopExplorePagerAdapter

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
