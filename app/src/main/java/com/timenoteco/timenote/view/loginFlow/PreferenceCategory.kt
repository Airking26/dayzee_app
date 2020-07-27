package com.timenoteco.timenote.view.loginFlow

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.timenoteco.timenote.R
import com.timenoteco.timenote.viewModel.LoginViewModel
import com.timenoteco.timenote.viewModel.PreferenceViewModel
import kotlinx.android.synthetic.main.fragment_preference_category.*

class PreferenceCategory : Fragment(), View.OnClickListener {

    lateinit var preferenceViewModel: PreferenceViewModel
    private val viewModel: LoginViewModel by activityViewModels()
    private val preferenceCategoryArgs: PreferenceCategoryArgs by navArgs()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_preference_category, container, false)
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        preferenceViewModel = ViewModelProvider(this).get(PreferenceViewModel::class.java)
        preferenceViewModel.getPreferences().observe(viewLifecycleOwner, Observer {
            for(preference in it){
                when(preference.index){
                    0 -> changeStatusCategory(preference.category.isSelected, pref_city_btn, R.drawable.ic_pref_city, R.drawable.ic_pref_city_clicked)
                    1 -> changeStatusCategory(preference.category.isSelected, pref_sport_btn, R.drawable.ic_pref_sport, R.drawable.ic_pref_sport_clicked)
                    2 -> changeStatusCategory(preference.category.isSelected, pref_music_btn, R.drawable.ic_pref_music, R.drawable.ic_pref_music_clicked)
                    3 -> changeStatusCategory(preference.category.isSelected, pref_esport_btn, R.drawable.ic_pref_esport, R.drawable.ic_pref_esport_clicked)
                    4 -> changeStatusCategory(preference.category.isSelected, pref_youtube_btn, R.drawable.ic_pref_youtube, R.drawable.ic_pref_youtube_clicked)
                    5 -> changeStatusCategory(preference.category.isSelected, pref_religion_btn, R.drawable.ic_pref_religion, R.drawable.ic_pref_religion_clicked)
                    6 -> changeStatusCategory(preference.category.isSelected, pref_culture_btn, R.drawable.ic_pref_culture, R.drawable.ic_pref_culture_clicked)
                    7 -> changeStatusCategory(preference.category.isSelected, pref_movie_btn, R.drawable.ic_pref_movie, R.drawable.ic_pref_movie_clicked)
                    8 -> changeStatusCategory(preference.category.isSelected, pref_shopping_btn, R.drawable.ic_pref_shopping, R.drawable.ic_pref_shopping_clicked)
                    9 -> changeStatusCategory(preference.category.isSelected, pref_holiday_btn, R.drawable.ic_pref_holiday, R.drawable.ic_pref_holiday_clicked)

                }
            }
        })
        setButtons()
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

    override fun onClick(v: View?) {
        when(v){
            pref_city_btn -> preferenceViewModel.setStatusCategory(0)
            pref_sport_btn -> preferenceViewModel.setStatusCategory(1)
            pref_music_btn -> preferenceViewModel.setStatusCategory(2)
            pref_esport_btn -> preferenceViewModel.setStatusCategory(3)
            pref_youtube_btn -> preferenceViewModel.setStatusCategory(4)
            pref_religion_btn -> preferenceViewModel.setStatusCategory(5)
            pref_culture_btn -> preferenceViewModel.setStatusCategory(6)
            pref_movie_btn -> preferenceViewModel.setStatusCategory(7)
            pref_shopping_btn -> preferenceViewModel.setStatusCategory(8)
            pref_holiday_btn -> preferenceViewModel.setStatusCategory(9)
            pref_category_btn_next -> {
                    var count = 0
                    preferenceViewModel.getPreferences().value?.forEach { if(it.category.isSelected) count++}
                    if(count > 0) view?.findNavController()?.navigate(PreferenceCategoryDirections.actionPreferenceCategoryToPreferenceSubCategory(preferenceCategoryArgs.isInLogin))
                    else {
                        viewModel.authenticationState.value = LoginViewModel.AuthenticationState.GUEST
                    }

            }
        }
    }

    private fun changeStatusCategory(boolean: Boolean, imageView: ImageView, icPrefCity: Int, icPrefCityClicked: Int){
        if(boolean) imageView.setImageResource(icPrefCityClicked)
        else imageView.setImageResource(icPrefCity)
    }

}
