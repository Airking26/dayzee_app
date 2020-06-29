package com.timenoteco.timenote.view.loginFlow

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.fragment_preference_suggestion.*


class PreferenceSuggestion : Fragment() {


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_preference_suggestion, container, false)
        view.isFocusableInTouchMode = true;
        view.requestFocus();
        view.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN){
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    false
                }
            }
            true
        }

        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        suggestion_ok_btn.setOnClickListener { findNavController().navigate(PreferenceSuggestionDirections.actionPreferenceSuggestionToLogin(true))}

        /*val prefs = PreferenceHelper.defaultPrefs(requireContext())
        val type: Type = object : TypeToken<List<Preference?>?>() {}.type
        val list = Gson().fromJson<List<Preference>>(prefs.getString("key", ""), type)*/
    }
}
