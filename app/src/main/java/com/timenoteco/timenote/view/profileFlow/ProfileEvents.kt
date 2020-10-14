package com.timenoteco.timenote.view.profileFlow

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
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.robertlevonyan.views.chip.Chip
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.*
import com.timenoteco.timenote.common.onItemClick
import com.timenoteco.timenote.listeners.ItemProfileCardListener
import com.timenoteco.timenote.listeners.OnRemoveFilterBarListener
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.view.searchFlow.ProfileSearchDirections
import com.timenoteco.timenote.view.searchFlow.SearchDirections
import com.timenoteco.timenote.viewModel.FollowViewModel
import com.timenoteco.timenote.viewModel.ProfileViewModel
import com.timenoteco.timenote.viewModel.TimenoteViewModel
import kotlinx.android.synthetic.main.fragment_profile_future_events.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.friends_search.view.*
import kotlinx.android.synthetic.main.users_participating.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.Type

private const val ARG_PARAM1 = "showHideFilterBar"
private const val ARG_PARAM2 = "from"
private const val ARG_PARAM3 = "id"
private const val ARG_PARAM4 = "is_future"

class ProfileFutureEvents : Fragment(), TimenoteOptionsListener, OnRemoveFilterBarListener,
    ItemProfileCardListener, UsersPagingAdapter.SearchPeopleListener,
    UsersShareWithPagingAdapter.SearchPeopleListener, UsersShareWithPagingAdapter.AddToSend{

    private var userInfoDTO: UserInfoDTO? = null
    private var sendTo: MutableList<String> = mutableListOf()
    private lateinit var handler: Handler
    private val TRIGGER_AUTO_COMPLETE = 200
    private val AUTO_COMPLETE_DELAY: Long = 200
    private lateinit var prefs: SharedPreferences
    val TOKEN: String = "TOKEN"
    private var tokenId: String? = null
    private var showHideFilterBar: Boolean = false
    private var from: Int? = null
    private lateinit var id: String
    private var isFuture = true
    private lateinit var onRemoveFilterBarListener: OnRemoveFilterBarListener
    private val followViewModel: FollowViewModel by activityViewModels()
    private val profileViewModel : ProfileViewModel by activityViewModels()
    private val timenoteViewModel: TimenoteViewModel by activityViewModels()
    private lateinit var profileEventPagingAdapter : ProfileEventPagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(TOKEN, null)
        arguments?.let {
            showHideFilterBar = it.getBoolean(ARG_PARAM1)
            from = it.getInt(ARG_PARAM2)
            id = it.getString(ARG_PARAM3)!!
            isFuture = it.getBoolean(ARG_PARAM4)
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

        handler = Handler { msg ->
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(searchBar.text)) {
                    //searchViewModel.searchChanged(tokenId!!, searchBar.text)
                    lifecycleScope.launch {
                        //searchViewModel.searchUser(tokenId!!, searchBar.text)
                    }

                }
            }
            false
        }

    }

    @ExperimentalPagingApi
    private fun loadData(userInfoDTO: UserInfoDTO) {
        profileEventPagingAdapter = ProfileEventPagingAdapter(
            ProfileEventComparator,
            this,
            this,
            userInfoDTO.id,
            isFuture)
        profile_rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = profileEventPagingAdapter
        }

        lifecycleScope.launch {
            profileViewModel.getEventProfile(tokenId!!, id, isFuture).collectLatest {
                profileEventPagingAdapter.submitData(it)
            }
        }

        profileEventPagingAdapter.addDataRefreshListener {
            profile_pb?.visibility = View.GONE
            if (it) {
                profile_nothing_to_display?.visibility = View.VISIBLE
                profile_rv?.visibility = View.GONE
            } else {
                profile_nothing_to_display?.visibility = View.GONE
                profile_rv?.visibility = View.VISIBLE
            }
        }
    }

    override fun onReportClicked() {
        Toast.makeText(requireContext(), "Reported", Toast.LENGTH_SHORT).show()
    }

    override fun onEditClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        if(from == 2){
            findNavController().navigate(SearchDirections.actionSearchToCreateTimenoteSearch(1, "", CreationTimenoteDTO(timenoteInfoDTO.createdBy.id!!, null, timenoteInfoDTO.title, timenoteInfoDTO.description, timenoteInfoDTO.pictures,
                timenoteInfoDTO.colorHex, timenoteInfoDTO.location, timenoteInfoDTO.category, timenoteInfoDTO.startingAt, timenoteInfoDTO.endingAt,
                timenoteInfoDTO.hashtags, timenoteInfoDTO.url, timenoteInfoDTO.price, null), from!!))
        } else findNavController().navigate(ProfileDirections.actionProfileToCreateTimenote(2, timenoteInfoDTO.id, CreationTimenoteDTO(timenoteInfoDTO.createdBy.id!!, null, timenoteInfoDTO.title, timenoteInfoDTO.description, timenoteInfoDTO.pictures,
            timenoteInfoDTO.colorHex, timenoteInfoDTO.location, timenoteInfoDTO.category, timenoteInfoDTO.startingAt, timenoteInfoDTO.endingAt,
            timenoteInfoDTO.hashtags, timenoteInfoDTO.url, timenoteInfoDTO.price, null), from!!))
    }

    override fun onAlarmClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            datePicker { dialog, datetime ->

            }
            lifecycleOwner(this@ProfileFutureEvents)
        }
    }

    override fun onDeleteClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        timenoteViewModel.deleteTimenote(tokenId!!, timenoteInfoDTO.id).observe(viewLifecycleOwner, Observer {
            profileEventPagingAdapter.notifyDataSetChanged()
        })
    }

    override fun onDuplicateClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        if(from == 2){
        findNavController().navigate(ProfileSearchDirections.actionProfileSearchToCreateTimenoteSearch(1, "", CreationTimenoteDTO(timenoteInfoDTO.createdBy.id!!, null, timenoteInfoDTO.title, timenoteInfoDTO.description, timenoteInfoDTO.pictures,
            timenoteInfoDTO.colorHex, timenoteInfoDTO.location, timenoteInfoDTO.category, timenoteInfoDTO.startingAt, timenoteInfoDTO.endingAt,
            timenoteInfoDTO.hashtags, timenoteInfoDTO.url, timenoteInfoDTO.price, null), from!!))
        }
        else findNavController().navigate(ProfileDirections.actionProfileToCreateTimenote(1, "", CreationTimenoteDTO(timenoteInfoDTO.createdBy.id!!, null, timenoteInfoDTO.title, timenoteInfoDTO.description, timenoteInfoDTO.pictures,
            timenoteInfoDTO.colorHex, timenoteInfoDTO.location, timenoteInfoDTO.category, timenoteInfoDTO.startingAt, timenoteInfoDTO.endingAt,
            timenoteInfoDTO.hashtags, timenoteInfoDTO.url, timenoteInfoDTO.price, null), from!!))
    }

    override fun onHideToOthersClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        timenoteViewModel.hideToOthers(tokenId!!, timenoteInfoDTO.id)
    }

    override fun onSeeParticipants(timenoteInfoDTO: TimenoteInfoDTO) {
        val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
            customView(R.layout.users_participating)
            lifecycleOwner(this@ProfileFutureEvents)
        }

        val recyclerview = dial.getCustomView().users_participating_rv
        val userAdapter = UsersPagingAdapter(UsersPagingAdapter.UserComparator, timenoteInfoDTO,this)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.adapter = userAdapter
        lifecycleScope.launch{
            timenoteViewModel.getUsersParticipating(tokenId!!, timenoteInfoDTO.id).collectLatest {
                userAdapter.submitData(it)
            }
        }
    }

    override fun onShareClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        sendTo.clear()
        val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
            customView(R.layout.friends_search)
            lifecycleOwner(this@ProfileFutureEvents)
            positiveButton(R.string.send){
                timenoteViewModel.shareWith(tokenId!!, ShareTimenoteDTO(timenoteInfoDTO.id, sendTo))
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
        val userAdapter = UsersShareWithPagingAdapter(UsersPagingAdapter.UserComparator, this, this)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.adapter = userAdapter
        lifecycleScope.launch{
            followViewModel.getUsers(tokenId!!, 0).collectLatest {
                userAdapter.submitData(it)
            }
        }
    }

    override fun onAdd(userInfoDTO: UserInfoDTO) {
        sendTo.add(userInfoDTO.id!!)
    }

    override fun onRemove(userInfoDTO: UserInfoDTO) {
        sendTo.remove(userInfoDTO.id!!)
    }

    override fun onHideFilterBarClicked(position: Int?) {
        onRemoveFilterBarListener.onHideFilterBarClicked(1)
    }

    fun setListener(onRemoveFilterBarListener: OnRemoveFilterBarListener){
        this.onRemoveFilterBarListener = onRemoveFilterBarListener
    }

    fun setShowFilterBar(b: Boolean) {
        //profileEventPagingAdapter.showHideFilterBar(b)
        if(b) profile_filter_rv_chips_in_rv.visibility = View.VISIBLE
        else profile_filter_rv_chips_in_rv.visibility = View.GONE
    }

    override fun onCardClicked(event: TimenoteInfoDTO) {
        if(from == 2)findNavController().navigate(ProfileSearchDirections.actionProfileSearchToDetailedTimenoteSearch(event))
        else findNavController().navigate(ProfileDirections.actionProfileToDetailedTimenote(from!!, event))
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
                        ).collectLatest {
                            profileEventPagingAdapter.submitData(it)
                        }
                    }
                }
                1 -> lifecycleScope.launch {
                    profileViewModel.getTimenotesFiltered(tokenId!!, TimenoteFilteredDTO(isFuture, false, false, true, false)).collectLatest { profileEventPagingAdapter.submitData(it) }
                }
                2 -> lifecycleScope.launch {
                    profileViewModel.getTimenotesFiltered(tokenId!!, TimenoteFilteredDTO(isFuture, true, false, false, false)).collectLatest { profileEventPagingAdapter.submitData(it) }
                }
                3 -> lifecycleScope.launch {
                    profileViewModel.getTimenotesFiltered(tokenId!!, TimenoteFilteredDTO(isFuture, false, false, false, true)).collectLatest { profileEventPagingAdapter.submitData(it) }
                }
            }
        } else {
            loadData(userInfoDTO!!)
        }
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
            isFuture : Boolean
        ) =
            ProfileFutureEvents().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_PARAM1, showHideFilterBar)
                    putInt(ARG_PARAM2, from)
                    putString(ARG_PARAM3, id)
                    putBoolean(ARG_PARAM4, isFuture)
                    setListener(context as OnRemoveFilterBarListener)
                }
            }
    }


    override fun onSearchClicked(userInfoDTO: UserInfoDTO) {}
    override fun onAddressClicked(timenoteInfoDTO: TimenoteInfoDTO) {}
    override fun onSeeMoreClicked(event: TimenoteInfoDTO) {}
    override fun onCommentClicked(event: TimenoteInfoDTO) {}
    override fun onPlusClicked(timenoteInfoDTO: TimenoteInfoDTO, isAdded: Boolean) {}
    override fun onPictureClicked(userInfoDTO: UserInfoDTO) {}
    override fun onMaskThisUser() {}
    override fun onDoubleClick() {}


}