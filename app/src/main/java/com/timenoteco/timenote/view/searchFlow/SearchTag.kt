package com.timenoteco.timenote.view.searchFlow

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.TimenoteComparator
import com.timenoteco.timenote.adapter.TimenotePagingAdapter
import com.timenoteco.timenote.adapter.UsersPagingAdapter
import com.timenoteco.timenote.adapter.UsersShareWithPagingAdapter
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.view.homeFlow.HomeDirections
import com.timenoteco.timenote.viewModel.*
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.BranchEvent
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search_people.*
import kotlinx.android.synthetic.main.fragment_search_tag.*
import kotlinx.android.synthetic.main.friends_search.view.*
import kotlinx.android.synthetic.main.users_participating.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.time.temporal.WeekFields.ISO

class SearchTag : Fragment(), TimenoteOptionsListener, UsersPagingAdapter.SearchPeopleListener,
    UsersShareWithPagingAdapter.SearchPeopleListener, UsersShareWithPagingAdapter.AddToSend {

    private var sendTo: MutableList<String> = mutableListOf()
    private val searchViewModel : SearchViewModel by activityViewModels()
    private val followViewModel : FollowViewModel by activityViewModels()
    private val timenoteViewModel: TimenoteViewModel by activityViewModels()
    private val alarmViewModel: AlarmViewModel by activityViewModels()
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val utils = Utils()
    private val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private lateinit var prefs : SharedPreferences
    private lateinit var handler: Handler
    private val TRIGGER_AUTO_COMPLETE = 200
    private val AUTO_COMPLETE_DELAY: Long = 200
    private var tokenId: String? = ""
    private lateinit var userInfoDTO: UserInfoDTO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
        val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
        userInfoDTO = Gson().fromJson<UserInfoDTO>(prefs.getString("UserInfoDTO", ""), typeUserInfo)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search_tag, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val userAdapter = TimenotePagingAdapter(TimenoteComparator, this, this, true, utils)
            search_tag_rv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = userAdapter
            }
            searchViewModel.getTagSearchLiveData().observe(viewLifecycleOwner, Observer {
                lifecycleScope.launch {
                    it.collectLatest {
                        userAdapter.submitData(it)
                    }
                }
            })


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

    override fun onReportClicked() {

    }

    override fun onEditClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        findNavController().navigate(SearchDirections.actionSearchToCreateTimenoteSearch(2, timenoteInfoDTO.id, CreationTimenoteDTO(timenoteInfoDTO.createdBy.id!!, null, timenoteInfoDTO.title, timenoteInfoDTO.description, timenoteInfoDTO.pictures,
            timenoteInfoDTO.colorHex, timenoteInfoDTO.location, timenoteInfoDTO.category, timenoteInfoDTO.startingAt, timenoteInfoDTO.endingAt,
            timenoteInfoDTO.hashtags, timenoteInfoDTO.url, timenoteInfoDTO.price, null), 2))
    }



    override fun onShareClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        sendTo.clear()
        val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
            customView(R.layout.friends_search)
            lifecycleOwner(this@SearchTag)
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
        val userAdapter = UsersShareWithPagingAdapter(UsersPagingAdapter.UserComparator, this, this)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.adapter = userAdapter
        lifecycleScope.launch{
            followViewModel.getUsers(tokenId!!, userInfoDTO.id!!,  0, prefs).collectLatest {
                userAdapter.submitData(it)
            }
        }
    }

    override fun onAddMarker(timenoteInfoDTO: TimenoteInfoDTO) {

    }


    override fun onAlarmClicked(timenoteInfoDTO: TimenoteInfoDTO) {
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

    override fun onDeleteClicked(timenoteInfoDTO: TimenoteInfoDTO) {
    }

    override fun onDuplicateClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        findNavController().navigate(SearchDirections.actionSearchToCreateTimenoteSearch(1, timenoteInfoDTO.id, CreationTimenoteDTO(timenoteInfoDTO.createdBy.id!!, null, timenoteInfoDTO.title, timenoteInfoDTO.description, timenoteInfoDTO.pictures,
            timenoteInfoDTO.colorHex, timenoteInfoDTO.location, timenoteInfoDTO.category, timenoteInfoDTO.startingAt, timenoteInfoDTO.endingAt,
            timenoteInfoDTO.hashtags, timenoteInfoDTO.url, timenoteInfoDTO.price, null), 2))
    }

    override fun onAddressClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        findNavController().navigate(SearchDirections.actionSearchToTimenoteAddressSearch(timenoteInfoDTO))
    }

    override fun onSeeMoreClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        findNavController().navigate(SearchDirections.actionSearchToDetailedTimenoteSearch(timenoteInfoDTO))
    }

    override fun onCommentClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        findNavController().navigate(SearchDirections.actionSearchToDetailedTimenoteSearch(timenoteInfoDTO))
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
        findNavController().navigate(SearchDirections.actionSearchToProfileSearch(userInfoDTO))
    }

    override fun onHideToOthersClicked(timenoteInfoDTO: TimenoteInfoDTO) {
    }

    override fun onMaskThisUser() {
    }

    override fun onDoubleClick() {
    }

    override fun onSeeParticipants(timenoteInfoDTO: TimenoteInfoDTO) {
        val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
            customView(R.layout.users_participating)
            lifecycleOwner(this@SearchTag)
        }

        val recyclerview = dial.getCustomView().users_participating_rv
        val userAdapter = UsersPagingAdapter(UsersPagingAdapter.UserComparator, timenoteInfoDTO, this)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.adapter = userAdapter
        lifecycleScope.launch{
            timenoteViewModel.getUsersParticipating(tokenId!!, timenoteInfoDTO.id, prefs).collectLatest {
                userAdapter.submitData(it)
            }
        }
    }

    override fun onSearchClicked(userInfoDTO: UserInfoDTO) {
    }

    override fun onAdd(userInfoDTO: UserInfoDTO) {
        sendTo.add(userInfoDTO.id!!)
    }

    override fun onRemove(userInfoDTO: UserInfoDTO) {
        sendTo.remove(userInfoDTO.id!!)
    }
}