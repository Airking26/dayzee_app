package com.timenoteco.timenote.common

import android.content.Context
import android.location.Geocoder
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.timenoteco.timenote.listeners.PlacePickerListener
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.AutoSuggestAdapter
import kotlinx.android.synthetic.main.autocomplete_search_address.view.*

class Utils {

    fun placePicker(context: Context, lifecycleOwner: LifecycleOwner, textView: TextView, placePickerListener: PlacePickerListener){
        val places : MutableList<String> = mutableListOf()
        val TRIGGER_AUTO_COMPLETE = 250
        val AUTO_COMPLETE_DELAY: Long = 500
        lateinit var handler: Handler

        val dialog = MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.where)
            customView(R.layout.autocomplete_search_address, scrollable = true, horizontalPadding = true)
            positiveButton(R.string.done)
            lifecycleOwner(lifecycleOwner)
        }
        val customView = dialog.getCustomView()
        val autoCompleteTextView = customView.autocompleteTextview_Address as AutoCompleteTextView
        val geocoder = Geocoder(context)
        val autocompleteAdapter = AutoSuggestAdapter(context, android.R.layout.simple_dropdown_item_1line)
        autoCompleteTextView.threshold = 3
        autoCompleteTextView.setAdapter(autocompleteAdapter)
        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            dialog.dismiss()
            placePickerListener.onPlacePicked(autocompleteAdapter.getObject(position))
            textView.text = autocompleteAdapter.getObject(position)
        }
        handler = Handler(Handler.Callback { msg ->
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(autoCompleteTextView.text)) {
                    places.clear()
                    val i = geocoder.getFromLocationName(autoCompleteTextView.text.toString(), 3)
                    if(!i.isNullOrEmpty()){
                        for(y in i){
                            //val city: String = y.locality ?: ""
                            //val country: String = y.countryName ?: ""
                            val address: String = y.getAddressLine(0) ?: ""
                            places.add(address)
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