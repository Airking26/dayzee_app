package com.timenoteco.timenote.view.nearByFlow

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings.System.DATE_FORMAT
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.R
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.common.stringLiveData
import com.timenoteco.timenote.model.Category
import com.timenoteco.timenote.model.NearbyFilterModel
import com.timenoteco.timenote.model.ProfilModifyModel
import com.timenoteco.timenote.viewModel.NearbyViewModel
import com.timenoteco.timenote.webService.NearbyFilterData
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.android.synthetic.main.fragment_nearby_filters.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class NearbyFilters : Fragment(), View.OnClickListener {

    private lateinit var prefs : SharedPreferences
    private lateinit var nearbyFilterData: NearbyFilterData
    private lateinit var nearbyFilterCategoryTv: TextView
    private lateinit var nearbyFilterFromTv: TextView
    private lateinit var nearbyFilterPaidTimenoteTv: TextView
    private lateinit var nearbyFilterWhenTv: TextView
    private lateinit var nearbyFilterWhereTv: TextView
    private lateinit var dateFormat : SimpleDateFormat
    private val DATE_FORMAT = "EEE, d MMM yyyy"
    private var placesList: List<Place.Field> = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
    private lateinit var placesClient: PlacesClient
    private val AUTOCOMPLETE_REQUEST_CODE: Int = 12
    private val nearbyViewModel : NearbyViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(requireContext(), "AIzaSyBhM9HQo1fzDlwkIVqobfmrRmEMCWTU1CA")
        placesClient = Places.createClient(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_nearby_filters, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        nearbyFilterData = NearbyFilterData(requireContext())
        dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

        nearbyFilterCategoryTv = nearby_filter_category_tv
        nearbyFilterFromTv = nearby_filter_from_tv
        nearbyFilterPaidTimenoteTv = nearby_filter_paid_timenote_tv
        nearbyFilterWhereTv = nearby_filter_where_tv
        nearbyFilterWhenTv = nearby_filter_when_tv

        nearby_filter_from.setOnClickListener(this)
        nearby_filter_category.setOnClickListener(this)
        nearby_filter_done_btn.setOnClickListener(this)
        nearby_filter_paid_timenote.setOnClickListener(this)
        nearby_filter_when.setOnClickListener(this)
        nearby_filter_where.setOnClickListener(this)

        nearby_distance_seekbar.onSeekChangeListener = object: OnSeekChangeListener{
            override fun onSeeking(seekParams: SeekParams?) {

            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                nearbyFilterData.setDistance(seekBar?.progress!!)
            }

        }

        prefs.stringLiveData("nearby", Gson().toJson(nearbyFilterData.loadNearbyFilter())).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val type: Type = object : TypeToken<NearbyFilterModel?>() {}.type
            val nearbyModifyModel : NearbyFilterModel? = Gson().fromJson<NearbyFilterModel>(prefs.getString("nearby", null), type)
            if(nearbyModifyModel?.categories.isNullOrEmpty()) nearby_filter_category_tv.text = getString(R.string.none) else nearby_filter_category_tv.text = nearbyModifyModel?.categories.toString()
            when (nearbyModifyModel?.from) {
                0 -> nearby_filter_from_tv.text = getString(R.string.public_and_private)
                1 -> nearby_filter_from_tv.text = getString(R.string.private_label)
                2 -> nearby_filter_from_tv.text = getString(R.string.public_label)
                else -> nearby_filter_from_tv.text = getString(R.string.public_label)
            }
            when(nearbyModifyModel?.paidTimenote){
                0 -> nearby_filter_paid_timenote_tv.text = getString(R.string.free_and_paid)
                1 -> nearby_filter_paid_timenote_tv.text = getString(R.string.free)
                2 -> nearby_filter_paid_timenote_tv.text = getString(R.string.paid)
                else -> nearby_filter_paid_timenote_tv.text = getText(R.string.free_and_paid)
            }
            when(nearbyModifyModel?.distance){
                in 1..100 -> nearby_distance_seekbar.setProgress(nearbyModifyModel?.distance?.toFloat()!!)
                else -> nearby_distance_seekbar.setProgress(10F)
            }
            if(nearbyModifyModel?.whenn.isNullOrBlank()) nearby_filter_when_tv.text = getString(R.string.today) else nearby_filter_when_tv.text = nearbyModifyModel?.whenn
            if(nearbyModifyModel?.where == null) nearby_filter_where_tv.text = "" else nearby_filter_where_tv.text = nearbyModifyModel.where?.address?.address
        })

    }

    override fun onClick(v: View?) {
        when (v) {
            nearby_filter_category -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.category)
                positiveButton(R.string.done)
                listItemsMultiChoice(items = listOf(
                        "Judaisme",
                        "Bouddhisme",
                        "Techno",
                        "Pop",
                        "Football",
                        "Tennis",
                        "Judaisme",
                        "Bouddhisme",
                        "Techno",
                        "Pop",
                        "Football",
                        "Tennis",
                        "Judaisme",
                        "Bouddhisme",
                        "Techno",
                        "Pop",
                        "Football",
                        "Tennis"
                    )) { _, index, text ->
                    nearbyFilterData.setCategories(listOf())
                }
                lifecycleOwner(this@NearbyFilters)
            }
            nearby_filter_from -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.from)
                listItems(null, listOf("Public", "Private", "Public and Private")) { dialog, index, text ->
                    nearbyFilterData.setFrom(index)
                }
                lifecycleOwner(this@NearbyFilters)
            }
            nearby_filter_done_btn -> findNavController().navigate(NearbyFiltersDirections.actionNearbyFiltersToNearBy())
            nearby_filter_paid_timenote -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.from)
                listItems(null, listOf(getString(R.string.free), getString(R.string.paid), getString(R.string.free_and_paid))) { dialog, index, text ->
                    nearbyFilterData.setPaidTimenote(index)
                }
                lifecycleOwner(this@NearbyFilters)
            }
            nearby_filter_when -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                datePicker { dialog, datetime ->
                    nearbyFilterData.setWhen(dateFormat.format(datetime.time.time))
                }
            }
            nearby_filter_where -> startActivityForResult(
                Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, placesList).build(requireContext()), AUTOCOMPLETE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        nearbyFilterWhereTv.text = place.address
                        nearbyViewModel.fetchLocation(place.id!!).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                            if(it.isSuccessful) nearbyFilterData.setWhere(Utils().setLocation(it.body()!!))
                        })
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> { data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(ContentValues.TAG, status.statusMessage!!)
                    } }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }



}
