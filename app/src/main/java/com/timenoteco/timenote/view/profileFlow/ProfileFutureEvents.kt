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
private const val ARG_PARAM3 = "id"

class ProfileFutureEvents : Fragment(), TimenoteOptionsListener, OnRemoveFilterBarListener,
    ItemProfileCardListener {

    private lateinit var prefs: SharedPreferences
    val TOKEN: String = "TOKEN"
    private var tokenId: String? = null
    private var showHideFilterBar: Boolean? = null
    private var from: Int? = null
    private lateinit var id: String
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
            id = it.getString(ARG_PARAM3)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_profile_future_events, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        profileEventPagingAdapter = ProfileEventPagingAdapter(ProfileEventComparator, showHideFilterBar!!, this, this, this)
        profile_rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = profileEventPagingAdapter
        }

        lifecycleScope.launch {
            profileViewModel.getEventProfile(tokenId!!, id, true).collectLatest {
                profileEventPagingAdapter.submitData(it)
            }
        }

    }

    override fun onReportClicked() {
    }

    override fun onEditClicked() {
        //findNavController().navigate(ProfileDirections.actionProfileToCreateTimenote(true, "", CreationTimenoteDTO(), from!!))
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

    override fun onDuplicateClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        if(from == 2){
        //    findNavController().navigate(ProfileSearchDirections.actionProfileSearchToCreateTimenoteSearch(true, "", CreationTimenoteDTO(), from!!))
        }
        //else findNavController().navigate(ProfileDirections.actionProfileToCreateTimenote(true, "", CreationTimenoteDTO(), from!!))
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
            from: Int,
            id: String
        ) =
            ProfileFutureEvents().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_PARAM1, showHideFilterBar)
                    putInt(ARG_PARAM2, from)
                    putString(ARG_PARAM3, id)
                    setListener(context as OnRemoveFilterBarListener)
                }
            }
    }

    override fun onCardClicked() {
        if(from == 2)findNavController().navigate(ProfileSearchDirections.actionProfileSearchToDetailedTimenoteSearch())
        else findNavController().navigate(ProfileDirections.actionProfileToDetailedTimenote(from!!))
    }

}