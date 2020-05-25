package com.timenoteco.timenote.view

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.R
import com.timenoteco.timenote.common.PreferenceHelper
import com.timenoteco.timenote.model.Preference
import java.lang.reflect.Type


class PreferenceSuggestion : Fragment() {

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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {



        /*val prefs = PreferenceHelper.defaultPrefs(requireContext())
        val type: Type = object : TypeToken<List<Preference?>?>() {}.type
        val list = Gson().fromJson<List<Preference>>(prefs.getString("key", ""), type)*/
    }
}
