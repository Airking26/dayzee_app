package com.timenoteco.timenote.view.loginFlow

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.fragment_preference_suggestion.*


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

        suggestion_ok_btn.setOnClickListener {
            view.findNavController().navigate(
                PreferenceSuggestionDirections.actionGlobalMainActivity(
                    true
                )
            )
        }

        /*val prefs = PreferenceHelper.defaultPrefs(requireContext())
        val type: Type = object : TypeToken<List<Preference?>?>() {}.type
        val list = Gson().fromJson<List<Preference>>(prefs.getString("key", ""), type)*/
    }
}
