package com.timenoteco.timenote.view.homeFlow

import android.content.Context
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.mancj.materialsearchbar.MaterialSearchBar
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.*
import com.timenoteco.timenote.common.BaseThroughFragment
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.viewModel.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.friends_search.view.*
import kotlinx.android.synthetic.main.users_participating.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import kotlin.math.log

class Home : BaseThroughFragment(), TimenoteOptionsListener, View.OnClickListener,
    UsersPagingAdapter.SearchPeopleListener, ItemTimenoteRecentAdapter.TimenoteRecentClicked {

    private lateinit var handler: Handler
    private val TRIGGER_AUTO_COMPLETE = 200
    private val AUTO_COMPLETE_DELAY: Long = 200
    private val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val timenoteViewModel: TimenoteViewModel by activityViewModels()
    private val alarmViewModel : AlarmViewModel by activityViewModels()
    private val followViewModel: FollowViewModel by activityViewModels()
    private var timenotePagingAdapter: TimenotePagingAdapter? = null
    private var timenoteRecentPagingAdapter: TimenoteRecentPagingAdapter? = null
    private lateinit var onGoToNearby: OnGoToNearby
    private lateinit var prefs: SharedPreferences
    val TOKEN: String = "TOKEN"
    private var tokenId: String? = null
    private val utils = Utils()

    interface OnGoToNearby{
        fun onGuestMode()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!tokenId.isNullOrBlank()) loginViewModel.markAsAuthenticated() else findNavController().navigate(HomeDirections.actionHomeToNavigation())
        loginViewModel.getAuthenticationState().observe(requireActivity(), Observer {
            when (it) {
                LoginViewModel.AuthenticationState.UNAUTHENTICATED -> {
                    findNavController().navigate(
                        HomeDirections.actionHomeToNavigation()
                    )
                }
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    tokenId = prefs.getString(TOKEN, null)
                    findNavController().popBackStack(R.id.home, false)
                }
                LoginViewModel.AuthenticationState.GUEST -> {
                    findNavController().popBackStack(R.id.home, false)
                    onGoToNearby.onGuestMode()
                }
                else -> Toast.makeText(requireContext(), "tg", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(TOKEN, null)
        if(!tokenId.isNullOrBlank()) {

            timenoteRecentPagingAdapter = TimenoteRecentPagingAdapter(TimenoteRecentComparator, this)
            timenotePagingAdapter = TimenotePagingAdapter(TimenoteComparator, this, this, true, utils)

            lifecycleScope.launch {
                timenoteViewModel.getRecentTimenotePagingFlow(tokenId!!).collectLatest {
                    timenoteRecentPagingAdapter?.submitData(it)
                }
            }

            lifecycleScope.launch {
                timenoteViewModel.getTimenotePagingFlow(tokenId!!).collectLatest {
                    timenotePagingAdapter?.submitData(it)
                }
            }
        }

        retainInstance = true
        onGoToNearby = context as OnGoToNearby
    }

    override fun onResume() {
        super.onResume()
        when(loginViewModel.getAuthenticationState().value){
            LoginViewModel.AuthenticationState.GUEST -> loginViewModel.markAsUnauthenticated()
            LoginViewModel.AuthenticationState.UNAUTHENTICATED -> loginViewModel.markAsUnauthenticated()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_home)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(tokenId != null) {
            if(timenoteRecentPagingAdapter == null || home_nothing_to_display?.visibility == View.VISIBLE) {
                timenoteRecentPagingAdapter = TimenoteRecentPagingAdapter(TimenoteRecentComparator, this)
                lifecycleScope.launch {
                    timenoteViewModel.getRecentTimenotePagingFlow(tokenId!!).collectLatest {
                        timenoteRecentPagingAdapter?.submitData(it)
                    }
                }
            }
            if(timenotePagingAdapter == null || home_nothing_to_display?.visibility == View.VISIBLE) {
                timenotePagingAdapter = TimenotePagingAdapter(TimenoteComparator, this, this, true, utils)
                lifecycleScope.launch {
                    timenoteViewModel.getTimenotePagingFlow(tokenId!!).collectLatest {
                        timenotePagingAdapter?.submitData(it)
                    }
                }
            }

            home_swipe_refresh.setColorSchemeResources(R.color.colorStartGradient, R.color.colorEndGradient)
            home_swipe_refresh.setOnRefreshListener {
                timenoteRecentPagingAdapter = TimenoteRecentPagingAdapter(TimenoteComparator, this)
                home_recent_rv.adapter = timenoteRecentPagingAdapter
                home_recent_rv.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                lifecycleScope.launch {
                    timenoteViewModel.getRecentTimenotePagingFlow(tokenId!!).collectLatest {
                        timenoteRecentPagingAdapter?.submitData(it)
                    }
                }


                timenotePagingAdapter =
                    TimenotePagingAdapter(TimenoteComparator, this, this, true, utils)
                home_rv?.adapter = timenotePagingAdapter
                home_rv?.layoutManager = LinearLayoutManager(requireContext())
                lifecycleScope.launch {
                    timenoteViewModel.getTimenotePagingFlow(tokenId!!).collectLatest {
                        home_swipe_refresh.isRefreshing = false
                        timenotePagingAdapter?.submitData(it)
                    }
                }
            }

            home_recent_rv?.adapter = timenoteRecentPagingAdapter
            home_recent_rv?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            home_rv?.adapter = timenotePagingAdapter
            home_rv?.layoutManager = LinearLayoutManager(requireContext())


            timenotePagingAdapter?.addLoadStateListener {
                home_swipe_refresh.isRefreshing = it.refresh is LoadState.Loading
                if (it.refresh != LoadState.Loading) {
                    if(timenoteRecentPagingAdapter?.itemCount  == 0 && timenotePagingAdapter?.itemCount == 0){
                        home_recent_rv?.visibility = View.GONE
                        home_rv?.visibility = View.GONE
                        home_posted_recently?.visibility = View.GONE
                        home_nothing_to_display?.visibility = View.VISIBLE
                    } else {
                        home_recent_rv?.visibility = View.VISIBLE
                        home_rv?.visibility = View.VISIBLE
                        home_posted_recently?.visibility = View.VISIBLE
                        home_nothing_to_display?.visibility = View.GONE
                    }
                }
            }

            home_past_timeline.setOnClickListener(this)
            home_future_timeline.setOnClickListener(this)

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
    }

    override fun onClick(v: View?) {
        when(v){
            home_past_timeline -> {
                if(home_past_timeline.drawable.constantState == resources.getDrawable(R.drawable.ic_passe_ok).constantState){
                    home_recent_rv.visibility = View.GONE
                    home_posted_recently.visibility = View.GONE
                    home_past_timeline.setImageDrawable(resources.getDrawable(R.drawable.ic_passe_plein_grad_ok))
                    home_future_timeline.setImageDrawable(resources.getDrawable(R.drawable.ic_futur_ok))
                }
            }
            home_future_timeline ->{
                if(home_future_timeline.drawable.constantState == resources.getDrawable(R.drawable.ic_futur_ok).constantState){
                    home_recent_rv.visibility = View.VISIBLE
                    home_future_timeline.setImageDrawable(resources.getDrawable(R.drawable.ic_futur_plein_grad))
                    home_past_timeline.setImageDrawable(resources.getDrawable(R.drawable.ic_passe_ok))
                }
            }
        }
    }

    override fun onCommentClicked(event: TimenoteInfoDTO) {
        findNavController().navigate(HomeDirections.actionHomeToDetailedTimenote(1, event))
    }

    override fun onPlusClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        if(false){
            timenoteViewModel.leaveTimenote(tokenId!!, timenoteInfoDTO.id).observe(viewLifecycleOwner, Observer {})
        } else {
            timenoteViewModel.joinTimenote(tokenId!!, timenoteInfoDTO.id).observe(viewLifecycleOwner, Observer {})
        }
    }

    override fun onPictureClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        findNavController().navigate(HomeDirections.actionHomeToProfile(true, 1, timenoteInfoDTO))
    }

    override fun onDoubleClick() {}

    override fun onSeeParticipants(timenoteInfoDTO: TimenoteInfoDTO) {
        val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            customView(R.layout.users_participating)
            lifecycleOwner(this@Home)
        }

        val recyclerview = dial.getCustomView().users_participating_rv
        val userAdapter = UsersPagingAdapter(UsersPagingAdapter.UserComparator, timenoteInfoDTO, this)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.adapter = userAdapter
        lifecycleScope.launch{
            timenoteViewModel.getUsersParticipating(tokenId!!, timenoteInfoDTO.id).collectLatest {
                userAdapter.submitData(it)
            }
        }
    }

    override fun onTimenoteRecentClicked(event: TimenoteInfoDTO) {
        findNavController().navigate(HomeDirections.actionHomeToDetailedTimenote(1, event))
    }

    override fun onSeeMoreClicked(event: TimenoteInfoDTO) {
        findNavController().navigate(HomeDirections.actionHomeToDetailedTimenote(1, event))
    }

    override fun onReportClicked() {
        timenoteViewModel.deleteTimenote(tokenId!!, "")
    }

    override fun onAlarmClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            dateTimePicker { dialog, datetime ->
                alarmViewModel.createAlarm(tokenId!!, AlarmCreationDTO(timenoteInfoDTO.createdBy.id!!, timenoteInfoDTO.id, SimpleDateFormat(ISO).format(datetime.time.time))).observe(viewLifecycleOwner, Observer {
                    if (it.isSuccessful) ""
                })
            }
            lifecycleOwner(this@Home)
        }
    }

    override fun onDuplicateClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        findNavController().navigate(HomeDirections.actionHomeToCreateTimenote(1, "",
            CreationTimenoteDTO(timenoteInfoDTO.createdBy.id!!, null, timenoteInfoDTO.title, timenoteInfoDTO.description, timenoteInfoDTO.pictures,
            timenoteInfoDTO.colorHex, timenoteInfoDTO.location, timenoteInfoDTO.category, timenoteInfoDTO.startingAt, timenoteInfoDTO.endingAt,
            timenoteInfoDTO.hashtags, timenoteInfoDTO.url, timenoteInfoDTO.price, null), 1))
    }

    override fun onAddressClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        findNavController().navigate(HomeDirections.actionHomeToTimenoteAddress(timenoteInfoDTO))
    }

    override fun onShareClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
            customView(R.layout.friends_search)
            lifecycleOwner(this@Home)
        }

        val searchbar = dial.getCustomView().searchBar_friends
        searchbar.setCardViewElevation(0)
        searchbar.addTextChangeListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE)
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY)
            }
            override fun afterTextChanged(s: Editable?) {}

        })
        val recyclerview = dial.getCustomView().shareWith_rv
        val userAdapter = UsersPagingAdapter(UsersPagingAdapter.UserComparator, timenoteInfoDTO, this)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.adapter = userAdapter
        lifecycleScope.launch{
            followViewModel.getUsers(tokenId!!, false).collectLatest {
                userAdapter.submitData(it)
            }
        }
    }

    override fun onSearchClicked(userInfoDTO: UserInfoDTO, timenoteInfoDTO: TimenoteInfoDTO?) {
        timenoteViewModel.shareWith(tokenId!!, ShareTimenoteDTO(timenoteInfoDTO?.id!!, listOf(userInfoDTO.id!!))).observe(viewLifecycleOwner, Observer {
            if(it.isSuccessful) ""
        })
    }



    override fun onHideToOthersClicked(timenoteInfoDTO: TimenoteInfoDTO) {}
    override fun onMaskThisUser() {}
    override fun onDeleteClicked(timenoteInfoDTO: TimenoteInfoDTO) {
    }
    override fun onEditClicked(timenoteInfoDTO: TimenoteInfoDTO) {}


}
