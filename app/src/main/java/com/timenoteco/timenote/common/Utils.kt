package com.timenoteco.timenote.common

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.asksira.bsimagepicker.BSImagePicker
import com.google.android.gms.maps.model.LatLng
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.AutoSuggestAdapter
import com.timenoteco.timenote.adapter.WebSearchAdapter
import com.timenoteco.timenote.listeners.PlacePickerListener
import com.timenoteco.timenote.model.DetailedPlace
import com.timenoteco.timenote.model.Location
import com.timenoteco.timenote.viewModel.WebSearchViewModel
import kotlinx.android.synthetic.main.autocomplete_search_address.view.*
import kotlinx.android.synthetic.main.web_search_rv.view.*
import org.apache.http.impl.cookie.DateUtils.formatDate
import java.util.*

class Utils {


    fun placePicker(context: Context, lifecycleOwner: LifecycleOwner, textView: TextView, placePickerListener: PlacePickerListener, fromNearby: Boolean, activity: Activity){
        val places : MutableList<Address> = mutableListOf()
        val TRIGGER_AUTO_COMPLETE = 500
        val AUTO_COMPLETE_DELAY: Long = 500
        lateinit var handler: Handler

        val dialog = MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.where)
            onDismiss {if(fromNearby) hideStatusBar(activity)}
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
            textView.text = autocompleteAdapter.getObject(position).getAddressLine(0)
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
                            val latLong: LatLng = LatLng(y.latitude, y.longitude)
                            places.add(y)
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

