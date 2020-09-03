package com.timenoteco.timenote.view.homeFlow

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
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
import com.timenoteco.timenote.adapter.ItemTimenoteAdapter
import com.timenoteco.timenote.adapter.TimenotePagingAdapter
import com.timenoteco.timenote.adapter.UsersPagingAdapter
import com.timenoteco.timenote.common.BaseThroughFragment
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.viewModel.LoginViewModel
import com.timenoteco.timenote.viewModel.ProfileViewModel
import com.timenoteco.timenote.viewModel.TimenoteViewModel
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import kotlinx.android.synthetic.main.fragment_detailed_fragment.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.users_participating.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class Home : BaseThroughFragment(), ItemTimenoteAdapter.TimenoteRecentClicked, TimenoteOptionsListener, View.OnClickListener {

    private var timenotes: List<Timenote> = mutableListOf()
    private lateinit var timenoteAdapter: ItemTimenoteAdapter
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val timenoteViewModel: TimenoteViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private lateinit var timenotePagingAdapter: TimenotePagingAdapter
    private lateinit var onGoToNearby: OnGoToNearby
    private lateinit var prefs: SharedPreferences
    val TOKEN: String = "TOKEN"
    private var tokenId: String? = null

    interface OnGoToNearby{
        fun onGuestMode()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(TOKEN, null)
        //if(!prefs.getString(TOKEN, "").isNullOrBlank()) loginViewModel.markAsAuthenticated() else loginViewModel.markAsUnauthenticated()
        loginViewModel.getAuthenticationState().observe(requireActivity(), Observer {
            when (it) {
                LoginViewModel.AuthenticationState.UNAUTHENTICATED -> findNavController().navigate(HomeDirections.actionHomeToNavigation())
                LoginViewModel.AuthenticationState.AUTHENTICATED -> findNavController().popBackStack(R.id.home, false)
                LoginViewModel.AuthenticationState.GUEST -> {
                    findNavController().popBackStack(R.id.home, false)
                    onGoToNearby.onGuestMode()
                }
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

        timenotes = mutableListOf(
            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "23\nSep.",
                "15:30\nPM",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.FREE,
                0
            ), Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10 Aug.\n15:45",
                "11 Sep.\n12:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.PAID,
                1
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "24\nAug.",
                "15:30\nPM",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.FREE,
                0
            ), Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10Aug.\n15:45",
                "11Aug.\n12:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.PAID,
                1
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "29\nFeb.",
                "15:30 PM",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.FREE,
                0
            ), Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10Aug.\n15:45",
                "11Aug.\n12:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.PAID,
                1
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10\nAug",
                "15:30 PM",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.FREE,
                0
            ), Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10Aug.\n15:45",
                "11Aug.\n12:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.PAID,
                1
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10\nAug",
                "15:30 PM",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.FREE,
                0
            ), Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10Aug.\n15:45",
                "11Aug.\n12:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.PAID,
                1
            ),            Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10\nAug",
                "15:30 PM",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.FREE,
                0
            ), Timenote(
                "https://media.istockphoto.com/photos/beautiful-woman-posing-against-dark-background-picture-id638756792",
                mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                    "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),
                "Samuel",
                "23 Herzl Street",
                "34 Likes",
            "See 63 comments",
                "#Beach#Sunset#Love A very good place to be also known for his cold drinks a good music open all day and night come join us we are waiting for you",
                true,
                "2020",
                "10Aug.\n15:45",
                "11Aug.\n12:30",
                "Beach Party",
                "In 23 days",
                12L,
                "www.google.com",
                StatusTimenote.PAID,
                1
            )
        )

        home_swipe_refresh.isRefreshing = true
        home_swipe_refresh.setColorSchemeResources(R.color.colorStartGradient, R.color.colorEndGradient)
        home_swipe_refresh.setOnRefreshListener {
            home_swipe_refresh.isRefreshing = false
        }

        /*timenotePagingAdapter = TimenotePagingAdapter(TimenoteComparator, this, this)
        home_rv.adapter = timenotePagingAdapter
        lifecycleScope.launch {
            timenoteViewModel.timenotePagingFlow.collectLatest {
                timenotePagingAdapter.submitData(it)
            }
        }*/

        timenoteAdapter = ItemTimenoteAdapter(timenotes, timenotes, true, this, this, this as Fragment, true)
        home_rv.apply {
            layoutManager = LinearLayoutManager(context)
            /*Handler().postDelayed({
                adapter = timenoteAdapter
                home_swipe_refresh?.isRefreshing = false
            }, 2000)*/

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
                    timenoteAdapter = ItemTimenoteAdapter(timenotes, timenotes, false, this, this, this as Fragment, false)
                    home_rv.adapter = timenoteAdapter
                    timenoteAdapter.notifyDataSetChanged()
                }
            }
            home_future_timeline ->{
                if(home_future_timeline.drawable.constantState == resources.getDrawable(R.drawable.ic_futur_ok).constantState){
                    home_recent_rv.visibility = View.VISIBLE
                    home_future_timeline.setImageDrawable(resources.getDrawable(R.drawable.ic_futur_plein_grad))
                    home_past_timeline.setImageDrawable(resources.getDrawable(R.drawable.ic_passe_ok))
                    timenoteAdapter = ItemTimenoteAdapter(timenotes, timenotes, true, this, this, this as Fragment, true)
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

    override fun onDoubleClick() {
        timenoteViewModel.getSpecificTimenote(tokenId!!, "").observe(viewLifecycleOwner, Observer {
            if(it.body()?.joinedBy?.contains("")!!) timenoteViewModel.joinTimenote(tokenId!!, "")
            else timenoteViewModel.leaveTimenote(tokenId!!, "")

        })
    }

    override fun onSeeParticipants() {
        val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            customView(R.layout.users_participating)
            lifecycleOwner(this@Home)
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
        findNavController().navigate(HomeDirections.actionHomeToCreateTimenote(true, "",
            TimenoteBody("", CreatedBy("", "", "", "", "", "", ""),
                "", "", listOf(), "", Location(0.0, 0.0, Address("", "", "", "")),
                Category("",""), "", "", listOf(), "", 0, ""), 1))
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

    override fun onDuplicateClicked() {
        findNavController().navigate(HomeDirections.actionHomeToCreateTimenote(true, "",
            TimenoteBody("", CreatedBy("", "", "", "", "", "", ""),
                "", "", listOf(), "", Location(0.0, 0.0, Address("", "", "", "")),
                Category("",""), "", "", listOf(), "", 0, ""), 1))    }

    override fun onAddressClicked() {
        findNavController().navigate(HomeDirections.actionHomeToTimenoteAddress())
    }
}
