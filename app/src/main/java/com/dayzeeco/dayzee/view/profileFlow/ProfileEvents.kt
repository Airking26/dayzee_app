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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.robertlevonyan.views.chip.Chip
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.*
import com.dayzeeco.dayzee.common.onItemClick
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "showHideFilterBar"
private const val ARG_PARAM2 = "from"
private const val ARG_PARAM3 = "id"
private const val ARG_PARAM4 = "is_future"
private const val ARG_PARAM5 = "is_on_my_profile"

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
    private val followViewModel: FollowViewModel by activityViewModels()
    private val profileViewModel : ProfileViewModel by activityViewModels()
    private val timenoteViewModel: TimenoteViewModel by activityViewModels()
    private val alarmViewModel: AlarmViewModel by activityViewModels()
    private val authViewModel: LoginViewModel by activityViewModels()
    private var profileEventPagingAdapter : ProfileEventPagingAdapter? = null
    private lateinit var listOfAlarms: AlarmData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
        arguments?.let {
            showHideFilterBar = it.getBoolean(ARG_PARAM1)
            from = it.getInt(ARG_PARAM2)
            id = it.getString(ARG_PARAM3)!!
            isFuture = it.getBoolean(ARG_PARAM4)
            isOnMyProfile = it.getBoolean(ARG_PARAM5)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_profile_future_events, container, false)

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
        userInfoDTO = Gson().fromJson<UserInfoDTO>(prefs.getString("UserInfoDTO", ""), typeUserInfo)

        listOfAlarms = AlarmData(requireContext())

        val profileFilterChipAdapter = ProfileFilterChipAdapter(listOf("My Timenotes", "The Joined", "With Alarm", "Group Related"), this)
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

    @ExperimentalPagingApi
    private fun loadData(userInfoDTO: UserInfoDTO) {
        profileEventPagingAdapter = ProfileEventPagingAdapter(
            ProfileEventComparator,
            this,
            this,
            userInfoDTO.id,
            isFuture, listOfAlarms.getAlarms(), isOnMyProfile)
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

        profileEventPagingAdapter?.addDataRefreshListener {
            profile_pb?.visibility = View.GONE
            if(it){
                profile_nothing_to_display?.visibility = View.VISIBLE
                profile_rv?.visibility = View.GONE
            } else {
                profile_nothing_to_display?.visibility = View.GONE
                profile_rv?.visibility = View.VISIBLE
            }
        }
    }

    override fun onReportClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        timenoteViewModel.signalTimenote(tokenId!!, TimenoteCreationSignalementDTO(userInfoDTO?.id!!, timenoteInfoDTO.id, "")).observe(viewLifecycleOwner, Observer {
            if(it.code() == 401){
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer {newAccessToken ->
                    tokenId = newAccessToken
                    timenoteViewModel.signalTimenote(tokenId!!, TimenoteCreationSignalementDTO(userInfoDTO?.id!!, timenoteInfoDTO.id, "")).observe(viewLifecycleOwner, Observer { rsp ->
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
                                Toast.makeText(requireContext(), "Alarm Deleted", Toast.LENGTH_SHORT).show()}
                        })

                    })
                }

                if(it.isSuccessful){
                    listOfAlarms.deleteAlarm(listOfAlarms.getAlarms().find { alr -> alr.timenote == timenoteInfoDTO.id }?.id!!)
                    Toast.makeText(requireContext(), "Alarm Deleted", Toast.LENGTH_SHORT).show()
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
                                            "Alarm Created",
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
                                "Alarm Created",
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
                                            Toast.makeText(requireContext(), "Alarm Updated", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                                })
                            } else if(it.isSuccessful) {
                                listOfAlarms.updateAlarm(timenoteInfoDTO.id, it.body()!!)
                                Toast.makeText(requireContext(), "Alarm Updated", Toast.LENGTH_SHORT).show()}
                        })
                    }
                    lifecycleOwner(this@ProfileEvents)
                }
            }
        }
    }

    override fun onDeleteClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        val map: MutableMap<Long, String> = Gson().fromJson(prefs.getString("mapEventIdToTimenote", null), object : TypeToken<MutableMap<String, String>>() {}.type) ?: mutableMapOf()
        timenoteViewModel.deleteTimenote(tokenId!!, timenoteInfoDTO.id).observe(viewLifecycleOwner, Observer {
            if(it.isSuccessful) {
                profileEventPagingAdapter?.refresh()
                if(map.isNotEmpty() && map.filterValues { id -> id == timenoteInfoDTO.id }.keys.isNotEmpty()) {
                    map.remove(map.filterValues { id -> id == timenoteInfoDTO.id }.keys.first())
                    prefs.edit().putString("mapEventIdToTimenote", Gson().toJson(map)).apply()
                }
            }
            else if(it.code() == 401) {
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer { newAccessToken ->
                    tokenId = newAccessToken
                    timenoteViewModel.deleteTimenote(tokenId!!, timenoteInfoDTO.id).observe(viewLifecycleOwner, Observer {tid ->
                        if(tid.isSuccessful) profileEventPagingAdapter?.refresh()
                        if(map.isNotEmpty() && map.filterValues { id -> id == timenoteInfoDTO.id }.keys.isNotEmpty()) {
                            map.remove(map.filterValues { id -> id == timenoteInfoDTO.id }.keys.first())
                            prefs.edit().putString("mapEventIdToTimenote", Gson().toJson(map)).apply()
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
        timenoteViewModel.hideToOthers(tokenId!!, timenoteInfoDTO.id).observe(viewLifecycleOwner, Observer {
            if(it.code() == 401) {
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer { newAccessToken ->
                    tokenId = newAccessToken
                    timenoteViewModel.hideToOthers(tokenId!!, timenoteInfoDTO.id)
                    Toast.makeText(requireContext(), "Hided", Toast.LENGTH_SHORT).show()
                })
            }
            if(it.isSuccessful) Toast.makeText(requireContext(), "Hided", Toast.LENGTH_SHORT).show()
        })
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
            null
        )
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
            null
        )
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.adapter = userAdapter
        lifecycleScope.launch{
            followViewModel.getUsers(tokenId!!, userInfoDTO?.id!!, 0, prefs).collectLatest {
                userAdapter.submitData(it)
            }
        }

        if(searchbar != null) {
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
                        lifecycleScope.launch{
                            followViewModel.getUsers(tokenId!!, userInfoDTO?.id!!, 0, prefs).collectLatest {
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
                    putBoolean(ARG_PARAM1, showHideFilterBar)
                    putInt(ARG_PARAM2, from)
                    putString(ARG_PARAM3, id)
                    putBoolean(ARG_PARAM4, isFuture)
                    putBoolean(ARG_PARAM5, onMyProfile)
                    setListener(context as OnRemoveFilterBarListener)
                }
            }
    }


    override fun onSearchClicked(userInfoDTO: UserInfoDTO) {}
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


}