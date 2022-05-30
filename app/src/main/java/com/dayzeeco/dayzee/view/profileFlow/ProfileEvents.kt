package com.dayzeeco.dayzee.view.profileFlow

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.robertlevonyan.views.chip.Chip
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.*
import com.dayzeeco.dayzee.common.*
import com.dayzeeco.dayzee.listeners.ItemProfileCardListener
import com.dayzeeco.dayzee.listeners.OnRemoveFilterBarListener
import com.dayzeeco.dayzee.listeners.TimenoteOptionsListener
import com.dayzeeco.dayzee.model.*
import com.dayzeeco.dayzee.view.searchFlow.SearchDirections
import com.dayzeeco.dayzee.viewModel.*
import com.dayzeeco.dayzee.webService.service.AlarmData
import kotlinx.android.synthetic.main.fragment_profile_future_events.*
import kotlinx.android.synthetic.main.friends_search_cl.view.*
import kotlinx.android.synthetic.main.users_participating.view.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*


class ProfileEvents : Fragment(), TimenoteOptionsListener, OnRemoveFilterBarListener,
    ItemProfileCardListener, UsersPagingAdapter.SearchPeopleListener,
    UsersShareWithPagingAdapter.SearchPeopleListener, UsersShareWithPagingAdapter.AddToSend{

    private var userInfoDTO: UserInfoDTO? = null
    private var sendTo: MutableList<String> = mutableListOf()
    private lateinit var handler: Handler
    private val TRIGGER_AUTO_COMPLETE = 200
    private val AUTO_COMPLETE_DELAY: Long = 200
    private lateinit var prefs: SharedPreferences
    private var tokenId: String? = null
    private var showHideFilterBar: Boolean = false
    private var from: Int? = null
    private lateinit var id: String
    private var isOnMyProfile : Boolean = false
    private val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"
    private val ISOX = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private var isFuture = true
    private lateinit var onRemoveFilterBarListener: OnRemoveFilterBarListener
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val profileViewModel : ProfileViewModel by activityViewModels()
    private val timenoteViewModel: TimenoteViewModel by activityViewModels()
    private val timenoteHiddedViewModel: TimenoteHiddedViewModel by activityViewModels()
    private val alarmViewModel: AlarmViewModel by activityViewModels()
    private val authViewModel: LoginViewModel by activityViewModels()
    private var profileEventPagingAdapter : ProfileEventPagingAdapter? = null
    private lateinit var listOfAlarms: AlarmData
    private val utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
        arguments?.let {
            showHideFilterBar = it.getBoolean(show_hide_filterbar)
            from = it.getInt(com.dayzeeco.dayzee.common.from)
            id = it.getString(com.dayzeeco.dayzee.common.id)!!
            isFuture = it.getBoolean(is_future)
            isOnMyProfile = it.getBoolean(is_on_my_profile)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_profile_future_events, container, false)

    @OptIn(ExperimentalPagingApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(!prefs.getString(accessToken, null).isNullOrBlank()){
            val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
            userInfoDTO = Gson().fromJson<UserInfoDTO>(prefs.getString(user_info_dto, ""), typeUserInfo)

            listOfAlarms = AlarmData(requireContext())

            val profileFilterChipAdapter = ProfileFilterChipAdapter(listOf(getString(R.string.my_posts), getString(
                R.string.the_joined), getString(R.string.with_alarm), getString(R.string.group_related)), this)
            profile_filter_rv_chips_in_rv.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = profileFilterChipAdapter
                onItemClick { recyclerView, position, v ->
                    when(position){
                        0 -> switchFilters(v, recyclerView[1], recyclerView[2], recyclerView[3], position)
                        1 -> switchFilters(v, recyclerView[0], recyclerView[2], recyclerView[3] , position)
                        2 -> switchFilters(v, recyclerView[1], recyclerView[0], recyclerView[3] ,position)
                        3 -> switchFilters(v, recyclerView[1], recyclerView[2], recyclerView[0] , position)
                        4 -> onRemoveFilterBarListener.onHideFilterBarClicked(null)
                    }
                }
            }


            loadData(userInfoDTO!!)
        }

    }

    @ExperimentalPagingApi
    private fun loadData(userInfoDTO: UserInfoDTO) {
        profileEventPagingAdapter = ProfileEventPagingAdapter(
            ProfileEventComparator,
            this,
            this,
            userInfoDTO.id,
            isFuture, listOfAlarms.getAlarms(), isOnMyProfile, utils)
        profileEventPagingAdapter?.resetAllSelected()
        profile_rv.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter =  profileEventPagingAdapter!!.withLoadStateFooter(
                footer = TimenoteLoadStateAdapter{ profileEventPagingAdapter!!.retry() }
            )
        }

        lifecycleScope.launch {
            profileViewModel.getEventProfile(tokenId!!, id, isFuture, prefs).collectLatest {
                profileEventPagingAdapter?.submitData(it)
            }
        }

        lifecycleScope.launch {
            profileEventPagingAdapter?.loadStateFlow?.distinctUntilChangedBy { it.source }?.collect {
                if(it.refresh is LoadState.Loading) profile_pb?.visibility = View.VISIBLE
                else if(it.refresh is LoadState.NotLoading && !profileEventPagingAdapter?.snapshot()?.items.isNullOrEmpty()){
                    profile_nothing_to_display?.visibility = View.GONE
                    profile_rv?.visibility = View.VISIBLE
                    profile_pb?.visibility = View.GONE
                } else {
                    profile_nothing_to_display?.visibility = View.VISIBLE
                    profile_rv?.visibility = View.GONE
                    profile_pb?.visibility = View.GONE
                }
            }
        }
    }

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
            TimenoteCreationSignalementDTO(userInfoDTO?.id!!, timenoteInfoDTO.id, reason)
        ).observe(viewLifecycleOwner
        ) {
            if (it.code() == 401) {
                authViewModel.refreshToken(prefs)
                    .observe(viewLifecycleOwner) { newAccessToken ->
                        tokenId = newAccessToken
                        timenoteViewModel.signalTimenote(
                            tokenId!!,
                            TimenoteCreationSignalementDTO(
                                userInfoDTO?.id!!,
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

    override fun onEditClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        findNavController().navigate(
            SearchDirections.actionGlobalCreateTimenote().setFrom(from!!).setModify(2).setId(timenoteInfoDTO.id).setTimenoteBody(CreationTimenoteDTO(timenoteInfoDTO.createdBy.id!!, null, timenoteInfoDTO.title, timenoteInfoDTO.description, timenoteInfoDTO.pictures,
            timenoteInfoDTO.colorHex, timenoteInfoDTO.location, timenoteInfoDTO.category, timenoteInfoDTO.startingAt, timenoteInfoDTO.endingAt,
            timenoteInfoDTO.hashtags, timenoteInfoDTO.url, timenoteInfoDTO.price, null, timenoteInfoDTO.urlTitle)))
    }

    override fun onAlarmClicked(timenoteInfoDTO: TimenoteInfoDTO, type: Int) {
        when (type) {
            2 -> alarmViewModel.deleteAlarm(tokenId!!, listOfAlarms.getAlarms().find { alr -> alr.timenote == timenoteInfoDTO.id }?.id!!).observe(viewLifecycleOwner, Observer {
                if(it.code() == 401){
                    authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer {newAccessToken ->
                        tokenId = newAccessToken
                        if(type == 2) alarmViewModel.deleteAlarm(tokenId!!, listOfAlarms.getAlarms().find { alr -> alr.timenote == timenoteInfoDTO.id }?.id!!).observe(viewLifecycleOwner, Observer {rsp ->
                            if(rsp.isSuccessful){
                                listOfAlarms.deleteAlarm(listOfAlarms.getAlarms().find { alr -> alr.timenote == timenoteInfoDTO.id }?.id!!)
                                Toast.makeText(requireContext(), getString(R.string.alarm_deleted), Toast.LENGTH_SHORT).show()}
                        })

                    })
                }

                if(it.isSuccessful){
                    listOfAlarms.deleteAlarm(listOfAlarms.getAlarms().find { alr -> alr.timenote == timenoteInfoDTO.id }?.id!!)
                    Toast.makeText(requireContext(), getString(R.string.alarm_deleted), Toast.LENGTH_SHORT).show()
                }
            })
            0 -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                val cal = Calendar.getInstance()
                cal.timeInMillis = SimpleDateFormat(ISOX, Locale.getDefault()).parse(timenoteInfoDTO.startingAt).time
                dateTimePicker (currentDateTime = cal) { _, datetime ->
                    alarmViewModel.createAlarm(tokenId!!, AlarmCreationDTO(timenoteInfoDTO.createdBy.id!!, timenoteInfoDTO.id, SimpleDateFormat(ISO).format(datetime.time.time))).observe(viewLifecycleOwner, Observer {
                        if(it.code() == 401){
                            authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer {newAccessToken ->
                                tokenId = newAccessToken
                                alarmViewModel.createAlarm(tokenId!!, AlarmCreationDTO(timenoteInfoDTO.createdBy.id!!, timenoteInfoDTO.id, SimpleDateFormat(ISO).format(datetime.time.time))).observe(viewLifecycleOwner, Observer {rsp ->
                                    if(rsp.isSuccessful) {
                                        listOfAlarms.addAlarm(rsp.body()!!)

                                        Toast.makeText(
                                            requireContext(),
                                            getString(R.string.alarm_created),
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                })
                            })
                        } else if(it.isSuccessful) {

                            listOfAlarms.addAlarm(it.body()!!)

                            Toast.makeText(
                                requireContext(),
                                getString(R.string.alarm_created),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
                lifecycleOwner(this@ProfileEvents)
            }
            else -> {
                MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = SimpleDateFormat(ISOX, Locale.getDefault()).parse(timenoteInfoDTO.startingAt).time
                    dateTimePicker (currentDateTime = cal) { _, datetime ->
                        alarmViewModel.updateAlarm(tokenId!!, timenoteInfoDTO.id, AlarmCreationDTO(timenoteInfoDTO.createdBy.id!!, timenoteInfoDTO.id, SimpleDateFormat(ISO).format(datetime.time.time))).observe(viewLifecycleOwner, Observer {
                            if(it.code() == 401){
                                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer {newAccessToken ->
                                    tokenId = newAccessToken
                                    alarmViewModel.updateAlarm(tokenId!!,timenoteInfoDTO.id, AlarmCreationDTO(timenoteInfoDTO.createdBy.id!!, timenoteInfoDTO.id, SimpleDateFormat(ISO).format(datetime.time.time))).observe(viewLifecycleOwner, Observer {rsp ->
                                        if(rsp.isSuccessful){
                                            listOfAlarms.updateAlarm(timenoteInfoDTO.id, rsp.body()!!)
                                            Toast.makeText(requireContext(), getString(R.string.alarm_updated), Toast.LENGTH_SHORT).show()
                                        }
                                    })
                                })
                            } else if(it.isSuccessful) {
                                listOfAlarms.updateAlarm(timenoteInfoDTO.id, it.body()!!)
                                Toast.makeText(requireContext(), getString(R.string.alarm_updated), Toast.LENGTH_SHORT).show()}
                        })
                    }
                    lifecycleOwner(this@ProfileEvents)
                }
            }
        }
    }

    override fun onDeleteClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        val map: MutableMap<Long, String> = Gson().fromJson(prefs.getString(map_event_id_to_timenote, null), object : TypeToken<MutableMap<String, String>>() {}.type) ?: mutableMapOf()
        timenoteViewModel.deleteTimenote(tokenId!!, timenoteInfoDTO.id).observe(viewLifecycleOwner, Observer {
            if(it.isSuccessful) {
                profileEventPagingAdapter?.refresh()
                if(map.isNotEmpty() && map.filterValues { id -> id == timenoteInfoDTO.id }.keys.isNotEmpty()) {
                    map.remove(map.filterValues { id -> id == timenoteInfoDTO.id }.keys.first())
                    prefs.edit().putString(map_event_id_to_timenote, Gson().toJson(map)).apply()
                }
            }
            else if(it.code() == 401) {
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer { newAccessToken ->
                    tokenId = newAccessToken
                    timenoteViewModel.deleteTimenote(tokenId!!, timenoteInfoDTO.id).observe(viewLifecycleOwner, Observer {tid ->
                        if(tid.isSuccessful) profileEventPagingAdapter?.refresh()
                        if(map.isNotEmpty() && map.filterValues { id -> id == timenoteInfoDTO.id }.keys.isNotEmpty()) {
                            map.remove(map.filterValues { id -> id == timenoteInfoDTO.id }.keys.first())
                            prefs.edit().putString(map_event_id_to_timenote, Gson().toJson(map)).apply()
                        }
                    })
                })
            }
        })
    }

    override fun onDuplicateClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        findNavController().navigate(SearchDirections.actionGlobalCreateTimenote().setFrom(from!!).setModify(1).setId(timenoteInfoDTO.id).setTimenoteBody(CreationTimenoteDTO(timenoteInfoDTO.createdBy.id!!, null, timenoteInfoDTO.title, timenoteInfoDTO.description, timenoteInfoDTO.pictures,
            timenoteInfoDTO.colorHex, timenoteInfoDTO.location, timenoteInfoDTO.category, timenoteInfoDTO.startingAt, timenoteInfoDTO.endingAt,
            timenoteInfoDTO.hashtags, timenoteInfoDTO.url, timenoteInfoDTO.price, null, timenoteInfoDTO.urlTitle)))
    }

    override fun onHideToOthersClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        timenoteViewModel.hideToOthers(tokenId!!, timenoteInfoDTO.id).observe(viewLifecycleOwner) {
            if (it.code() == 401) {
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner) { newAccessToken ->
                    tokenId = newAccessToken
                    timenoteViewModel.hideToOthers(tokenId!!, timenoteInfoDTO.id)
                    Toast.makeText(requireContext(), getString(R.string.hided), Toast.LENGTH_SHORT)
                        .show()
                }
            }
            if (it.isSuccessful) Toast.makeText(
                requireContext(),
                getString(R.string.hided),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onSeeParticipants(timenoteInfoDTO: TimenoteInfoDTO) {
        val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
            customView(R.layout.users_participating)
            lifecycleOwner(this@ProfileEvents)
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
            lifecycleOwner(this@ProfileEvents)
            positiveButton(R.string.send){
                timenoteViewModel.shareWith(tokenId!!, ShareTimenoteDTO(timenoteInfoDTO.id, sendTo)).observe(viewLifecycleOwner, Observer {
                    if(it.code() == 401){
                        authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer { newAccessToken ->
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
            searchViewModel.getUsers(tokenId!!, userInfoDTO?.id!!,  prefs).collectLatest {
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
                            searchViewModel.getUsers(tokenId!!, userInfoDTO?.id!!,  prefs).collectLatest {
                                userAdapter.submitData(it)
                            }
                        }
                    }
                }
                false
            }
        }
    }

    override fun onAddMarker(timenoteInfoDTO: TimenoteInfoDTO) {

    }

    override fun onHashtagClicked(timenoteInfoDTO: TimenoteInfoDTO ,hashtag: String?) {}

    override fun onAdd(userInfoDTO: UserInfoDTO, createGroup: Int?) {
        sendTo.add(userInfoDTO.id!!)
    }

    override fun onRemove(userInfoDTO: UserInfoDTO, createGroup: Int?) {
        sendTo.remove(userInfoDTO.id!!)
    }

    override fun onHideFilterBarClicked(position: Int?) {
        onRemoveFilterBarListener.onHideFilterBarClicked(1)
    }

    fun setListener(onRemoveFilterBarListener: OnRemoveFilterBarListener){
        this.onRemoveFilterBarListener = onRemoveFilterBarListener
    }

    fun setShowFilterBar(b: Boolean) {
        if(b) profile_filter_rv_chips_in_rv?.visibility = View.VISIBLE
        else profile_filter_rv_chips_in_rv?.visibility = View.GONE
    }

    override fun onCardClicked(event: TimenoteInfoDTO) {
        findNavController().navigate(ProfileElseDirections.actionGlobalDetailedTimenote(from!!, event))
    }

    @ExperimentalPagingApi
    private fun onFilterClicked(position: Int, isClicked : Boolean) {
        if(isClicked) {
            when (position) {
                0 -> {
                    lifecycleScope.launch {
                        profileViewModel.getTimenotesFiltered(
                            tokenId!!, TimenoteFilteredDTO(
                                upcoming = isFuture,
                                alarm = false,
                                created = true,
                                joined = false,
                                sharedWith = false
                            )
                        , prefs).collectLatest {
                            profileEventPagingAdapter?.submitData(it)
                        }
                    }
                }
                1 -> lifecycleScope.launch {
                    profileViewModel.getTimenotesFiltered(tokenId!!, TimenoteFilteredDTO(isFuture, false, false, true, false), prefs).collectLatest { profileEventPagingAdapter?.submitData(it) }
                }
                2 -> lifecycleScope.launch {
                    profileViewModel.getTimenotesFiltered(tokenId!!, TimenoteFilteredDTO(isFuture, true, false, false, false), prefs).collectLatest { profileEventPagingAdapter?.submitData(it) }
                }
                3 -> lifecycleScope.launch {
                    profileViewModel.getTimenotesFiltered(tokenId!!, TimenoteFilteredDTO(isFuture, false, false, false, true), prefs).collectLatest { profileEventPagingAdapter?.submitData(it) }
                }
            }
        } else {
            loadData(userInfoDTO!!)
        }

    }

    fun notifyAdapterUpdate(){
        if(profileEventPagingAdapter!= null)
            profileEventPagingAdapter?.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        notifyAdapterUpdate()
    }

    fun scrollToTop(){
        if(profile_rv?.layoutManager != null)
            profile_rv?.layoutManager?.smoothScrollToPosition(profile_rv, null, 0)
    }

    @ExperimentalPagingApi
    fun switchFilters(
        view: View,
        view1: View,
        view2: View,
        view3: View,
        position: Int
    ){
        var isCliked: Boolean

        (view as Chip).apply {
            if(this.chipTextColor == resources.getColor(R.color.colorText)){
                isCliked = true
                chipBackgroundColor = resources.getColor(R.color.colorBackground)
                chipTextColor = resources.getColor(R.color.colorYellow)
            } else {
                isCliked = false
                chipBackgroundColor = resources.getColor(R.color.colorBackground)
                chipTextColor = resources.getColor(R.color.colorText)
            }
        }
        (view1 as Chip).apply {
            chipBackgroundColor = resources.getColor(R.color.colorBackground)
            chipTextColor = resources.getColor(R.color.colorText)
        }
        (view2 as Chip).apply {
            chipBackgroundColor = resources.getColor(R.color.colorBackground)
            chipTextColor = resources.getColor(R.color.colorText)
        }
        (view3 as Chip).apply {
            chipBackgroundColor = resources.getColor(R.color.colorBackground)
            chipTextColor = resources.getColor(R.color.colorText)
        }

        onFilterClicked(position, isCliked)

    }


    companion object{
        @JvmStatic
        fun newInstance(
            showHideFilterBar: Boolean,
            context: Fragment,
            from: Int,
            id: String,
            isFuture : Boolean,
            onMyProfile: Boolean
        ) =
            ProfileEvents().apply {
                arguments = Bundle().apply {
                    putBoolean(show_hide_filterbar, showHideFilterBar)
                    putInt(com.dayzeeco.dayzee.common.from, from)
                    putString(com.dayzeeco.dayzee.common.id, id)
                    putBoolean(is_future, isFuture)
                    putBoolean(is_on_my_profile, onMyProfile)
                    setListener(context as OnRemoveFilterBarListener)
                }
            }
    }


    override fun onSearchClicked(userInfoDTO: UserInfoDTO, isTagged: Boolean) {}
    override fun onUnfollow(id: String) {

    }

    override fun onRemove(id: String) {
    }

    override fun onAddressClicked(timenoteInfoDTO: TimenoteInfoDTO) {}
    override fun onSeeMoreClicked(event: TimenoteInfoDTO) {}
    override fun onCommentClicked(event: TimenoteInfoDTO) {}
    override fun onPlusClicked(timenoteInfoDTO: TimenoteInfoDTO, isAdded: Boolean) {}
    override fun onPictureClicked(userInfoDTO: UserInfoDTO) {}
    override fun onMaskThisUser() {}
    override fun onDoubleClick() {}

    override fun onHidePostClicked(timenoteInfoDTO: TimenoteInfoDTO, position: Int) {
        timenoteHiddedViewModel.hideEventOrUSer(tokenId!!, TimenoteHiddedCreationDTO(createdBy = userInfoDTO?.id!!, timenote = timenoteInfoDTO.id)).observe(viewLifecycleOwner) {
            if (it.code() == 401) {
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner) { newAccessToken ->
                    tokenId = newAccessToken
                    timenoteHiddedViewModel.hideEventOrUSer(
                        tokenId!!,
                        TimenoteHiddedCreationDTO(
                            createdBy = userInfoDTO?.id!!,
                            timenote = timenoteInfoDTO.id
                        )
                    ).observe(viewLifecycleOwner) { nr ->
                        if (nr.isSuccessful) profileEventPagingAdapter?.refresh()
                    }
                }
            } else if (it.isSuccessful) profileEventPagingAdapter?.refresh()
        }
    }

    override fun onHideUserClicked(timenoteInfoDTO: TimenoteInfoDTO, position: Int) {
        timenoteHiddedViewModel.hideEventOrUSer(tokenId!!, TimenoteHiddedCreationDTO(createdBy = userInfoDTO?.id!!, user = timenoteInfoDTO.createdBy.id)).observe(viewLifecycleOwner) {
            if (it.code() == 401) {
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner) { newAccessToken ->
                    tokenId = newAccessToken
                    timenoteHiddedViewModel.hideEventOrUSer(
                        tokenId!!,
                        TimenoteHiddedCreationDTO(
                            createdBy = userInfoDTO?.id!!,
                            user = timenoteInfoDTO.createdBy.id
                        )
                    ).observe(viewLifecycleOwner) { nr ->
                        if (nr.isSuccessful) profileEventPagingAdapter?.refresh()
                    }
                }
            } else if (it.isSuccessful) profileEventPagingAdapter?.refresh()
        }
    }

}