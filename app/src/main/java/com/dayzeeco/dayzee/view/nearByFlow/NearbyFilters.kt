package com.dayzeeco.dayzee.view.nearByFlow

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.common.nearby
import com.dayzeeco.dayzee.common.stringLiveData
import com.dayzeeco.dayzee.model.*
import com.dayzeeco.dayzee.viewModel.NearbyViewModel
import com.dayzeeco.dayzee.viewModel.PreferencesViewModel
import com.dayzeeco.dayzee.webService.NearbyFilterData
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.android.synthetic.main.fragment_nearby_filters.*
import java.text.SimpleDateFormat
import java.util.*

class NearbyFilters : Fragment(), View.OnClickListener {

    private lateinit var prefs : SharedPreferences
    private lateinit var nearbyFilterData: NearbyFilterData
    private lateinit var nearbyFilterCategoryTv: TextView
    private lateinit var nearbyFilterFromTv: TextView
    val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private lateinit var nearbyFilterPaidTimenoteTv: TextView
    private lateinit var nearbyFilterWhenTv: TextView
    private lateinit var nearbyFilterWhereTv: TextView
    private lateinit var dateFormat : SimpleDateFormat
    private val DATE_FORMAT = "EEE, d MMM yyyy"
    private var placesList: List<Place.Field> = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
    private lateinit var placesClient: PlacesClient
    private val AUTOCOMPLETE_REQUEST_CODE: Int = 12
    private val nearbyViewModel : NearbyViewModel by activityViewModels()
    private val preferencesViewModel : PreferencesViewModel by activityViewModels()
    private lateinit var subcatview : View

