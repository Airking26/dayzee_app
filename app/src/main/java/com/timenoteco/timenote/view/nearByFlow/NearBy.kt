package com.timenoteco.timenote.view.nearByFlow

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
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
import com.timenoteco.timenote.adapter.*
import com.timenoteco.timenote.common.BaseThroughFragment
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.common.stringLiveData
import com.timenoteco.timenote.listeners.BackToHomeListener
import com.timenoteco.timenote.listeners.GoToProfile
import com.timenoteco.timenote.listeners.ShowBarListener
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.view.homeFlow.HomeDirections
import com.timenoteco.timenote.viewModel.*
import com.timenoteco.timenote.webService.NearbyFilterData
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.BranchEvent
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.fragment_near_by.*
import kotlinx.android.synthetic.main.friends_search_cl.view.*
import kotlinx.android.synthetic.main.users_participating.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class NearBy : BaseThroughFragment(), View.OnClickListener, TimenoteOptionsListener,
    UsersPagingAdapter.SearchPeopleListener, UsersShareWithPagingAdapter.SearchPeopleListener,
    UsersShareWithPagingAdapter.AddToSend {

    private lateinit var goToProfileLisner : GoToProfile
    private lateinit var goBackHome: BackToHomeListener
    private var sendTo: MutableList<String> = mutableListOf()
    private lateinit var handler: Handler
    private val TRIGGER_AUTO_COMPLETE = 200
    private val AUTO_COMPLETE_DELAY: Long = 200
    private lateinit var makeBarVisibleListener: ShowBarListener
    private val timenoteViewModel: TimenoteViewModel by activityViewModels()
    private val AUTOCOMPLETE_REQUEST_CODE: Int = 12
    private lateinit var locationManager: LocationManager
    private lateinit var nearbyDateTv: TextView
    private val loginViewModel : LoginViewModel by activityViewModels()
    private val followViewModel: FollowViewModel by activityViewModels()
    private val DATE_FORMAT = "EEE, d MMM yyyy"
    val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private lateinit var dateFormat : SimpleDateFormat
    private var googleMap: GoogleMap? = null
    private var placesList: List<Place.Field> = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
    private lateinit var placesClient: PlacesClient
    private var mapFragment : SupportMapFragment? = null
    private var firstTime: Boolean = true
    private lateinit var timenotePagingAdapter: TimenotePagingAdapter
    private lateinit var prefs : SharedPreferences
    private lateinit var nearbyFilterData: NearbyFilterData
    private val nearbyViewModel: NearbyViewModel by activityViewModels()
    private var tokenId: String? = null
    private var nearbyToCompare: String = ""
    private val utils = Utils()
    private var userInfoDTO: UserInfoDTO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
        loginViewModel.getAuthenticationState().observe(requireActivity(), androidx.lifecycle.Observer {
            when (it) {
                LoginViewModel.AuthenticationState.UNAUTHENTICATED ->{
                    findNavController().navigate(NearByDirections.actionGlobalNavigation())
                }
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    tokenId = prefs.getString(accessToken, null)
                    findNavController().popBackStack(R.id.nearBy, false)
                }
                LoginViewModel.AuthenticationState.GUEST ->
                    findNavController().popBackStack(R.id.nearBy, false)
            }
        })
        Places.initialize(requireContext(), "AIzaSyBhM9HQo1fzDlwkIVqobfmrRmEMCWTU1CA")
        placesClient = Places.createClient(requireContext())


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        makeBarVisibleListener.onBarAskedToShow()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        //return getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_near_by)
        return inflater.inflate(R.layout.fragment_near_by, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        goBackHome = context as BackToHomeListener
        goToProfileLisner = context as GoToProfile
        makeBarVisibleListener = context as ShowBarListener
    }

    override fun onResume() {
        super.onResume()
        Utils().hideStatusBar(requireActivity())
    }

    @OptIn(ExperimentalPagingApi::class)
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Utils().hideStatusBar(requireActivity())

        nearby_swipe_refresh.isEnabled = false
        nearby_swipe_refresh.isRefreshing = true
        nearby_swipe_refresh.setColorSchemeResources(R.color.colorStartGradient, R.color.colorEndGradient)

        nearbyFilterData = NearbyFilterData(requireContext())
        dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        nearbyDateTv = nearby_time
        nearby_place.setOnClickListener(this)
        nearby_time.setOnClickListener(this)
        nearby_filter_btn.setOnClickListener(this)

        val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
        userInfoDTO = Gson().fromJson<UserInfoDTO>(prefs.getString("UserInfoDTO", null), typeUserInfo)

        if(userInfoDTO != null) nearbyFilterData.setID(userInfoDTO?.id!!)

        //nearby_swipe_refresh.setOnRefreshListener { loadData(nearbyModifyModel) }

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

        prefs.stringLiveData("nearby", Gson().toJson(nearbyFilterData.loadNearbyFilter())).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val typeNearby: Type = object : TypeToken<NearbyRequestBody?>() {}.type
            val nearbyModifyModel : NearbyRequestBody? = Gson().fromJson<NearbyRequestBody>(it, typeNearby)
            nearby_place.text = nearbyModifyModel?.location?.address?.address
            if(nearbyModifyModel?.date == null || nearbyModifyModel.date.isBlank())
                nearby_time.text = dateFormat.format(System.currentTimeMillis())
            else nearby_time.text = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(SimpleDateFormat(ISO, Locale.getDefault()).parse(nearbyModifyModel.date).time)

            if(nearbyModifyModel?.location?.latitude != 0.0 && nearbyModifyModel?.location?.longitude != 0.0) {
                loadData(nearbyModifyModel)
                if(nearbyToCompare.isNotBlank() && nearbyToCompare.isNotEmpty()){
                    val nearbyToCompareFormatted = Gson().fromJson<NearbyRequestBody>(nearbyToCompare, typeNearby)
                    if(nearbyToCompareFormatted.location != nearbyModifyModel?.location){
                        this.googleMap?.addMarker(MarkerOptions().position(LatLng(nearbyModifyModel?.location?.latitude!!, nearbyModifyModel.location.longitude)))
                        this.googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(nearbyModifyModel?.location?.latitude!!, nearbyModifyModel.location.longitude), 15F))
                    }
                }
            }

            nearbyToCompare = Gson().toJson(nearbyFilterData.loadNearbyFilter())
        })
     }

    @ExperimentalPagingApi
    private fun loadData(nearbyModifyModel: NearbyRequestBody?) {
        timenotePagingAdapter = TimenotePagingAdapter(TimenoteComparator, this, this, true, Utils(), userInfoDTO?.id)
        nearby_rv.adapter =  timenotePagingAdapter.withLoadStateFooter(
            footer = TimenoteLoadStateAdapter{ timenotePagingAdapter.retry() }
        )
        nearby_rv.layoutManager = LinearLayoutManager(requireContext())
                lifecycleScope.launch {
                    nearbyViewModel.getNearbyResults(nearbyModifyModel!!, prefs).collectLatest { pagingData ->
                        timenotePagingAdapter.submitData(pagingData)
                    }
                }

        timenotePagingAdapter.addDataRefreshListener { isEmpty ->
            nearby_swipe_refresh?.isRefreshing = false
            if (isEmpty) {
                nearby_rv?.visibility = View.GONE
                nearby_nothing_to_display?.visibility = View.VISIBLE
            } else {
                nearby_rv?.visibility = View.VISIBLE
                nearby_nothing_to_display?.visibility = View.GONE
            }
        }
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
                            val location = Utils().setLocation(detailedPlace.body()!!, false, null)
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
                if(view != null) nearbyViewModel.fetchLocation(place?.id!!).observe(viewLifecycleOwner, androidx.lifecycle.Observer { detailedPlace ->
                    if(detailedPlace.isSuccessful) nearbyFilterData.setWhere(Utils().setLocation(detailedPlace.body()!!, false, null))
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

    override fun onCommentClicked(event: TimenoteInfoDTO) {
        if(userInfoDTO == null) loginViewModel.markAsUnauthenticated()
        else findNavController().navigate(NearByDirections.actionGlobalDetailedTimenote(3, event))
    }

    override fun onPlusClicked(timenoteInfoDTO: TimenoteInfoDTO, isAdded: Boolean) {
        if(userInfoDTO == null) loginViewModel.markAsUnauthenticated()
        else {
            if (isAdded) {
                timenoteViewModel.joinTimenote(tokenId!!, timenoteInfoDTO.id)
                    .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                        if (it.code() == 401) {
                            loginViewModel.refreshToken(prefs).observe(
                                viewLifecycleOwner,
                                androidx.lifecycle.Observer { newAccessToken ->
                                    tokenId = newAccessToken
                                    timenoteViewModel.joinTimenote(tokenId!!, timenoteInfoDTO.id)
                                })
                        }
                    })
            } else {
                timenoteViewModel.leaveTimenote(tokenId!!, timenoteInfoDTO.id)
                    .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                        if (it.code() == 401) {
                            loginViewModel.refreshToken(prefs).observe(
                                viewLifecycleOwner,
                                androidx.lifecycle.Observer { newAccessToken ->
                                    tokenId = newAccessToken
                                    timenoteViewModel.leaveTimenote(tokenId!!, timenoteInfoDTO.id)
                                })
                        }
                    })
            }
        }
    }

    override fun onClick(v: View?) {
        when(v){
            nearby_place -> {
                if(userInfoDTO == null) loginViewModel.markAsUnauthenticated()
                else startActivityForResult(Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, placesList).build(requireContext()), AUTOCOMPLETE_REQUEST_CODE)
            }
            nearby_time -> {
                if(userInfoDTO == null) loginViewModel.markAsUnauthenticated()
                else MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    onDismiss { Utils().hideStatusBar(requireActivity()) }
                    datePicker { dialog, datetime ->
                        nearbyDateTv.text = dateFormat.format(datetime.time.time)
                        nearbyFilterData.setWhen(utils.formatDate(ISO, datetime.time.time))
                    }
                }
            }
            nearby_filter_btn -> {
                if(userInfoDTO == null) loginViewModel.markAsUnauthenticated()
                else findNavController().navigate(NearByDirections.actionNearByToNearbyFilters())
            }
        }
    }

    override fun onPictureClicked(userInfoDTO: UserInfoDTO) {
        if(this.userInfoDTO == null) loginViewModel.markAsUnauthenticated()
        else {
            if (userInfoDTO.id == this.userInfoDTO?.id) goToProfileLisner.goToProfile()
            else findNavController().navigate(
                NearByDirections.actionGlobalProfileElse(3).setUserInfoDTO(userInfoDTO)
            )
        }
    }

    override fun onHideToOthersClicked(timenoteInfoDTO: TimenoteInfoDTO) {

    }

    override fun onMaskThisUser() {
    }

    override fun onDoubleClick() {}

    override fun onSeeParticipants(infoDTO: TimenoteInfoDTO) {
        if(userInfoDTO == null) loginViewModel.markAsUnauthenticated()
        else {
            val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
                customView(R.layout.users_participating)
                lifecycleOwner(this@NearBy)
            }

            val recyclerview = dial.getCustomView().users_participating_rv
            val userAdapter = UsersPagingAdapter(
                UsersPagingAdapter.UserComparator,
                infoDTO,
                this,
                null,
                null
            )
            recyclerview.layoutManager = LinearLayoutManager(requireContext())
            recyclerview.adapter = userAdapter
            lifecycleScope.launch {
                timenoteViewModel.getUsersParticipating(tokenId!!, infoDTO.id, prefs)
                    .collectLatest {
                        userAdapter.submitData(it)
                    }
            }
        }
    }

    override fun onSeeMoreClicked(event: TimenoteInfoDTO) {
        if(userInfoDTO == null) loginViewModel.markAsUnauthenticated()
        else findNavController().navigate(NearByDirections.actionGlobalDetailedTimenote(3, event))
    }

    override fun onReportClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        if(userInfoDTO == null) loginViewModel.markAsUnauthenticated()
        else timenoteViewModel.signalTimenote(tokenId!!, TimenoteCreationSignalementDTO(userInfoDTO?.id!!, timenoteInfoDTO.id, "")).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it.code() == 401){
                loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner, androidx.lifecycle.Observer {newAccessToken ->
                    tokenId = newAccessToken
                    timenoteViewModel.signalTimenote(tokenId!!, TimenoteCreationSignalementDTO(userInfoDTO?.id!!, timenoteInfoDTO.id, "")).observe(viewLifecycleOwner, androidx.lifecycle.Observer { rsp ->
                        if(rsp.isSuccessful) Toast.makeText(
                            requireContext(),
                            "Reported",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                })
            }

            if(it.isSuccessful) Toast.makeText(
                requireContext(),
                "Reported",
                Toast.LENGTH_SHORT
            ).show()
        })
    }

    override fun onEditClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        if(userInfoDTO == null) loginViewModel.markAsUnauthenticated()
        else findNavController().navigate(
            NearByDirections.actionGlobalCreateTimenote().setFrom(3).setModify(2).setId(timenoteInfoDTO.id).setTimenoteBody(CreationTimenoteDTO(timenoteInfoDTO.createdBy.id!!, null, timenoteInfoDTO.title, timenoteInfoDTO.description, timenoteInfoDTO.pictures,
                timenoteInfoDTO.colorHex, timenoteInfoDTO.location, timenoteInfoDTO.category, timenoteInfoDTO.startingAt, timenoteInfoDTO.endingAt,
                timenoteInfoDTO.hashtags, timenoteInfoDTO.url, timenoteInfoDTO.price, null, timenoteInfoDTO.urlTitle)))
    }

    override fun onAlarmClicked(timenoteInfoDTO: TimenoteInfoDTO, type: Int) {
        if(userInfoDTO == null) loginViewModel.markAsUnauthenticated()
        else share(timenoteInfoDTO)
    }

    private fun share(timenoteInfoDTO: TimenoteInfoDTO) {

        val linkProperties: LinkProperties = LinkProperties().setChannel("whatsapp").setFeature("sharing")

        val branchUniversalObject = if(!timenoteInfoDTO.pictures?.isNullOrEmpty()!!) BranchUniversalObject()
            .setTitle(timenoteInfoDTO.title)
            .setContentDescription(timenoteInfoDTO.description)
            .setContentImageUrl(timenoteInfoDTO.pictures[0])
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setContentMetadata(ContentMetadata().addCustomMetadata("timenoteInfoDTO", Gson().toJson(timenoteInfoDTO)))
        else BranchUniversalObject()
            .setTitle(timenoteInfoDTO.title)
            .setContentDescription(timenoteInfoDTO.description)
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setContentMetadata(ContentMetadata().addCustomMetadata("timenoteInfoDTO", Gson().toJson(timenoteInfoDTO)))

        branchUniversalObject.generateShortUrl(requireContext(), linkProperties) { url, error ->
            BranchEvent("branch_url_created").logEvent(requireContext())
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_TEXT, String.format("Dayzee : %s at %s", timenoteInfoDTO.title, url))
            startActivityForResult(i, 111)
        }


    }
    override fun onShareClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        if(userInfoDTO == null) loginViewModel.markAsUnauthenticated()
        else {
        sendTo.clear()
        val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
            customView(R.layout.friends_search_cl)
            lifecycleOwner(this@NearBy)
            positiveButton(R.string.send) {
                timenoteViewModel.shareWith(tokenId!!, ShareTimenoteDTO(timenoteInfoDTO.id, sendTo))
                    .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                        if (it.code() == 401) {
                            loginViewModel.refreshToken(prefs).observe(
                                viewLifecycleOwner,
                                androidx.lifecycle.Observer { newAccessToken ->
                                    tokenId = newAccessToken
                                    timenoteViewModel.shareWith(
                                        tokenId!!,
                                        ShareTimenoteDTO(timenoteInfoDTO.id, sendTo)
                                    )
                                })
                        }
                    })
            }
            negativeButton(R.string.cancel)
        }

        dial.getActionButton(WhichButton.NEGATIVE)
            .updateTextColor(resources.getColor(android.R.color.darker_gray))
        val searchbar = dial.getCustomView().searchBar_friends
        searchbar.setCardViewElevation(0)
        searchbar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE)
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY)
            }

            override fun afterTextChanged(s: Editable?) {}

        })
        val recyclerview = dial.getCustomView().shareWith_rv
        val userAdapter = UsersShareWithPagingAdapter(
            UsersPagingAdapter.UserComparator,
            this,
            this,
            null,
            sendTo,
            null
        )
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.adapter = userAdapter
        lifecycleScope.launch {
            followViewModel.getUsers(tokenId!!, userInfoDTO?.id!!, 0, prefs).collectLatest {
                userAdapter.submitData(it)
            }
        }

        if (searchbar != null) {
            handler = Handler { msg ->
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(searchbar.text)) {
                        lifecycleScope.launch {
                            followViewModel.searchInFollowing(tokenId!!, searchbar.text, prefs)
                                .collectLatest {
                                    userAdapter.submitData(it)
                                }
                        }

                    } else {
                        lifecycleScope.launch {
                            followViewModel.getUsers(tokenId!!, userInfoDTO?.id!!, 0, prefs)
                                .collectLatest {
                                    userAdapter.submitData(it)
                                }
                        }
                    }
                }
                false
            }
        }
    }
    }

    override fun onAddMarker(timenoteInfoDTO: TimenoteInfoDTO) {
        this.googleMap?.addMarker(MarkerOptions().position(LatLng(timenoteInfoDTO.location?.latitude!!, timenoteInfoDTO.location.longitude)))
    }

    override fun onHashtagClicked(timenoteInfoDTO: TimenoteInfoDTO ,hashtag: String?) {
        if(userInfoDTO == null) loginViewModel.markAsUnauthenticated()
        else findNavController().navigate(NearByDirections.actionGlobalTimenoteTAG(timenoteInfoDTO, 3, hashtag))
    }

    override fun onAdd(userInfoDTO: UserInfoDTO, createGroup: Int?) {
        sendTo.add(userInfoDTO.id!!)
    }

    override fun onRemove(userInfoDTO: UserInfoDTO, createGroup: Int?) {
        sendTo.remove(userInfoDTO.id!!)
    }

    override fun onDeleteClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        if(userInfoDTO == null) loginViewModel.markAsUnauthenticated()
        else timenoteViewModel.deleteTimenote(tokenId!!, timenoteInfoDTO.id).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it.code() == 401) {
                loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner, androidx.lifecycle.Observer {newAccessToken ->
                    tokenId = newAccessToken
                    timenoteViewModel.deleteTimenote(tokenId!!, timenoteInfoDTO.id)
                })
            }
        })
    }

    override fun onDuplicateClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        if(userInfoDTO == null) loginViewModel.markAsUnauthenticated()
        else findNavController().navigate(
            NearByDirections.actionGlobalCreateTimenote().setFrom(3).setModify(1).setId(timenoteInfoDTO.id).setTimenoteBody(CreationTimenoteDTO(timenoteInfoDTO.createdBy.id!!, null, timenoteInfoDTO.title, timenoteInfoDTO.description, timenoteInfoDTO.pictures,
            timenoteInfoDTO.colorHex, timenoteInfoDTO.location, timenoteInfoDTO.category, timenoteInfoDTO.startingAt, timenoteInfoDTO.endingAt,
            timenoteInfoDTO.hashtags, timenoteInfoDTO.url, timenoteInfoDTO.price, null, timenoteInfoDTO.urlTitle)))
    }

    override fun onAddressClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        if(userInfoDTO == null) loginViewModel.markAsUnauthenticated()
        else findNavController().navigate(NearByDirections.actionGlobalTimenoteAddress(timenoteInfoDTO, 3))
    }

    override fun onSearchClicked(userInfoDTO: UserInfoDTO) {

    }

    override fun onUnfollow(id: String) {

    }

    override fun onRemove(id: String) {
    }


}
