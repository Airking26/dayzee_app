package com.timenoteco.timenote.view.homeFlow

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
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SnapHelper
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
import com.timenoteco.timenote.androidView.carousel.CarouselAdapter
import com.timenoteco.timenote.androidView.carousel.GridAdapter
import com.timenoteco.timenote.common.HashTagHelper
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.common.bytesEqualTo
import com.timenoteco.timenote.common.pixelsEqualTo
import com.timenoteco.timenote.listeners.GoToProfile
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.viewModel.CommentViewModel
import com.timenoteco.timenote.viewModel.FollowViewModel
import com.timenoteco.timenote.viewModel.LoginViewModel
import com.timenoteco.timenote.viewModel.TimenoteViewModel
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.BranchEvent
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.activity_grid.*
import kotlinx.android.synthetic.main.fragment_detailed_fragment.*
import kotlinx.android.synthetic.main.friends_search_cl.view.*
import kotlinx.android.synthetic.main.item_timenote_root.*
import kotlinx.android.synthetic.main.users_participating.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import kotlin.time.ExperimentalTime


class DetailedTimenoteCarousel : Fragment(), View.OnClickListener, CommentAdapter.CommentPicUserListener,
    CommentAdapter.CommentMoreListener, UsersPagingAdapter.SearchPeopleListener,
    UsersShareWithPagingAdapter.SearchPeopleListener, UsersShareWithPagingAdapter.AddToSend {


    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: CarouselAdapter
    private lateinit var snapHelper: SnapHelper

    private lateinit var imm: InputMethodManager
    private lateinit var goToProfileLisner : GoToProfile
    private var sendTo: MutableList<String> = mutableListOf()
    private lateinit var handler: Handler
    private val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private val TRIGGER_AUTO_COMPLETE = 200
    private val AUTO_COMPLETE_DELAY: Long = 200
    private lateinit var userInfoDTO: UserInfoDTO
    private lateinit var prefs: SharedPreferences
    private lateinit var commentAdapter: CommentPagingAdapter
    private val commentViewModel: CommentViewModel by activityViewModels()
    private val followViewModel: FollowViewModel by activityViewModels()
    private val timenoteViewModel: TimenoteViewModel by activityViewModels()
    private val authViewModel: LoginViewModel by activityViewModels()
    private var tokenId: String? = null
    private lateinit var screenSlideCreationTimenotePagerAdapter : ScreenSlideTimenotePagerAdapter
    private val args : DetailedTimenoteCarouselArgs by navArgs()
    private val utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        goToProfileLisner = context as GoToProfile

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return inflater.inflate(R.layout.fragment_detailed_fragment_carousel, container, false)
    }

    private fun initRecyclerViewPosition(position: Int) {
        // This initial scroll will be slightly off because it doesn't respect the SnapHelper.
        // Do it anyway so that the target view is laid out, then adjust onPreDraw.
        layoutManager.scrollToPosition(position)

        recycler_view.doOnPreDraw {
            val targetView = layoutManager.findViewByPosition(position) ?: return@doOnPreDraw
            val distanceToFinalSnap =
                snapHelper.calculateDistanceToFinalSnap(layoutManager, targetView)
                    ?: return@doOnPreDraw

            layoutManager.scrollToPositionWithOffset(position, -distanceToFinalSnap[0])
        }
    }

    /** Display a random set of images each time */
    private fun reloadImages() {
        val images: ArrayList<CarouselImageModel> = ArrayList(CarouselImage.images.shuffled())
        recycler_view.swapAdapter(
            GridAdapter(
                images
            ) { _, position ->
                findNavController().navigate(DetailedTimenoteCarouselDirections.actionGlobalCarouselActivity())
            }, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        with(recycler_view) {
            layoutManager = this@DetailedTimenoteCarousel.layoutManager
            //addItemDecoration(GridSpacingDecoration(resources.getDimensionPixelSize(R.dimen.grid_spacing)))
            reloadImages()
        }

        /*layoutManager = ProminentLayoutManager(requireContext())
        adapter = CarouselAdapter(CarouselImage.images)
        snapHelper = PagerSnapHelper()

        with(recycler_view){
            setItemViewCacheSize(4)
            layoutManager = this@DetailedTimenoteCarousel.layoutManager
            adapter = this@DetailedTimenoteCarousel.adapter

            val spacing = resources.getDimensionPixelSize(R.dimen.carousel_spacing)
            addItemDecoration(LinearHorizontalSpacingDecoration(spacing))
            addItemDecoration(BoundsOffsetDecoration())

            snapHelper.attachToRecyclerView(this)
        }*/

        //initRecyclerViewPosition(0)

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
                commentViewModel.getComments(tokenId!!, args.event?.id!!, prefs).collectLatest {
                    commentAdapter.submitData(it)
                }
            }

            timenote_title.text = args.event?.title
            if (args.event?.pictures?.size == 1 || args.event?.pictures.isNullOrEmpty()) timenote_indicator.visibility =
                View.GONE


            /*screenSlideCreationTimenotePagerAdapter = ScreenSlideTimenotePagerAdapter(this, if (args.event?.pictures.isNullOrEmpty()) listOf(args.event?.colorHex!!) else args.event?.pictures, true, args.event?.pictures.isNullOrEmpty()) { i: Int, i1: Int ->
                if(i1 == 0){
                if (args.event?.price?.price!! >= 0 && !args.event?.url.isNullOrBlank()) {
                    timenote_buy_cl.visibility = View.VISIBLE
                    if (args.event?.price?.price!! > 0) timenote_buy.text = args.event?.price?.price!!.toString().plus(args.event?.price?.currency ?: "$") }
                } else {
                    if (userInfoDTO.id != args.event?.createdBy?.id) {
                        if (timenote_plus.drawable.bytesEqualTo(resources.getDrawable(R.drawable.ic_ajout_cal)) && timenote_plus.drawable.pixelsEqualTo(
                                resources.getDrawable(R.drawable.ic_ajout_cal)
                            )
                        ) {
                            timenote_plus.setImageDrawable(resources.getDrawable(R.drawable.ic_ajout_cal_plein_gradient))
                            timenoteViewModel.joinTimenote(tokenId!!, args.event?.id!!).observe(
                                viewLifecycleOwner,
                                Observer {
                                    if (it.code() == 401) {
                                        authViewModel.refreshToken(prefs).observe(
                                            viewLifecycleOwner,
                                            Observer { newAccessToken ->
                                                tokenId = newAccessToken
                                                timenoteViewModel.joinTimenote(
                                                    tokenId!!,
                                                    args.event?.id!!
                                                )
                                            })
                                    }
                                })
                        } else {
                            timenote_plus.setImageDrawable(resources.getDrawable(R.drawable.ic_ajout_cal))
                            timenoteViewModel.leaveTimenote(tokenId!!, args.event?.id!!)
                                .observe(viewLifecycleOwner, Observer {
                                    if (it.code() == 401) {
                                        authViewModel.refreshToken(prefs).observe(
                                            viewLifecycleOwner,
                                            Observer { newAccessToken ->
                                                tokenId = newAccessToken
                                                timenoteViewModel.leaveTimenote(
                                                    tokenId!!,
                                                    args.event?.id!!
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
            screenSlideCreationTimenotePagerAdapter.registerAdapterDataObserver(timenote_indicator.adapterDataObserver)*/

            timenote_plus.setImageDrawable(resources.getDrawable(R.drawable.ic_ajout_cal))
            if (args.event?.isParticipating!! || userInfoDTO.id == args.event?.createdBy?.id) timenote_plus.setImageDrawable(resources.getDrawable(R.drawable.ic_ajout_cal_plein_gradient))
            else timenote_plus.setImageDrawable(resources.getDrawable(R.drawable.ic_ajout_cal))

            timenote_year.text = utils.setYear(args.event?.startingAt!!)
            timenote_day_month.text =
                utils.setFormatedStartDate(args.event?.startingAt!!, args.event?.endingAt!!)
            timenote_time.text =
                utils.setFormatedEndDate(args.event?.startingAt!!, args.event?.endingAt!!)

            var addedBy = ""
            var addedByFormated = SpannableStringBuilder(addedBy)
            val p = Typeface.create("sans-serif-light", Typeface.NORMAL)
            val m = Typeface.create("sans-serif", Typeface.NORMAL)
            val light = ItemTimenoteRecentAdapter.CustomTypefaceSpan(p)
            val bold = ItemTimenoteRecentAdapter.CustomTypefaceSpan(m)

            if (!args.event?.joinedBy?.users.isNullOrEmpty()) {

                when {
                    args.event?.joinedBy?.count == 1 -> addedBy =
                        "Saved by ${args.event?.joinedBy?.users?.get(0)?.userName}"
                    args.event?.joinedBy?.count in 1..20 -> addedBy =
                        "Saved by ${args.event?.joinedBy?.users?.get(0)?.userName} and ${args.event?.joinedBy?.count!! - 1} other people"
                    args.event?.joinedBy?.count in 21..100 -> addedBy =
                        "Saved by ${args.event?.joinedBy?.users?.get(0)?.userName} and tens of other people"
                    args.event?.joinedBy?.count in 101..2000 -> addedBy =
                        "Saved by ${args.event?.joinedBy?.users?.get(0)?.userName} and hundreds of other people"
                    args.event?.joinedBy?.count in 2001..2000000 -> addedBy =
                        "Saved by ${args.event?.joinedBy?.users?.get(0)?.userName} and thousands of other people"
                    args.event?.joinedBy?.count!! > 2000000 -> addedBy =
                        "Saved by ${args.event?.joinedBy?.users?.get(0)?.userName} and millions of other people"
                }

                addedByFormated = SpannableStringBuilder(addedBy)
                addedByFormated.setSpan(light, 0, 8, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                addedByFormated.setSpan(bold, 9, addedBy.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                timenote_added_by.text = addedByFormated

                when (args.event?.joinedBy?.users?.size) {
                    1 -> {
                        Glide
                            .with(requireContext())
                            .load(args.event?.joinedBy?.users!![0].picture)
                            .apply(RequestOptions.circleCropTransform())
                            .into(timenote_pic_participant_three)
                        timenote_pic_participant_two_rl.visibility = View.GONE
                        timenote_pic_participant_one_rl.visibility = View.GONE
                    }
                    2 -> {
                        Glide
                            .with(requireContext())
                            .load(args.event?.joinedBy?.users!![0].picture)
                            .apply(RequestOptions.circleCropTransform())
                            .into(timenote_pic_participant_two)

                        Glide
                            .with(requireContext())
                            .load(args.event?.joinedBy?.users!![1].picture)
                            .apply(RequestOptions.circleCropTransform())
                            .into(timenote_pic_participant_three)
                        timenote_pic_participant_one_rl.visibility = View.GONE
                    }
                    else -> {
                        Glide
                            .with(requireContext())
                            .load(args.event?.joinedBy?.users!![0].picture)
                            .apply(RequestOptions.circleCropTransform())
                            .into(timenote_pic_participant_one)

                        Glide
                            .with(requireContext())
                            .load(args.event?.joinedBy?.users!![1].picture)
                            .apply(RequestOptions.circleCropTransform())
                            .into(timenote_pic_participant_two)

                        Glide
                            .with(requireContext())
                            .load(args.event?.joinedBy?.users!![2].picture)
                            .apply(RequestOptions.circleCropTransform())
                            .into(timenote_pic_participant_three)
                    }
                }
            } else {
                if(args.event?.joinedBy?.count!! > 0){
                    addedBy = "Saved by ${args.event?.joinedBy?.count!!} people"
                    addedByFormated = SpannableStringBuilder(addedBy)
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
                findNavController().navigate(DetailedTimenoteDirections.actionGlobalTimenoteTAG(args.event, hashTag).setFrom(args.from))
            }

        }, null, resources)
        hashTagHelper.handle(timenote_username_desc)

        if(args.event?.description.isNullOrBlank()){
            timenote_username_desc.visibility = View.GONE
        } else {
            val description = "${args.event?.createdBy?.userName} ${args.event?.description}"
            val descriptionFormatted = SpannableStringBuilder(description)
            descriptionFormatted.setSpan(bold, 0, args.event?.createdBy?.userName?.length!!, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            descriptionFormatted.setSpan(light, args.event?.createdBy?.userName?.length!!, description.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            timenote_username_desc.text = descriptionFormatted
        }

        /*val username = SpannableStringBuilder(args.event?.createdBy?.userName)
        username.setSpan(bold, 0, args.event?.createdBy?.userName?.length!!, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

            if (args.event?.hashtags.isNullOrEmpty() && args.event?.description.isNullOrBlank()) {
                timenote_username_desc.visibility = View.GONE
            } else if (args.event?.hashtags.isNullOrEmpty() && !args.event?.description.isNullOrBlank()) {
                val desc = SpannableStringBuilder(args.event?.description)
                desc.setSpan(light, 0, args.event?.description?.length!!, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                timenote_username_desc.text = username.append(" ").append(desc)
            } else if (!args.event?.hashtags.isNullOrEmpty() && args.event?.description.isNullOrBlank()) {
                val hashtags =
                    SpannableStringBuilder(args.event?.hashtags?.joinToString(separator = ""))
                hashtags.setSpan(bold, 0, hashtags.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                timenote_username_desc.text = username.append(" ").append(hashtags)
            } else {
                val hashtags =
                    SpannableStringBuilder(args.event?.hashtags?.joinToString(separator = ""))
                val completeDesc =
                    SpannableStringBuilder(args.event?.hashtags?.joinToString(separator = "")).append(
                        " ${args.event?.description}"
                    )
                completeDesc.setSpan(bold, 0, hashtags.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                completeDesc.setSpan(
                    light,
                    hashtags.length,
                    completeDesc.toString().length,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
                timenote_username_desc.text = username.append(" ").append(completeDesc)
            }*/

            Glide
                .with(this)
                .load(args.event?.createdBy?.picture)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.circle_pic)
                .into(detailed_timenote_pic_user)

            detailed_timenote_username.text = args.event?.createdBy?.userName

        comments_edittext.setOnClickListener(this)
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

    @ExperimentalTime
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
            detailed_timenote_btn_more -> createOptionsOnTimenote(requireContext(), userInfoDTO.id == args.event?.createdBy?.id)
            timenote_detailed_send_comment -> commentViewModel.postComment(tokenId!!, CommentCreationDTO(userInfoDTO.id!!, args.event?.id!!, comments_edittext.text.toString(), "#ok")).observe(viewLifecycleOwner, Observer {
                    if (it.isSuccessful){
                        comments_edittext.clearFocus()
                        imm.hideSoftInputFromWindow(comments_edittext.windowToken, 0)
                        comments_edittext.text.clear()

                        lifecycleScope.launch {
                            commentViewModel.getComments(tokenId!!, args.event?.id!!, prefs).collectLatest { data ->
                                commentAdapter.submitData(data)
                            }
                        }
                    }
                })
            timenote_share -> {
                sendTo.clear()
                val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
                    customView(R.layout.friends_search_cl)
                    lifecycleOwner(this@DetailedTimenoteCarousel)
                    positiveButton(R.string.send){
                        timenoteViewModel.shareWith(tokenId!!, ShareTimenoteDTO(args.event?.id!!, sendTo)).observe(viewLifecycleOwner, Observer {
                            if(it.code() == 401) {
                                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer {newAccessToken ->
                                    tokenId = newAccessToken
                                    timenoteViewModel.shareWith(tokenId!!, ShareTimenoteDTO(args.event?.id!!, sendTo))
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
            timenote_plus -> {
                if (userInfoDTO.id != args.event?.createdBy?.id) {
                    if (timenote_plus.drawable.bytesEqualTo(resources.getDrawable(R.drawable.ic_ajout_cal)) && timenote_plus.drawable.pixelsEqualTo(
                            resources.getDrawable(R.drawable.ic_ajout_cal)
                        )
                    ) {
                        timenote_plus.setImageDrawable(resources.getDrawable(R.drawable.ic_ajout_cal_plein_gradient))
                        timenoteViewModel.joinTimenote(tokenId!!, args.event?.id!!).observe(
                            viewLifecycleOwner,
                            Observer {
                                if (it.code() == 401) {
                                    authViewModel.refreshToken(prefs)
                                        .observe(viewLifecycleOwner, Observer { newAccessToken ->
                                            tokenId = newAccessToken
                                            timenoteViewModel.joinTimenote(
                                                tokenId!!,
                                                args.event?.id!!
                                            )
                                        })
                                }
                            })
                    } else {
                        timenote_plus.setImageDrawable(resources.getDrawable(R.drawable.ic_ajout_cal))
                        timenoteViewModel.leaveTimenote(tokenId!!, args.event?.id!!)
                            .observe(viewLifecycleOwner, Observer {
                                if (it.code() == 401) {
                                    authViewModel.refreshToken(prefs)
                                        .observe(viewLifecycleOwner, Observer { newAccessToken ->
                                            tokenId = newAccessToken
                                            timenoteViewModel.leaveTimenote(
                                                tokenId!!,
                                                args.event?.id!!
                                            )
                                        })
                                }
                            })
                    }
                }
            }
            timenote_fl -> {
                val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
                    customView(R.layout.users_participating)
                    lifecycleOwner(this@DetailedTimenoteCarousel)
                }

                val recyclerview = dial.getCustomView().users_participating_rv
                val userAdapter = UsersPagingAdapter(
                    UsersPagingAdapter.UserComparator,
                    args.event,
                    this,
                    null,
                    null
                )
                recyclerview.layoutManager = LinearLayoutManager(requireContext())
                recyclerview.adapter = userAdapter
                lifecycleScope.launch{
                    timenoteViewModel.getUsersParticipating(tokenId!!, args.event?.id!!, prefs).collectLatest {
                        userAdapter.submitData(it)
                    }
                }
            }
            timenote_buy_cl -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(if (args.event?.url?.contains("https://")!!) args.event?.url else "https://" + args.event?.url)
                startActivity(i)
            }
            timenote_day_month -> showInTime(utils, args.event!!)
            timenote_year -> showInTime(utils, args.event!!)
            timenote_time ->  showInTime(utils, args.event!!)
            separator_1 -> showInTime(utils, args.event!!)
            separator_2 -> showInTime(utils, args.event!!)

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


    @ExperimentalTime
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
        if (SimpleDateFormat(ISO).parse(timenote.endingAt).time > System.currentTimeMillis()) timenote_in_label.text = utils.inTime(timenote.startingAt)
        else timenote_in_label.text = utils.sinceTime(timenote.endingAt)
    }

    private fun createOptionsOnTimenote(context: Context, isMine: Boolean){
        val listItems: MutableList<String> = if(!isMine) mutableListOf(context.getString(R.string.share_to),context.getString(R.string.duplicate), context.getString(R.string.report))
        else mutableListOf(context.getString(R.string.share_to), context.getString(R.string.duplicate),context.getString(R.string.edit), context.getString(R.string.delete))
        MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy")
            val ISO =  "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            title(text = "Posted : " + dateFormat.format(SimpleDateFormat(ISO).parse(args.event?.createdAt).time))
            listItems (items = listItems){ dialog, index, text ->
                when(text.toString()){
                    context.getString(R.string.share_to) -> share()
                    context.getString(R.string.duplicate) -> findNavController().navigate(DetailedTimenoteDirections.actionGlobalCreateTimenote().setFrom(args.from).setModify(1).setId(args.event?.id!!).setTimenoteBody(CreationTimenoteDTO(args.event?.createdBy?.id!!, null, args.event?.title!!, args.event?.description, args.event?.pictures,
                        args.event?.colorHex, args.event?.location, args.event?.category, args.event?.startingAt!!, args.event?.endingAt!!,
                        args.event?.hashtags, args.event?.url, args.event?.price!!, null)))
                    context.getString(R.string.edit) -> findNavController().navigate(DetailedTimenoteDirections.actionGlobalCreateTimenote().setFrom(args.from).setModify(2).setId(args.event?.id!!).setTimenoteBody(CreationTimenoteDTO(args.event?.createdBy?.id!!, null, args.event?.title!!, args.event?.description, args.event?.pictures,
                        args.event?.colorHex, args.event?.location, args.event?.category, args.event?.startingAt!!, args.event?.endingAt!!,
                        args.event?.hashtags, args.event?.url, args.event?.price!!, null)))
                    context.getString(R.string.report) -> Toast.makeText(
                        requireContext(),
                        "Reported. Thank You.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    context.getString(R.string.alarm) -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        dateTimePicker { dialog, datetime ->

                        }
                        lifecycleOwner(this@DetailedTimenoteCarousel)
                    }
                }
            }
        }
    }

    private fun share() {

        val linkProperties: LinkProperties = LinkProperties().setChannel("whatsapp").setFeature("sharing")

        val branchUniversalObject = if(!args.event?.pictures?.isNullOrEmpty()!!) BranchUniversalObject()
            .setTitle(args.event?.title!!)
            .setContentDescription(args.event?.description)
            .setContentImageUrl(args.event?.pictures?.get(0)!!)
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setContentMetadata(ContentMetadata().addCustomMetadata("timenoteInfoDTO", Gson().toJson(args.event)))
        else BranchUniversalObject()
            .setTitle(args.event?.title!!)
            .setContentDescription(args.event?.description)
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setContentMetadata(ContentMetadata().addCustomMetadata("timenoteInfoDTO", Gson().toJson(args.event)))

        branchUniversalObject.generateShortUrl(requireContext(), linkProperties) { url, error ->
            BranchEvent("branch_url_created").logEvent(requireContext())
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_TEXT, String.format("Dayzee : %s at %s", args.event?.title, url))
            startActivityForResult(i, 111)
        }


    }

    override fun onPicUserCommentClicked(userInfoDTO: UserInfoDTO) {
        if(userInfoDTO.id == this.userInfoDTO.id) goToProfileLisner.goToProfile()
        else findNavController().navigate(DetailedTimenoteDirections.actionGlobalProfileElse(args.from).setUserInfoDTO(userInfoDTO))
    }

    override fun onCommentMoreClicked(createdBy: String?, commentId: String?) {
        val actionsComment : List<String> = if(userInfoDTO.id == createdBy || args.event?.createdBy?.id == userInfoDTO.id)listOf(getString(R.string.delete))
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
            lifecycleOwner(this@DetailedTimenoteCarousel)
        }
    }

    override fun onSearchClicked(userInfoDTO: UserInfoDTO) {

    }

    override fun onUnfollow(id: String) {

    }

    override fun onRemove(id: String) {
    }

    override fun onAdd(userInfoDTO: UserInfoDTO, createGroup: Int?) {
        sendTo.add(userInfoDTO.id!!)
    }

    override fun onRemove(userInfoDTO: UserInfoDTO, createGroup: Int?) {
        sendTo.remove(userInfoDTO.id!!)
    }

}