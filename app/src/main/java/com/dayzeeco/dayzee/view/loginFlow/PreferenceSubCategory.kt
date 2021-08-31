package com.dayzeeco.dayzee.view.loginFlow

import android.content.Context
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
import com.dayzeeco.dayzee.common.accessToken
import com.dayzeeco.dayzee.common.list_subcategory_rated
import com.dayzeeco.dayzee.common.stringLiveData
import com.dayzeeco.dayzee.listeners.GoToTop
import com.dayzeeco.dayzee.model.Preferences
import com.dayzeeco.dayzee.model.SubCategoryRated
import com.dayzeeco.dayzee.viewModel.LoginViewModel
import com.dayzeeco.dayzee.viewModel.PreferencesViewModel
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_preference_sub_category.*
import java.lang.reflect.Type


class PreferenceSubCategory: Fragment(), SubCategoryCardAdapter.SubCategorySeekBarListener, View.OnClickListener,
    SubCategoryChipAdapter.SubCategoryChipListener {

    private lateinit var cardAdapter: SubCategoryCardAdapter
    private lateinit var chipAdapter: SubCategoryChipAdapter
    private var subcategories: MutableMap<String, MutableList<SubCategoryRated>> = mutableMapOf()
    private var chips: MutableList<String> = mutableListOf()
    private val preferenceSubCategoryArgs: PreferenceCategoryArgs by navArgs()
    private val preferencesViewModel: PreferencesViewModel by activityViewModels()
    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var prefs: SharedPreferences
    private lateinit var tokenId: String
    private lateinit var preferencesCategoryRated: MutableList<SubCategoryRated>
    private lateinit var goToTopListener: GoToTop


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)!!
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        goToTopListener = context as GoToTop
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

        prefs.stringLiveData(list_subcategory_rated, Gson().toJson(prefs.getString(
            list_subcategory_rated, null))).observe(viewLifecycleOwner, {
            val typeSubCat: Type = object : TypeToken<MutableList<SubCategoryRated?>>() {}.type
            preferencesCategoryRated = Gson().fromJson(it, typeSubCat) ?: mutableListOf()
            updateListCategoryAndSubCategory(preferencesCategoryRated)
        })
    }


    private fun updateListCategoryAndSubCategory(prefList: List<SubCategoryRated>?) {
        chips.clear()
        subcategories.clear()
        var list : MutableList<SubCategoryRated> = mutableListOf()
        prefList?.distinctBy { subCategoryRated -> subCategoryRated.category.category }?.forEach { chips.add(it.category.category) }
        prefList?.forEach {
            if(it.rating > 0){
            if(subcategories.containsKey(it.category.category)){
                list.add(it)
                subcategories[it.category.category] = list
            } else {
                list = mutableListOf()
                list.add(it)
                subcategories[it.category.category] = list
            }
        }}
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
        preferencesCategoryRated = preferencesCategoryRated.filter { it.rating > 0 }.toMutableList()
        preferencesViewModel.modifyPreferences(tokenId, Preferences(preferencesCategoryRated)).observe(viewLifecycleOwner, {
            prefs.edit().putString(list_subcategory_rated, Gson().toJson(preferencesCategoryRated)).apply()
            if(it.isSuccessful){
                if(preferenceSubCategoryArgs.isInLogin){
                    goToTopListener.goToTop()
                    loginViewModel.markAsAuthenticated()
                    findNavController().popBackStack(R.id.home, false)
                } else {
                    goToTopListener.goToTop()
                    findNavController().popBackStack(R.id.myProfile, false)
                }
        //        findNavController().navigate(PreferenceSubCategoryDirections.actionPreferenceSubCategoryToPreferenceSuggestion(preferenceSubCategoryArgs.isInLogin))
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCloseChip(index: Int) {
        val category = chips[index]
        if(subcategories.containsKey(category)) subcategories.remove(category)
        preferencesCategoryRated.removeIf { subcategories -> subcategories.category.category == category }
        prefs.edit().putString(list_subcategory_rated, Gson().toJson(preferencesCategoryRated)).apply()
    }


    override fun onSeekBarModified(likedLevel: Int, subCategoryName: String) {
        for(scr in preferencesCategoryRated){
            if(scr.category.subcategory == subCategoryName){
                scr.rating = likedLevel
            }
        }
    }

}






