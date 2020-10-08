package com.timenoteco.timenote.view.loginFlow

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
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.Category
import com.timenoteco.timenote.model.Preferences
import com.timenoteco.timenote.model.SubCategoryRated
import com.timenoteco.timenote.viewModel.PreferencesViewModel
import kotlinx.android.synthetic.main.fragment_preference_category.*

class PreferenceCategory : Fragment(), View.OnClickListener {

    private var categories: List<Category>? =
        listOf(Category("city", "foot"),
            Category("city", "tennis"),
            Category("sport", "jazz"),
            Category("sport", "col"),
            Category("esport", "jazz"),
            Category("holiday", "ja"),
            Category("music", "z"),
            Category("music", "jz"),
            Category("shopping", "jzz"),
            Category("movie", "jaz"),
            Category("movie", "jazz")
        )
    private lateinit var tokenId: String
    private val preferencesViewModel: PreferencesViewModel by activityViewModels()
    private val preferenceCategoryArgs: PreferenceCategoryArgs by navArgs()
    private lateinit var prefs: SharedPreferences
    open val TOKEN: String = "TOKEN"
    private var preferencesCategoryRated: MutableList<SubCategoryRated> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(TOKEN, null)!!
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_preference_category, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setButtons()


        /*preferencesViewModel.getPreferences(tokenId).observe(viewLifecycleOwner, Observer {
            preferencesCategoryRated = it.body()!!
        })

        preferencesViewModel.getCategories().observe(viewLifecycleOwner, Observer {
            if(it.code() == 200){
                categories = it.body()
            }
        })*/
    }

    private fun setButtons() {
        pref_city_btn.setOnClickListener(this)
        pref_sport_btn.setOnClickListener(this)
        pref_music_btn.setOnClickListener(this)
        pref_esport_btn.setOnClickListener(this)
        pref_youtube_btn.setOnClickListener(this)
        pref_religion_btn.setOnClickListener(this)
        pref_culture_btn.setOnClickListener(this)
        pref_movie_btn.setOnClickListener(this)
        pref_shopping_btn.setOnClickListener(this)
        pref_holiday_btn.setOnClickListener(this)
        pref_category_btn_next.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
        when(v){
            pref_city_btn -> {
                if(pref_city_btn.drawable.constantState == resources.getDrawable(R.drawable.ic_pref_city_clicked).constantState){
                    pref_city_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_pref_city))
                    preferencesCategoryRated.removeIf { it.category.category == "city" }
                } else {
                    pref_city_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_pref_city_clicked))
                    categories?.forEach {
                        if(it.category == "city") preferencesCategoryRated.add(SubCategoryRated(it, 0))
                    }
                }
            }
            pref_sport_btn -> {
                if(pref_sport_btn.drawable.constantState == resources.getDrawable(R.drawable.ic_pref_sport_clicked).constantState){
                    pref_sport_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_pref_sport))
                    preferencesCategoryRated.removeIf { it.category.category == "sport" }
                } else {
                    pref_sport_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_pref_sport_clicked))
                    categories?.forEach {
                        if(it.category == "sport") preferencesCategoryRated.add(SubCategoryRated(it, 0))
                    }
                }
            }
            pref_music_btn -> {
                if(pref_music_btn.drawable.constantState == resources.getDrawable(R.drawable.ic_pref_music_clicked).constantState){
                    pref_music_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_pref_music))
                    preferencesCategoryRated.removeIf { it.category.category == "music" }
                } else {
                    pref_music_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_pref_music_clicked))
                    categories?.forEach {
                        if(it.category == "music") preferencesCategoryRated.add(SubCategoryRated(it, 0))
                    }
                }
            }
            pref_esport_btn -> {
                if(pref_esport_btn.drawable.constantState == resources.getDrawable(R.drawable.ic_pref_esport_clicked).constantState){
                    pref_esport_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_pref_esport))
                    preferencesCategoryRated.removeIf { it.category.category == "esport" }
                } else {
                    pref_esport_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_pref_esport_clicked))
                    categories?.forEach {
                        if(it.category == "esport") preferencesCategoryRated.add(SubCategoryRated(it, 0))
                    }
                }
            }
            pref_youtube_btn -> {
                if(pref_youtube_btn.drawable.constantState == resources.getDrawable(R.drawable.ic_pref_youtube_clicked).constantState){
                    pref_youtube_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_pref_youtube))
                    preferencesCategoryRated.removeIf { it.category.category == "youtube" }
                } else {
                    pref_youtube_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_pref_youtube_clicked))
                    categories?.forEach {
                        if(it.category == "youtube") preferencesCategoryRated.add(SubCategoryRated(it, 0))
                    }
                }
            }
            pref_religion_btn -> {
                if(pref_religion_btn.drawable.constantState == resources.getDrawable(R.drawable.ic_pref_religion_clicked).constantState){
                    pref_religion_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_pref_religion))
                    preferencesCategoryRated.removeIf { it.category.category == "religion" }
                } else {
                    pref_religion_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_pref_religion_clicked))
                    categories?.forEach {
                        if(it.category == "religion") preferencesCategoryRated.add(SubCategoryRated(it, 0))
                    }
                }
            }
            pref_culture_btn -> {
                if(pref_culture_btn.drawable.constantState == resources.getDrawable(R.drawable.ic_pref_culture_clicked).constantState){
                    pref_culture_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_pref_culture))
                    preferencesCategoryRated.removeIf { it.category.category == "culture" }
                } else {
                    pref_culture_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_pref_culture_clicked))
                    categories?.forEach {
                        if(it.category == "culture") preferencesCategoryRated.add(SubCategoryRated(it, 0))
                    }
                }
            }
            pref_movie_btn -> {
                if(pref_movie_btn.drawable.constantState == resources.getDrawable(R.drawable.ic_pref_movie_clicked).constantState){
                    pref_movie_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_pref_movie))
                    preferencesCategoryRated.removeIf { it.category.category == "movie" }
                } else {
                    pref_movie_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_pref_movie_clicked))
                    categories?.forEach {
                        if(it.category == "movie") preferencesCategoryRated.add(SubCategoryRated(it, 0))
                    }
                }
            }
            pref_shopping_btn -> {
                if(pref_shopping_btn.drawable.constantState == resources.getDrawable(R.drawable.ic_pref_shopping_clicked).constantState){
                    pref_shopping_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_pref_shopping))
                    preferencesCategoryRated.removeIf { it.category.category == "shopping" }
                } else {
                    pref_shopping_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_pref_shopping_clicked))
                    categories?.forEach {
                        if(it.category == "shopping") preferencesCategoryRated.add(SubCategoryRated(it, 0))
                    }
                }
            }
            pref_holiday_btn ->{
                if(pref_holiday_btn.drawable.constantState == resources.getDrawable(R.drawable.ic_pref_holiday_clicked).constantState){
                    pref_holiday_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_pref_holiday))
                    preferencesCategoryRated.removeIf { it.category.category == "holiday" }
                } else {
                    pref_holiday_btn.setImageDrawable(resources.getDrawable(R.drawable.ic_pref_holiday_clicked))
                    categories?.forEach {
                        if(it.category == "holiday") preferencesCategoryRated.add(SubCategoryRated(it, 0))
                    }
                }
            }
            pref_category_btn_next -> {
                if(preferencesCategoryRated.size > 0){
                    preferencesViewModel.modifyPreferences(tokenId, Preferences(preferencesCategoryRated))
                        .observe(viewLifecycleOwner, Observer {
                        if(it.isSuccessful)
                            view?.findNavController()?.navigate(PreferenceCategoryDirections.actionPreferenceCategoryToPreferenceSubCategory(preferenceCategoryArgs.isInLogin))
                    })
                } else Toast.makeText(
                    requireContext(),
                    "Choose at least one category",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun changeStatusCategory(boolean: Boolean, imageView: ImageView, icPrefCity: Int, icPrefCityClicked: Int){
        if(boolean) imageView.setImageResource(icPrefCityClicked)
        else imageView.setImageResource(icPrefCity)
    }

}
