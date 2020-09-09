package com.timenoteco.timenote.view.nearByFlow

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.*
import android.location.Address
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ItemTimenoteAdapter
import com.timenoteco.timenote.adapter.TimenoteComparator
import com.timenoteco.timenote.adapter.TimenotePagingAdapter
import com.timenoteco.timenote.adapter.UsersPagingAdapter
import com.timenoteco.timenote.common.BaseThroughFragment
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.common.stringLiveData
import com.timenoteco.timenote.listeners.PlacePickerListener
import com.timenoteco.timenote.listeners.ShowBarListener
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.model.Location
import com.timenoteco.timenote.view.profileFlow.ProfileDirections
import com.timenoteco.timenote.viewModel.LoginViewModel
import com.timenoteco.timenote.viewModel.NearbyViewModel
import com.timenoteco.timenote.viewModel.ProfileViewModel
import com.timenoteco.timenote.viewModel.TimenoteViewModel
import com.timenoteco.timenote.webService.NearbyFilterData
import kotlinx.android.synthetic.main.fragment_near_by.*
import kotlinx.android.synthetic.main.users_participating.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class NearBy : BaseThroughFragment(), View.OnClickListener, TimenoteOptionsListener{

    private lateinit var makeBarVisibleListener: ShowBarListener
    private val timenoteViewModel: TimenoteViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private val AUTOCOMPLETE_REQUEST_CODE: Int = 12
    private lateinit var locationManager: LocationManager
    private lateinit var nearbyDateTv: TextView
    private val loginViewModel : LoginViewModel by activityViewModels()
    private var timenotes: List<TimenoteInfoDTO> = mutableListOf()
    private val DATE_FORMAT = "EEE, d MMM yyyy"
    private lateinit var dateFormat : SimpleDateFormat
    private lateinit var timenoteAdapter: ItemTimenoteAdapter
    private var googleMap: GoogleMap? = null
    private var placesList: List<Place.Field> = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
    private lateinit var placesClient: PlacesClient
    private var mapFragment : SupportMapFragment? = null
    private var firstTime: Boolean = true
    private lateinit var timenotePagingAdapter: TimenotePagingAdapter
    private lateinit var prefs : SharedPreferences
    private lateinit var nearbyFilterData: NearbyFilterData
    private val nearbyViewModel: NearbyViewModel by activityViewModels()
    val TOKEN: String = "TOKEN"
    private var tokenId: String? = null
    private var nearbyToCompare: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(TOKEN, null)
        loginViewModel.getAuthenticationState().observe(requireActivity(), androidx.lifecycle.Observer {
            when (it) {
                //LoginViewModel.AuthenticationState.UNAUTHENTICATED -> findNavController().navigate(NearByDirections.actionNearByToNavigation())
                LoginViewModel.AuthenticationState.AUTHENTICATED -> findNavController().popBackStack(R.id.nearBy, false)
                LoginViewModel.AuthenticationState.GUEST -> findNavController().popBackStack(R.id.nearBy, false)
            }
        })
        Places.initialize(requireContext(), "AIzaSyBhM9HQo1fzDlwkIVqobfmrRmEMCWTU1CA")
        placesClient = Places.createClient(requireContext())


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        makeBarVisibleListener.onBarAskedToShow()
         return inflater.inflate(R.layout.fragment_near_by, container, false)}

    override fun onAttach(context: Context) {
        super.onAttach(context)
        makeBarVisibleListener = context as ShowBarListener
    }

    override fun onResume() {
        super.onResume()
        Utils().hideStatusBar(requireActivity())
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Utils().hideStatusBar(requireActivity())

        nearbyFilterData = NearbyFilterData(requireContext())
        dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        nearbyDateTv = nearby_time
        nearby_place.setOnClickListener(this)
        nearby_time.setOnClickListener(this)
        nearby_filter_btn.setOnClickListener(this)

        if(mapFragment == null){
            mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        }

        mapFragment?.getMapAsync {
            this.googleMap = it
            if(firstTime) {
                checkIfCanGetLocation()
            }
        }

        transparent_image_map.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_MOVE -> {
                    nearBy_coordinator_layout.requestDisallowInterceptTouchEvent(false)
                    true
                }
                MotionEvent.ACTION_DOWN -> {
                    nearBy_coordinator_layout.requestDisallowInterceptTouchEvent(true)
                    false
                }
                MotionEvent.ACTION_UP -> {
                    nearBy_coordinator_layout.requestDisallowInterceptTouchEvent(true)
                    false
                }
                else -> false
            }
        }

        timenoteAdapter = ItemTimenoteAdapter(timenotes, timenotes, false, null, this, this as Fragment, true)

        nearby_rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timenoteAdapter
        }

        prefs.stringLiveData("nearby", Gson().toJson(nearbyFilterData.loadNearbyFilter())).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val nearbyModifyModel : NearbyRequestBody? = Gson().fromJson<NearbyRequestBody>(prefs.getString("nearby", null),
                object : TypeToken<NearbyRequestBody?>() {}.type)
            nearby_place.text = nearbyModifyModel?.location?.address?.address
            if(nearbyModifyModel?.date == null || nearbyModifyModel.date.isBlank())
                nearby_time.text = dateFormat.format(System.currentTimeMillis())
            else
                nearby_time.text = nearbyModifyModel.date
            if(nearbyToCompare != Gson().toJson(nearbyFilterData.loadNearbyFilter())){
                lifecycleScope.launch {
                    nearbyViewModel.getNearbyResults(tokenId!!, nearbyModifyModel!!).collectLatest {
                        val i = it
                        i.toString()
                    }
                }

               /* timenotePagingAdapter = TimenotePagingAdapter(TimenoteComparator, this, this)
                nearby_rv.adapter = timenotePagingAdapter
                lifecycleScope.launch {
                    nearbyViewModel.getNearbyResults(tokenId, NearbyRequestBody(nearbyModifyModel?.where!!,
                        nearbyModifyModel.distance!!, Categories(nearbyModifyModel.categories!![0].category,
                            nearbyModifyModel.categories!![0].subcategory), nearbyModifyModel.whenn!!,
                        0, nearbyModifyModel.from.toString())).collectLatest {
                        timenotePagingAdapter.submitData(it)
                    }
                }*/
            }
            nearbyToCompare = Gson().toJson(nearbyFilterData.loadNearbyFilter())
        })
     }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Utils().hideStatusBar(requireActivity())
                        this.googleMap?.addMarker(MarkerOptions().position(LatLng(place.latLng?.latitude!!, place.latLng?.longitude!!)))
                        this.googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(place.latLng?.latitude!!, place.latLng?.longitude!!), 15F))
                        nearbyViewModel.fetchLocation(place.id!!).observe(viewLifecycleOwner, androidx.lifecycle.Observer { detailedPlace ->
                            val location = Utils().setLocation(detailedPlace.body()!!)
                            if(detailedPlace.isSuccessful) nearbyFilterData.setWhere(location)
                        })
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(ContentValues.TAG, status.statusMessage!!)
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun checkIfCanGetLocation() {
        locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            this.requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 3
            )
        } else {
            getLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        placesClient.findCurrentPlace(FindCurrentPlaceRequest.newInstance(placesList)).addOnCompleteListener {
            if(it.isSuccessful){
                val place = it.result?.placeLikelihoods?.get(0)?.place
                googleMap?.addMarker(MarkerOptions().position(LatLng(place?.latLng?.latitude!!, place.latLng?.longitude!!)))
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(place?.latLng?.latitude!!, place.latLng?.longitude!!), 15F))
                nearbyViewModel.fetchLocation(place?.id!!).observe(viewLifecycleOwner, androidx.lifecycle.Observer { detailedPlace ->
                    if(detailedPlace.isSuccessful) nearbyFilterData.setWhere(Utils().setLocation(detailedPlace.body()!!))
                    //nearbyFilterData.setWhen(getString(R.string.today))
                    firstTime = false
                })
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == 3){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocation()
            }
        }
    }

    override fun onCommentClicked() {
        findNavController().navigate(NearByDirections.actionNearByToDetailedTimenote(3))
    }

    override fun onPlusClicked() {
    }

    override fun onClick(v: View?) {
        when(v){
            nearby_place -> startActivityForResult(Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, placesList).build(requireContext()), AUTOCOMPLETE_REQUEST_CODE)
            nearby_time -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                onDismiss { Utils().hideStatusBar(requireActivity()) }
                datePicker { dialog, datetime ->
                    nearbyDateTv.text = dateFormat.format(datetime.time.time)
                    nearbyFilterData.setWhen(dateFormat.format(datetime.time.time))
                }
            }
            nearby_filter_btn -> findNavController().navigate(NearByDirections.actionNearByToNearbyFilters())
        }
    }

    override fun onPictureClicked() {
        findNavController().navigate(NearByDirections.actionNearByToProfile(true, 3))
    }

    override fun onHideToOthersClicked() {

    }

    override fun onMaskThisUser() {
    }

    override fun onDoubleClick() {
        timenoteViewModel.getSpecificTimenote(tokenId!!, "").observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            /*if(it.body()?.joinedBy?.users.contains("")!!) timenoteViewModel.joinTimenote(tokenId!!, "")
            else timenoteViewModel.leaveTimenote(tokenId!!, "")*/
        })
    }

    override fun onSeeParticipants() {
        val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            customView(R.layout.users_participating)
            lifecycleOwner(this@NearBy)
        }

        val recyclerview = dial.getCustomView().users_participating_rv
        val adapter = UsersPagingAdapter(UsersPagingAdapter.UserComparator)
        recyclerview.adapter = adapter
        lifecycleScope.launch{
            profileViewModel.getUsers(tokenId!!, followers = true, useTimenoteService = true, id =  null).collectLatest {
                adapter.submitData(it)
            }
        }
    }

    override fun onSeeMoreClicked() {
        findNavController().navigate(NearByDirections.actionNearByToDetailedTimenote(3))
    }

    override fun onReportClicked() {
        timenoteViewModel.deleteTimenote(tokenId!!, "")
    }

    override fun onEditClicked() {
        findNavController().navigate(NearByDirections.actionNearByToCreateTimenote(true, "",
            TimenoteBody("", CreatedBy("", "", "", "", "", "", ""),
                "", "", listOf(), "", com.timenoteco.timenote.model.Location(0.0, 0.0, com.timenoteco.timenote.model.Address("", "", "", "")),
                Category("",""), "", "", listOf(), "", 0, ""), 3
        ))
    }

    override fun onAlarmClicked() {
    }

    override fun onDeleteClicked() {
        timenoteViewModel.deleteTimenote(tokenId!!, "")
    }

    override fun onDuplicateClicked() {
        findNavController().navigate(NearByDirections.actionNearByToCreateTimenote(true, "",
            TimenoteBody("", CreatedBy("", "", "", "", "", "", ""),
                "", "", listOf(), "", com.timenoteco.timenote.model.Location(0.0, 0.0, com.timenoteco.timenote.model.Address("", "", "", "")),
                Category("",""), "", "", listOf(), "", 0, ""), 3
        ))
    }

    override fun onAddressClicked() {
        findNavController().navigate(NearByDirections.actionNearByToTimenoteAddress())
    }

}