    fun picturePicker(context: Context, resources: Resources, view: View, view1: View, fragment: Fragment, webSearchViewModel: WebSearchViewModel) {
        val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.take_add_a_picture)
            listItems(items = listOf(resources.getString(R.string.take_a_photo), resources.getString(R.string.choose_from_gallery), resources.getString(R.string.search_on_web))) { _, index, text ->
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    view.visibility = View.GONE
                    view1.visibility = View.VISIBLE
                    when (text) {
                        resources.getString(R.string.take_a_photo) -> createPictureSingleBS(fragment.childFragmentManager, "single")
                        resources.getString(R.string.choose_from_gallery) -> createPictureMultipleBS(fragment.childFragmentManager, "multiple")
                        resources.getString(R.string.search_on_web) -> createWebSearchDialog(context, webSearchViewModel, fragment, view, view1)
                    }
                } else fragment.requestPermissions(PERMISSIONS_STORAGE, 2)
            }
            lifecycleOwner(fragment)
        }
    }

    private fun createWebSearchDialog(context: Context, webSearchViewModel: WebSearchViewModel, fragment: Fragment, view: View, view1: View) {
        var recyclerView : RecyclerView?
        var webSearchAdapter : WebSearchAdapter? = null
        var progressDialog: Dialog = progressDialog(context)
        MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            input { _, charSequence ->
                progressDialog.show()
                webSearchViewModel.search(charSequence.toString(), context, 0)
                webSearchViewModel.getListResults().removeObservers(fragment.viewLifecycleOwner)
                webSearchViewModel.getListResults().observe(fragment.viewLifecycleOwner, Observer {
                    if (!it.isNullOrEmpty()) {
                        view.visibility = View.GONE
                        view1.visibility = View.VISIBLE
                        if (it.size <= 10) {
                            val dialog =
                                MaterialDialog(context, BottomSheet(LayoutMode.MATCH_PARENT)).show {
                                    customView(R.layout.web_search_rv, scrollable = true)
                                    lifecycleOwner(fragment.viewLifecycleOwner)
                                }

                            recyclerView = dialog.getCustomView().websearch_rv as RecyclerView
                            recyclerView?.apply {
                                webSearchAdapter = WebSearchAdapter(
                                    it,
                                    fragment as WebSearchAdapter.ImageChoosedListener,
                                    fragment as WebSearchAdapter.MoreImagesClicked,
                                    charSequence.toString(),
                                    dialog
                                )
                                layoutManager = GridLayoutManager(context, 2)
                                (layoutManager as GridLayoutManager).spanSizeLookup
                                adapter = webSearchAdapter
                                webSearchAdapter?.notifyDataSetChanged()
                                progressDialog.hide()
                            }

                            dialog.onDismiss {
                                webSearchAdapter?.clear()
                                webSearchAdapter = null
                                webSearchViewModel.getListResults().removeObservers(fragment.viewLifecycleOwner)
                            }

                        } else {
                            webSearchAdapter?.notifyDataSetChanged()
                        }


                    }
                })
            }
            onDismiss {
                if (webSearchAdapter != null && webSearchAdapter?.images.isNullOrEmpty()) {
                    view.visibility = View.VISIBLE
                    view1.visibility = View.GONE
                } else {
                    webSearchAdapter?.clear()
                }
            }
            positiveButton(R.string.search_on_web)
            lifecycleOwner(fragment.viewLifecycleOwner)
        }
    }

    fun showPicSelected(bitmap: Bitmap, position:Int?, croper: (Bitmap?, Int?) -> Unit){
        croper(bitmap, position)
    }

    fun createPictureSingleBS(childFragmentManager: FragmentManager, tag: String){
        BSImagePicker.Builder("com.timenoteco.timenote.fileprovider")
            .setSpanCount(3)
            .useFrontCamera()
            .setTag(tag)
            .build()
            .show(childFragmentManager, "")
    }

    fun createPictureMultipleBS(childFragmentManager: FragmentManager, tag: String){
        BSImagePicker.Builder("com.timenoteco.timenote.fileprovider")
            .isMultiSelect
            .setSpanCount(3)
            .setTag(tag)
            .build()
            .show(childFragmentManager, "")
    }

    fun hideStatusBar(activity: Activity){
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_IMMERSIVE
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun showStatusBar(activity: Activity){
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    fun createPb(context: Context): CircularProgressDrawable {
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        return circularProgressDrawable
    }

    fun progressDialog(context: Context): Dialog {
        val dialog = Dialog(context)
        val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
        dialog.setContentView(inflate)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    fun calculateDecountTime(): String {
        val o = 1627243200000
        val t = System.currentTimeMillis()
        val time = o - t
        val c: Calendar = Calendar.getInstance(TimeZone.getTimeZone("fr"))
        c.timeInMillis = time
        val mYear: Int = c.get(Calendar.YEAR) - 1970
        val mMonth: Int = c.get(Calendar.MONTH)
        val mDay: Int = c.get(Calendar.DAY_OF_MONTH) - 1
        val mHours: Int = c.get(Calendar.HOUR)
        val mMin : Int = c.get(Calendar.MINUTE)
        val mWeek: Int = (c.get(Calendar.DAY_OF_MONTH) - 1) / 7

        var decountTime = ""
        if(mYear == 0){
            if(mMonth == 0){
                if(mDay == 0){
                    if(mHours > 1){
                        if(mMin > 1){
                            decountTime = "In $mHours hours and $mMin minutes"
                        } else {
                            decountTime = "In $mHours hours and $mMin minute"
                        }
                    }
                } else {
                    if(mMin > 1){
                        decountTime = "In $mHours hour and $mMin minutes"
                    } else {
                        decountTime = "In $mHours hour and $mMin minute"
                    }
                }
            } else {
                if(mMonth > 1){
                    if(mDay > 1){
                        decountTime = "In $mMonth months and $mDay days"
                    } else {
                        decountTime = "In $mMonth months and $mDay day"
                    }
                } else {
                    if(mDay >1){
                        decountTime = "In $mMonth month and $mDay days"
                    } else {
                        decountTime = "In $mMonth month and $mDay day"
                    }
                }
            }
        } else {
            if(mYear > 1){
                if(mMonth > 1) {
                    decountTime = "In $mYear years and $mMonth months"
                } else {
                    decountTime = "In $mYear years and $mMonth month"
                }
            } else {
                if(mMonth > 1){
                    decountTime = "In $mYear year and $mMonth months"
                } else {
                    decountTime = "In $mYear year and $mMonth month"
                }
            }
        }
        return decountTime
    }

    fun setLocation(detailedPlace: DetailedPlace): Location {
        var zipcode = ""
        var city = ""
        var country = ""
        for(n in detailedPlace.result.address_components){
            if(n.types.contains("locality")) city = n.long_name
            if(n.types.contains("postal_code")) zipcode = n.short_name
            if(n.types.contains("country")) country = n.long_name
        }
        return Location(detailedPlace.result.geometry.location.lat, detailedPlace.result.geometry.location.lng,
            com.timenoteco.timenote.model.Address(detailedPlace.result.name, zipcode, city, country)
        )
    }

}
