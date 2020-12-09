package com.timenoteco.timenote.view.searchFlow

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
import com.afollestad.materialdialogs.list.listItems
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.*
import com.timenoteco.timenote.common.HashTagHelper
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.common.bytesEqualTo
import com.timenoteco.timenote.common.pixelsEqualTo
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.viewModel.CommentViewModel
import com.timenoteco.timenote.viewModel.FollowViewModel
import com.timenoteco.timenote.viewModel.LoginViewModel
import com.timenoteco.timenote.viewModel.TimenoteViewModel
import kotlinx.android.synthetic.main.fragment_detailed_fragment.*
import kotlinx.android.synthetic.main.friends_search_cl.view.*
import kotlinx.android.synthetic.main.item_timenote_root.*
import kotlinx.android.synthetic.main.users_participating.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import java.text.SimpleDateFormat


class DetailedTimenoteSearch : Fragment(), View.OnClickListener, UsersPagingAdapter.SearchPeopleListener,
    CommentAdapter.CommentPicUserListener, CommentAdapter.CommentMoreListener,
    UsersShareWithPagingAdapter.SearchPeopleListener, UsersShareWithPagingAdapter.AddToSend {

    private lateinit var imm: InputMethodManager
    private var sendTo: MutableList<String> = mutableListOf()
    private lateinit var handler: Handler
    private val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private val TRIGGER_AUTO_COMPLETE = 200
    private val AUTO_COMPLETE_DELAY: Long = 200
    private lateinit var userInfoDTO: UserInfoDTO
    private val followViewModel: FollowViewModel by activityViewModels()
    private val timenoteViewModel: TimenoteViewModel by activityViewModels()
    private lateinit var prefs: SharedPreferences
    private lateinit var commentAdapter: CommentPagingAdapter
    private val commentViewModel: CommentViewModel by activityViewModels()
    private val authViewModel: LoginViewModel by activityViewModels()
    private lateinit var screenSlideCreationTimenotePagerAdapter : ScreenSlideTimenotePagerAdapter
    private val args : DetailedTimenoteSearchArgs by navArgs()
    private var tokenId: String? = null
    private val utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return inflater.inflate(R.layout.fragment_detailed_fragment, container, false)}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        imm = (requireActivity().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)!!
        val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
        userInfoDTO = Gson().fromJson<UserInfoDTO>(prefs.getString("UserInfoDTO", ""), typeUserInfo)

        timenote_username_desc.maxLines = Int.MAX_VALUE
        timenote_comment_account.visibility = View.GONE
        comments_edittext.requestFocus()

        commentAdapter = CommentPagingAdapter(CommentComparator, this, this)

        detailed_timenote_comments_rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }

        lifecycleScope.launch {
            commentViewModel.getComments(tokenId!!, args.timenoteInfoDTO?.id!!, prefs).collectLatest {
                commentAdapter.submitData(it)
            }
        }

        timenote_title.text = args.timenoteInfoDTO?.title
        if(args.timenoteInfoDTO?.pictures?.size == 1 || args.timenoteInfoDTO?.pictures.isNullOrEmpty()) timenote_indicator.visibility = View.GONE


        screenSlideCreationTimenotePagerAdapter = ScreenSlideTimenotePagerAdapter(this, if (args.timenoteInfoDTO?.pictures.isNullOrEmpty()) listOf(args.timenoteInfoDTO?.colorHex!!) else args.timenoteInfoDTO?.pictures, true, args.timenoteInfoDTO?.pictures.isNullOrEmpty()) { i: Int, i1: Int ->
            if(i1 == 0){
                if (args.timenoteInfoDTO?.price?.price!! >= 0 && !args.timenoteInfoDTO?.url.isNullOrBlank()) {
                    timenote_buy_cl.visibility = View.VISIBLE
                    if (args.timenoteInfoDTO?.price?.price!! > 0) timenote_buy.text = args.timenoteInfoDTO?.price?.price!!.toString().plus(args.timenoteInfoDTO?.price?.currency ?: "$") }
            } else {
                if (userInfoDTO.id != args.timenoteInfoDTO?.createdBy?.id) {
                    if (timenote_plus.drawable.bytesEqualTo(resources.getDrawable(R.drawable.ic_ajout_cal)) && timenote_plus.drawable.pixelsEqualTo(
                            resources.getDrawable(R.drawable.ic_ajout_cal)
                        )
                    ) {
                        timenote_plus.setImageDrawable(resources.getDrawable(R.drawable.ic_ajout_cal_plein_gradient))
                        timenoteViewModel.joinTimenote(tokenId!!, args.timenoteInfoDTO?.id!!)
                            .observe(
                                viewLifecycleOwner,
                                Observer {
                                    if (it.code() == 401) {
                                        authViewModel.refreshToken(prefs).observe(
                                            viewLifecycleOwner,
                                            Observer { newAccessToken ->
                                                tokenId = newAccessToken
                                                timenoteViewModel.joinTimenote(
                                                    tokenId!!,
                                                    args.timenoteInfoDTO?.id!!
                                                )
                                            })
                                    }
                                })
                    } else {
                        timenote_plus.setImageDrawable(resources.getDrawable(R.drawable.ic_ajout_cal))
                        timenoteViewModel.leaveTimenote(tokenId!!, args.timenoteInfoDTO?.id!!)
                            .observe(viewLifecycleOwner, Observer {
                                if (it.code() == 401) {
                                    authViewModel.refreshToken(prefs)
                                        .observe(viewLifecycleOwner, Observer { newAccessToken ->
                                            tokenId = newAccessToken
                                            timenoteViewModel.leaveTimenote(
                                                tokenId!!,
                                                args.timenoteInfoDTO?.id!!
                                            )
                                        })
                                }
                            })
                    }
                }
            }
        }


        timenote_vp.adapter = screenSlideCreationTimenotePagerAdapter
        timenote_indicator.setViewPager(timenote_vp)
        screenSlideCreationTimenotePagerAdapter.registerAdapterDataObserver(timenote_indicator.adapterDataObserver)

        timenote_plus.setImageDrawable(resources.getDrawable(R.drawable.ic_ajout_cal))
        if(args.timenoteInfoDTO?.isParticipating!!|| userInfoDTO.id == args.timenoteInfoDTO?.createdBy?.id) timenote_plus.setImageDrawable(resources.getDrawable(R.drawable.ic_ajout_cal_plein_gradient))
        else timenote_plus.setImageDrawable(resources.getDrawable(R.drawable.ic_ajout_cal))

        timenote_year.text = utils.setYear(args.timenoteInfoDTO?.startingAt!!)
        timenote_day_month.text = utils.setFormatedStartDate(args.timenoteInfoDTO?.startingAt!!, args.timenoteInfoDTO?.endingAt!!)
        timenote_time.text = utils.setFormatedEndDate(args.timenoteInfoDTO?.startingAt!!, args.timenoteInfoDTO?.endingAt!!)

        var addedBy = ""
        var addedByFormated = SpannableStringBuilder(addedBy)
        val p = Typeface.create("sans-serif-light", Typeface.NORMAL)
        val m = Typeface.create("sans-serif", Typeface.NORMAL)
        val light = ItemTimenoteRecentAdapter.CustomTypefaceSpan(p)
        val bold = ItemTimenoteRecentAdapter.CustomTypefaceSpan(m)

        if(!args.timenoteInfoDTO?.joinedBy?.users.isNullOrEmpty()){

            when {
                args.timenoteInfoDTO?.joinedBy?.count == 1 -> addedBy = "Saved by ${args.timenoteInfoDTO?.joinedBy?.users?.get(0)?.userName}"
                args.timenoteInfoDTO?.joinedBy?.count in 1..20 -> addedBy = "Saved by ${args.timenoteInfoDTO?.joinedBy?.users?.get(0)?.userName} and ${args.timenoteInfoDTO?.joinedBy?.count!! - 1} other people"
                args.timenoteInfoDTO?.joinedBy?.count in 21..100 -> addedBy = "Saved by ${args.timenoteInfoDTO?.joinedBy?.users?.get(0)?.userName} and tens of other people"
                args.timenoteInfoDTO?.joinedBy?.count in 101..2000 -> addedBy = "Saved by ${args.timenoteInfoDTO?.joinedBy?.users?.get(0)?.userName} and hundreds of other people"
                args.timenoteInfoDTO?.joinedBy?.count in 2001..2000000 -> addedBy = "Saved by ${args.timenoteInfoDTO?.joinedBy?.users?.get(0)?.userName} and thousands of other people"
                args.timenoteInfoDTO?.joinedBy?.count!! > 2000000 -> addedBy = "Saved by ${args.timenoteInfoDTO?.joinedBy?.users?.get(0)?.userName} and millions of other people"
            }

            addedByFormated = SpannableStringBuilder(addedBy)
            addedByFormated.setSpan(light, 0, 8, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            addedByFormated.setSpan(bold, 9, addedBy.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            timenote_added_by.text = addedByFormated

            when (args.timenoteInfoDTO?.joinedBy?.users?.size) {
                1 -> {
                    Glide
                        .with(requireContext())
                        .load(args.timenoteInfoDTO?.joinedBy?.users!![0].picture)
                        .apply(RequestOptions.circleCropTransform())
                        .into(timenote_pic_participant_three)
                    timenote_pic_participant_two_rl.visibility = View.GONE
                    timenote_pic_participant_one_rl.visibility = View.GONE
                }
                2 -> {
                    Glide
                        .with(requireContext())
                        .load(args.timenoteInfoDTO?.joinedBy?.users!![0].picture)
                        .apply(RequestOptions.circleCropTransform())
                        .into(timenote_pic_participant_two)

                    Glide
                        .with(requireContext())
                        .load(args.timenoteInfoDTO?.joinedBy?.users!![1].picture)
                        .apply(RequestOptions.circleCropTransform())
                        .into(timenote_pic_participant_three)
                    timenote_pic_participant_one_rl.visibility = View.GONE
                }
                else -> {
                    Glide
                        .with(requireContext())
                        .load(args.timenoteInfoDTO?.joinedBy?.users!![0].picture)
                        .apply(RequestOptions.circleCropTransform())
                        .into(timenote_pic_participant_one)

                    Glide
                        .with(requireContext())
                        .load(args.timenoteInfoDTO?.joinedBy?.users!![1].picture)
                        .apply(RequestOptions.circleCropTransform())
                        .into(timenote_pic_participant_two)

                    Glide
                        .with(requireContext())
                        .load(args.timenoteInfoDTO?.joinedBy?.users!![2].picture)
                        .apply(RequestOptions.circleCropTransform())
                        .into(timenote_pic_participant_three)
                }
            }
        }
        else {
            if(args.timenoteInfoDTO?.joinedBy?.count!! > 0){
                addedBy = "Saved by ${args.timenoteInfoDTO?.joinedBy?.count!!} people"
                val addedByFormated = SpannableStringBuilder(addedBy)
                addedByFormated.setSpan(light, 0, 8, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                addedByFormated.setSpan(bold, 9, addedBy.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                timenote_added_by.text = addedByFormated
                timenote_pic_participant_two_rl.visibility = View.GONE
                timenote_pic_participant_three_rl.visibility = View.GONE
                timenote_pic_participant_one_rl.visibility = View.GONE
            } else {
                timenote_pic_participant_three_rl.visibility = View.GONE
                timenote_pic_participant_two_rl.visibility = View.GONE
                timenote_pic_participant_one_rl.visibility = View.GONE
                timenote_fl.visibility = View.GONE
            }
        }

        timenote_added_by.text = addedByFormated

        val hashTagHelper = HashTagHelper.Creator.create(R.color.colorAccent, object : HashTagHelper.OnHashTagClickListener{
            override fun onHashTagClicked(hashTag: String?) {
                findNavController().navigate(DetailedTimenoteSearchDirections.actionGlobalTimenoteTAGSearch(hashTag))
            }

        }, null, resources)
        hashTagHelper.handle(timenote_username_desc)

        if(args.timenoteInfoDTO?.description.isNullOrBlank()){
            timenote_username_desc.visibility = View.GONE
        } else {
            val description = "${args.timenoteInfoDTO?.createdBy?.userName} ${args.timenoteInfoDTO?.description}"
            val descriptionFormatted = SpannableStringBuilder(description)
            descriptionFormatted.setSpan(bold, 0, args.timenoteInfoDTO?.createdBy?.userName?.length!!, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            descriptionFormatted.setSpan(light, args.timenoteInfoDTO?.createdBy?.userName?.length!!, description.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            timenote_username_desc.text = descriptionFormatted
        }

        /*val username = SpannableStringBuilder(args.timenoteInfoDTO?.createdBy?.userName)
        username.setSpan(bold, 0, args.timenoteInfoDTO?.createdBy?.userName?.length!!, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        if(args.timenoteInfoDTO?.hashtags.isNullOrEmpty() && args.timenoteInfoDTO?.description.isNullOrBlank()){
            timenote_username_desc.visibility = View.GONE
        } else if(args.timenoteInfoDTO?.hashtags.isNullOrEmpty() && !args.timenoteInfoDTO?.description.isNullOrBlank()){
            val desc = SpannableStringBuilder(args.timenoteInfoDTO?.description)
            desc.setSpan(light, 0, args.timenoteInfoDTO?.description?.length!!, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            timenote_username_desc.text = username.append(" ").append(desc)
        } else if(!args.timenoteInfoDTO?.hashtags.isNullOrEmpty() && args.timenoteInfoDTO?.description.isNullOrBlank()){
            val hashtags = SpannableStringBuilder(args.timenoteInfoDTO?.hashtags?.joinToString(separator = ""))
            hashtags.setSpan(bold, 0, hashtags.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            timenote_username_desc.text = username.append(" ").append(hashtags)
        } else {
            val hashtags = SpannableStringBuilder(args.timenoteInfoDTO?.hashtags?.joinToString(separator = ""))
            val completeDesc = SpannableStringBuilder(args.timenoteInfoDTO?.hashtags?.joinToString(separator = "")).append(" ${args.timenoteInfoDTO?.description}")
            completeDesc.setSpan(bold, 0, hashtags.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            completeDesc.setSpan(light, hashtags.length, completeDesc.toString().length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            timenote_username_desc.text = username.append(" ").append(completeDesc)
        }*/

        Glide
            .with(this)
            .load(args.timenoteInfoDTO?.createdBy?.picture)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.circle_pic)
            .into(detailed_timenote_pic_user)

        detailed_timenote_username.text = args.timenoteInfoDTO?.createdBy?.userName

        timenote_comment.setOnClickListener(this)
        timenote_detailed_send_comment.setOnClickListener(this)
        detailed_timenote_btn_more.setOnClickListener(this)
        timenote_share.setOnClickListener(this)
        timenote_plus.setOnClickListener(this)
        timenote_fl.setOnClickListener(this)
        timenote_buy_cl.setOnClickListener(this)
        timenote_day_month.setOnClickListener(this)
        timenote_year.setOnClickListener(this)
        timenote_time.setOnClickListener(this)
        separator_1.setOnClickListener(this)
        separator_2.setOnClickListener(this)
        timenote_in_label.setOnClickListener(this)

        detailed_timenote_btn_back.setOnClickListener { findNavController().popBackStack() }
    }

    private fun createOptionsOnTimenote(context: Context, timenoteInfoDTO: TimenoteInfoDTO){
        val listItems: MutableList<String> = mutableListOf(context.getString(R.string.duplicate), context.getString(R.string.alarm), context.getString(R.string.report))
        MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.posted_false)
            listItems (items = listItems){ dialog, index, text ->
                when(index){
                    2 -> Toast.makeText(
                        requireContext(),
                        "Reported, thank you",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    1 -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        dateTimePicker { dialog, datetime ->

                        }
                        lifecycleOwner(this@DetailedTimenoteSearch)
                    }
                    0 -> {
                        findNavController().navigate(DetailedTimenoteSearchDirections.actionGlobalCreateTimenoteSearch(1, "", CreationTimenoteDTO(timenoteInfoDTO.createdBy.id!!, null, timenoteInfoDTO.title, timenoteInfoDTO.description, timenoteInfoDTO.pictures,
                            timenoteInfoDTO.colorHex, timenoteInfoDTO.location, timenoteInfoDTO.category, timenoteInfoDTO.startingAt, timenoteInfoDTO.endingAt,
                            timenoteInfoDTO.hashtags, timenoteInfoDTO.url, timenoteInfoDTO.price, null), 2))
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when(v){
            timenote_comment -> {
                comments_edittext.requestFocus()
                imm.showSoftInput(comments_edittext, InputMethodManager.SHOW_IMPLICIT)
            }
            comments_edittext -> {
                comments_edittext.requestFocus()
                imm.showSoftInput(comments_edittext, InputMethodManager.SHOW_IMPLICIT)
            }
            detailed_timenote_btn_more -> createOptionsOnTimenote(requireContext(), args.timenoteInfoDTO!!)
            timenote_detailed_send_comment -> commentViewModel.postComment(tokenId!!, CommentCreationDTO(userInfoDTO.id!!, args.timenoteInfoDTO?.id!!, comments_edittext.text.toString(), "#ok")).observe(viewLifecycleOwner, Observer {
            if (it.isSuccessful){
                comments_edittext.clearFocus()
                imm.hideSoftInputFromWindow(comments_edittext.windowToken, 0)
                comments_edittext.text.clear()

                lifecycleScope.launch {
                    commentViewModel.getComments(tokenId!!, args.timenoteInfoDTO?.id!!, prefs).collectLatest { data ->
                        commentAdapter.submitData(data)
                    }
                }
            }
        })
            timenote_share -> {
                sendTo.clear()
                val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
                    customView(R.layout.friends_search_cl)
                    lifecycleOwner(this@DetailedTimenoteSearch)
                    positiveButton(R.string.send){
                        timenoteViewModel.shareWith(tokenId!!, ShareTimenoteDTO(args.timenoteInfoDTO?.id!!, sendTo)).observe(viewLifecycleOwner, Observer {
                            if(it.code() == 401) {
                                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer {newAccessToken ->
                                    tokenId = newAccessToken
                                    timenoteViewModel.shareWith(tokenId!!, ShareTimenoteDTO(args.timenoteInfoDTO?.id!!, sendTo))
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
            timenote_plus ->
                if(userInfoDTO.id != args.timenoteInfoDTO?.createdBy?.id) {
                    if (timenote_plus.drawable.bytesEqualTo(resources.getDrawable(R.drawable.ic_ajout_cal)) && timenote_plus.drawable.pixelsEqualTo(
                            resources.getDrawable(R.drawable.ic_ajout_cal)
                        )
                    ) {
                        timenote_plus.setImageDrawable(resources.getDrawable(R.drawable.ic_ajout_cal_plein_gradient))
                        timenoteViewModel.joinTimenote(tokenId!!, args.timenoteInfoDTO?.id!!)
                            .observe(
                                viewLifecycleOwner,
                                Observer {
                                    if (it.code() == 401) {
                                        authViewModel.refreshToken(prefs).observe(
                                            viewLifecycleOwner,
                                            Observer { newAccessToken ->
                                                tokenId = newAccessToken
                                                timenoteViewModel.joinTimenote(
                                                    tokenId!!,
                                                    args.timenoteInfoDTO?.id!!
                                                )
                                            })
                                    }
                                })
                    } else {
                        timenote_plus.setImageDrawable(resources.getDrawable(R.drawable.ic_ajout_cal))
                        timenoteViewModel.leaveTimenote(tokenId!!, args.timenoteInfoDTO?.id!!)
                            .observe(viewLifecycleOwner, Observer {
                                if (it.code() == 401) {
                                    authViewModel.refreshToken(prefs)
                                        .observe(viewLifecycleOwner, Observer { newAccessToken ->
                                            tokenId = newAccessToken
                                            timenoteViewModel.leaveTimenote(
                                                tokenId!!,
                                                args.timenoteInfoDTO?.id!!
                                            )
                                        })
                                }
                            })
                    }
                }
            timenote_fl -> {
                val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
                    customView(R.layout.users_participating)
                    lifecycleOwner(this@DetailedTimenoteSearch)
                }

                val recyclerview = dial.getCustomView().users_participating_rv
                val userAdapter = UsersPagingAdapter(
                    UsersPagingAdapter.UserComparator,
                    args.timenoteInfoDTO,
                    this,
                    null,
                    null
                )
                recyclerview.layoutManager = LinearLayoutManager(requireContext())
                recyclerview.adapter = userAdapter
                lifecycleScope.launch{
                    timenoteViewModel.getUsersParticipating(tokenId!!, args.timenoteInfoDTO?.id!!, prefs).collectLatest {
                        userAdapter.submitData(it)
                    }
                }
            }
            timenote_buy_cl -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(if (args.timenoteInfoDTO?.url?.contains("https://")!!) args.timenoteInfoDTO?.url else "https://" + args.timenoteInfoDTO?.url)
                startActivity(i)
            }
            timenote_day_month -> showInTime(utils, args.timenoteInfoDTO!!)
            timenote_year -> showInTime(utils, args.timenoteInfoDTO!!)
            timenote_time ->  showInTime(utils, args.timenoteInfoDTO!!)
            separator_1 -> showInTime(utils, args.timenoteInfoDTO!!)
            separator_2 -> showInTime(utils, args.timenoteInfoDTO!!)

            timenote_in_label -> {
                separator_1.visibility = View.VISIBLE
                separator_2.visibility = View.VISIBLE
                timenote_day_month.visibility = View.VISIBLE
                timenote_time.visibility = View.VISIBLE
                timenote_year.visibility = View.VISIBLE
                timenote_in_label.visibility = View.INVISIBLE
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun showInTime(
        utils: Utils,
        timenote: TimenoteInfoDTO
    ) {
        separator_1.visibility = View.INVISIBLE
        separator_2.visibility = View.INVISIBLE
        timenote_day_month.visibility = View.INVISIBLE
        timenote_time.visibility = View.INVISIBLE
        timenote_year.visibility = View.INVISIBLE
        timenote_in_label.visibility = View.VISIBLE
        if (SimpleDateFormat(ISO).parse(timenote.endingAt).time < System.currentTimeMillis()) timenote_in_label.text = utils.inTime(timenote.startingAt)
        else timenote_in_label.text = utils.sinceTime(timenote.endingAt)
    }

    override fun onCommentMoreClicked(createdBy: String?, commentId: String?) {
        val actionsComment : List<String> = if(userInfoDTO.id == createdBy || args.timenoteInfoDTO?.createdBy?.id == userInfoDTO.id)listOf(getString(R.string.delete))
        else listOf(getString(R.string.report))
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            listItems (items = actionsComment){ _, _, text ->
                when(text){
                    getString(R.string.delete)  -> commentViewModel.deleteComment(tokenId!!, commentId!!).observe(viewLifecycleOwner, Observer {
                        if(it.code() == 401){
                            authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer { newAccessToken ->
                                tokenId = newAccessToken
                                commentViewModel.deleteComment(tokenId!!, commentId).observe(viewLifecycleOwner, Observer { resp ->
                                    if(resp.isSuccessful) commentAdapter.refresh()
                                })
                            })
                        }
                        if(it.isSuccessful) commentAdapter.refresh()
                    })

                    getString(R.string.report) -> Toast.makeText(
                        requireContext(),
                        "Reported",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            lifecycleOwner(this@DetailedTimenoteSearch)
        }
    }

    override fun onSearchClicked(userInfoDTO: UserInfoDTO) {
    }

    override fun onUnfollow(id: String) {

    }

    override fun onRemove(id: String) {
    }

    override fun onPicUserCommentClicked(userInfoDTO: UserInfoDTO) {
        findNavController().navigate(DetailedTimenoteSearchDirections.actionGlobalProfileSearch(args.timenoteInfoDTO?.createdBy))
    }

    override fun onAdd(userInfoDTO: UserInfoDTO, createGroup: Int?) {
        sendTo.add(userInfoDTO.id!!)
    }

    override fun onRemove(userInfoDTO: UserInfoDTO, createGroup: Int?) {
        sendTo.remove(userInfoDTO.id!!)
    }

}