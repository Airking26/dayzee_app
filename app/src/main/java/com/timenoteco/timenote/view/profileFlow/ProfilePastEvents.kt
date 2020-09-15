package com.timenoteco.timenote.view.profileFlow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ItemProfileEventAdapter
import com.timenoteco.timenote.listeners.ItemProfileCardListener
import com.timenoteco.timenote.listeners.OnRemoveFilterBarListener
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.TimenoteInfoDTO
import kotlinx.android.synthetic.main.fragment_profile_future_events.*
import java.sql.Time

private const val ARG_PARAM1 = "showHideFilterBar"
private const val ARG_PARAM3 = "isPrivate"


class ProfilePastEvents : Fragment(), TimenoteOptionsListener, OnRemoveFilterBarListener,
    ItemProfileCardListener {

    private var showHideFilterBar: Boolean? = null
    private var eventAdapter: ItemProfileEventAdapter? = null
    private var timenotes: MutableList<TimenoteInfoDTO> = mutableListOf()
    private lateinit var onRemoveFilterBarListener: OnRemoveFilterBarListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            showHideFilterBar = it.getBoolean(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile_future_events, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        eventAdapter = ItemProfileEventAdapter(
            timenotes,
            this,
            this,
            this,
            showHideFilterBar!!)

        profile_rv.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = eventAdapter
        }
    }

    override fun onReportClicked() {
    }

    override fun onEditClicked() {
    }

    override fun onAlarmClicked(timenoteInfoDTO: TimenoteInfoDTO) {
    }

    override fun onDeleteClicked() {
    }

    override fun onDuplicateClicked(timenoteInfoDTO: TimenoteInfoDTO) {
    }

    override fun onAddressClicked() {
    }

    override fun onSeeMoreClicked(event: TimenoteInfoDTO) {
    }

    override fun onCommentClicked(event: TimenoteInfoDTO) {
    }

    override fun onPlusClicked() {
    }

    override fun onPictureClicked() {
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
        this.onRemoveFilterBarListener.onHideFilterBarClicked(0)
    }

    fun setShowFilterBar(b: Boolean) {
        eventAdapter?.showHideFilterBar(b)
    }


    fun setListener(onRemoveFilterBarListener: OnRemoveFilterBarListener){
        this.onRemoveFilterBarListener = onRemoveFilterBarListener
    }

    companion object{
        @JvmStatic
        fun newIstance(showHideFilterBar: Boolean, fragment: Fragment) =
            ProfilePastEvents().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_PARAM1, showHideFilterBar)
                    setListener(fragment as OnRemoveFilterBarListener)
                }
            }

    }

    override fun onCardClicked(event: TimenoteInfoDTO) {

    }

}