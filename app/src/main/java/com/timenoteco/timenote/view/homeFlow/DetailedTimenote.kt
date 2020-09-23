package com.timenoteco.timenote.view.homeFlow

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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
import com.timenoteco.timenote.common.RoundedCornersTransformation
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.viewModel.CommentViewModel
import com.timenoteco.timenote.viewModel.FollowViewModel
import com.timenoteco.timenote.viewModel.TimenoteViewModel
import kotlinx.android.synthetic.main.fragment_detailed_fragment.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.friends_search.view.*
import kotlinx.android.synthetic.main.item_timenote_root.*
import kotlinx.android.synthetic.main.users_participating.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.Type


class DetailedTimenote : Fragment(), View.OnClickListener, CommentAdapter.CommentPicUserListener,
    CommentAdapter.CommentMoreListener, UsersPagingAdapter.SearchPeopleListener {

    private lateinit var handler: Handler
    private val TRIGGER_AUTO_COMPLETE = 200
    private val AUTO_COMPLETE_DELAY: Long = 200
    private lateinit var userInfoDTO: UserInfoDTO
    private lateinit var prefs: SharedPreferences
    private lateinit var commentAdapter: CommentPagingAdapter
    val TOKEN: String = "TOKEN"
    private val commentViewModel: CommentViewModel by activityViewModels()
    private val followViewModel: FollowViewModel by activityViewModels()
    private val timenoteViewModel: TimenoteViewModel by activityViewModels()
    private var tokenId: String? = null
    private lateinit var screenSlideCreationTimenotePagerAdapter : ScreenSlideTimenotePagerAdapter
    private val args : DetailedTimenoteArgs by navArgs()
    private val utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(TOKEN, null)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
         inflater.inflate(R.layout.fragment_detailed_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
        userInfoDTO = Gson().fromJson<UserInfoDTO>(prefs.getString("UserInfoDTO", ""), typeUserInfo)

        timenote_see_more.visibility = View.GONE
        comments_edittext.requestFocus()

        commentAdapter = CommentPagingAdapter(CommentComparator, this, this)

        detailed_timenote_comments_rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }

        lifecycleScope.launch {
            commentViewModel.getComments(tokenId!!, args.event?.id!!).collectLatest {
                commentAdapter.submitData(it)
            }
        }

        timenote_title.text = args.event?.title

        screenSlideCreationTimenotePagerAdapter = if(!args.event?.pictures.isNullOrEmpty()){
            ScreenSlideTimenotePagerAdapter(this, args.event?.pictures, true){ i: Int, i1: Int -> }
        } else {
            ScreenSlideTimenotePagerAdapter(this, mutableListOf("https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"), true){ i: Int, i1: Int -> }
        }


        timenote_vp.adapter = screenSlideCreationTimenotePagerAdapter
        timenote_indicator.setViewPager(timenote_vp)
        screenSlideCreationTimenotePagerAdapter.registerAdapterDataObserver(timenote_indicator.adapterDataObserver)

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

        timenote_year.text = utils.setYear(args.event?.startingAt!!)
        timenote_day_month.text = utils.setFormatedStartDate(args.event?.startingAt!!, args.event?.endingAt!!)
        timenote_time.text = utils.setFormatedEndDate(args.event?.startingAt!!, args.event?.endingAt!!)

        var addedBy = ""
        var addedByFormated = SpannableStringBuilder(addedBy)
        val p = Typeface.create("sans-serif-light", Typeface.NORMAL)
        val m = Typeface.create("sans-serif", Typeface.NORMAL)
        val light = ItemTimenoteRecentAdapter.CustomTypefaceSpan(p)
        val bold = ItemTimenoteRecentAdapter.CustomTypefaceSpan(m)

        if(!args.event?.joinedBy?.users.isNullOrEmpty()){

            when {
                args.event?.joinedBy?.count == 1 -> addedBy = "Saved by ${args.event?.joinedBy?.users?.get(0)?.userName}"
                args.event?.joinedBy?.count in 1..20 -> addedBy = "Saved by ${args.event?.joinedBy?.users?.get(0)?.userName} and ${args.event?.joinedBy?.count!! - 1} other people"
                args.event?.joinedBy?.count in 21..100 -> addedBy = "Saved by ${args.event?.joinedBy?.users?.get(0)?.userName} and tens of other people"
                args.event?.joinedBy?.count in 101..2000 -> addedBy = "Saved by ${args.event?.joinedBy?.users?.get(0)?.userName} and hundreds of other people"
                args.event?.joinedBy?.count in 2001..2000000 -> addedBy = "Saved by ${args.event?.joinedBy?.users?.get(0)?.userName} and thousands of other people"
                args.event?.joinedBy?.count!! > 2000000 -> addedBy = "Saved by ${args.event?.joinedBy?.users?.get(0)?.userName} and millions of other people"
            }

            addedByFormated = SpannableStringBuilder(addedBy)
            addedByFormated.setSpan(light, 0, 8, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            addedByFormated.setSpan(bold, 9, addedBy.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

            when (args.event?.joinedBy?.users?.size) {
                1 -> {
                    Glide
                        .with(requireContext())
                        .load(args.event?.joinedBy?.users!![0].picture)
                        .apply(RequestOptions.circleCropTransform())
                        .into(timenote_pic_participant_three)
                    timenote_pic_participant_two.visibility = View.GONE
                    timenote_pic_participant_one.visibility = View.GONE
                }
                2 -> {
                    Glide
                        .with(requireContext())
                        .load(args.event?.joinedBy?.users!![0].picture)
                        .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(context, 90, 0, getString(0 + R.color.colorBackground), 4)))
                        .into(timenote_pic_participant_two)

                    Glide
                        .with(requireContext())
                        .load(args.event?.joinedBy?.users!![1].picture)
                        .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(context, 90, 0, getString(0 + R.color.colorBackground), 4)))
                        .into(timenote_pic_participant_two)
                    timenote_pic_participant_one.visibility = View.GONE
                }
                else -> {
                    Glide
                        .with(requireContext())
                        .load(args.event?.joinedBy?.users!![0].picture)
                        .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(context, 90, 0,getString(0 + R.color.colorBackground), 4)))
                        .into(timenote_pic_participant_one)

                    Glide
                        .with(requireContext())
                        .load(args.event?.joinedBy?.users!![1].picture)
                        .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(context, 90, 0, getString(0 + R.color.colorBackground), 4)))
                        .into(timenote_pic_participant_two)

                    Glide
                        .with(requireContext())
                        .load(args.event?.joinedBy?.users!![3].picture)
                        .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(context, 90, 0, getString(0 + R.color.colorBackground), 4)))
                        .into(timenote_pic_participant_three)
                }
            }
        }
        else {
            timenote_pic_participant_one.visibility = View.GONE
            timenote_pic_participant_two.visibility = View.GONE
            timenote_pic_participant_three.visibility = View.GONE
            timenote_fl.visibility = View.GONE
        }

        timenote_added_by.text = addedByFormated

        if(args.event?.hashtags.isNullOrEmpty() && args.event?.description.isNullOrBlank()){
            timenote_username_desc.visibility = View.GONE
            if(args.event?.joinedBy?.count == 0){
                timenote_see_more.visibility = View.GONE
            }
        } else if(args.event?.hashtags.isNullOrEmpty() && !args.event?.description.isNullOrBlank()){
            timenote_username_desc.text = args.event?.description
        } else if(!args.event?.hashtags.isNullOrEmpty() && args.event?.description.isNullOrBlank()){
            val hashtags = SpannableStringBuilder(args.event?.hashtags?.joinToString(separator = ""))
            hashtags.setSpan(bold, 0, hashtags.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            timenote_username_desc.text = hashtags
        } else {
            val hashtags = SpannableStringBuilder(args.event?.hashtags?.joinToString(separator = ""))
            val completeDesc = SpannableStringBuilder(args.event?.hashtags?.joinToString(separator = "")).append(" ${args.event?.description}")
            completeDesc.setSpan(bold, 0, hashtags.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            completeDesc.setSpan(light, hashtags.length, completeDesc.toString().length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            timenote_username_desc.text = completeDesc
        }

        Glide
            .with(this)
            .load(args.event?.createdBy?.picture)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.circle_pic)
            .into(detailed_timenote_pic_user)

        detailed_timenote_username.text = args.event?.createdBy?.userName

        timenote_comment.setOnClickListener(this)
        timenote_detailed_send_comment.setOnClickListener(this)
        detailed_timenote_btn_more.setOnClickListener(this)
        timenote_share.setOnClickListener(this)
        timenote_plus.setOnClickListener(this)
        timenote_fl.setOnClickListener(this)

        detailed_timenote_btn_back.setOnClickListener { findNavController().popBackStack() }
    }

    override fun onClick(v: View?) {
        when(v){
            timenote_comment -> {
                comments_edittext.requestFocus()
                val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.showSoftInput(comments_edittext, InputMethodManager.SHOW_IMPLICIT)
            }
            detailed_timenote_btn_more -> createOptionsOnTimenote(requireContext(), false)
            timenote_detailed_send_comment -> commentViewModel.postComment(tokenId!!, CommentCreationDTO(userInfoDTO.id!!, args.event?.id!!, comments_edittext.text.toString(), "#ok")).observe(viewLifecycleOwner, Observer {
                    if (it.isSuccessful){
                        val view = requireActivity().currentFocus
                        view?.let { v ->
                            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                            imm?.hideSoftInputFromWindow(v.windowToken, 0)
                        }
                        comments_edittext.text.clear()

                        lifecycleScope.launch {
                            commentViewModel.getComments(tokenId!!, args.event?.id!!).collectLatest { data ->
                                commentAdapter.submitData(data)
                            }
                        }
                    }
                })
            timenote_share -> {
                val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
                    customView(R.layout.friends_search)
                    lifecycleOwner(this@DetailedTimenote)
                }

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
                val userAdapter = UsersPagingAdapter(UsersPagingAdapter.UserComparator, args.event, this)
                recyclerview.layoutManager = LinearLayoutManager(requireContext())
                recyclerview.adapter = userAdapter
                lifecycleScope.launch{
                    followViewModel.getUsers(tokenId!!, false).collectLatest {
                        userAdapter.submitData(it)
                    }
                }
            }
            timenote_plus ->
                if(false)timenoteViewModel.leaveTimenote(tokenId!!, args.event?.id!!).observe(viewLifecycleOwner, Observer {})else
                timenoteViewModel.joinTimenote(tokenId!!, args.event?.id!!).observe(viewLifecycleOwner, Observer {})
            timenote_fl -> {
                val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    customView(R.layout.users_participating)
                    lifecycleOwner(this@DetailedTimenote)
                }

                val recyclerview = dial.getCustomView().users_participating_rv
                val userAdapter = UsersPagingAdapter(UsersPagingAdapter.UserComparator, args.event, this)
                recyclerview.layoutManager = LinearLayoutManager(requireContext())
                recyclerview.adapter = userAdapter
                lifecycleScope.launch{
                    timenoteViewModel.getUsersParticipating(tokenId!!, args.event?.id!!).collectLatest {
                        userAdapter.submitData(it)
                    }
                }
            }
        }
    }

    private fun createOptionsOnTimenote(context: Context, isMine: Boolean){
        val listItems: MutableList<String>
        if(isMine) listItems = mutableListOf(context.getString(R.string.duplicate), context.getString(R.string.alarm), context.getString(R.string.report))
        else listItems = mutableListOf(context.getString(R.string.duplicate), context.getString(R.string.delete), context.getString(R.string.alarm),  context.getString(R.string.mask_user))
        MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.posted_false)
            listItems (items = listItems){ dialog, index, text ->
                when(text.toString()){
                    //context.getString(R.string.duplicate) -> findNavController().navigate(DetailedTimenoteDirections.actionDetailedTimenoteToCreateTimenote(true, "", CreationTimenoteDTO(), args.from))
                    context.getString(R.string.report) -> Toast.makeText(
                        requireContext(),
                        "Reported. Thank You.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    context.getString(R.string.alarm) -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        dateTimePicker { dialog, datetime ->

                        }
                        lifecycleOwner(this@DetailedTimenote)
                    }
                }
            }
        }
    }

    override fun onPicUserCommentClicked() {
        findNavController().navigate(DetailedTimenoteDirections.actionDetailedTimenoteToProfile().setWhereFrom(true).setFrom(args.from))
    }

    override fun onCommentMoreClicked() {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            listItems (items = listOf(getString(R.string.report))){ dialog, index, text ->  }
            lifecycleOwner(this@DetailedTimenote)
        }
    }

    override fun onSearchClicked(userInfoDTO: UserInfoDTO, timenoteInfoDTO: TimenoteInfoDTO?) {

    }

}