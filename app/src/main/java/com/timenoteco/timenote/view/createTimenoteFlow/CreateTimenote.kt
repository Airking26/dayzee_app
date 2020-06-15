package com.timenoteco.timenote.view.createTimenoteFlow

import android.app.Dialog
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.AutoSuggestAdapter
import kotlinx.android.synthetic.main.autocomplete_search_address.*
import kotlinx.android.synthetic.main.autocomplete_search_address.view.*
import kotlinx.android.synthetic.main.autocomplete_search_address.view.autocompleteTextview_Address
import kotlinx.android.synthetic.main.fragment_create_timenote.*
import java.util.*

class CreateTimenote : Fragment(), View.OnClickListener {

    var places : MutableList<String> = mutableListOf()
    private val TRIGGER_AUTO_COMPLETE = 100
    private val AUTO_COMPLETE_DELAY: Long = 300
    private lateinit var handler: Handler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_create_timenote, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        create_timenote_next_btn.setOnClickListener(this)
        from_label.setOnClickListener(this)
        to_label.setOnClickListener(this)
        where_cardview.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            create_timenote_next_btn ->findNavController().navigate(CreateTimenoteDirections.actionCreateTimenoteToPreviewTimenoteCreated())
            from_label -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                dateTimePicker { dialog, datetime -> }
                lifecycleOwner(this@CreateTimenote)
            }
            to_label -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                dateTimePicker { dialog, datetime -> }
                lifecycleOwner(this@CreateTimenote)
            }
            where_cardview -> {
                val dialog = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    title(R.string.where)
                    customView(R.layout.autocomplete_search_address, scrollable = true, horizontalPadding = true)
                    positiveButton(R.string.done)
                    lifecycleOwner(this@CreateTimenote)
                }

                val customView = dialog.getCustomView()
                var autoCompleteTextView = customView.autocompleteTextview_Address as AutoCompleteTextView
                val geocoder = Geocoder(requireContext())
                var autocompleteAdapter = AutoSuggestAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line)
                autoCompleteTextView.threshold = 3
                autoCompleteTextView.setAdapter(autocompleteAdapter)

                handler = Handler(Handler.Callback { msg ->
                    if (msg.what == TRIGGER_AUTO_COMPLETE) {
                        if (!TextUtils.isEmpty(autoCompleteTextView.text)) {
                            places.clear()
                            val i = geocoder.getFromLocationName(autoCompleteTextView.text.toString(), 3)
                            if(!i.isNullOrEmpty()){
                                for(y in i){
                                    val city: String = y.locality ?: ""
                                    val country: String = y.countryName ?: ""
                                    val address: String = y.getAddressLine(0) ?: ""
                                    places.add("$address, $city, $country")
                                }
                                autocompleteAdapter.setData(places)
                                autocompleteAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                    false;
                })

                autoCompleteTextView.addTextChangedListener(object: TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                        handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
                    }

                })
            }
        }
    }
}
