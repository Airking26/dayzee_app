package com.timenoteco.timenote.view.homeFlow

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.*
import com.timenoteco.timenote.common.BaseThroughFragment
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.viewModel.LoginViewModel
import com.timenoteco.timenote.viewModel.ProfileViewModel
import com.timenoteco.timenote.viewModel.TimenoteViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.users_participating.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.log

class Home : BaseThroughFragment(), TimenoteOptionsListener, View.OnClickListener,
    UsersPagingAdapter.SearchPeopleListener, ItemTimenoteRecentAdapter.TimenoteRecentClicked {

    private var timenotes: List<TimenoteInfoDTO> = mutableListOf()
    private lateinit var timenoteAdapter: ItemTimenoteAdapter
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val timenoteViewModel: TimenoteViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private lateinit var timenotePagingAdapter: TimenotePagingAdapter
    private lateinit var timenoteRecentPagingAdapter: TimenoteRecentPagingAdapter
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
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(TOKEN, null)
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
        home_swipe_refresh.isRefreshing = true
        home_swipe_refresh.setColorSchemeResources(R.color.colorStartGradient, R.color.colorEndGradient)
        home_swipe_refresh.setOnRefreshListener {
            timenoteRecentPagingAdapter = TimenoteRecentPagingAdapter(TimenoteComparator, this)
            home_recent_rv.adapter = timenoteRecentPagingAdapter
            home_recent_rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            lifecycleScope.launch {
                timenoteViewModel.getRecentTimenotePagingFlow(tokenId!!).collectLatest {
                    timenoteRecentPagingAdapter.submitData(it)
                }
            }

            timenotePagingAdapter = TimenotePagingAdapter(TimenoteComparator, this, this, true, utils)
            home_rv.adapter = timenotePagingAdapter
            home_rv.layoutManager = LinearLayoutManager(requireContext())
            lifecycleScope.launch {
                timenoteViewModel.getTimenotePagingFlow(tokenId!!).collectLatest {
                    timenotePagingAdapter.submitData(it)
                    home_swipe_refresh.isRefreshing = false
                }
            }
        }

        if(!tokenId.isNullOrBlank()) {
            timenoteRecentPagingAdapter =
                TimenoteRecentPagingAdapter(TimenoteRecentComparator, this)
            home_recent_rv.adapter = timenoteRecentPagingAdapter
            home_recent_rv.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            lifecycleScope.launch {
                timenoteViewModel.getRecentTimenotePagingFlow(tokenId!!).collectLatest {
                    timenoteRecentPagingAdapter.submitData(it)
                }
            }

            timenotePagingAdapter =
                TimenotePagingAdapter(TimenoteComparator, this, this, true, utils)
            home_rv.adapter = timenotePagingAdapter
            home_rv.layoutManager = LinearLayoutManager(requireContext())
            lifecycleScope.launch {
                timenoteViewModel.getTimenotePagingFlow(tokenId!!).collectLatest {
                    home_swipe_refresh.isRefreshing = false
                    timenotePagingAdapter.submitData(it)
                }
            }
        }

        home_past_timeline.setOnClickListener(this)
        home_future_timeline.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v){
            home_past_timeline -> {
                if(home_past_timeline.drawable.constantState == resources.getDrawable(R.drawable.ic_passe_ok).constantState){
                    home_recent_rv.visibility = View.GONE
                    home_past_timeline.setImageDrawable(resources.getDrawable(R.drawable.ic_passe_plein_grad_ok))
                    home_future_timeline.setImageDrawable(resources.getDrawable(R.drawable.ic_futur_ok))
                    timenoteAdapter = ItemTimenoteAdapter(timenotes, this, this, false, utils)
                    home_rv.adapter = timenoteAdapter
                    timenoteAdapter.notifyDataSetChanged()
                }
            }
            home_future_timeline ->{
                if(home_future_timeline.drawable.constantState == resources.getDrawable(R.drawable.ic_futur_ok).constantState){
                    home_recent_rv.visibility = View.VISIBLE
                    home_future_timeline.setImageDrawable(resources.getDrawable(R.drawable.ic_futur_plein_grad))
                    home_past_timeline.setImageDrawable(resources.getDrawable(R.drawable.ic_passe_ok))
                    timenoteAdapter = ItemTimenoteAdapter(timenotes,this, this, true, utils)
                    timenoteAdapter.notifyDataSetChanged()
                    home_rv.adapter = timenoteAdapter
                }
            }
        }
    }

    override fun onCommentClicked() {
        findNavController().navigate(HomeDirections.actionHomeToDetailedTimenote(1))
    }


    override fun onPlusClicked() {
    }

    override fun onPictureClicked() {
        findNavController().navigate(HomeDirections.actionHomeToProfile(true, 1))
    }

    override fun onHideToOthersClicked() {

    }

    override fun onMaskThisUser() {

    }

    override fun onDoubleClick() {}

    override fun onSeeParticipants() {
        val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            customView(R.layout.users_participating)
            lifecycleOwner(this@Home)
        }

        val recyclerview = dial.getCustomView().users_participating_rv
        val adapter = UsersPagingAdapter(UsersPagingAdapter.UserComparator, this)
        recyclerview.adapter = adapter
        lifecycleScope.launch{
            profileViewModel.getUsers(tokenId!!, followers = true, useTimenoteService = true, id =  null).collectLatest {
                adapter.submitData(it)
            }
        }
    }

    override fun onTimenoteRecentClicked() {
        findNavController().navigate(HomeDirections.actionHomeToDetailedTimenote(1))
    }

    override fun onSeeMoreClicked() {
        findNavController().navigate(HomeDirections.actionHomeToDetailedTimenote(1))
    }

    override fun onReportClicked() {
        timenoteViewModel.deleteTimenote(tokenId!!, "")
    }

    override fun onEditClicked() {
    }

    override fun onAlarmClicked() {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            dateTimePicker { dialog, datetime ->

            }
            lifecycleOwner(this@Home)
        }
    }

    override fun onDeleteClicked() {
        timenoteViewModel.deleteTimenote(tokenId!!, "")
    }

    override fun onDuplicateClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        findNavController().navigate(HomeDirections.actionHomeToCreateTimenote(true, "",
            CreationTimenoteDTO(timenoteInfoDTO.createdBy.id, null, timenoteInfoDTO.title, timenoteInfoDTO.description, timenoteInfoDTO.pictures,
            timenoteInfoDTO.colorHex, timenoteInfoDTO.location, timenoteInfoDTO.category, timenoteInfoDTO.startingAt, timenoteInfoDTO.endingAt,
            timenoteInfoDTO.hashtags, timenoteInfoDTO.url, timenoteInfoDTO.price, null), 1))
    }

    override fun onAddressClicked() {
        findNavController().navigate(HomeDirections.actionHomeToTimenoteAddress())
    }

    override fun onSearchClicked(userInfoDTO: UserInfoDTO) {

    }


}
