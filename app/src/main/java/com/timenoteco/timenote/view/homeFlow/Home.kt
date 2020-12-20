package com.timenoteco.timenote.view.homeFlow

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
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
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.*
import com.timenoteco.timenote.common.BaseThroughFragment
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.common.bytesEqualTo
import com.timenoteco.timenote.common.pixelsEqualTo
import com.timenoteco.timenote.listeners.GoToProfile
import com.timenoteco.timenote.listeners.RefreshPicBottomNavListener
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.viewModel.*
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.BranchEvent
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.friends_search_cl.view.*
import kotlinx.android.synthetic.main.users_participating.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.Type

class Home : BaseThroughFragment(), TimenoteOptionsListener, View.OnClickListener,
    UsersPagingAdapter.SearchPeopleListener, ItemTimenoteRecentAdapter.TimenoteRecentClicked, UsersShareWithPagingAdapter.SearchPeopleListener,
    UsersShareWithPagingAdapter.AddToSend {

    private lateinit var goToProfileLisner : GoToProfile
    private var sendTo: MutableList<String> = mutableListOf()
    private lateinit var handler: Handler
    private val TRIGGER_AUTO_COMPLETE = 200
    private val AUTO_COMPLETE_DELAY: Long = 200
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val alarmViewModel: AlarmViewModel by activityViewModels()
    private val timenoteViewModel: TimenoteViewModel by activityViewModels()
    private val followViewModel: FollowViewModel by activityViewModels()
    private val meViewModel : MeViewModel by activityViewModels()
    private var timenotePagingAdapter: TimenotePagingAdapter? = null
    private var timenoteRecentPagingAdapter: TimenoteRecentPagingAdapter? = null
    private lateinit var onGoToNearby: OnGoToNearby
    private lateinit var onRefreshPicBottomNavListener: RefreshPicBottomNavListener
    private lateinit var prefs: SharedPreferences
    private var tokenId: String? = null
    private var refreshTokenId: String? = null
    private val utils = Utils()
    private lateinit var userInfoDTO: UserInfoDTO

    interface OnGoToNearby{
        fun onGuestMode()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
        refreshTokenId = prefs.getString(refreshToken, null)
        if(!tokenId.isNullOrBlank()) loginViewModel.markAsAuthenticated() else findNavController().navigate(HomeDirections.actionGlobalNavigation())
        loginViewModel.getAuthenticationState().observe(requireActivity(), Observer {
            when (it) {
                LoginViewModel.AuthenticationState.UNAUTHENTICATED -> { findNavController().navigate(HomeDirections.actionGlobalNavigation()) }

                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    tokenId = prefs.getString(accessToken, null)
                    findNavController().popBackStack(R.id.home, false) }

                LoginViewModel.AuthenticationState.GUEST -> {
                    findNavController().popBackStack(R.id.home, false)
                    onGoToNearby.onGuestMode()
                }
            }
        })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        goToProfileLisner = context as GoToProfile
        onGoToNearby = context as OnGoToNearby
        onRefreshPicBottomNavListener = context as RefreshPicBottomNavListener
    }

    override fun onResume() {
        super.onResume()
        if(tokenId != null) {
            tokenId = prefs.getString(accessToken, null)
            retrieveCurrentRegistrationToken(prefs.getString(accessToken, null)!!)
        }
        when(loginViewModel.getAuthenticationState().value){
            LoginViewModel.AuthenticationState.GUEST -> loginViewModel.markAsUnauthenticated()
            LoginViewModel.AuthenticationState.UNAUTHENTICATED -> loginViewModel.markAsUnauthenticated()
            else -> ""
        }

        onRefreshPicBottomNavListener.onrefreshPicBottomNav(userInfoDTO)
    }

    @SuppressLint("StringFormatInvalid")
    fun retrieveCurrentRegistrationToken(tokenId: String){
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    meViewModel.putFCMToken(tokenId, FCMDTO(task.result?.token!!)).observe(this, Observer {
                        if(it.isSuccessful) ""
                    })
                    return@OnCompleteListener
                }
            })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_home)
    }

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(tokenId != null) {

            if(prefs.getString("alarms", null)== null) getAlarms()

            val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
            userInfoDTO = Gson().fromJson<UserInfoDTO>(prefs.getString("UserInfoDTO", ""), typeUserInfo)

            home_swipe_refresh.setColorSchemeResources(R.color.colorStartGradient, R.color.colorEndGradient)
            home_swipe_refresh.setOnRefreshListener {
                if(home_future_timeline.drawable.bytesEqualTo(resources.getDrawable(R.drawable.ic_futur_ok)) && home_future_timeline.drawable.pixelsEqualTo(resources.getDrawable(R.drawable.ic_futur_ok))) loadPastData()
                else if(home_past_timeline.drawable.bytesEqualTo(resources.getDrawable(R.drawable.ic_passe_ok)) && home_past_timeline.drawable.pixelsEqualTo(resources.getDrawable(R.drawable.ic_passe_ok))) loadUpcomingData()
            }

            if(timenoteRecentPagingAdapter == null || timenotePagingAdapter == null || home_nothing_to_display?.visibility == View.VISIBLE) loadUpcomingData()


            home_past_timeline.setOnClickListener(this)
            home_future_timeline.setOnClickListener(this)
        }

    }

    private fun getAlarms() {
        alarmViewModel.getAlarms(tokenId!!).observe(viewLifecycleOwner, Observer {
            if (it.code() == 401) {
                loginViewModel.refreshToken(prefs)
                    .observe(viewLifecycleOwner, Observer { newAccessToken ->
                        tokenId = newAccessToken
                        alarmViewModel.getAlarms(tokenId!!)
                            .observe(viewLifecycleOwner, Observer { lst ->
                                prefs.edit().putString("alarms", Gson().toJson(lst.body())).apply()
                            })
                    })
            }
            prefs.edit().putString("alarms", Gson().toJson(it.body())).apply()
        })
    }

    @ExperimentalPagingApi
    private fun loadUpcomingData() {

        home_swipe_refresh?.isRefreshing = true

        timenoteRecentPagingAdapter = TimenoteRecentPagingAdapter(TimenoteComparator, this)
        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        lifecycleScope.launch {
            timenoteViewModel.getRecentTimenotePagingFlow(tokenId!!, prefs).collectLatest {
                timenoteRecentPagingAdapter?.submitData(it)
            }
        }

        timenotePagingAdapter = TimenotePagingAdapter(TimenoteComparator, this, this, true, utils, userInfoDTO.id)
        lifecycleScope.launch {
            timenoteViewModel.getUpcomingTimenotePagingFlow(tokenId!!, true, prefs).collectLatest {
                timenotePagingAdapter?.submitData(it)
            }
        }

        home_recent_rv?.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = timenoteRecentPagingAdapter
        }
        home_rv?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = timenotePagingAdapter
        }

        timenotePagingAdapter?.addDataRefreshListener {
            home_swipe_refresh?.isRefreshing = false
            if(it){
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

    @ExperimentalPagingApi
    private fun loadPastData(){
        home_swipe_refresh?.isRefreshing = true

        timenotePagingAdapter = TimenotePagingAdapter(TimenoteComparator, this, this, false, utils, userInfoDTO.id)
        lifecycleScope.launch {
            timenoteViewModel.getUpcomingTimenotePagingFlow(tokenId!!, false, prefs).collectLatest {
                timenotePagingAdapter?.submitData(it)
            }
        }

        home_rv?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = timenotePagingAdapter
        }

        timenotePagingAdapter?.addDataRefreshListener {
            home_swipe_refresh?.isRefreshing = false
            if(it){
                home_recent_rv?.visibility = View.GONE
                home_rv?.visibility = View.GONE
                home_posted_recently?.visibility = View.GONE
                home_nothing_to_display?.visibility = View.VISIBLE
            } else {
                home_rv?.visibility = View.VISIBLE
                home_nothing_to_display?.visibility = View.GONE
            }
        }

    }

    @ExperimentalPagingApi
    override fun onClick(v: View?) {
        when(v){
            home_past_timeline -> {
                if(home_past_timeline.drawable.bytesEqualTo(resources.getDrawable(R.drawable.ic_passe_ok)) && home_past_timeline.drawable.pixelsEqualTo(resources.getDrawable(R.drawable.ic_passe_ok))){
                    home_recent_rv.visibility = View.GONE
                    home_posted_recently.visibility = View.GONE
                    home_past_timeline.setImageDrawable(resources.getDrawable(R.drawable.ic_passe_plein_grad_ok))
                    home_future_timeline.setImageDrawable(resources.getDrawable(R.drawable.ic_futur_ok))
                    loadPastData()
                }
            }
            home_future_timeline ->{
                if(home_future_timeline.drawable.bytesEqualTo(resources.getDrawable(R.drawable.ic_futur_ok)) && home_future_timeline.drawable.pixelsEqualTo(resources.getDrawable(R.drawable.ic_futur_ok))){
                    home_recent_rv.visibility = View.VISIBLE
                    home_future_timeline.setImageDrawable(resources.getDrawable(R.drawable.ic_futur_plein_grad))
                    home_past_timeline.setImageDrawable(resources.getDrawable(R.drawable.ic_passe_ok))
                    loadUpcomingData()
                }
            }
        }
    }

    override fun onCommentClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        //findNavController().navigate(HomeDirections.actionGlobalDetailedTimenoteCarousel(1, timenoteInfoDTO))
        findNavController().navigate(HomeDirections.actionGlobalDetailedTimenote(1, timenoteInfoDTO))
    }

    override fun onPlusClicked(timenoteInfoDTO: TimenoteInfoDTO, isAdded: Boolean) {
        if(isAdded){
            timenoteViewModel.joinTimenote(tokenId!!, timenoteInfoDTO.id).observe(
                viewLifecycleOwner,
                Observer {
                    if(it.code() == 401) {
                        loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer {newAccessToken ->
                            tokenId = newAccessToken
                            timenoteViewModel.joinTimenote(tokenId!!, timenoteInfoDTO.id)
                        })
                    }
                })
        } else {
            timenoteViewModel.leaveTimenote(tokenId!!, timenoteInfoDTO.id).observe(viewLifecycleOwner, Observer {
                if(it.code() == 401) {
                    loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer {newAccessToken ->
                        tokenId = newAccessToken
                        timenoteViewModel.leaveTimenote(tokenId!!, timenoteInfoDTO.id)
                    })
                }
            })
        }
    }

    override fun onPictureClicked(userInfoDTO: UserInfoDTO) {
        if(userInfoDTO.id == this.userInfoDTO.id) goToProfileLisner.goToProfile()
        else findNavController().navigate(HomeDirections.actionGlobalProfileElse(1).setUserInfoDTO(userInfoDTO))
    }

    override fun onDoubleClick() {}

    override fun onSeeParticipants(timenoteInfoDTO: TimenoteInfoDTO) {
        val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
            customView(R.layout.users_participating)
            lifecycleOwner(this@Home)
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

    override fun onTimenoteRecentClicked(event: TimenoteInfoDTO) {
        findNavController().navigate(HomeDirections.actionGlobalDetailedTimenote(1, event))
    }

    override fun onSeeMoreClicked(event: TimenoteInfoDTO) {
        findNavController().navigate(HomeDirections.actionGlobalDetailedTimenote(1, event))
    }

    override fun onReportClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        timenoteViewModel.signalTimenote(tokenId!!, TimenoteCreationSignalementDTO(userInfoDTO.id!!, timenoteInfoDTO.id, "essai")).observe(viewLifecycleOwner, Observer {
            if(it.code() == 401){
                loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer {newAccessToken ->
                    tokenId = newAccessToken
                    timenoteViewModel.signalTimenote(tokenId!!, TimenoteCreationSignalementDTO(userInfoDTO.id!!, timenoteInfoDTO.id, "essai")).observe(viewLifecycleOwner, Observer { rsp ->
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

    override fun onAlarmClicked(timenoteInfoDTO: TimenoteInfoDTO, type: Int) {
        share(timenoteInfoDTO)
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

    override fun onDuplicateClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        findNavController().navigate(HomeDirections.actionGlobalCreateTimenote().setFrom(1).setModify(1).setId(timenoteInfoDTO.id).setTimenoteBody(CreationTimenoteDTO(timenoteInfoDTO.createdBy.id!!, null, timenoteInfoDTO.title, timenoteInfoDTO.description, timenoteInfoDTO.pictures,
            timenoteInfoDTO.colorHex, timenoteInfoDTO.location, timenoteInfoDTO.category, timenoteInfoDTO.startingAt, timenoteInfoDTO.endingAt,
            timenoteInfoDTO.hashtags, timenoteInfoDTO.url, timenoteInfoDTO.price, null, timenoteInfoDTO.urlTitle)))
    }

    override fun onAddressClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        findNavController().navigate(HomeDirections.actionGlobalTimenoteAddress(timenoteInfoDTO).setFrom(1))
    }

    override fun onShareClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        sendTo.clear()
        val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
            customView(R.layout.friends_search_cl)
            lifecycleOwner(this@Home)
            positiveButton(R.string.send){
                timenoteViewModel.shareWith(tokenId!!, ShareTimenoteDTO(timenoteInfoDTO.id, sendTo)).observe(viewLifecycleOwner, Observer {
                    if(it.code() == 401) {
                        loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer {newAccessToken ->
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
        searchbar.addTextChangeListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE)
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY)
            }
            override fun afterTextChanged(s: Editable?) {}

        })
        val recyclerview = dial.getCustomView().shareWith_rv
        val  userAdapter = UsersShareWithPagingAdapter(
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
            followViewModel.getUsers(tokenId!!, userInfoDTO.id!!, 0, prefs).collectLatest {
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
                            followViewModel.getUsers(tokenId!!, userInfoDTO.id!!, 0, prefs).collectLatest {
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

    override fun onHashtagClicked(timenoteInfoDTO: TimenoteInfoDTO, hashtag: String?) {
        findNavController().navigate(HomeDirections.actionGlobalTimenoteTAG(timenoteInfoDTO, hashtag))
    }

    override fun onAdd(userInfoDTO: UserInfoDTO, createGroup: Int?) {
        sendTo.add(userInfoDTO.id!!)
    }

    override fun onRemove(userInfoDTO: UserInfoDTO, createGroup: Int?) {
        sendTo.remove(userInfoDTO.id!!)
    }

    override fun onSearchClicked(userInfoDTO: UserInfoDTO) {
        if(userInfoDTO.id == this.userInfoDTO.id) goToProfileLisner.goToProfile()
        else findNavController().navigate(HomeDirections.actionGlobalProfileElse(1).setUserInfoDTO(userInfoDTO))
    }

    override fun onDeleteClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        timenoteViewModel.deleteTimenote(tokenId!!, timenoteInfoDTO.id).observe(viewLifecycleOwner, Observer {
            if(it.code() == 401) {
                loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer { newAccessToken ->
                    tokenId = newAccessToken
                    timenoteViewModel.deleteTimenote(tokenId!!, timenoteInfoDTO.id).observe(viewLifecycleOwner, Observer {tid ->
                        if(tid.isSuccessful) timenotePagingAdapter?.refresh()
                    })
                })
            }
            if(it.isSuccessful) timenotePagingAdapter?.refresh()
        })
    }

    override fun onEditClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        findNavController().navigate(HomeDirections.actionGlobalCreateTimenote().setFrom(1).setModify(2).setId(timenoteInfoDTO.id).setTimenoteBody(CreationTimenoteDTO(timenoteInfoDTO.createdBy.id!!, null, timenoteInfoDTO.title, timenoteInfoDTO.description, timenoteInfoDTO.pictures,
            timenoteInfoDTO.colorHex, timenoteInfoDTO.location, timenoteInfoDTO.category, timenoteInfoDTO.startingAt, timenoteInfoDTO.endingAt,
            timenoteInfoDTO.hashtags, timenoteInfoDTO.url, timenoteInfoDTO.price, null, timenoteInfoDTO.urlTitle)))
    }

    override fun onUnfollow(id: String) {}
    override fun onRemove(id: String) {}
    override fun onHideToOthersClicked(timenoteInfoDTO: TimenoteInfoDTO) {}
    override fun onMaskThisUser() {}


}
