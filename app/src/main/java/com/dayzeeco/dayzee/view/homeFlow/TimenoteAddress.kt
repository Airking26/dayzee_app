package com.dayzeeco.dayzee.view.homeFlow

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.*
import com.dayzeeco.dayzee.common.*
import com.dayzeeco.dayzee.listeners.GoToProfile
import com.dayzeeco.dayzee.listeners.TimenoteOptionsListener
import com.dayzeeco.dayzee.model.*
import com.dayzeeco.dayzee.viewModel.*
import kotlinx.android.synthetic.main.fragment_timenote_address.*
import kotlinx.android.synthetic.main.friends_search_cl.view.*
import kotlinx.android.synthetic.main.users_participating.view.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import java.lang.reflect.Type

class TimenoteAddress : Fragment(), TimenoteOptionsListener,
    UsersShareWithPagingAdapter.SearchPeopleListener, UsersShareWithPagingAdapter.AddToSend,
    UsersPagingAdapter.SearchPeopleListener, View.OnClickListener {

    private lateinit var goToProfileLisner : GoToProfile
    private val args: TimenoteAddressArgs by navArgs()
    private val timenoteViewModel : TimenoteViewModel by activityViewModels()
    private var timenotePagingAdapter: TimenotePagingAdapter? = null
    private lateinit var prefs: SharedPreferences
    private var tokenId : String? = null
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val timenoteHiddedViewModel: TimenoteHiddedViewModel by activityViewModels()
    private val authViewModel: LoginViewModel by activityViewModels()
    private val utils = Utils()
    private var sendTo: MutableList<String> = mutableListOf()
    private lateinit var handler: Handler
    private val TRIGGER_AUTO_COMPLETE = 200
    private val AUTO_COMPLETE_DELAY: Long = 200
    private lateinit var userInfoDTO: UserInfoDTO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
    }

    private var googleMap: GoogleMap? = null
    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap
        this.googleMap?.uiSettings?.setAllGesturesEnabled(false)
        this.googleMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(args.timenoteInfoDTO?.location?.latitude!!, args.timenoteInfoDTO?.location?.longitude!!)))
        this.googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(args.timenoteInfoDTO?.location?.latitude!!, args.timenoteInfoDTO?.location?.longitude!!), 15F))
        this.googleMap?.addMarker(MarkerOptions().position(LatLng(args.timenoteInfoDTO?.location?.latitude!!, args.timenoteInfoDTO?.location?.longitude!!)))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        goToProfileLisner = context as GoToProfile

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_timenote_address, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
        userInfoDTO = Gson().fromJson(prefs.getString(user_info_dto, ""), typeUserInfo)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_timenote) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        timenote_address_toolbar.text = args.timenoteInfoDTO?.location?.address?.address?.plus(", ")?.plus(args.timenoteInfoDTO?.location?.address?.city)?.plus(" ")?.plus(args.timenoteInfoDTO?.location?.address?.country)

        val lm = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        timenotePagingAdapter = TimenotePagingAdapter(TimenoteComparator,lm, requireContext(), this, this, true, utils, userInfoDTO.id, prefs.getInt(
            format_date_default, 0), userInfoDTO)
        timenote_around_rv.apply {
            layoutManager = lm
            adapter =  timenotePagingAdapter!!.withLoadStateFooter(
                footer = TimenoteLoadStateAdapter{ timenotePagingAdapter!!.retry() }
            )
        }

        lifecycleScope.launch {
            timenoteViewModel.getAroundTimenotePagingFlow(tokenId!!, FilterLocationDTO(Location(args.timenoteInfoDTO?.location?.longitude!!, args.timenoteInfoDTO?.location?.latitude!!,
                Address(args.timenoteInfoDTO?.location?.address?.address ?: "", args.timenoteInfoDTO?.location?.address?.zipCode ?: "", args.timenoteInfoDTO?.location?.address?.city!!, args.timenoteInfoDTO?.location?.address?.country!!)), 0),  prefs).collectLatest {
                timenotePagingAdapter?.submitData(it)
            }
        }

        lifecycleScope.launch {
            timenotePagingAdapter?.loadStateFlow?.distinctUntilChangedBy { it.source }?.collect {
                timenote_around_rv.setMediaObjects(timenotePagingAdapter?.snapshot()?.items!!)
            }
        }
        timenote_address_btn_back.setOnClickListener(this)
    }

    override fun onDestroy() {
        timenote_around_rv?.releasePlayer()
        super.onDestroy()
    }

    override fun onAlarmClicked(timenoteInfoDTO: TimenoteInfoDTO, type: Int) {}
    override fun onDuplicateClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        findNavController().navigate(TimenoteAddressDirections.actionGlobalCreateTimenote().setFrom(args.from).setModify(1).setId(timenoteInfoDTO.id).setTimenoteBody(CreationTimenoteDTO(timenoteInfoDTO.createdBy.id!!, null, timenoteInfoDTO.title, timenoteInfoDTO.description, timenoteInfoDTO.pictures,
            timenoteInfoDTO.colorHex, timenoteInfoDTO.location, timenoteInfoDTO.category, timenoteInfoDTO.startingAt, timenoteInfoDTO.endingAt,
            timenoteInfoDTO.hashtags, timenoteInfoDTO.url, timenoteInfoDTO.price, null, timenoteInfoDTO.urlTitle)))
    }

    override fun onSeeMoreClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        findNavController().navigate(TimenoteAddressDirections.actionGlobalDetailedTimenote(1, timenoteInfoDTO))
    }

    override fun onCommentClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        findNavController().navigate(TimenoteAddressDirections.actionGlobalDetailedTimenote(args.from, timenoteInfoDTO))
    }

    override fun onPlusClicked(timenoteInfoDTO: TimenoteInfoDTO, isAdded: Boolean) {
        if(isAdded){
            timenoteViewModel.joinTimenote(tokenId!!, timenoteInfoDTO.id).observe(
                viewLifecycleOwner,
                Observer {
                    if(it.code() == 401) {
                        authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer {newAccessToken ->
                            tokenId = newAccessToken
                            timenoteViewModel.joinTimenote(tokenId!!, timenoteInfoDTO.id)
                        })
                    }
                })
        } else {
            timenoteViewModel.leaveTimenote(tokenId!!, timenoteInfoDTO.id).observe(viewLifecycleOwner, Observer {
                if(it.code() == 401) {
                    authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer {newAccessToken ->
                        tokenId = newAccessToken
                        timenoteViewModel.leaveTimenote(tokenId!!, timenoteInfoDTO.id)
                    })
                }
            })
        }
    }

    override fun onPictureClicked(userInfoDTO: UserInfoDTO) {
        if(userInfoDTO.id == this.userInfoDTO.id) goToProfileLisner.goToProfile()
        else findNavController().navigate(TimenoteAddressDirections.actionGlobalProfileElse(args.from).setUserInfoDTO(userInfoDTO))
    }


    override fun onSeeParticipants(timenoteInfoDTO: TimenoteInfoDTO) {
        val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
            customView(R.layout.users_participating)
            lifecycleOwner(this@TimenoteAddress)
        }

        val recyclerview = dial.getCustomView().users_participating_rv
        val userAdapter = UsersPagingAdapter(
            UsersPagingAdapter.UserComparator,
            timenoteInfoDTO,
            this,
            null,
            null,
            false
            , Utils())
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.adapter = userAdapter
        lifecycleScope.launch{
            timenoteViewModel.getUsersParticipating(tokenId!!, timenoteInfoDTO.id, prefs).collectLatest {
                userAdapter.submitData(it)
            }
        }
    }

    override fun onShareClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        sendTo.clear()
        val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
            customView(R.layout.friends_search_cl)
            lifecycleOwner(this@TimenoteAddress)
            positiveButton(R.string.send){
                timenoteViewModel.shareWith(tokenId!!, ShareTimenoteDTO(timenoteInfoDTO.id, sendTo)).observe(viewLifecycleOwner, Observer {
                    if(it.code() == 401) {
                        authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer {newAccessToken ->
                            tokenId = newAccessToken
                            timenoteViewModel.shareWith(tokenId!!, ShareTimenoteDTO(timenoteInfoDTO.id, sendTo))
                        })
                    }
                })
            }
            negativeButton(R.string.cancel)
        }

        dial.getActionButton(WhichButton.NEGATIVE).updateTextColor(resources.getColor(android.R.color.darker_gray))
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
            null,
            false
            , Utils())
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.adapter = userAdapter
        lifecycleScope.launch{
            searchViewModel.getUsers(tokenId!!, userInfoDTO.id!!, prefs).collectLatest {
                userAdapter.submitData(it)
            }
        }

        if(searchbar != null) {
            handler = Handler { msg ->
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(searchbar.text)) {
                        lifecycleScope.launch {
                            searchViewModel.getUsers(tokenId!!, searchbar.text, prefs)
                                .collectLatest {
                                    userAdapter.submitData(it)
                                }
                        }

                    } else {
                        lifecycleScope.launch{
                            searchViewModel.getUsers(tokenId!!, userInfoDTO.id!!,  prefs).collectLatest {
                                userAdapter.submitData(it)
                            }
                        }
                    }
                }
                false
            }
        }
    }

    override fun onAdd(userInfoDTO: UserInfoDTO, createGroup: Int?) {
        sendTo.add(userInfoDTO.id!!)
    }

    override fun onRemove(userInfoDTO: UserInfoDTO, createGroup: Int?) {
        sendTo.remove(userInfoDTO.id!!)
    }

    override fun onDoubleClick() {}
    override fun onReportClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(text = resources.getString(R.string.choose_a_reason))
            message(text = resources.getString(R.string.choose_a_reason_message))
            val listOfItems = mutableListOf(resources.getString(R.string.spam), resources.getString(R.string.nudity_post), resources.getString(R.string.dont_like_it), resources.getString(R.string.hate_speech),
                resources.getString(R.string.scam_fraud), resources.getString(R.string.false_info), resources.getString(R.string.intimidation_bullying), resources.getString(R.string.violence_post),
                resources.getString(R.string.intellectual_property), resources.getString(R.string.suicide_automutilation), resources.getString(R.string.illegal_sales))
            listItemsSingleChoice (items = listOfItems){ _, _, text ->
                when(text.toString()){
                    context.getString(R.string.spam) -> signal(timenoteInfoDTO, context.getString(R.string.spam))
                    context.getString(R.string.nudity_post) -> signal(timenoteInfoDTO, context.getString(R.string.nudity_post))
                    context.getString(R.string.dont_like_it) -> signal(timenoteInfoDTO, context.getString(R.string.dont_like_it))
                    context.getString(R.string.hate_speech) -> signal(timenoteInfoDTO, context.getString(R.string.hate_speech))
                    context.getString(R.string.scam_fraud)  -> signal(timenoteInfoDTO, context.getString(R.string.scam_fraud))
                    context.getString(R.string.false_info) -> signal(timenoteInfoDTO, context.getString(R.string.false_info))
                    context.getString(R.string.intimidation_bullying) -> signal(timenoteInfoDTO, context.getString(R.string.intimidation_bullying))
                    context.getString(R.string.violence_post) -> signal(timenoteInfoDTO, context.getString(R.string.violence_post))
                    context.getString(R.string.intellectual_property) -> signal(timenoteInfoDTO, context.getString(R.string.intellectual_property))
                    context.getString(R.string.suicide_automutilation) -> signal(timenoteInfoDTO, context.getString(R.string.suicide_automutilation))
                    context.getString(R.string.illegal_sales) -> signal(timenoteInfoDTO, context.getString(R.string.illegal_sales))
                }
            }
        }
    }

    private fun signal(
        timenoteInfoDTO: TimenoteInfoDTO,
        reason: String
    ) {
        timenoteViewModel.signalTimenote(
            tokenId!!,
            TimenoteCreationSignalementDTO(userInfoDTO.id!!, timenoteInfoDTO.id, reason)
        ).observe(viewLifecycleOwner
        ) {
            if (it.code() == 401) {
                authViewModel.refreshToken(prefs)
                    .observe(viewLifecycleOwner) { newAccessToken ->
                        tokenId = newAccessToken
                        timenoteViewModel.signalTimenote(
                            tokenId!!,
                            TimenoteCreationSignalementDTO(
                                userInfoDTO.id!!,
                                timenoteInfoDTO.id,
                                reason
                            )
                        ).observe(viewLifecycleOwner
                        ) { rsp ->
                            if (rsp.isSuccessful) Toast.makeText(
                                requireContext(),
                                getString(R.string.reported),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }

            if (it.isSuccessful) Toast.makeText(
                requireContext(),
                getString(R.string.reported),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    override fun onAddMarker(timenoteInfoDTO: TimenoteInfoDTO) {}
    override fun onHashtagClicked(timenoteInfoDTO: TimenoteInfoDTO ,hashtag: String?) {
        findNavController().navigate(TimenoteAddressDirections.actionGlobalTimenoteTAG(timenoteInfoDTO, hashtag).setFrom(args.from))
    }

    override fun onHideToOthersClicked(timenoteInfoDTO: TimenoteInfoDTO) {}
    override fun onMaskThisUser() {}
    override fun onAddressClicked(timenoteInfoDTO: TimenoteInfoDTO) {}
    override fun onEditClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        findNavController().navigate(TimenoteAddressDirections.actionGlobalCreateTimenote().setFrom(args.from).setModify(2).setId(timenoteInfoDTO.id).setTimenoteBody(CreationTimenoteDTO(timenoteInfoDTO.createdBy.id!!, null, timenoteInfoDTO.title, timenoteInfoDTO.description, timenoteInfoDTO.pictures,
            timenoteInfoDTO.colorHex, timenoteInfoDTO.location, timenoteInfoDTO.category, timenoteInfoDTO.startingAt, timenoteInfoDTO.endingAt,
            timenoteInfoDTO.hashtags, timenoteInfoDTO.url, timenoteInfoDTO.price, null, timenoteInfoDTO.urlTitle)))
    }
    override fun onDeleteClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        val map: MutableMap<Long, String> = Gson().fromJson(prefs.getString(map_event_id_to_timenote, null), object : TypeToken<MutableMap<Long, String>>() {}.type) ?: mutableMapOf()
        timenoteViewModel.deleteTimenote(tokenId!!, timenoteInfoDTO.id).observe(viewLifecycleOwner, Observer {
            if(it.isSuccessful) {
                timenotePagingAdapter?.refresh()
                if(map.isNotEmpty() && map.filterValues { id -> id == timenoteInfoDTO.id }.keys.isNotEmpty()) {
                    map.remove(map.filterValues { id -> id == timenoteInfoDTO.id }.keys.first())
                    prefs.edit().putString(map_event_id_to_timenote, Gson().toJson(map)).apply()
                }
            }
            else if(it.code() == 401) {
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer { newAccessToken ->
                    tokenId = newAccessToken
                    timenoteViewModel.deleteTimenote(tokenId!!, timenoteInfoDTO.id).observe(viewLifecycleOwner, Observer {tid ->
                        if(tid.isSuccessful) timenotePagingAdapter?.refresh()
                        if(map.isNotEmpty() && map.filterValues { id -> id == timenoteInfoDTO.id }.keys.isNotEmpty()) {
                            map.remove(map.filterValues { id -> id == timenoteInfoDTO.id }.keys.first())
                            prefs.edit().putString(map_event_id_to_timenote, Gson().toJson(map)).apply()
                        }
                    })
                })
            }
        })
    }
    override fun onSearchClicked(userInfoDTO: UserInfoDTO, isTagged: Boolean) {}
    override fun onUnfollow(id: String) {

    }

    override fun onRemove(id: String) {
    }

    override fun onClick(v: View?) {
        when(v){
            timenote_address_btn_back -> findNavController().popBackStack()
        }
    }

    override fun onHidePostClicked(timenoteInfoDTO: TimenoteInfoDTO, position: Int) {
        timenoteHiddedViewModel.hideEventOrUSer(tokenId!!, TimenoteHiddedCreationDTO(createdBy = userInfoDTO.id!!, timenote = timenoteInfoDTO.id)).observe(viewLifecycleOwner) {
            if (it.code() == 401) {
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner) { newAccessToken ->
                    tokenId = newAccessToken
                    timenoteHiddedViewModel.hideEventOrUSer(
                        tokenId!!,
                        TimenoteHiddedCreationDTO(
                            createdBy = userInfoDTO.id!!,
                            timenote = timenoteInfoDTO.id
                        )
                    ).observe(viewLifecycleOwner) { nr ->
                        if (nr.isSuccessful) timenotePagingAdapter?.notifyDataSetChanged()
                    }
                }
            } else if (it.isSuccessful) timenotePagingAdapter?.refresh()
        }
    }

    override fun onHideUserClicked(timenoteInfoDTO: TimenoteInfoDTO, position: Int) {
        timenoteHiddedViewModel.hideEventOrUSer(tokenId!!, TimenoteHiddedCreationDTO(createdBy = userInfoDTO.id!!, user = timenoteInfoDTO.createdBy.id)).observe(viewLifecycleOwner) {
            if (it.code() == 401) {
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner) { newAccessToken ->
                    tokenId = newAccessToken
                    timenoteHiddedViewModel.hideEventOrUSer(
                        tokenId!!,
                        TimenoteHiddedCreationDTO(
                            createdBy = userInfoDTO.id!!,
                            user = timenoteInfoDTO.createdBy.id
                        )
                    ).observe(viewLifecycleOwner) { nr ->
                        if (nr.isSuccessful) timenotePagingAdapter?.refresh()
                    }
                }
            } else if (it.isSuccessful) timenotePagingAdapter?.refresh()
        }
    }

}