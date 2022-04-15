package com.dayzeeco.dayzee.view.homeFlow

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.*
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.filter
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.*
import com.dayzeeco.dayzee.androidView.instaLike.GlideEngine
import com.dayzeeco.dayzee.common.*
import com.dayzeeco.dayzee.listeners.GoToProfile
import com.dayzeeco.dayzee.model.*
import com.dayzeeco.dayzee.viewModel.*
import com.dayzeeco.picture_library.config.PictureMimeType
import com.dayzeeco.picture_library.entity.LocalMedia
import com.dayzeeco.picture_library.instagram.InsGallery
import com.dayzeeco.picture_library.listener.OnResultCallbackListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.BranchEvent
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.fragment_detailed_fragment.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.friends_search_cl.view.*
import kotlinx.android.synthetic.main.item_last_request.view.*
import kotlinx.android.synthetic.main.item_profile_timenote_list_style.view.*
import kotlinx.android.synthetic.main.item_timenote.view.*
import kotlinx.android.synthetic.main.item_timenote_root.*
import kotlinx.android.synthetic.main.item_timenote_root.view.*
import kotlinx.android.synthetic.main.item_user.view.*
import kotlinx.android.synthetic.main.users_participating.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.time.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime


@ExperimentalTime
class DetailedTimenote : Fragment(), View.OnClickListener, CommentAdapter.CommentPicUserListener,
    CommentAdapter.CommentMoreListener, UsersPagingAdapter.SearchPeopleListener,
    UsersShareWithPagingAdapter.SearchPeopleListener, UsersShareWithPagingAdapter.AddToSend,
    CommentAdapter.UserTaggedListener {

    private var isLoading = false
    private lateinit var mentionHelper: MentionHelper
    private var imagesUrl: String? = null
    private var listOfUsersTagged : MutableList<UserInfoDTO> = mutableListOf()
    private var allTextEntered: String = ""
    private lateinit var userAdapter: UsersPagingAdapter
    private lateinit var imm: InputMethodManager
    private lateinit var goToProfileLisner: GoToProfile
    private var sendTo: MutableList<String> = mutableListOf()
    private lateinit var handler: Handler
    private lateinit var handlerMention: Handler
    private val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private val TRIGGER_AUTO_COMPLETE = 200
    private val AUTO_COMPLETE_DELAY: Long = 200
    private lateinit var userInfoDTO: UserInfoDTO
    private lateinit var prefs: SharedPreferences
    private lateinit var am : AmazonS3Client
    private lateinit var commentAdapter: CommentPagingAdapter
    private val commentViewModel: CommentViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val timenoteViewModel: TimenoteViewModel by activityViewModels()
    private val authViewModel: LoginViewModel by activityViewModels()
    private val timenoteHiddedViewModel: TimenoteHiddedViewModel by activityViewModels()
    private var tokenId: String? = null
    private lateinit var screenSlideCreationTimenotePagerAdapter: ScreenSlideTimenotePagerAdapter
    private val args: DetailedTimenoteArgs by navArgs()
    private val utils = Utils()
    private var timer : CountDownTimer? = null
    private lateinit var addPicIv: ImageView
    private lateinit var previewPic: ImageView
    private var textEntered : String = ""
    val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        goToProfileLisner = context as GoToProfile

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return inflater.inflate(R.layout.fragment_detailed_fragment, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addPicIv = detailed_timenote_add_picture
        previewPic = detailed_timenote_picture_prev
        imm = (requireActivity().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)!!
        val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
        userInfoDTO = Gson().fromJson(prefs.getString(user_info_dto, ""), typeUserInfo)

        am = AmazonS3Client(
            CognitoCachingCredentialsProvider(
                requireContext(),
                identity_pool_id, // ID du groupe d'identités
                Regions.US_EAST_1 // Région
            ))

        if(args.event?.location != null) {
            detailed_timenote_address.visibility = View.VISIBLE
                if(args.event?.location?.address?.address?.isEmpty()!! && args.event?.location?.address?.city?.isNotEmpty()!! && args.event?.location?.address?.country?.isNotEmpty()!!){
                    detailed_timenote_address.text = args.event?.location?.address?.city.plus(" ").plus(
                        args.event?.location?.address?.country
                    )
                }
                else if(args.event?.location?.address?.address?.isNotEmpty()!! && args.event?.location?.address?.city?.isNotEmpty()!! && args.event?.location?.address?.country?.isNotEmpty()!!) {
                    detailed_timenote_address.text = args.event?.location?.address?.address.plus(", ")
                        .plus(args.event?.location?.address?.city).plus(" ")
                        .plus(args.event?.location?.address?.country)
                }
                else detailed_timenote_address.text = args.event?.location?.address?.address
            } else detailed_timenote_address.visibility = View.GONE


        timenote_username_desc.maxLines = Int.MAX_VALUE
        timenote_comment_account.visibility = View.GONE
        comments_edittext.requestFocus()

        userAdapter = UsersPagingAdapter(
            UsersPagingAdapter.UserComparator,
            null,
            this,
            null,
            null,
            true, Utils()
        )

        detailed_timenote_rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter.withLoadStateFooter(
                footer = TimenoteLoadStateAdapter{ userAdapter.retry() }
            )
        }

        handlerMention = Handler { msg ->
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(textEntered)) {
                    searchViewModel.setSearchIsEmpty(false)
                    lifecycleScope.launch {
                        searchViewModel.getUsers(tokenId!!, textEntered, prefs)
                            .collectLatest {
                                userAdapter.submitData(it.filter { userInfoDTO -> userInfoDTO.id != args.event?.createdBy?.id })
                                userAdapter.notifyDataSetChanged()
                            }
                    }
                } else {
                    searchViewModel.setSearchIsEmpty(true)
                }
            }
            false
        }

        comments_edittext.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty()){
                    allTextEntered = s.toString()
                    val textValid = (s.toString().substringAfterLast(" @").isNotEmpty() && s.toString().substringAfterLast(" @") != s.toString() && s.toString().takeLast(1) != " " && !s.toString().substringAfterLast(" @").contains(" "))
                    val textValidFirst = (s.toString()[0] == '@' && s.toString().substringAfterLast("@").isNotEmpty() && s.toString().substringAfterLast("@") != s.toString() && s.toString().takeLast(1) != " " && !s.toString().substringAfterLast("@").contains(" "))
                    when {
                        textValid -> {
                            textEntered = s.toString().substringAfterLast(" @")
                            detailed_timenote_rv.visibility = View.VISIBLE
                        }
                        textValidFirst -> {
                            textEntered = s.toString().substringAfterLast("@")
                            detailed_timenote_rv.visibility = View.VISIBLE
                        }
                        else -> detailed_timenote_rv.visibility = View.GONE
                    }
                    handlerMention.removeMessages(TRIGGER_AUTO_COMPLETE)
                    handlerMention.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        mentionHelper = MentionHelper.Creator.create(
            R.color.colorAccent,
            object : MentionHelper.OnMentionClickListener {
                override fun onMentionClicked(mention: String?) {
                } },
            null,
            resources,
            listOfUsersTagged
        )
        mentionHelper.handle(comments_edittext)

        commentAdapter = CommentPagingAdapter(CommentComparator, this, this, this, Utils())

        detailed_timenote_comments_rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter.withLoadStateFooter(
                footer = TimenoteLoadStateAdapter { commentAdapter.retry() }
            )
        }

        lifecycleScope.launch {
            commentViewModel.getComments(tokenId!!, args.event?.id!!, prefs).collectLatest {
                commentAdapter.submitData(it)
            }
        }

        timenote_title.text = args.event?.title
        timenote_title.maxLines = 2
        if (args.event?.pictures?.size == 1 || args.event?.pictures.isNullOrEmpty()) timenote_indicator.visibility =
            View.GONE

        if(prefs.getInt(format_date_default, 0) == 1){
            showInTime(utils, args.event!!)
        } else {
            separator_1.visibility = View.VISIBLE
            separator_2.visibility = View.VISIBLE
            timenote_day_month.visibility = View.VISIBLE
            timenote_time.visibility = View.VISIBLE
            timenote_year.visibility = View.VISIBLE
            timenote_in_label.visibility = View.INVISIBLE
        }

        screenSlideCreationTimenotePagerAdapter = ScreenSlideTimenotePagerAdapter(
            this,
            if (args.event?.pictures.isNullOrEmpty()) listOf(if (args.event?.colorHex.isNullOrEmpty()) "#09539d" else args.event?.colorHex!!) else args.event?.pictures,
            true,
            args.event?.pictures.isNullOrEmpty()
        ) { i: Int, i1: Int ->
            if (i1 == 0) {
                if (args.event?.price?.price!! >= 0 && !args.event?.url.isNullOrBlank()) {
                    timenote_buy_cl.visibility = View.VISIBLE
                    if (args.event?.price?.price!! > 0) timenote_buy.text =
                        args.event?.price?.price!!.toString()
                            .plus(args.event?.price?.currency)
                    if (!args.event?.urlTitle.isNullOrEmpty() || !args.event?.urlTitle.isNullOrBlank()) {
                        more_label.text = args.event?.urlTitle?.capitalize()
                    } else more_label.text = resources.getString(R.string.find_out_more)
                } else if(args.event?.price?.price!! > 0 && args.event?.url.isNullOrBlank()){
                    timenote_buy_cl.visibility = View.VISIBLE
                    timenote_buy.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    timenote_buy.setPadding(0, 0, 48, 0)
                    timenote_buy.text =
                        args.event?.price?.price!!.toString()
                            .plus(args.event?.price?.currency)
                }
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
        screenSlideCreationTimenotePagerAdapter.registerAdapterDataObserver(timenote_indicator.adapterDataObserver)

        timenote_plus.setImageDrawable(resources.getDrawable(R.drawable.ic_ajout_cal))
        if (args.event?.isParticipating!! || userInfoDTO.id == args.event?.createdBy?.id) timenote_plus.setImageDrawable(
            resources.getDrawable(R.drawable.ic_ajout_cal_plein_gradient)
        )
        else timenote_plus.setImageDrawable(resources.getDrawable(R.drawable.ic_ajout_cal))

        timenote_year.text = utils.setYear(args.event?.startingAt!!)
        timenote_day_month.text =
            utils.setFormatedStartDate(
                args.event?.startingAt!!,
                args.event?.endingAt!!,
                requireContext()
            )
        timenote_time.text =
            utils.setFormatedEndDate(
                args.event?.startingAt!!,
                args.event?.endingAt!!,
                requireContext()
            )

        var addedBy = ""
        var addedByFormated = SpannableStringBuilder(addedBy)
        val p = Typeface.create("sans-serif-light", Typeface.NORMAL)
        val m = Typeface.create("sans-serif", Typeface.NORMAL)
        val light = ItemTimenoteRecentAdapter.CustomTypefaceSpan(p)
        val bold = ItemTimenoteRecentAdapter.CustomTypefaceSpan(m)

        if (!args.event?.joinedBy?.users.isNullOrEmpty()) {

            when {
                args?.event?.joinedBy?.count == 1 -> addedBy = String.format(
                    getString(R.string.saved_by_one),
                    args.event?.joinedBy?.users?.get(0)?.userName
                )
                args?.event?.joinedBy?.count in 1..20 -> addedBy = String.format(
                    getString(
                        R.string.saved_by_one_and_other,
                        args.event?.joinedBy?.users?.get(0)?.userName,
                        args.event?.joinedBy?.count!! - 1
                    )
                )
                args?.event?.joinedBy?.count in 21..100 -> addedBy = String.format(
                    getString(R.string.saved_by_tens),
                    args.event?.joinedBy?.users?.get(0)?.userName
                )
                args?.event?.joinedBy?.count in 101..2000 -> addedBy = String.format(
                    getString(R.string.saved_by_hundreds),
                    args.event?.joinedBy?.users?.get(0)?.userName
                )
                args?.event?.joinedBy?.count in 2001..2000000 -> addedBy = String.format(
                    getString(R.string.saved_by_thousands),
                    args.event?.joinedBy?.users?.get(0)?.userName
                )
                args?.event?.joinedBy?.count!! > 2000000 -> addedBy = String.format(
                    getString(R.string.saved_by_millions),
                    args.event?.joinedBy?.users?.get(0)?.userName
                )
            }

            addedByFormated = SpannableStringBuilder(addedBy)
            addedByFormated.setSpan(
                light,
                0,
                addedBy.split(" ")[0].length + addedBy.split(" ")[1].length + 1,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            addedByFormated.setSpan(
                bold,
                addedBy.split(" ")[0].length + addedBy.split(" ")[1].length + 2,
                addedBy.length,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            timenote_added_by.text = addedByFormated

            when (args.event?.joinedBy?.users?.size) {
                1 -> {
                    if (args.event?.joinedBy?.users!![0].picture.isNullOrBlank()){
                        timenote_pic_participant_three.setImageDrawable(utils.determineLetterLogo(args.event?.joinedBy?.users!![0].userName!!, requireContext()))
                    } else Glide
                        .with(requireContext())
                        .load(args.event?.joinedBy?.users!![0].picture)
                        .apply(RequestOptions.circleCropTransform())
                        .into(timenote_pic_participant_three)
                    timenote_pic_participant_two_rl.visibility = View.GONE
                    timenote_pic_participant_one_rl.visibility = View.GONE
                }
                2 -> {
                    if (args.event?.joinedBy?.users!![0].picture.isNullOrBlank()){
                        timenote_pic_participant_two.setImageDrawable(utils.determineLetterLogo(args.event?.joinedBy?.users!![0].userName!!, requireContext()))
                    } else Glide
                        .with(requireContext())
                        .load(args.event?.joinedBy?.users!![0].picture)
                        .apply(RequestOptions.circleCropTransform())
                        .into(timenote_pic_participant_two)

                    if (args.event?.joinedBy?.users!![1].picture.isNullOrBlank()){
                        timenote_pic_participant_three.setImageDrawable(utils.determineLetterLogo(args.event?.joinedBy?.users!![1].userName!!, requireContext()))
                    } else Glide
                        .with(requireContext())
                        .load(args.event?.joinedBy?.users!![1].picture)
                        .apply(RequestOptions.circleCropTransform())
                        .into(timenote_pic_participant_three)
                    timenote_pic_participant_one_rl.visibility = View.GONE
                }
                else -> {
                    if (args.event?.joinedBy?.users!![0].picture.isNullOrBlank()){
                        timenote_pic_participant_one.setImageDrawable(utils.determineLetterLogo(args.event?.joinedBy?.users!![0].userName!!, requireContext()))
                    } else Glide
                        .with(requireContext())
                        .load(args.event?.joinedBy?.users!![0].picture)
                        .apply(RequestOptions.circleCropTransform())
                        .into(timenote_pic_participant_one)

                    if (args.event?.joinedBy?.users!![0].picture.isNullOrBlank()){
                        timenote_pic_participant_two.setImageDrawable(utils.determineLetterLogo(args.event?.joinedBy?.users!![1].userName!!, requireContext()))
                    } else Glide
                        .with(requireContext())
                        .load(args.event?.joinedBy?.users!![1].picture)
                        .apply(RequestOptions.circleCropTransform())
                        .into(timenote_pic_participant_two)

                    if (args.event?.joinedBy?.users!![2].picture.isNullOrBlank()){
                        timenote_pic_participant_three.setImageDrawable(utils.determineLetterLogo(args.event?.joinedBy?.users!![2].userName!!, requireContext()))
                    } else Glide
                        .with(requireContext())
                        .load(args.event?.joinedBy?.users!![2].picture)
                        .apply(RequestOptions.circleCropTransform())
                        .into(timenote_pic_participant_three)
                }
            }
        } else {
            if (args.event?.joinedBy?.count!! > 0) {
                addedBy = resources.getQuantityString(
                    R.plurals.saved_by_count,
                    args.event?.joinedBy?.count!!,
                    args.event?.joinedBy?.count
                )
                addedByFormated = SpannableStringBuilder(addedBy)
                addedByFormated.setSpan(
                    light,
                    0,
                    addedBy.split(" ")[0].length + addedBy.split(" ")[1].length + 1,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
                addedByFormated.setSpan(
                    bold,
                    addedBy.split(" ")[0].length + addedBy.split(" ")[1].length + 2,
                    addedBy.length,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
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

        val hashTagHelper = HashTagHelper.Creator.create(
            R.color.colorAccent,
            object : HashTagHelper.OnHashTagClickListener {
                override fun onHashTagClicked(hashTag: String?) {
                    findNavController().navigate(
                        DetailedTimenoteDirections.actionGlobalTimenoteTAG(
                            args.event,
                            hashTag
                        ).setFrom(args.from)
                    )
                }

            },
            null,
            resources
        )
        hashTagHelper.handle(timenote_username_desc)

        if (args.event?.description.isNullOrBlank()) {
            timenote_username_desc.visibility = View.GONE
        } else {
            val description = "${args.event?.createdBy?.userName} ${args.event?.description}"
            val descriptionFormatted = SpannableStringBuilder(description)
            descriptionFormatted.setSpan(
                bold,
                0,
                args.event?.createdBy?.userName?.length!!,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            descriptionFormatted.setSpan(
                light,
                args.event?.createdBy?.userName?.length!!,
                description.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            timenote_username_desc.text = descriptionFormatted
        }

        if (args.event?.createdBy?.picture.isNullOrBlank()){
            detailed_timenote_pic_user.setImageDrawable(utils.determineLetterLogo(args.event?.createdBy?.userName!!, requireContext()))
        } else Glide
            .with(this)
            .load(args.event?.createdBy?.picture)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.circle_pic)
            .into(detailed_timenote_pic_user)

        detailed_timenote_username.text = args.event?.createdBy?.userName
        if(args.event?.createdBy?.certified!!) detailed_timenote_username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_certified_other, 0)
        else detailed_timenote_username.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

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
        detailed_timenote_username.setOnClickListener(this)
        detailed_timenote_pic_user.setOnClickListener(this)
        detailed_timenote_address.setOnClickListener(this)
        detailed_timenote_add_picture.setOnClickListener(this)
        detailed_timenote_picture_prev.setOnClickListener(this)

        detailed_timenote_btn_back.setOnClickListener { findNavController().popBackStack() }
    }

    @SuppressLint("CheckResult")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when (v) {
            addPicIv -> {
                openGallery()
            }
            previewPic ->{
                 MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    title(text = getString(R.string.picture_attached_to_comment))
                    listItems(items= listOf(getString(R.string.delete), getString(R.string.replace))){ dialog, index, text ->
                        when(text.toString()){
                            getString(R.string.delete) -> {
                                imagesUrl = null

                                addPicIv.visibility = View.VISIBLE
                                previewPic.visibility =View.GONE
                            }
                            getString(R.string.replace) -> openGallery()
                        }
                    }
                }
            }
            detailed_timenote_address -> findNavController().navigate(
                DetailedTimenoteDirections.actionGlobalTimenoteAddress(args.event).setFrom(args.from)
            )
            detailed_timenote_username, detailed_timenote_pic_user -> {
                if (userInfoDTO.id != args.event?.createdBy?.id) findNavController().navigate(
                    DetailedTimenoteDirections.actionGlobalProfileElse(args.from)
                        .setUserInfoDTO(args.event?.createdBy)
                )
            }
            timenote_comment -> {
                comments_edittext.requestFocus()
                imm.showSoftInput(comments_edittext, InputMethodManager.SHOW_IMPLICIT)
            }
            comments_edittext -> {
                comments_edittext.requestFocus()
                imm.showSoftInput(comments_edittext, InputMethodManager.SHOW_IMPLICIT)
            }
            detailed_timenote_btn_more -> createOptionsOnTimenote(
                requireContext(),
                userInfoDTO.id == args.event?.createdBy?.id
            )
            timenote_detailed_send_comment -> {
                if(imagesUrl != null || comments_edittext.text.isNotEmpty()){
                commentViewModel.postComment(
                    tokenId!!,
                    CommentCreationDTO(
                        userInfoDTO.id!!,
                        args.event?.id!!,
                        comments_edittext.text.toString(),
                        listOf(), listOfUsersTagged.filter { userInfoDTO -> mentionHelper.allMentions.contains(userInfoDTO.userName?.replace("\\s".toRegex(), "")?.replace("[^A-Za-z0-9 ]".toRegex(), "") ) }.map { userInfoDTO -> userInfoDTO.id!! },
                        imagesUrl
                    )
                ).observe(viewLifecycleOwner) {
                    if (it.code() == 401) {
                        authViewModel.refreshToken(prefs)
                            .observe(viewLifecycleOwner) { newAccessToken ->
                                tokenId = newAccessToken
                                commentViewModel.postComment(
                                    tokenId!!,
                                    CommentCreationDTO(
                                        userInfoDTO.id!!,
                                        args.event?.id!!,
                                        comments_edittext.text.toString(),
                                        listOf(),
                                        listOfUsersTagged.filter { userInfoDTO ->
                                            mentionHelper.allMentions.contains(
                                                userInfoDTO.userName?.replace(
                                                    "\\s".toRegex(),
                                                    ""
                                                )?.replace("[^A-Za-z0-9 ]".toRegex(), "")
                                            )
                                        }.map { userInfoDTO -> userInfoDTO.id!! },
                                        imagesUrl
                                    )
                                ).observe(viewLifecycleOwner) { newReq ->
                                    if (newReq.isSuccessful) {
                                        comments_edittext.clearFocus()
                                        imm.hideSoftInputFromWindow(
                                            comments_edittext.windowToken,
                                            0
                                        )
                                        comments_edittext.text.clear()
                                        imagesUrl = null
                                        addPicIv.visibility = View.VISIBLE
                                        previewPic.visibility = View.GONE
                                        commentAdapter.refresh()

                                        /*lifecycleScope.launch {
                                    commentViewModel.getComments(tokenId!!, args.event?.id!!, prefs)
                                        .collectLatest { data ->
                                            commentAdapter.submitData(data)
                                        }
                                }*/
                                    }
                                }
                            }
                    }
                    if (it.isSuccessful) {
                        comments_edittext.clearFocus()
                        imm.hideSoftInputFromWindow(comments_edittext.windowToken, 0)
                        comments_edittext.text.clear()
                        imagesUrl = null
                        addPicIv.visibility = View.VISIBLE
                        previewPic.visibility = View.GONE
                        commentAdapter.refresh()

                        /*lifecycleScope.launch {
                            commentViewModel.getComments(tokenId!!, args.event?.id!!, prefs)
                                .collectLatest { data ->
                                    commentAdapter.submitData(data)
                                }
                        }*/
                    }
                }
                }}
            timenote_share -> {
                sendTo.clear()
                val dial =
                    MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
                        customView(R.layout.friends_search_cl)
                        lifecycleOwner(this@DetailedTimenote)
                        positiveButton(R.string.send) {
                            timenoteViewModel.shareWith(
                                tokenId!!,
                                ShareTimenoteDTO(args.event?.id!!, sendTo)
                            ).observe(viewLifecycleOwner, Observer {
                                if (it.code() == 401) {
                                    authViewModel.refreshToken(prefs)
                                        .observe(viewLifecycleOwner, Observer { newAccessToken ->
                                            tokenId = newAccessToken
                                            timenoteViewModel.shareWith(
                                                tokenId!!,
                                                ShareTimenoteDTO(args.event?.id!!, sendTo)
                                            )
                                        })
                                }
                            })
                        }
                        negativeButton(R.string.cancel)
                    }

                dial.getActionButton(WhichButton.NEGATIVE)
                    .updateTextColor(resources.getColor(android.R.color.darker_gray))
                val searchbar = dial.getCustomView().searchBar_friends
                searchbar.setCardViewElevation(0)
                searchbar.addTextChangeListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
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
                    null,
                    false
                    , Utils())
                recyclerview.layoutManager = LinearLayoutManager(requireContext())
                recyclerview.adapter = userAdapter
                lifecycleScope.launch {
                    searchViewModel.getUsers(tokenId!!, userInfoDTO.id!!, prefs).collectLatest {
                        userAdapter.submitData(it)
                    }
                }
                if (searchbar != null) {
                    handler = Handler { msg ->
                        if (msg.what == TRIGGER_AUTO_COMPLETE) {
                            if (!TextUtils.isEmpty(searchbar.text)) {
                                lifecycleScope.launch {
                                    searchViewModel.getUsers(
                                        tokenId!!,
                                        searchbar.text,
                                        prefs
                                    )
                                        .collectLatest {
                                            userAdapter.submitData(it)
                                        }
                                }

                            } else {
                                lifecycleScope.launch {
                                    searchViewModel.getUsers(tokenId!!, userInfoDTO.id!!, prefs)
                                        .collectLatest {
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
                val dial =
                    MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
                        customView(R.layout.users_participating)
                        lifecycleOwner(this@DetailedTimenote)
                    }

                val recyclerview = dial.getCustomView().users_participating_rv
                val userAdapter = UsersPagingAdapter(
                    UsersPagingAdapter.UserComparator,
                    args.event,
                    this,
                    null,
                    null,
                    false
                    , Utils())
                recyclerview.layoutManager = LinearLayoutManager(requireContext())
                recyclerview.adapter = userAdapter
                lifecycleScope.launch {
                    timenoteViewModel.getUsersParticipating(tokenId!!, args.event?.id!!, prefs)
                        .collectLatest {
                            userAdapter.submitData(it)
                        }
                }
            }
            timenote_buy_cl -> {
                if (!args.event?.url.isNullOrBlank()) {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data =
                        Uri.parse(if (args.event?.url?.contains("https://")!!) args.event?.url else "https://" + args.event?.url)
                    startActivity(i)
                }
            }
            timenote_day_month -> showInTime(utils, args.event!!)
            timenote_year -> showInTime(utils, args.event!!)
            timenote_time -> showInTime(utils, args.event!!)
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

    override fun onResume() {
        super.onResume()
        if (imagesUrl.isNullOrBlank() && !isLoading){
            addPicIv.visibility = View.VISIBLE
            detailed_timenote_add_picture_pb.visibility = View.GONE
            previewPic.visibility =View.GONE
        }
        else if(!imagesUrl.isNullOrBlank() && !isLoading) {
            addPicIv.visibility = View.INVISIBLE
            previewPic.visibility = View.VISIBLE
            detailed_timenote_add_picture_pb.visibility = View.GONE
        }
    }

    private fun openGallery(){
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED) {

            detailed_timenote_add_picture.visibility = View.INVISIBLE
            detailed_timenote_picture_prev.visibility = View.GONE
            detailed_timenote_add_picture_pb.visibility = View.VISIBLE

            InsGallery
                .openGallery(
                    requireActivity(),
                    GlideEngine.createGlideEngine(),
                    object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: MutableList<LocalMedia>?) {
                            for (media in result!!) {
                                var path: String =
                                    if (media.isCut && !media.isCompressed) {
                                        media.cutPath
                                    } else if (media.isCompressed || media.isCut && media.isCompressed) {
                                        media.compressPath
                                    } else if (PictureMimeType.isHasVideo(media.mimeType) && !TextUtils.isEmpty(
                                            media.coverPath
                                        )
                                    ) {
                                        media.coverPath
                                    } else {
                                        media.path
                                    }
                                ImageCompressor.compressBitmap(requireContext(), File(path)) {
                                    isLoading = true
                                    pushPic(it)
                                }
                            }
                        }

                        override fun onCancel() {

                        }

                    }, 1)
        } else requestPermissions(PERMISSIONS_STORAGE, 2)
    }

    private fun pushPic(file: File){
        val transferUtiliy = TransferUtility(am, requireContext())
        val key = "timenote/${UUID.randomUUID().mostSignificantBits}"
        val transferObserver = transferUtiliy.upload(
            bucket_dayzee_dev_image, key,
            file, CannedAccessControlList.Private
        )
        transferObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState?) {
                Log.d(ContentValues.TAG, "onStateChanged: ${state?.name}")
                if (state == TransferState.COMPLETED) {
                    isLoading = false
                    Glide.
                        with(requireContext())
                        .load(am.getResourceUrl(bucket_dayzee_dev_image, key).toString())
                        .apply(RequestOptions.circleCropTransform())
                        .into(detailed_timenote_picture_prev)
                    detailed_timenote_picture_prev.visibility = View.VISIBLE
                    detailed_timenote_add_picture_pb.visibility = View.GONE
                    imagesUrl = am.getResourceUrl(bucket_dayzee_dev_image, key).toString()
                }

            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                Log.d(ContentValues.TAG, "onProgressChanged: ")
            }

            override fun onError(id: Int, ex: java.lang.Exception?) {
                Log.d(ContentValues.TAG, "onError: ${ex?.message}")
                Toast.makeText(requireContext(), ex?.message, Toast.LENGTH_LONG).show()
            }

        })

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
        val o = SimpleDateFormat(ISO)
        o.timeZone = TimeZone.getTimeZone("UTC")
        val m = o.parse(timenote.endingAt)
        o.timeZone = TimeZone.getDefault()
        val k = o.format(m)
        if (SimpleDateFormat(ISO, Locale.getDefault()).parse(k).time > System.currentTimeMillis()) {
            if (utils.inTime(timenote.startingAt, requireContext()) == getString(R.string.live)) timenote_in_label.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_oval,
                0,
                0,
                0
            )
            else timenote_in_label.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)

            val duration = Duration.between(Instant.now(), Instant.parse(timenote.startingAt)).toMillis()
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = duration
            if(timer != null){
                timer!!.cancel()
            }
            timer = object: CountDownTimer(duration, 1000){
                override fun onTick(millisUntilFinished: Long) {
                    val years : Long
                    val months: Long
                    val days : Long
                    val hours: Long
                    val minutes: Long
                    val seconds: Long
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val period = Period.between(
                            LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC).toLocalDate(),
                            LocalDateTime.ofInstant(Instant.parse(timenote.startingAt), ZoneOffset.UTC).toLocalDate()
                        )

                        years = period.years.toLong()
                        months = period.minusYears(years).months.toLong()
                        days = if(TimeUnit.MILLISECONDS.toDays(millisUntilFinished) < period.minusYears(years).minusMonths(months).days.toLong()) TimeUnit.MILLISECONDS.toDays(millisUntilFinished) else period.minusYears(years).minusMonths(months).days.toLong()
                        hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millisUntilFinished))
                        minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))
                        seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                    } else {
                        val calendarLocal = Calendar.getInstance()
                        calendarLocal.timeInMillis = millisUntilFinished
                        years = (calendarLocal.get(Calendar.YEAR) - 1970).toLong()
                        months = (calendarLocal.get(Calendar.MONTH)).toLong()
                        days = (calendarLocal.get(Calendar.DAY_OF_MONTH) - 1).toLong()
                        hours = (calendarLocal.get(Calendar.HOUR) + 12).toLong()
                        minutes = (calendarLocal.get(Calendar.MINUTE)).toLong()
                        seconds = (calendarLocal.get(Calendar.SECOND)).toLong()
                    }

                    timenote_in_label?.text =  utils.formatInTime(
                        years,
                        months,
                        days,
                        hours,
                        minutes,
                        seconds,
                        requireContext()
                    )
                }

                override fun onFinish() {
                    timenote_in_label?.text =  requireContext().getString(R.string.live)
                }

            }.start()        } else {
            timenote_in_label.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            timenote_in_label.text = utils.sinceTime(timenote.endingAt, requireContext())
        }
    }

    @SuppressLint("CheckResult")
    private fun createOptionsOnTimenote(context: Context, isMine: Boolean) {
        val listItems: MutableList<String> = if (!isMine) mutableListOf(
            context.getString(R.string.share_to),
            context.getString(R.string.hide_post),
            context.getString(R.string.hide_all_posts),
            context.getString(R.string.duplicate),
            context.getString(R.string.report)
        )
        else mutableListOf(
            context.getString(R.string.share_to),
            context.getString(R.string.duplicate),
            context.getString(R.string.edit),
            context.getString(R.string.delete)
        )
        MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy")
            val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            title(text = dateFormat.format(SimpleDateFormat(ISO).parse(args.event?.createdAt).time))
            listItems(items = listItems) { dialog, index, text ->
                when (text.toString()) {
                    context.getString(R.string.share_to) -> share()
                    context.getString(R.string.duplicate) -> findNavController().navigate(
                        DetailedTimenoteDirections.actionGlobalCreateTimenote().setFrom(args.from)
                            .setModify(1).setId(args.event?.id!!).setTimenoteBody(
                                CreationTimenoteDTO(
                                    args.event?.createdBy?.id!!,
                                    null,
                                    args.event?.title!!,
                                    args.event?.description,
                                    args.event?.pictures,
                                    args.event?.colorHex,
                                    args.event?.location,
                                    args.event?.category,
                                    args.event?.startingAt!!,
                                    args.event?.endingAt!!,
                                    args.event?.hashtags,
                                    args.event?.url,
                                    args.event?.price!!,
                                    null,
                                    args.event?.urlTitle
                                )
                            )
                    )
                    context.getString(R.string.edit) -> findNavController().navigate(
                        DetailedTimenoteDirections.actionGlobalCreateTimenote().setFrom(args.from)
                            .setModify(2).setId(args.event?.id!!).setTimenoteBody(
                                CreationTimenoteDTO(
                                    args.event?.createdBy?.id!!,
                                    null,
                                    args.event?.title!!,
                                    args.event?.description,
                                    args.event?.pictures,
                                    args.event?.colorHex,
                                    args.event?.location,
                                    args.event?.category,
                                    args.event?.startingAt!!,
                                    args.event?.endingAt!!,
                                    args.event?.hashtags,
                                    args.event?.url,
                                    args.event?.price!!,
                                    null,
                                    args.event?.urlTitle
                                )
                            )
                    )
                    context.getString(R.string.report) -> Toast.makeText(
                        requireContext(),
                        getString(R.string.reported),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    context.getString(R.string.delete) -> {
                        val map: MutableMap<Long, String> = Gson().fromJson(
                            prefs.getString(
                                map_event_id_to_timenote, null
                            ), object : TypeToken<MutableMap<String, String>>() {}.type
                        ) ?: mutableMapOf()
                        timenoteViewModel.deleteTimenote(tokenId!!, args.event?.id!!)
                            .observe(viewLifecycleOwner) {
                                if (it.isSuccessful) {
                                    if (map.isNotEmpty() && map.filterValues { id -> id == args.event?.id!! }.keys.isNotEmpty()) {
                                        map.remove(map.filterValues { id -> id == args.event?.id!! }.keys.first())
                                        prefs.edit()
                                            .putString(map_event_id_to_timenote, Gson().toJson(map))
                                            .apply()
                                    }
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.deleted),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    findNavController().popBackStack()
                                } else if (it.code() == 401) {
                                    authViewModel.refreshToken(prefs)
                                        .observe(viewLifecycleOwner) { newAccessToken ->
                                            tokenId = newAccessToken
                                            timenoteViewModel.deleteTimenote(
                                                tokenId!!,
                                                args.event?.id!!
                                            ).observe(viewLifecycleOwner) { tid ->
                                                if (tid.isSuccessful) {
                                                    Toast.makeText(
                                                        requireContext(),
                                                        getString(R.string.deleted),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    findNavController().popBackStack()
                                                }
                                                if (map.isNotEmpty() && map.filterValues { id -> id == args.event?.id!! }.keys.isNotEmpty()) {
                                                    map.remove(map.filterValues { id -> id == args.event?.id!! }.keys.first())
                                                    prefs.edit().putString(
                                                        map_event_id_to_timenote,
                                                        Gson().toJson(map)
                                                    ).apply()
                                                }
                                            }
                                        }
                                }
                            }
                    }
                    context.getString(R.string.hide_post) -> {
                        timenoteHiddedViewModel.hideEventOrUSer(tokenId!!, TimenoteHiddedCreationDTO(createdBy = userInfoDTO.id!!, timenote = args.event?.id)).observe(viewLifecycleOwner) {
                            if (it.code() == 401) {
                                authViewModel.refreshToken(prefs)
                                    .observe(viewLifecycleOwner) { newAccessToken ->
                                        tokenId = newAccessToken
                                        timenoteHiddedViewModel.hideEventOrUSer(
                                            tokenId!!,
                                            TimenoteHiddedCreationDTO(
                                                createdBy = userInfoDTO.id!!,
                                                timenote = args.event?.id
                                            )
                                        ).observe(viewLifecycleOwner) {

                                        }
                                    }
                            }
                        }
                    }
                    context.getString(R.string.hide_all_posts) -> {
                        timenoteHiddedViewModel.hideEventOrUSer(tokenId!!, TimenoteHiddedCreationDTO(createdBy = userInfoDTO.id!!, user = args.event?.createdBy?.id)).observe(viewLifecycleOwner) {
                            if (it.code() == 401) {
                                authViewModel.refreshToken(prefs)
                                    .observe(viewLifecycleOwner) { newAccessToken ->
                                        tokenId = newAccessToken
                                        timenoteHiddedViewModel.hideEventOrUSer(
                                            tokenId!!,
                                            TimenoteHiddedCreationDTO(
                                                createdBy = userInfoDTO.id!!,
                                                timenote = args.event?.createdBy?.id
                                            )
                                        ).observe(viewLifecycleOwner) {

                                        }
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun share() {

        val linkProperties: LinkProperties =
            LinkProperties().setChannel("whatsapp").setFeature("sharing")

        val branchUniversalObject =
            if (!args.event?.pictures?.isNullOrEmpty()!!) BranchUniversalObject()
                .setTitle(args.event?.title!!)
                .setContentDescription(args.event?.description)
                .setContentImageUrl(args.event?.pictures?.get(0)!!)
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(
                    ContentMetadata().addCustomMetadata(
                        timenote_info_dto,
                        Gson().toJson(args.event)
                    )
                )
            else BranchUniversalObject()
                .setTitle(args.event?.title!!)
                .setContentDescription(args.event?.description)
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(
                    ContentMetadata().addCustomMetadata(
                        timenote_info_dto,
                        Gson().toJson(args.event)
                    )
                )

        branchUniversalObject.generateShortUrl(requireContext(), linkProperties) { url, error ->
            BranchEvent("branch_url_created").logEvent(requireContext())
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(
                Intent.EXTRA_TEXT,
                String.format(
                    resources.getString(R.string.invitation_externe),
                    userInfoDTO.userName,
                    args.event?.title,
                    utils.formatDateToShare(
                        args.event?.startingAt!!
                    ),
                    utils.formatHourToShare(args.event?.startingAt!!),
                    url
                )
            )
            startActivityForResult(i, 111)
        }


    }

    override fun onPicUserCommentClicked(userInfoDTO: UserInfoDTO) {
        if (userInfoDTO.id == this.userInfoDTO.id) goToProfileLisner.goToProfile()
        else findNavController().navigate(
            DetailedTimenoteDirections.actionGlobalProfileElse(args.from)
                .setUserInfoDTO(userInfoDTO)
        )
    }

    @SuppressLint("CheckResult")
    override fun onCommentMoreClicked(createdBy: String?, commentId: String?) {
        val actionsComment: List<String> =
            if (userInfoDTO.id == createdBy || args.event?.createdBy?.id == userInfoDTO.id) listOf(
                getString(R.string.delete)
            )
            else listOf(getString(R.string.report))
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            listItems(items = actionsComment) { _, _, text ->
                when (text) {
                    getString(R.string.delete) -> commentViewModel.deleteComment(
                        tokenId!!,
                        commentId!!
                    ).observe(viewLifecycleOwner, Observer {
                        if (it.code() == 401) {
                            authViewModel.refreshToken(prefs)
                                .observe(viewLifecycleOwner, Observer { newAccessToken ->
                                    tokenId = newAccessToken
                                    commentViewModel.deleteComment(tokenId!!, commentId)
                                        .observe(viewLifecycleOwner, Observer { resp ->
                                            if (resp.isSuccessful) commentAdapter.refresh()
                                        })
                                })
                        }
                        if (it.isSuccessful) commentAdapter.refresh()
                    })

                    getString(R.string.report) -> Toast.makeText(
                        requireContext(),
                        getString(R.string.reported),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            lifecycleOwner(this@DetailedTimenote)
        }
    }

    override fun onSearchClicked(userInfoDTO: UserInfoDTO, isTagged: Boolean) {
        if(isTagged){
            val textUpdated = allTextEntered.replace("\\Q@$textEntered\\E\\b".toRegex(), "") + "@" +userInfoDTO.userName!!.replace("\\s".toRegex(), "").replace("[^A-Za-z0-9 ]".toRegex(), "") + " "
            if(!listOfUsersTagged.contains(userInfoDTO)) listOfUsersTagged.add(userInfoDTO)
            comments_edittext.setText(textUpdated)
            comments_edittext.setSelection(comments_edittext.text.toString().length)
        } else {
            if (userInfoDTO.id == this.userInfoDTO.id) goToProfileLisner.goToProfile()
            else findNavController().navigate(
                HomeDirections.actionGlobalProfileElse(1).setUserInfoDTO(userInfoDTO)
            )
        }
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

    override fun onUserTaggedClicked(userInfoDTO: UserInfoDTO?) {
        if(userInfoDTO != null) findNavController().navigate(HomeDirections.actionGlobalProfileElse(1).setUserInfoDTO(userInfoDTO))
        else Toast.makeText(
            requireContext(),
            getString(R.string.no_user_corresponding_to_mention),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 2){
            if (resultCode == Activity.RESULT_OK){
                openGallery()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 2){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                detailed_timenote_add_picture.visibility = View.INVISIBLE
                detailed_timenote_picture_prev.visibility = View.GONE
                detailed_timenote_add_picture_pb.visibility = View.VISIBLE
                openGallery()
            }
        }
    }

}