    enum class Type {
        FROMFOLLOWER,
        NOTFROMFOLLOWER,
        ALL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(requireContext(), getString(R.string.api_web_key))
        placesClient = Places.createClient(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_nearby_filters, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        subcatview = nearby_filter_subcategory
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
        nearby_filter_clear_btn.setOnClickListener(this)
        subcatview.setOnClickListener(this)

        nearby_distance_seekbar.onSeekChangeListener = object: OnSeekChangeListener{
            override fun onSeeking(seekParams: SeekParams?) {

            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                nearbyFilterData.setDistance(seekBar?.progress!!)
            }

        }

        prefs.stringLiveData(nearby, Gson().toJson(nearbyFilterData.loadNearbyFilter())).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val type = object : TypeToken<NearbyRequestBody?>() {}.type
            val nearbyModifyModel : NearbyRequestBody? = Gson().fromJson<NearbyRequestBody>(prefs.getString(
                nearby, null), type)
            if(nearbyModifyModel?.categories?.category.isNullOrBlank()) nearby_filter_category_tv.text = getString(R.string.none) else nearby_filter_category_tv.text = nearbyModifyModel?.categories?.category
            if(nearbyModifyModel?.categories?.subcategory.isNullOrBlank()) {
                nearby_filter_subcategory_tv.text = getString(R.string.none)
            } else {
                subcatview.visibility = View.VISIBLE
                nearby_filter_subcategory_tv.text = nearbyModifyModel?.categories?.subcategory
            }
            when (nearbyModifyModel!!.type) {
                Type.ALL.ordinal -> nearby_filter_from_tv.text = getString(R.string.all)
                Type.FROMFOLLOWER.ordinal -> nearby_filter_from_tv.text = getString(R.string.friends)
                Type.NOTFROMFOLLOWER.ordinal -> nearby_filter_from_tv.text = getString(R.string.discover)
                else -> nearby_filter_from_tv.text = getString(R.string.discover)
            }
            when(nearbyModifyModel.price.price){
                0.0 -> nearby_filter_paid_timenote_tv.text = getString(R.string.free)
                in 0.1 .. Double.MAX_VALUE  -> nearby_filter_paid_timenote_tv.text = getString(R.string.paid)
                else -> nearby_filter_paid_timenote_tv.text = getText(R.string.free)
            }
            when(nearbyModifyModel.maxDistance){
                in 1..250 -> nearby_distance_seekbar.setProgress(nearbyModifyModel.maxDistance.toFloat())
                else -> nearby_distance_seekbar.setProgress(10F)
            }
            if(nearbyModifyModel.date.isBlank()) nearby_filter_when_tv.text = SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis()) else nearby_filter_when_tv.text = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(SimpleDateFormat(ISO, Locale.getDefault()).parse(nearbyModifyModel.date).time)
            nearby_filter_where_tv.text = nearbyModifyModel.location.address.address
        })

    }

    override fun onClick(v: View?) {
        when (v) {
            nearby_filter_category -> preferencesViewModel.getCategories().observe(viewLifecycleOwner){
                MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    title(R.string.category)
                    listItems(items = it.body()?.distinctBy { category -> category.category }?.map { it -> it.category }) { _, index, text ->
                        subcatview.visibility = View.VISIBLE
                        nearbyFilterData.setCategories(Categories(text.toString(), ""))
                    }
                }
            }
            subcatview -> {
                preferencesViewModel.getCategories().observe(viewLifecycleOwner) {
                    MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(R.string.category)
                        listItems(items = it.body()?.filter { it -> it.category == nearbyFilterData.loadNearbyFilter()?.categories?.category!! }?.map { it -> it.subcategory }) { _, index, text ->
                            nearbyFilterData.setCategories(
                                Categories(
                                    nearbyFilterData.loadNearbyFilter()?.categories?.category!!,
                                    text.toString()
                                )
                            )
                        }
                    }
                }
            }
            nearby_filter_from -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.from)
                listItems(null, listOf(getString(R.string.discover), getString(R.string.friends), getString(R.string.all))) { _, index, _ ->
                    when(index){
                        0 -> nearbyFilterData.setFrom(Type.NOTFROMFOLLOWER.ordinal)
                        1 -> nearbyFilterData.setFrom(Type.FROMFOLLOWER.ordinal)
                        2 -> nearbyFilterData.setFrom(Type.ALL.ordinal)
                    }
                }
                lifecycleOwner(this@NearbyFilters)
            }
            nearby_filter_done_btn -> {
                nearby_filter_done_btn.visibility = View.INVISIBLE
                nearby_filter_pb.visibility = View.VISIBLE
                if (nearbyFilterData.loadNearbyFilter()?.categories != null && nearbyFilterData.loadNearbyFilter()?.categories?.category?.isNotBlank()!! && nearbyFilterData.loadNearbyFilter()?.categories?.subcategory?.isBlank()!!) {
                    nearby_filter_done_btn.visibility = View.VISIBLE
                    nearby_filter_pb.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.subcategory_needed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else findNavController().navigate(NearbyFiltersDirections.actionNearbyFiltersToNearBy())
            }
            nearby_filter_paid_timenote -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.from)
                listItems(null, listOf(getString(R.string.free), getString(R.string.paid))) { _, index, _ ->
                    when(index){
                        0 -> nearbyFilterData.setPaidTimenote(Price(0.0, ""))
                        1 -> nearbyFilterData.setPaidTimenote(Price(Double.MAX_VALUE, ""))
                    }
                }
                lifecycleOwner(this@NearbyFilters)
            }
            nearby_filter_when -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                datePicker { _, datetime ->
                    nearbyFilterData.setWhen(Utils().formatDate(ISO, datetime.time.time))
                }
            }
            nearby_filter_where -> startActivityForResult(
                Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, placesList).build(requireContext()), AUTOCOMPLETE_REQUEST_CODE)
            nearby_filter_clear_btn -> {
                subcatview.visibility = View.GONE
                nearbyFilterData.clearData(nearbyFilterData.loadNearbyFilter()!!)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        nearbyFilterWhereTv.text = place.address
                        nearbyViewModel.fetchLocation(place.id!!, getString(R.string.api_web_key)).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                            if(it.isSuccessful) nearbyFilterData.setWhere(Utils().setLocation(it.body()!!, false, null))
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
