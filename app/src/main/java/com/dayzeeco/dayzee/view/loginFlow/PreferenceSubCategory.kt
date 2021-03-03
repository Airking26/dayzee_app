package com.dayzeeco.dayzee.view.loginFlow

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.SubCategoryCardAdapter
import com.dayzeeco.dayzee.adapter.SubCategoryChipAdapter
import com.dayzeeco.dayzee.model.Preferences
import com.dayzeeco.dayzee.model.SubCategoryRated
import com.dayzeeco.dayzee.model.accessToken
import com.dayzeeco.dayzee.viewModel.PreferencesViewModel
import kotlinx.android.synthetic.main.fragment_preference_sub_category.*


class PreferenceSubCategory: Fragment(), SubCategoryCardAdapter.SubCategorySeekBarListener, View.OnClickListener,
    SubCategoryChipAdapter.SubCategoryChipListener {

    private lateinit var cardAdapter: SubCategoryCardAdapter
    private lateinit var chipAdapter: SubCategoryChipAdapter
    private var subcategories: MutableMap<String, MutableList<String>> = mutableMapOf()
    private var chips: MutableList<String> = mutableListOf()
    private val preferenceSubCategoryArgs: PreferenceCategoryArgs by navArgs()
    private val preferencesViewModel: PreferencesViewModel by activityViewModels()
    private lateinit var preferences: Preferences
    private lateinit var prefs: SharedPreferences
    private lateinit var tokenId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)!!
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_preference_sub_category, container, false).apply {
            isFocusableInTouchMode = true
            requestFocus()
            setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN){
                    if(keyCode == KeyEvent.KEYCODE_BACK){ false }
                }
                true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pref_sub_category_btn_back.setOnClickListener(this)
        pref_sub_category_btn_done.setOnClickListener(this)

        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        setRvAndAdapters(view)
        preferencesViewModel.getPreferences(tokenId).observe(viewLifecycleOwner, {
            preferences = Preferences(it.body()!!)
            updateListCategoryAndSubCategory(it.body())
        })
    }

    private fun setRvAndAdapters(view: View) {
        chipAdapter = SubCategoryChipAdapter(chips, this)
        pref_sub_category_rv_chips.apply {
            layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = chipAdapter
        }

        cardAdapter = SubCategoryCardAdapter(subcategories, this)
        pref_sub_category_card_rv.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = cardAdapter
        }
    }

    private fun updateListCategoryAndSubCategory(prefList: List<SubCategoryRated>?) {
        chips.clear()
        subcategories.clear()
        val listSubCat : MutableList<String> = mutableListOf()
        prefList?.distinctBy { subCategoryRated -> subCategoryRated.category.category }?.forEach { chips.add(it.category.category) }
        prefList?.forEach {
            if(!subcategories.containsKey(it.category.category)) {
                listSubCat.clear()
                listSubCat.add(it.category.subcategory)
                subcategories[it.category.category] = listSubCat
            }
            else {
                listSubCat.add(it.category.subcategory)
                subcategories[it.category.category] = listSubCat
            }
        }
        chipAdapter.notifyDataSetChanged()
        cardAdapter.notifyDataSetChanged()
    }


    override fun onClick(v: View?) {
        when(v){
            pref_sub_category_btn_back -> findNavController().popBackStack()
            pref_sub_category_btn_done -> saveAndNavigate()
        }
    }

    private fun saveAndNavigate() {
        preferencesViewModel.modifyPreferences(tokenId, preferences).observe(viewLifecycleOwner,
            {
                findNavController().navigate(PreferenceSubCategoryDirections.actionPreferenceSubCategoryToPreferenceSuggestion(preferenceSubCategoryArgs.isInLogin))
            })
    }

    override fun onCloseChip(index: Int) {
        preferences.category.removeAt(index)
        preferencesViewModel.modifyPreferences(tokenId, preferences).observe(viewLifecycleOwner, {
                if(it.isSuccessful) {
                    chipAdapter.notifyDataSetChanged()
                    cardAdapter.notifyDataSetChanged()
                }
            })
    }


    override fun onSeekBarModified(likedLevel: Int, subCategoryName: String) {
        for(n in preferences.category){
            if(n.category.subcategory == subCategoryName){
                n.rating = likedLevel
            }
        }
    }

}






