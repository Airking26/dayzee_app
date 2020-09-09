package com.timenoteco.timenote.view.profileFlow

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.*
import com.timenoteco.timenote.listeners.ItemProfileCardListener
import com.timenoteco.timenote.listeners.OnRemoveFilterBarListener
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.view.searchFlow.ProfileSearchDirections
import com.timenoteco.timenote.viewModel.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile_future_events.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "showHideFilterBar"
private const val ARG_PARAM2 = "from"

class ProfileFutureEvents : Fragment(), TimenoteOptionsListener, OnRemoveFilterBarListener,
    ItemProfileCardListener {

    private lateinit var prefs: SharedPreferences
    val TOKEN: String = "TOKEN"
    private var tokenId: String? = null
    private var showHideFilterBar: Boolean? = null
    private var from: Int? = null
    private var eventAdapter: ItemProfileEventAdapter? = null
    private var timenotes: MutableList<TimenoteInfoDTO> = mutableListOf()
    private lateinit var onRemoveFilterBarListener: OnRemoveFilterBarListener
    private val profileViewModel : ProfileViewModel by activityViewModels()
    private lateinit var profileEventPagingAdapter : ProfileEventPagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(TOKEN, null)
        arguments?.let {
            showHideFilterBar = it.getBoolean(ARG_PARAM1)
            from = it.getInt(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_profile_future_events, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //eventAdapter = ItemProfileEventAdapter(timenotes, this, this, this, showHideFilterBar!!)

        profileEventPagingAdapter = ProfileEventPagingAdapter(ProfileEventComparator, showHideFilterBar!!, this, this, this)
        profile_rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = profileEventPagingAdapter
        }

        lifecycleScope.launch {
            profileViewModel.getEventProfile(tokenId!!, true).collectLatest {
                profileEventPagingAdapter.submitData(it)
            }
        }

    }

    override fun onReportClicked() {
    }

    override fun onEditClicked() {
        findNavController().navigate(ProfileDirections.actionProfileToCreateTimenote(true, "",
            TimenoteBody("", CreatedBy("", "", "", "", "", "", ""),
                "", "", listOf(), "", Location(0.0, 0.0, Address("", "", "", "")),
                Category("",""), "", "", listOf(), "", 0, ""), from!!))
    }

    override fun onAlarmClicked() {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            datePicker { dialog, datetime ->

            }
            lifecycleOwner(this@ProfileFutureEvents)
        }
    }

    override fun onDeleteClicked() {
    }

    override fun onDuplicateClicked() {
        if(from == 2){
            findNavController().navigate(ProfileSearchDirections.actionProfileSearchToCreateTimenoteSearch(true, "",
                TimenoteBody("", CreatedBy("", "", "", "", "", "", ""),
                    "", "", listOf(), "", Location(0.0, 0.0, Address("", "", "", "")),
                    Category("",""), "", "", listOf(), "", 0, ""), from!!))
        } else findNavController().navigate(ProfileDirections.actionProfileToCreateTimenote(true, "",
            TimenoteBody("", CreatedBy("", "", "", "", "", "", ""),
                "", "", listOf(), "", Location(0.0, 0.0, Address("", "", "", "")),
                Category("",""), "", "", listOf(), "", 0, ""), from!!)
        )
    }

    override fun onHideToOthersClicked() {
    }

    override fun onMaskThisUser() {
    }

    override fun onDoubleClick() {

    }

    override fun onSeeParticipants() {

    }

    override fun onHideFilterBarClicked(position: Int?) {
        this.onRemoveFilterBarListener.onHideFilterBarClicked(1)
    }

    fun setListener(onRemoveFilterBarListener: OnRemoveFilterBarListener){
        this.onRemoveFilterBarListener = onRemoveFilterBarListener
    }

    fun setShowFilterBar(b: Boolean) {
        eventAdapter?.showHideFilterBar(b)
    }

    override fun onAddressClicked() {
    }
    override fun onSeeMoreClicked() {
    }
    override fun onCommentClicked() {
    }
    override fun onPlusClicked() {
    }
    override fun onPictureClicked() {
    }

    companion object{
        @JvmStatic
        fun newInstance(
            showHideFilterBar: Boolean,
            context: Fragment,
            from: Int
        ) =
            ProfileFutureEvents().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_PARAM1, showHideFilterBar)
                    putInt(ARG_PARAM2, from)
                    setListener(context as OnRemoveFilterBarListener)
                }
            }
    }

    override fun onCardClicked() {
        if(from == 2)findNavController().navigate(ProfileSearchDirections.actionProfileSearchToDetailedTimenoteSearch())
        else findNavController().navigate(ProfileDirections.actionProfileToDetailedTimenote(from!!))
    }

}