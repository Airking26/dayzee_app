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
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.Preferences
import com.timenoteco.timenote.viewModel.LoginViewModel
import com.timenoteco.timenote.viewModel.PreferencesViewModel
import kotlinx.android.synthetic.main.fragment_preference_category.*

class PreferenceCategory : Fragment(), View.OnClickListener {

    private val preferencesViewModel: PreferencesViewModel by activityViewModels()
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val preferenceCategoryArgs: PreferenceCategoryArgs by navArgs()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_preference_category, container, false)
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setButtons()
        preferencesViewModel.getCategories().observe(viewLifecycleOwner, Observer {
            if(it.code() == 200){

            }
        })
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
            /*pref_city_btn ->
            pref_sport_btn ->
            pref_music_btn ->
            pref_esport_btn ->
            pref_youtube_btn ->
            pref_religion_btn ->
            pref_culture_btn ->
            pref_movie_btn ->
            pref_shopping_btn ->
            pref_holiday_btn -> */
            pref_category_btn_next -> {
                    var count = 0
                    preferencesViewModel.getPreferences().value?.body()?.forEach {
                //        if(it.category.isSelected) count++
                    }

                if(count > 0){
                    preferencesViewModel.modifyPreferences(Preferences(mutableListOf())).observe(viewLifecycleOwner, Observer {
                        if(it.isSuccessful) view?.findNavController()?.navigate(PreferenceCategoryDirections.actionPreferenceCategoryToPreferenceSubCategory(preferenceCategoryArgs.isInLogin))
                    })
                } else loginViewModel.markAsGuest()

            }
        }
    }

    private fun changeStatusCategory(boolean: Boolean, imageView: ImageView, icPrefCity: Int, icPrefCityClicked: Int){
        if(boolean) imageView.setImageResource(icPrefCityClicked)
        else imageView.setImageResource(icPrefCity)
    }

}
