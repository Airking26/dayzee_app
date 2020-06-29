package com.timenoteco.timenote.view.loginFlow

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.SubCategoryCardAdapter
import com.timenoteco.timenote.adapter.SubCategoryChipAdapter
import com.timenoteco.timenote.model.Preference
import com.timenoteco.timenote.model.SubCategory
import com.timenoteco.timenote.viewModel.PreferenceViewModel
import kotlinx.android.synthetic.main.fragment_preference_sub_category.*

private lateinit var cardAdapter: SubCategoryCardAdapter
private lateinit var chipAdapter: SubCategoryChipAdapter
private var subcategories: MutableMap<String, List<SubCategory>> = mutableMapOf()
private var chips: MutableList<String> = mutableListOf()
lateinit var preferenceViewModel: PreferenceViewModel


class PreferenceSubCategory: Fragment(), SubCategoryCardAdapter.SubCategorySeekBarListener, View.OnClickListener,
    SubCategoryChipAdapter.SubCategoryChipListener {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_preference_sub_category, container, false).apply {
            isFocusableInTouchMode = true
            requestFocus()
            setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN){
                    if(keyCode == KeyEvent.KEYCODE_BACK){ false }
                }
                true
            }
        }

        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        preferenceViewModel = ViewModelProvider(this).get(PreferenceViewModel::class.java)
        setListenerOnClick()
        setRvAndAdapters(view)

        preferenceViewModel.getPreferences().observe(viewLifecycleOwner, Observer {
            updateListCategoryAndSubCategory(it)
        })
    }

    private fun setRvAndAdapters(view: View) {
        chipAdapter = SubCategoryChipAdapter(
            chips, this)
        pref_sub_category_rv_chips.apply {
            layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = chipAdapter
        }

        cardAdapter = SubCategoryCardAdapter(
            subcategories, this)
        pref_sub_category_card_rv.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = cardAdapter
        }
    }

    private fun setListenerOnClick() {
        pref_sub_category_btn_back.setOnClickListener(this)
        pref_sub_category_btn_done.setOnClickListener(this)
    }

    private fun updateListCategoryAndSubCategory(prefList: MutableList<Preference>) {
        chips.clear()
        subcategories.clear()
        for (p in prefList) {
            if (p.category.isSelected) {
                chips.add(p.category.name)
                subcategories[p.category.name] = p.subCategories
            }
        }
        chipAdapter.notifyDataSetChanged()
        cardAdapter.notifyDataSetChanged()
    }

    override fun onSeekBarModified(likedLevel: Int, categoryName: String, subCategoryPosition: Int) =
        preferenceViewModel.setLikedLevelSubCategory(likedLevel, categoryName, subCategoryPosition)

    override fun onClick(v: View?) {
        when(v){
            pref_sub_category_btn_back -> Navigation.findNavController(v!!).popBackStack()
            pref_sub_category_btn_done -> saveAndNavigate()
        }
    }

    private fun saveAndNavigate() {
        setListInSP("key", preferenceViewModel.getPreferences().value!!)
        view?.findNavController()?.navigate(PreferenceSubCategoryDirections.actionPreferenceSubCategoryToPreferenceSuggestion())
    }

    override fun onCloseChip(name: String) = preferenceViewModel.closeChip(name)

    private fun setListInSP(key: String, list: List<Any>){
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        prefs.edit().putString(key, Gson().toJson(list)).apply()
    }

}
