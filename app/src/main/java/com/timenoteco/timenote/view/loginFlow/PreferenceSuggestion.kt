package com.timenoteco.timenote.view.loginFlow

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.timenoteco.timenote.R
import com.timenoteco.timenote.viewModel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_preference_suggestion.*


class PreferenceSuggestion : Fragment() {

    private val args : PreferenceCategoryArgs by navArgs()
    private val viewModel: LoginViewModel by activityViewModels()


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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        suggestion_ok_btn.setOnClickListener {
            if(args.isInLogin) viewModel.markAsAuthenticated()
            else findNavController().popBackStack(R.id.profile, false)
        }
    }
}
