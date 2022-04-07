package com.dayzeeco.dayzee.view.loginFlow

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.CategoryAdapter
import com.dayzeeco.dayzee.common.*
import com.dayzeeco.dayzee.model.*
import com.dayzeeco.dayzee.viewModel.PreferencesViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_preference_category.*
import kotlinx.android.synthetic.main.item_category.view.*
import java.lang.reflect.Type

class PreferenceCategory : Fragment(), View.OnClickListener {

    private var categories: List<Category>? = null
    private var tokenId: String? = null
    private val preferencesViewModel: PreferencesViewModel by activityViewModels()
    private val preferenceCategoryArgs: PreferenceCategoryArgs by navArgs()
    private lateinit var prefs: SharedPreferences
    private lateinit var preferencesCategoryRated: MutableList<SubCategoryRated>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_preference_category, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pref_category_btn_next.setOnClickListener(this)


        prefs.stringLiveData(list_subcategory_noted, Gson().toJson(prefs.getString(list_subcategory_noted, null))).observe(viewLifecycleOwner) {
            val typeSubCat: Type = object : TypeToken<MutableList<SubCategoryRated?>>() {}.type
            preferencesCategoryRated = Gson().fromJson(it, typeSubCat) ?: mutableListOf()
        }

        preferencesViewModel.getCategories().observe(viewLifecycleOwner) { resp ->
            if (resp.isSuccessful) {
                categories = resp.body()
                val adapterGV =
                    CategoryAdapter(categories?.distinctBy { category -> category.category },
                        preferencesCategoryRated.distinctBy { ca -> ca.category }
                            .map { subCategoryRated ->
                                Category(
                                    subCategoryRated.category.category,
                                    subCategoryRated.category.subcategory
                                )
                            })
                category_gv.adapter = adapterGV
                category_pb.visibility = View.GONE
            }
        }


        category_gv.setOnItemClickListener { parent, viewChild, position, id ->
            if(viewChild.category_btn.drawable.bytesEqualTo(resources.getDrawable(R.drawable.ic_uncheck)) && viewChild.category_btn.drawable.pixelsEqualTo(resources.getDrawable(R.drawable.ic_uncheck))){
                viewChild.category_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_checktwo))
                preferencesCategoryRated.addAll(categories?.filter { category -> viewChild.category_tv.text == category.category }?.map { SubCategoryRated(it, 3) }!!)
            } else {
                viewChild.category_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_uncheck))
                preferencesCategoryRated.removeAll { subCategoryRated -> viewChild.category_tv.text == subCategoryRated.category.category }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
        when(v){
            pref_category_btn_next -> {
                if(preferencesCategoryRated.size > 0){
                    prefs.edit().putString(list_subcategory_noted, Gson().toJson(preferencesCategoryRated)).apply()
                    preferencesViewModel.modifyPreferences(tokenId!!, Preferences(preferencesCategoryRated)).observe(viewLifecycleOwner) {
                        prefs.edit().putString(
                            list_subcategory_rated,
                            Gson().toJson(preferencesCategoryRated)
                        ).apply()
                        if (it.isSuccessful) {
                            view?.findNavController()?.navigate(
                                PreferenceCategoryDirections.actionPreferenceCategoryToPreferenceSubCategory(
                                    preferenceCategoryArgs.isInLogin
                                )
                            )
                        }
                    }
                } else Toast.makeText(
                    requireContext(),
                    getString(R.string.choose_at_least_one_category),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}
