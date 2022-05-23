package com.dayzeeco.dayzee.view.createTimenoteFlow

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.*
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onCancel
import com.afollestad.materialdialogs.color.ColorPalette
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.bumptech.glide.Glide
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.*
import com.dayzeeco.dayzee.androidView.dialog.input
import com.dayzeeco.dayzee.androidView.instaLike.GlideCacheEngine
import com.dayzeeco.dayzee.androidView.instaLike.GlideEngine
import com.dayzeeco.dayzee.common.*
import com.dayzeeco.dayzee.listeners.BackToHomeListener
import com.dayzeeco.dayzee.listeners.GoToProfile
import com.dayzeeco.dayzee.listeners.RefreshPicBottomNavListener
import com.dayzeeco.dayzee.listeners.TimenoteCreationPicListeners
import com.dayzeeco.dayzee.model.*
import com.dayzeeco.dayzee.viewModel.*
import com.dayzeeco.picture_library.config.PictureMimeType
import com.dayzeeco.picture_library.entity.LocalMedia
import com.dayzeeco.picture_library.instagram.InsGallery
import com.dayzeeco.picture_library.listener.OnResultCallbackListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.dialog_reccurence.view.*
import kotlinx.android.synthetic.main.fragment_create_timenote.*
import kotlinx.android.synthetic.main.friends_search_cl.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import mehdi.sakout.fancybuttons.FancyButton
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Type
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class CreateTimenote : Fragment(), View.OnClickListener, WebSearchAdapter.ImageChoosedListener, WebSearchAdapter.MoreImagesClicked,
    HashTagHelper.OnHashTagClickListener, UsersPagingAdapter.SearchPeopleListener,
    UsersShareWithPagingAdapter.SearchPeopleListener, UsersShareWithPagingAdapter.AddToSend,
    DateAdapter.DateCloseListener {

    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private lateinit var goToProfileLisner : GoToProfile
    private var accountType: Int = -1
    private lateinit var dateAdapter: DateAdapter
    private var dateToAdd : MutableList<String> = mutableListOf()
    private var sendTo: MutableList<String> = mutableListOf()
    private var organizers: MutableList<String> = mutableListOf()
    private var indexGroupChosen: MutableList<Int> = mutableListOf()
    private lateinit var handler: Handler
    private val TRIGGER_AUTO_COMPLETE = 200
    private val AUTO_COMPLETE_DELAY: Long = 200
    private var visibilityTimenote: Int = -1
    private var startDateDisplayed: String? = null
    private var endDateDisplayed: String? = null
    private lateinit var am : AmazonS3Client
    private val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"
    private val ISORECEIVED = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private lateinit var args : CreateTimenoteArgs
    private val timenoteViewModel: TimenoteViewModel by activityViewModels()
    private val switchToPreviewDetailedTimenoteViewModel : SwitchToPreviewDetailedTimenoteViewModel by activityViewModels()
    private val AUTOCOMPLETE_REQUEST_CODE: Int = 11
    private lateinit var utils: Utils
    private lateinit var fromLabel: TextView
    private lateinit var toLabel : TextView
    private lateinit var addEndDateTv: TextView
    private lateinit var fixedDate : TextView
    private lateinit var paidTimenote : CardView
    private lateinit var subcategoryCv : CardView
    private lateinit var picCl: ConstraintLayout
    private lateinit var labelCv: CardView
    private lateinit var noAnswer: TextView
    private var images: MutableList<File>? = mutableListOf()
    private var video : File? = null
    private var videoUrl : String? = null
    private lateinit var titleInput: String
    private var endDate: Long? = null
    private var formCompleted: Boolean = true
    private var startDate: Long? = null
    private val creationTimenoteViewModel: CreationTimenoteViewModel by activityViewModels()
    private val profileViewModel : ProfileViewModel by activityViewModels()
    private val webSearchViewModel : WebSearchViewModel by activityViewModels()
    private val followViewModel: FollowViewModel by activityViewModels()
    private val authViewModel: LoginViewModel by activityViewModels()
    private lateinit var categoryTv: TextView
    private lateinit var fromTv: TextView
    private lateinit var toTv: TextView
    private lateinit var configureRecurrence: TextView
    private lateinit var datesRv : RecyclerView
    private lateinit var titleTv: TextView
    private lateinit var shareWithTv: TextView
    private lateinit var moreColorTv: FancyButton
    private lateinit var firstColorTv: FancyButton
    private lateinit var secondColorTv: FancyButton
    private lateinit var thirdColorTv: FancyButton
    private lateinit var fourthColorTv: FancyButton
    private lateinit var takeAddPicTv: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var descTv: TextView
    private lateinit var descCv: CardView
    private lateinit var catLabelTv : TextView
    private lateinit var subCatTv : TextView
    private lateinit var subCatLabelTv : TextView
    private lateinit var titleError : ImageView
    private lateinit var whenError: ImageView
    private lateinit var paidLabelTv : TextView
    private lateinit var picVideoIv : ImageView
    private val DATE_FORMAT_DAY_AND_TIME = "EEE, d MMM yyyy hh:mm aaa"
    private val DATE_FORMAT_DAY_AND_TIME_DIALOG = "d MMM yyyy"
    private val DATE_FORMAT_REPEAT = "d"
    private lateinit var dateFormatDateAndTime: SimpleDateFormat
    private lateinit var dateFormatDateAndTimeISO: SimpleDateFormat
    private lateinit var dateFormatDateAndTimeDialog: SimpleDateFormat
    private lateinit var dateFormatDateRepeat: SimpleDateFormat
    private var placesList: List<Place.Field> = listOf(
        Place.Field.ID,
        Place.Field.NAME,
        Place.Field.ADDRESS
    )
    private lateinit var placesClient: PlacesClient
    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var backToHomeListener: BackToHomeListener
    private lateinit var prefs: SharedPreferences
    private var tokenId: String? = null
    private var imagesUrl: MutableList<String> = mutableListOf()
    private lateinit var userInfoDTO: UserInfoDTO
    private lateinit var onRefreshPicBottomNavListener: RefreshPicBottomNavListener
    private var isCreatedOffset : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
        loginViewModel.getAuthenticationState().observe(requireActivity()) {
            when (it) {
                LoginViewModel.AuthenticationState.UNAUTHENTICATED -> findNavController().navigate(
                    CreateTimenoteDirections.actionGlobalNavigation()
                )
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    tokenId = prefs.getString(accessToken, null)
                    findNavController().popBackStack(
                        R.id.createTimenote,
                        false
                    )
                }
                LoginViewModel.AuthenticationState.GUEST -> findNavController().popBackStack(
                    R.id.createTimenote,
                    false
                )
            }
        }
        Places.initialize(requireContext(), getString(R.string.api_web_key))
        placesClient = Places.createClient(requireContext())
    }

    override fun onResume() {
        super.onResume()
        isCreatedOffset = false
        if(!prefs.getString(accessToken, null).isNullOrBlank()) onRefreshPicBottomNavListener.onrefreshPicBottomNav(userInfoDTO.picture, userInfoDTO.userName)
        when(loginViewModel.getAuthenticationState().value){
            LoginViewModel.AuthenticationState.GUEST -> backToHomeListener.onBackHome()
            LoginViewModel.AuthenticationState.UNAUTHENTICATED -> backToHomeListener.onBackHome()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        goToProfileLisner = context as GoToProfile
        backToHomeListener = context as BackToHomeListener
        onRefreshPicBottomNavListener = context as RefreshPicBottomNavListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_create_timenote, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> { InsGallery.setCurrentTheme(InsGallery.THEME_STYLE_DARK) }
            Configuration.UI_MODE_NIGHT_NO -> {InsGallery.setCurrentTheme(InsGallery.THEME_STYLE_DEFAULT)}
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {InsGallery.setCurrentTheme(InsGallery.THEME_STYLE_DARK_BLUE)}
        }

        am = AmazonS3Client(CognitoCachingCredentialsProvider(
            requireContext(),
            identity_pool_id, // ID du groupe d'identités
            Regions.US_EAST_1 // Région
        ))
        try {
            args = navArgs<CreateTimenoteArgs>().value
        } catch (i : InvocationTargetException){
        }

        if(!prefs.getString(accessToken, null).isNullOrBlank()) {
            setUp()

            val type: Type = object : TypeToken<UserInfoDTO?>() {}.type
            userInfoDTO = Gson().fromJson(prefs.getString(user_info_dto, ""), type)
            creationTimenoteViewModel.setCreatedBy(userInfoDTO.id!!)
            accountType = userInfoDTO.status

            if (args.modify == 0)
                creationTimenoteViewModel.getCreateTimeNoteLiveData().observe(viewLifecycleOwner) {
                    populateModel(it)
                } else {
                creationTimenoteViewModel.setDuplicateOrEdit(args.timenoteBody!!)
                creationTimenoteViewModel.setCreatedBy(userInfoDTO.id!!)
                creationTimenoteViewModel.getCreateTimeNoteLiveData().observe(viewLifecycleOwner) {
                    populateModel(args.timenoteBody!!)
                }
            }

            prefs.stringLiveData(offset, "+00:00").observe(viewLifecycleOwner) {
                if (isCreatedOffset) {
                    if (creationTimenoteViewModel.getCreateTimeNoteLiveData().value != null && creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.startingAt?.isNotBlank()!!)
                        creationTimenoteViewModel.setStartDateOffset(
                            creationTimenoteViewModel.setOffset(
                                ISO,
                                creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.startingAt!!,
                                it
                            )
                        )
                    if (creationTimenoteViewModel.getCreateTimeNoteLiveData().value != null && creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.endingAt?.isNotBlank()!!)
                        creationTimenoteViewModel.setEndDateOffset(
                            creationTimenoteViewModel.setOffset(
                                ISO,
                                creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.endingAt!!,
                                it
                            )
                        )
                }
            }

            visibilityTimenote = prefs.getInt(default_settings_at_creation_time, 1)
            when (visibilityTimenote) {
                0 -> {
                    shareWithTv.text = getString(R.string.only_me)
                    creationTimenoteViewModel.setSharedWith(listOf(userInfoDTO.id!!))
                }
                1 -> {
                    shareWithTv.text = if(accountType == 1) getString(R.string.everyone) else getString(R.string.everyone)
                    creationTimenoteViewModel.setSharedWith(listOf())
                }
            }
        }
    }

    private fun setUp() {
        utils = Utils()
        create_timenote_clear.paintFlags = create_timenote_clear.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        dateFormatDateAndTime = SimpleDateFormat(DATE_FORMAT_DAY_AND_TIME, Locale.getDefault())
        dateFormatDateAndTimeDialog = SimpleDateFormat(DATE_FORMAT_DAY_AND_TIME_DIALOG, Locale.getDefault())
        dateFormatDateRepeat = SimpleDateFormat(DATE_FORMAT_REPEAT, Locale.getDefault())
        dateFormatDateAndTimeISO = SimpleDateFormat(ISO, Locale.getDefault())
        subcategoryCv = sub_category_cardview
        addEndDateTv = profile_add_end_date
        configureRecurrence = profile_configure_recurrence
        fromLabel = from_label
        toLabel = to_label
        datesRv = profile_recurence_rv
        paidTimenote = paid_timenote_cardview
        fixedDate = create_timenote_fixed_date
        fromTv = create_timenote_from
        toTv = create_timenote_to
        categoryTv = create_timenote_category
        picVideoIv = timenote_pic
        titleTv = create_timenote_title_btn
        noAnswer = create_timenote_paid_timenote_status_no_answer
        shareWithTv = create_timenote_share_with
        moreColorTv = create_timenote_fifth_color
        firstColorTv = create_timenote_first_color
        secondColorTv = create_timenote_second_color
        thirdColorTv = create_timenote_third_color
        fourthColorTv = create_timenote_fourth_color
        takeAddPicTv = create_timenote_take_add_pic
        progressBar = create_timenote_pb
        descTv = create_timenote_desc_btn
        descCv = desc_cardview
        titleError = create_timenote_title_error
        whenError = create_timenote_when_error
        subCatTv = create_timenote_sub_category
        subCatLabelTv = create_timenote_sub_category_label
        catLabelTv = create_timenote_category_label
        paidLabelTv = create_timenote_paid_timenote_label
        labelCv = url_title_cardview
        picCl = pic_cl

        create_timenote_next_btn.setOnClickListener(this)
        from_label.setOnClickListener(this)
        to_label.setOnClickListener(this)
        where_cardview.setOnClickListener(this)
        share_with_cardview.setOnClickListener(this)
        category_cardview.setOnClickListener(this)
        title_cardview.setOnClickListener(this)
        create_timenote_fifth_color.setOnClickListener(this)
        create_timenote_first_color.setOnClickListener(this)
        create_timenote_second_color.setOnClickListener(this)
        create_timenote_third_color.setOnClickListener(this)
        create_timenote_fourth_color.setOnClickListener(this)
        create_timenote_take_add_pic.setOnClickListener(this)
        create_timenote_desc_btn.setOnClickListener(this)
        desc_cardview.setOnClickListener(this)
        when_cardview.setOnClickListener(this)
        addEndDateTv.setOnClickListener(this)
        configureRecurrence.setOnClickListener(this)
        paidTimenote.setOnClickListener(this)
        noAnswer.setOnClickListener(this)
        subcategoryCv.setOnClickListener(this)
        create_timenote_btn_back.setOnClickListener(this)
        url_cardview.setOnClickListener(this)
        organizers_cv.setOnClickListener(this)
        create_timenote_clear.setOnClickListener(this)
        url_title_cardview.setOnClickListener(this)
    }

    private fun populateModel(it: CreationTimenoteDTO) {
        when (it.price.price) {
            0.0 -> {
                noAnswer.text = getString(R.string.free)
                    paidLabelTv.setTextColor(resources.getColor(R.color.colorText))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        paidLabelTv.compoundDrawableTintList = ColorStateList.valueOf(resources.getColor(R.color.colorText))
                    }
            }
            in 0.1..Double.MAX_VALUE-> {
                noAnswer.text = it.price.price.toString() + it.price.currency
                paidLabelTv.setTextColor(resources.getColor(R.color.colorText))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    paidLabelTv.compoundDrawableTintList = ColorStateList.valueOf(resources.getColor(R.color.colorText))
                }
            }
        }
        if(it.url.isNullOrBlank()) {
            url_title_cardview.visibility = View.GONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                create_timenote_url_btn.compoundDrawableTintList = ColorStateList.valueOf(resources.getColor(android.R.color.darker_gray))
                create_timenote_url_btn.setTextColor(resources.getColor(android.R.color.darker_gray))
            }
            create_timenote_url_btn.text = getString(R.string.add_an_url)
        } else {
            url_title_cardview.visibility = View.VISIBLE
            create_timenote_url_btn.text = it.url
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                create_timenote_url_btn.compoundDrawableTintList = ColorStateList.valueOf(resources.getColor(R.color.colorText))
                create_timenote_url_btn.setTextColor(resources.getColor(R.color.colorText))
            }
            if(it.urlTitle.isNullOrBlank() || it.urlTitle.isNullOrEmpty()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    create_timenote_url_title_btn.compoundDrawableTintList = ColorStateList.valueOf(resources.getColor(android.R.color.darker_gray))
                    create_timenote_url_title_btn.setTextColor(resources.getColor(android.R.color.darker_gray))
                }
                create_timenote_url_title_btn.text = ""
            } else {
                create_timenote_url_title_btn.text = it.urlTitle
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    create_timenote_url_title_btn.compoundDrawableTintList = ColorStateList.valueOf(resources.getColor(R.color.colorText))
                    create_timenote_url_title_btn.setTextColor(resources.getColor(R.color.colorText))
                }
            }
        }
        if (it.category == null || it.category?.category.isNullOrEmpty() || it.category?.category.isNullOrBlank()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                catLabelTv.compoundDrawableTintList = ColorStateList.valueOf(resources.getColor(android.R.color.darker_gray))
                catLabelTv.setTextColor(resources.getColor(android.R.color.darker_gray))
            }
            create_timenote_category.text = ""
        } else {
            create_timenote_category.text = it.category!!.category
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                catLabelTv.compoundDrawableTintList = ColorStateList.valueOf(resources.getColor(R.color.colorText))
                catLabelTv.setTextColor(resources.getColor(R.color.colorText))
            }
        }
        if (it.category != null && it.category!!.subcategory.isNotBlank() && it.category!!.subcategory.isNotEmpty()){
                subCatLabelTv.setTextColor(resources.getColor(R.color.colorText))
            subcategoryCv.visibility = View.VISIBLE
            subCatTv.text = it.category!!.subcategory
        } else {
            subCatTv.text = ""
            subCatLabelTv.setTextColor(resources.getColor(android.R.color.darker_gray))
        }
        if(it.category == null || (it.category!=null && it.category!!.category.isEmpty()) || (it.category != null && it.category!!.category.isBlank())) subcategoryCv.visibility = View.GONE
        if(args.modify == 0){
            if (images?.isNullOrEmpty()!!) {
                showChooseBackground()
                takeAddPicTv.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                picCl.visibility = View.GONE
            } else {
                hideChooseBackground()
                takeAddPicTv.visibility = View.GONE
                progressBar.visibility = View.GONE
                picCl.visibility = View.VISIBLE
                Glide.with(requireContext()).load(images!![0]).into(picVideoIv)
            }
        } else {
            if (it.pictures?.isNullOrEmpty()!!) {
                showChooseBackground()
                takeAddPicTv.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                picCl.visibility = View.GONE
            } else {
                hideChooseBackground()
                takeAddPicTv.visibility = View.GONE
                progressBar.visibility = View.GONE
                picCl.visibility = View.VISIBLE
                Glide.with(requireContext()).load(it.pictures!![0]).into(picVideoIv)
            }
        }

        if (it.title.isBlank()) titleTv.text = getString(R.string.title_create_event) else titleTv.text = it.title
        if (it.location == null || it.location?.address?.address?.isBlank()!!) create_timenote_where_btn.text =
            getString(R.string.where) else create_timenote_where_btn.text = it.location?.address?.address?.plus(
            ", "
        )?.plus(it.location?.address?.city)?.plus(" ")?.plus(it.location?.address?.country)
        if (it.startingAt.isNotBlank() && it.endingAt.isNotBlank() && it.startingAt != it.endingAt) {
            fixedDate.visibility = View.GONE
            toLabel.visibility = View.VISIBLE
            fromLabel.visibility = View.VISIBLE
            toTv.visibility = View.VISIBLE
            fromTv.visibility = View.VISIBLE
            if(it.startingAt.contains("Z")) creationTimenoteViewModel.setStartDate(SimpleDateFormat(ISORECEIVED, Locale.getDefault()).parse(it.startingAt)!!.time, ISO)
            fromTv.text = dateFormatDateAndTime.format(SimpleDateFormat(ISO, Locale.getDefault()).parse(it.startingAt)!!)
            if(it.endingAt.contains("Z")) creationTimenoteViewModel.setEndDate(SimpleDateFormat(ISORECEIVED, Locale.getDefault()).parse(it.endingAt)!!.time, ISO)
            toTv.text = dateFormatDateAndTime.format(SimpleDateFormat(ISO, Locale.getDefault()).parse(it.endingAt)!!)
        } else if(it.startingAt.isNotBlank() && it.endingAt.isNotBlank() && it.startingAt == it.endingAt) {
            fixedDate.visibility = View.VISIBLE
            toLabel.visibility = View.GONE
            fromLabel.visibility = View.GONE
            toTv.visibility = View.GONE
            fromTv.visibility = View.GONE
            if(it.startingAt.contains("Z")) creationTimenoteViewModel.setStartDate(SimpleDateFormat(ISORECEIVED, Locale.getDefault()).parse(it.startingAt)!!.time, ISO)
            fixedDate.text = dateFormatDateAndTime.format(SimpleDateFormat(ISO, Locale.getDefault()).parse(it.startingAt)!!)
        } else if(it.startingAt.isEmpty() || it.startingAt.isBlank()){
            addEndDateTv.visibility = View.GONE
            fixedDate.visibility = View.GONE
            toLabel.visibility = View.GONE
            fromLabel.visibility = View.GONE
            toTv.visibility = View.GONE
            fromTv.visibility = View.GONE
        }
        if (!it.colorHex.isNullOrBlank()) {
            when (it.colorHex) {
                "#ff8800" -> colorChoosedUI(
                    secondColorTv,
                    thirdColorTv,
                    fourthColorTv,
                    moreColorTv,
                    firstColorTv
                )
                "#cc0000" -> colorChoosedUI(
                    thirdColorTv,
                    fourthColorTv,
                    moreColorTv,
                    firstColorTv,
                    secondColorTv
                )
                "#0099cc" -> colorChoosedUI(
                    fourthColorTv,
                    moreColorTv,
                    firstColorTv,
                    secondColorTv,
                    thirdColorTv
                )
                "#aa66cc" -> colorChoosedUI(
                    thirdColorTv,
                    moreColorTv,
                    firstColorTv,
                    secondColorTv,
                    fourthColorTv
                )
                else -> {
                    colorChoosedUI(
                        firstColorTv,
                        secondColorTv,
                        thirdColorTv,
                        fourthColorTv,
                        moreColorTv
                    )
                    moreColorTv.setBackgroundColor(Color.parseColor(it.colorHex))
                }

            }
        } else {
            firstColorTv.setBorderWidth(0)
            secondColorTv.setBorderWidth(0)
            thirdColorTv.setBorderWidth(0)
            fourthColorTv.setBorderWidth(0)
            moreColorTv.setBorderWidth(0)
            moreColorTv.setBackgroundColor(resources.getColor(R.color.colorText))
        }
        if(it.organizers.isNullOrEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                create_timenote_organizers_btn.compoundDrawableTintList = ColorStateList.valueOf(resources.getColor(android.R.color.darker_gray))
                create_timenote_organizers_btn.setTextColor(resources.getColor(android.R.color.darker_gray))
            }
            create_timenote_organizers_btn.text = getString(R.string.organizers)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                create_timenote_organizers_btn.compoundDrawableTintList = ColorStateList.valueOf(resources.getColor(R.color.colorText))
                create_timenote_organizers_btn.setTextColor(resources.getColor(R.color.colorText))
            }
            if(it.organizers?.size == 1) create_timenote_organizers_btn.text = resources.getQuantityText(R.plurals.nbr_orga, it.organizers?.size!!)
            else create_timenote_organizers_btn.text = resources.getQuantityText(R.plurals.nbr_orga, it.organizers?.size!!)
        }
        when {
            it.sharedWith.isNullOrEmpty() -> create_timenote_share_with.text = if(accountType == 1) getString(R.string.followers) else getString(R.string.everyone)
            else -> {
                if(it.sharedWith?.size == 1 && it.sharedWith?.get(0) == it.createdBy) create_timenote_share_with.text = getString(R.string.only_me)
                else create_timenote_share_with.text = getString(R.string.shared)
            }
        }

        if(it.title.isNotEmpty() && it.title.isNotBlank()){
            descCv.visibility = View.VISIBLE
            descTv.text = it.description
        } else {
            descCv.visibility = View.GONE
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        creationTimenoteViewModel.fetchLocation(place.id!!, getString(R.string.api_web_key)).observe(
                            viewLifecycleOwner
                        ) {
                            creationTimenoteViewModel.setLocation(
                                utils.setLocation(
                                    it.body()!!,
                                    true,
                                    prefs
                                )
                            )
                        }
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {

                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openDialogToChoosePic()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
        when (v) {
            when_cardview -> if(dateToAdd.isNullOrEmpty()) MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    whenError.visibility = View.GONE
                    dateTimePicker { _, datetime ->
                        fixedDate.visibility = View.VISIBLE
                        toLabel.visibility = View.GONE
                        fromLabel.visibility = View.GONE
                        toTv.visibility = View.GONE
                        fromTv.visibility = View.GONE
                        startDateDisplayed = dateFormatDateAndTime.format(datetime.time.time)
                        creationTimenoteViewModel.setStartDate(datetime.time.time, ISO)
                        startDate = datetime.time.time
                        creationTimenoteViewModel.setEndDate(datetime.time.time, ISO)
                        addEndDateTv.visibility = View.VISIBLE
                        configureRecurrence.visibility = View.VISIBLE
                    }
                }
            configureRecurrence -> {
                dateToAdd.clear()
                val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
                    customView(R.layout.dialog_reccurence)
                    lifecycleOwner(this@CreateTimenote)
                }
                val repeatAllNP = dial.getCustomView().numberPicker
                val nbrOccurencesNP = dial.getCustomView().numberPicker_occurences

                val pb = dial.getCustomView().pb
                var status = 0
                var endDateDialog = 0L
                val spinner = dial.getCustomView().spinner_reccurence
                val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.period, R.layout.custom_spinner_dd)
                spinner.adapter = adapter
                val repeatMonth = dial.getCustomView().dialog_reccurence_repeat_month
                val repeatWeek = dial.getCustomView().dialog_reccurence_repeat_week
                val week = dial.getCustomView().dialog_reccurence_ll
                val monday = dial.getCustomView().dialog_reccurence_monday
                var mondayStatus = true
                val tuesday = dial.getCustomView().dialog_reccurence_tuesday
                var tuesdayStatus = false
                val wednesday = dial.getCustomView().dialog_reccurence_wednesday
                var wednesdayStatus = false
                val thursday = dial.getCustomView().dialog_reccurence_thursday
                var thursdayStatus = false
                val friday = dial.getCustomView().dialog_reccurence_friday
                var fridayStatus = false
                val saturday = dial.getCustomView().dialog_reccurence_saturday
                var saturdayStatus = false
                val sunday = dial.getCustomView().dialog_reccurence_sunday
                var sundayStatus = false
                monday.setOnClickListener {
                    mondayStatus = !mondayStatus
                    if(mondayStatus) monday.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorAccent)) else monday.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorText)) }
                tuesday.setOnClickListener {
                    tuesdayStatus = !tuesdayStatus
                    if(tuesdayStatus) tuesday.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorAccent)) else tuesday.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorText)) }
                wednesday.setOnClickListener {
                    wednesdayStatus = !wednesdayStatus
                    if(wednesdayStatus) wednesday.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorAccent)) else wednesday.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorText)) }
                thursday.setOnClickListener {
                    thursdayStatus = !thursdayStatus
                    if(thursdayStatus) thursday.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorAccent)) else thursday.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorText)) }
                friday.setOnClickListener {
                    fridayStatus = !fridayStatus
                    if(fridayStatus) friday.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorAccent)) else friday.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorText)) }
                saturday.setOnClickListener {
                    saturdayStatus = !saturdayStatus
                    if(saturdayStatus) saturday.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorAccent)) else saturday.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorText)) }
                sunday.setOnClickListener {
                    sundayStatus = !sundayStatus
                    if(sundayStatus) sunday.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorAccent)) else sunday.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorText)) }
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        status = p2
                        when(p2){
                            0 -> {
                                repeatMonth.visibility = View.GONE
                                repeatWeek.visibility = View.GONE
                                week.visibility = View.GONE
                            }
                            1 -> {
                                repeatMonth.visibility = View.GONE
                                repeatMonth.text = String.format(resources.getString(R.string.every_month), dateFormatDateRepeat.format(startDate))
                                repeatWeek.visibility = View.GONE
                                week.visibility = View.GONE
                            }
                            2 -> {
                                repeatMonth.visibility = View.GONE
                                repeatWeek.visibility = View.VISIBLE
                                week.visibility = View.VISIBLE
                            }
                            3 -> {
                                repeatMonth.visibility = View.GONE
                                repeatWeek.visibility = View.GONE
                                week.visibility = View.GONE
                            }
                        }                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
                }
                val endChoiceThe = dial.getCustomView().dialog_reccurence_the
                endChoiceThe.isChecked = true
                val endChoiceAfter = dial.getCustomView().dialog_reccurence_after
                endChoiceThe.setOnCheckedChangeListener { _, b ->
                    if(b) endChoiceAfter.isChecked = false
                }
                endChoiceAfter.setOnCheckedChangeListener{_,b ->
                    if(b) endChoiceThe.isChecked = false
                }
                val endDate = dial.getCustomView().dialog_reccurence_date
                endDate.text = dateFormatDateAndTimeDialog.format(startDate)
                endDate.setOnClickListener {
                    MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        val c = Calendar.getInstance()
                        if(startDate != null) c.timeInMillis = startDate!!
                        datePicker(requireFutureDate = true, currentDate = c) { dialog, datetime ->
                            endDateDialog = datetime.time.time
                            endDate.text = dateFormatDateAndTimeDialog.format(datetime.time.time)
                        }
                    }
                }

                val validateBtn = dial.getCustomView().dialog_reccurence_validate
                validateBtn.setOnClickListener {
                    dateToAdd.add(dateFormatDateAndTimeISO.format(startDate!!))
                    val every = repeatAllNP.progress
                    var ocurence = nbrOccurencesNP.progress
                    val c = Calendar.getInstance()
                    c.timeInMillis = startDate!!
                    when(status){
                        0 -> {
                            validateBtn.visibility = View.GONE
                            pb.visibility = View.VISIBLE
                            if(endChoiceAfter.isChecked){
                                while(ocurence > 0){
                                    c.add(Calendar.DAY_OF_MONTH, every)
                                    dateToAdd.add(dateFormatDateAndTimeISO.format(c.timeInMillis))
                                    ocurence--
                                }
                            } else{
                                var date = 0L
                                while(endDateDialog > date){
                                    c.add(Calendar.DAY_OF_MONTH, every)
                                    dateToAdd.add(dateFormatDateAndTimeISO.format(c.timeInMillis))
                                    date = c.timeInMillis
                                }
                                dateToAdd.removeLast()
                            }
                            if(dateToAdd.size > 30) Toast.makeText(
                                requireContext(),
                                resources.getString(R.string.too_many_occurrences),
                                Toast.LENGTH_SHORT
                            )
                                .show() else dial.dismiss()                        }
                        1 -> {
                            validateBtn.visibility = View.GONE
                            pb.visibility = View.VISIBLE
                            if(endChoiceAfter.isChecked){
                                while (ocurence > 0){
                                    c.add(Calendar.MONTH, every)
                                    dateToAdd.add(dateFormatDateAndTimeISO.format(c.timeInMillis))
                                    ocurence--
                                }
                            } else{
                                var date = 0L
                                while(endDateDialog > date){
                                    c.add(Calendar.MONTH, every)
                                    dateToAdd.add(dateFormatDateAndTimeISO.format(c.timeInMillis))
                                    date = c.timeInMillis
                                }
                                dateToAdd.removeLast()
                            }
                            if(dateToAdd.size > 30) Toast.makeText(
                                requireContext(),
                                resources.getString(R.string.too_many_occurrences),
                                Toast.LENGTH_SHORT
                            )
                                .show() else dial.dismiss()                        }
                        2 -> {
                            validateBtn.visibility = View.GONE
                            pb.visibility = View.VISIBLE
                            val statusDay = listOf(mondayStatus, tuesdayStatus, wednesdayStatus, thursdayStatus, fridayStatus, saturdayStatus, sundayStatus)
                            val calendarDay = listOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY)
                            if(endChoiceAfter.isChecked){
                                statusDay.forEachIndexed { index, b ->  if(b) repeatEveryWeekAtDay(ocurence, c, every, calendarDay[index], true, endDateDialog) }
                            } else{
                                statusDay.forEachIndexed { index, b ->  if(b) {
                                    repeatEveryWeekAtDay(ocurence, c, every, calendarDay[index], false, endDateDialog)
                                    dateToAdd.removeLast()
                                } }
                            }
                            if(dateToAdd.size > 30) Toast.makeText(
                                requireContext(),
                                resources.getString(R.string.too_many_occurrences),
                                Toast.LENGTH_SHORT
                            )
                                .show() else dial.dismiss()                        }
                        3 -> {
                            validateBtn.visibility = View.GONE
                            pb.visibility = View.VISIBLE
                            if(endChoiceAfter.isChecked){
                                while (ocurence > 0){
                                    c.add(Calendar.YEAR, every)
                                    dateToAdd.add(dateFormatDateAndTimeISO.format(c.timeInMillis))
                                    ocurence--
                                }
                            } else{
                                var date = 0L
                                while(endDateDialog > date){
                                    c.add(Calendar.YEAR, every)
                                    dateToAdd.add(dateFormatDateAndTimeISO.format(c.timeInMillis))
                                    date = c.timeInMillis
                                }
                                dateToAdd.removeLast()
                            }
                            if(dateToAdd.size > 30) Toast.makeText(
                                requireContext(),
                                resources.getString(R.string.too_many_occurrences),
                                Toast.LENGTH_SHORT
                            )
                                .show() else dial.dismiss()
                        }
                    }
                    addEndDateTv.visibility = View.GONE
                    configureRecurrence.visibility = View.GONE
                    datesRv.visibility = View.VISIBLE
                    dateAdapter = DateAdapter(dateToAdd, this)
                    datesRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    datesRv.adapter = dateAdapter
                }
            }
            addEndDateTv -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                val c = Calendar.getInstance()
                if(startDate != null) c.timeInMillis = startDate!!
                dateTimePicker(requireFutureDateTime = true,  currentDateTime = c) { _, datetime ->
                    fixedDate.visibility = View.GONE
                    addEndDateTv.visibility = View.GONE
                    toLabel.visibility = View.VISIBLE
                    fromLabel.visibility = View.VISIBLE
                    toTv.visibility = View.VISIBLE
                    fromTv.visibility = View.VISIBLE
                    configureRecurrence.visibility = View.GONE
                    creationTimenoteViewModel.setEndDate(datetime.time.time, ISO)
                    startDateDisplayed = dateFormatDateAndTime.format(startDate)
                    endDateDisplayed = dateFormatDateAndTime.format(datetime.time.time)
                }
            }
            from_label, fromTv -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                dateTimePicker { _, datetime ->
                    startDate = datetime.time.time
                    startDateDisplayed = dateFormatDateAndTime.format(startDate)
                    creationTimenoteViewModel.setStartDate(startDate!!, ISO)
                }
                lifecycleOwner(this@CreateTimenote)
            }
            to_label, toTv -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                dateTimePicker { dialog, datetime ->
                    endDate = datetime.time.time
                    endDateDisplayed = dateFormatDateAndTime.format(endDate)
                    creationTimenoteViewModel.setEndDate(endDate!!, ISO)
                }
                lifecycleOwner(this@CreateTimenote)
            }
            where_cardview -> startActivityForResult(Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, placesList).build(requireContext()), AUTOCOMPLETE_REQUEST_CODE)
            share_with_cardview -> shareWith()
            category_cardview -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.category)
                listItems(items = listOfHeaders) { _, index, text ->
                    if(!creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.category?.category.isNullOrEmpty() && creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.category?.category != text){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            subCatLabelTv.compoundDrawableTintList = ColorStateList.valueOf(resources.getColor(android.R.color.darker_gray))
                            subCatLabelTv.setTextColor(resources.getColor(android.R.color.darker_gray))
                        }
                        subCatTv.text = ""
                        creationTimenoteViewModel.setCategory(Category(creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.category?.category!!, ""))
                    }
                    creationTimenoteViewModel.setCategory(Category(text.toString(), ""))
                    if(!creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.category?.category.isNullOrEmpty()) subcategoryCv.visibility = View.VISIBLE
                    dismiss()
                }
                negativeButton(R.string.clear){
                    categoryTv.text = ""
                    subCatTv.text = ""
                    creationTimenoteViewModel.setCategory(Category("", ""))
                    subcategoryCv.visibility = View.GONE
                }

                lifecycleOwner(this@CreateTimenote)
            }
            sub_category_cardview -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.subcategory)
                val listSubcategories : MutableList<String> = mutableListOf()
                listOfCategories.filter { categories -> categories.category == creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.category?.category }.forEach { category -> listSubcategories.add(category.subcategory)}
                listItems(items = listSubcategories) { _, _, text ->
                    creationTimenoteViewModel.setCategory(Category(creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.category?.category!!, text.toString()))
                    dismiss()
                }

                negativeButton(R.string.clear){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        subCatLabelTv.compoundDrawableTintList = ColorStateList.valueOf(resources.getColor(android.R.color.darker_gray))
                        subCatLabelTv.setTextColor(resources.getColor(android.R.color.darker_gray))
                    }
                    subCatTv.text = ""
                    creationTimenoteViewModel.setCategory(Category(creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.category?.category!!, ""))
                }

                lifecycleOwner(this@CreateTimenote)
            }
            title_cardview -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                titleError.visibility = View.GONE
                title(R.string.title_create_event)
                input(
                    inputType = InputType.TYPE_CLASS_TEXT,
                    maxLength = 20,
                    prefill = creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.title
                ) { _, text ->
                    titleInput = text.toString()
                    titleTv.text = titleInput
                    creationTimenoteViewModel.setTitle(titleInput)
                }
                positiveButton(R.string.done) {
                    if (titleInput.isNotBlank()) {
                        descCv.visibility = View.VISIBLE
                    }
                }
                lifecycleOwner(this@CreateTimenote)
            }
            descCv, descTv -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.description)
                input(inputType = InputType.TYPE_CLASS_TEXT, prefill = creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.description, allowEmpty = true) { _, charSequence ->
                    descTv.text = charSequence.toString()
                    val hashTagHelper = HashTagHelper.Creator.create(R.color.colorText, this@CreateTimenote, null, resources)
                    hashTagHelper.handle(descTv)
                    val hashtagList = hashTagHelper.getAllHashTags(true)
                    var descWithoutHashtag = descTv.text.toString()
                    if(!descWithoutHashtag.trim().startsWith("#")) descWithoutHashtag = descWithoutHashtag.capitalize()
                    creationTimenoteViewModel.setHashtags(hashtagList)
                    creationTimenoteViewModel.setDescription(descWithoutHashtag)
                }
            }
            create_timenote_fifth_color -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.colors)
                colorChooser(
                    colors = ColorPalette.Primary,
                    subColors = ColorPalette.PrimarySub,
                    allowCustomArgb = true,
                    showAlphaSelector = true
                ) { _, color ->
                    var colorHex = '#' + Integer.toHexString(color)
                    colorHex = colorHex.removePrefix("#ff")
                    colorHex = "#$colorHex"
                    moreColorTv.setBackgroundColor(Color.parseColor(colorHex))
                    creationTimenoteViewModel.setColor(colorHex)
                    colorChoosedUI(
                        firstColorTv,
                        secondColorTv,
                        thirdColorTv,
                        fourthColorTv,
                        moreColorTv
                    )
                }

                lifecycleOwner(this@CreateTimenote)
            }
            create_timenote_first_color -> {
                colorChoosedUI(
                    secondColorTv,
                    thirdColorTv,
                    fourthColorTv,
                    moreColorTv,
                    firstColorTv
                )
                creationTimenoteViewModel.setColor("#ff8800")
            }
            create_timenote_second_color -> {
                colorChoosedUI(
                    thirdColorTv,
                    fourthColorTv,
                    moreColorTv,
                    firstColorTv,
                    secondColorTv
                )
                creationTimenoteViewModel.setColor("#cc0000")
            }
            create_timenote_third_color -> {
                colorChoosedUI(
                    fourthColorTv,
                    moreColorTv,
                    firstColorTv,
                    secondColorTv,
                    thirdColorTv
                )
                creationTimenoteViewModel.setColor("#0099cc")
            }
            create_timenote_fourth_color -> {
                colorChoosedUI(
                    thirdColorTv,
                    moreColorTv,
                    firstColorTv,
                    secondColorTv,
                    fourthColorTv
                )
                creationTimenoteViewModel.setColor("#aa66cc")
            }
            create_timenote_take_add_pic -> {
                images?.clear()
                openDialogToChoosePic()
            }
            paid_timenote_cardview -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.paid_timenote)
                listItems(
                    items = listOf(
                        getString(R.string.free), getString(R.string.paid)
                    )
                ) { _, index, text ->
                    when (index) {
                        0 -> {
                            noAnswer.text = text.toString()
                            creationTimenoteViewModel.setPrice(Price(0.0, ""))
                        }
                        1 -> {
                            noAnswer.text = text.toString()
                            MaterialDialog(
                                requireContext(),
                                BottomSheet(LayoutMode.WRAP_CONTENT)
                            ).show {
                                onCancel { creationTimenoteViewModel.setPrice(Price(0.0, "")) }
                                title(R.string.price)
                                input(inputType = InputType.TYPE_CLASS_NUMBER) { _, price ->
                                    lifecycleOwner(this@CreateTimenote)
                                    MaterialDialog(
                                        requireContext(),
                                        BottomSheet(LayoutMode.WRAP_CONTENT)
                                    ).show {
                                        title(R.string.currency)
                                        input(inputType = InputType.TYPE_CLASS_TEXT) { _, charSequence ->
                                            creationTimenoteViewModel.setPrice(
                                                Price(
                                                    price.toString().toDouble(),
                                                    charSequence.toString()
                                                )
                                            )
                                            lifecycleOwner(this@CreateTimenote)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            url_cardview -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.link)
                input(allowEmpty = true, inputType = InputType.TYPE_TEXT_VARIATION_URI, prefill = creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.url) { _, charSequence ->
                    creationTimenoteViewModel.setUrl(charSequence.toString())
                    if(charSequence.toString().isNotEmpty() && charSequence.toString().isNotBlank()){
                        labelCv.visibility = View.VISIBLE
                    }
                }
                lifecycleOwner(this@CreateTimenote)
            }
            url_title_cardview -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.label)
                input(allowEmpty = true, inputType = InputType.TYPE_CLASS_TEXT, prefill = creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.urlTitle, maxLength = 20){ _, charSequence ->
                    creationTimenoteViewModel.setUrlTitle(charSequence.toString())
                }
            }
            organizers_cv -> createFriendsBottomSheet(2, null)
            create_timenote_clear -> {
                dateToAdd.clear()
                datesRv.visibility = View.GONE
                creationTimenoteViewModel.clear()
                images = mutableListOf()
                sendTo.clear()
                organizers.clear()
                indexGroupChosen.clear()
                creationTimenoteViewModel.setCreatedBy(userInfoDTO.id!!)
            }
            create_timenote_btn_back -> {
                if (args.from == 0) backToHomeListener.onBackHome()
                else findNavController().popBackStack()
            }
            create_timenote_next_btn -> {
                if (checkFormCompleted()) {
                    if (!images?.isNullOrEmpty()!!) {
                        create_timenote_next_btn.visibility = View.GONE
                        done_pb.visibility = View.VISIBLE
                        images?.forEach {
                            pushFile(it, false)
                        }

                    } else if (!creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.colorHex.isNullOrBlank() || !creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.pictures.isNullOrEmpty()) {
                        create_timenote_next_btn.visibility = View.GONE
                        done_pb.visibility = View.VISIBLE
                        if (args.modify == 0 || args.modify == 1) {
                            createTimenoteEmptyPic()
                        } else {
                            modifyTimenote()
                        }

                    }
                }
            }
        }
    }

    private fun repeatEveryWeekAtDay(ocurence: Int, c: Calendar, every: Int, day: Int, isAfter: Boolean, endDateDialog : Long) {
        var localOccurrence = ocurence
        var date = 0L
        c.set(Calendar.DAY_OF_WEEK, day)
        if (c.timeInMillis > startDate!!)
            dateToAdd.add(dateFormatDateAndTimeISO.format(c.timeInMillis))
        if(isAfter) while (localOccurrence > 0) {
            c.add(Calendar.WEEK_OF_YEAR, every)
            dateToAdd.add(dateFormatDateAndTimeISO.format(c.timeInMillis))
            localOccurrence--
        } else while (endDateDialog > date) {
            c.add(Calendar.WEEK_OF_YEAR, every)
            dateToAdd.add(dateFormatDateAndTimeISO.format(c.timeInMillis))
            date = c.timeInMillis
        }
    }

    private fun openDialogToChoosePic() {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.take_add_a_picture)
            listItems(
                items = listOf(resources.getString(R.string.add_a_picture), resources.getString(R.string.search_on_web))) { _, index, text ->
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    when (text) {
                        resources.getString(R.string.add_a_picture) -> {
                            InsGallery.openGallery(requireActivity(), GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), object : OnResultCallbackListener<LocalMedia> {
                                        override fun onResult(result: MutableList<LocalMedia>?) {
                                            for (media in result!!) {
                                                val path: String =
                                                    if (media.isCut && !media.isCompressed) {
                                                        media.cutPath
                                                    } else if (media.isCompressed || media.isCut && media.isCompressed) {
                                                        media.compressPath
                                                    } else if (PictureMimeType.isHasVideo(media.mimeType) && !TextUtils.isEmpty(media.coverPath)) {
                                                        val o = ThumbnailUtils.createVideoThumbnail(File(media.path), Size(1024, 500), null)
                                                        val f = File(requireContext().cacheDir, UUID.randomUUID().toString())
                                                        f.createNewFile()
                                                        val bos = ByteArrayOutputStream()
                                                        o.compress(Bitmap.CompressFormat.PNG, 0, bos)
                                                        val fos = FileOutputStream(f)
                                                        fos.write(bos.toByteArray())
                                                        fos.flush()
                                                        fos.close()
                                                        f.path
                                                    } else {
                                                        media.path
                                                    }


                                                if(PictureMimeType.isHasVideo(media.mimeType)) video = File(media.path)
                                                ImageCompressor.compressBitmap(requireContext(), File(path)) {
                                                    images?.add(it)
                                                }
                                            }
                                            Glide.with(requireContext()).load(images!![0]).into(picVideoIv)
                                            indicator?.setViewPager(vp_pic)
                                            pic_cl?.visibility = View.VISIBLE
                                            progressBar.visibility = View.GONE
                                            takeAddPicTv.visibility = View.GONE
                                            hideChooseBackground()
                                            creationTimenoteViewModel.setTitle(creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.title ?: getString(R.string.title_create_event))
                                        }

                                        override fun onCancel() {
                                        }

                                    }, ) }
                        resources.getString(R.string.search_on_web) -> utils.createWebSearchDialog(
                            context,
                            webSearchViewModel,
                            this@CreateTimenote,
                            takeAddPicTv,
                            progressBar
                        )
                    }
                } else requestPermissions(PERMISSIONS_STORAGE, 2)
            }
            lifecycleOwner(this@CreateTimenote)
        }
    }

    private fun modifyTimenote() {
        timenoteViewModel.modifySpecificTimenote(tokenId!!, args.id!!, creationTimenoteViewModel.getCreateTimeNoteLiveData().value!!).observe(viewLifecycleOwner
        ) {
            if (it.code() == 401) {
                authViewModel.refreshToken(prefs)
                    .observe(viewLifecycleOwner, androidx.lifecycle.Observer { newAccessToken ->
                        tokenId = newAccessToken
                        modifyTimenote()
                    })
            }
            if (it.isSuccessful) clearAndPreview(it.body()!!)
        }
    }

    private fun createTimenoteEmptyPic() {
        if (dateToAdd.isNullOrEmpty()) timenoteViewModel.createTimenote(tokenId!!, creationTimenoteViewModel.getCreateTimeNoteLiveData().value!!
        ).observe(viewLifecycleOwner) {
            if (it.code() == 401) {
                authViewModel.refreshToken(prefs)
                    .observe(viewLifecycleOwner) { newAccessToken ->
                        tokenId = newAccessToken
                        createTimenoteEmptyPic()
                    }
            }
            else if (it.isSuccessful) clearAndPreview(it.body()!!)
        } else {
            val timenotes : MutableList<CreationTimenoteDTO> = mutableListOf()
            dateToAdd.forEach {
                creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.startingAt = it
                creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.endingAt = it
                timenotes.add(creationTimenoteViewModel.getCreateTimeNoteLiveData().value!!.copy())
            }
            timenoteViewModel.bulkTimenote(tokenId!!, timenotes).observe(viewLifecycleOwner){
                if(it.code() == 401){
                    authViewModel.refreshToken(prefs).observe(viewLifecycleOwner){
                            newAccessToken -> tokenId = newAccessToken
                        createTimenoteEmptyPic()
                    }
                } else if(it.isSuccessful) clearAndPreview(it.body()?.first()!!)
            }
        }
    }

    private fun clearAndPreview(it: TimenoteInfoDTO) {
        startDateDisplayed = null
        endDateDisplayed = null
        create_timenote_next_btn.visibility = View.VISIBLE
        done_pb.visibility = View.GONE
        creationTimenoteViewModel.clear()
        images = mutableListOf()
        sendTo.clear()
        organizers.clear()
        indexGroupChosen.clear()
        creationTimenoteViewModel.setCreatedBy(userInfoDTO.id!!)
        imagesUrl = mutableListOf()
        findNavController().popBackStack()
        goToProfileLisner.goToProfile()
        switchToPreviewDetailedTimenoteViewModel.setTimenoteInfoDTO(it)
        switchToPreviewDetailedTimenoteViewModel.switchToPreviewDetailedTimenoteViewModel(true)
    }

    private fun shareWith() {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.share_with)
            val all = if(accountType == 1) getString(R.string.followers) else getString(R.string.everyone)
            listItems(items = listOf(all, getString(R.string.only_me), getString(R.string.groups), getString(
                            R.string.friends), getString(R.string.create_new_group))) { _, index, text ->
                when (index) {
                    0 -> creationTimenoteViewModel.setSharedWith(listOf())
                    1 -> creationTimenoteViewModel.setSharedWith(listOf(creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.createdBy!!))
                    2 -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
                        title(R.string.share_with)
                        getAllGroups()

                        positiveButton(R.string.done)
                        lifecycleOwner(this@CreateTimenote)
                    }
                    3 -> createFriendsBottomSheet(0, null)
                    4 -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                            title(R.string.name_group)
                            input(inputType = InputType.TYPE_CLASS_TEXT, maxLength = 20) { _, charSequence ->
                                createFriendsBottomSheet(1, charSequence.toString())
                            }
                            positiveButton(R.string.confirm)
                            lifecycleOwner(this@CreateTimenote)
                        }
                    else -> shareWithTv.text = text.toString()
                }
            }
        }
    }

    private fun MaterialDialog.getAllGroups() {
        profileViewModel.getAllGroups(tokenId!!).observe(viewLifecycleOwner) { response ->
            val listGroups: MutableList<String> = mutableListOf()
            if (response.code() == 401) {
                authViewModel.refreshToken(prefs)
                    .observe(viewLifecycleOwner) { newAccessToken ->
                        tokenId = newAccessToken
                        getAllGroups()
                    }
            }
            if (response.isSuccessful) response.body()!!
                .forEach { group -> listGroups.add(group.name) }
            listItemsMultiChoice(
                items = listGroups,
                initialSelection = indexGroupChosen.toIntArray(),
                allowEmptySelection = true
            ) { _, index, text ->
                index.forEach { indexes ->
                    response.body()?.get(indexes)?.users?.forEach { user ->
                        if (creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.sharedWith != null && !creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.sharedWith?.contains(
                                user.id
                            )!!
                        ) {
                            sendTo.add(user.id!!)
                        } else if (!sendTo.contains(user.id)) sendTo.add(user.id!!)
                    }
                }

                val removedList =
                    indexGroupChosen.filterNot { i -> index.toMutableList().any { i == it } }
                if (removedList.isNotEmpty()) {
                    removedList.forEach { indexes ->
                        response.body()?.get(indexes)?.users?.forEach { user ->
                            sendTo.remove(user.id)
                        }
                    }
                }

                indexGroupChosen = index.toMutableList()
                creationTimenoteViewModel.setSharedWith(sendTo)
            }
        }
    }

    private fun createFriendsBottomSheet(createGroup: Int, groupName: String?) {
        val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
            customView(R.layout.friends_search_cl)
            lifecycleOwner(this@CreateTimenote)
            positiveButton(R.string.confirm) {
                when (createGroup) {
                    0 -> creationTimenoteViewModel.setSharedWith(sendTo)
                    1 -> {
                        profileViewModel.createGroup(tokenId!!, CreateGroupDTO(groupName!!, sendTo)).observe(viewLifecycleOwner
                        ) {
                            if (it.isSuccessful) creationTimenoteViewModel.setSharedWith(sendTo)
                        }
                    }
                    2 -> creationTimenoteViewModel.setOrganizers(organizers)

                }
            }
            negativeButton(R.string.cancel)
        }
        dial.getActionButton(WhichButton.NEGATIVE)
            .updateTextColor(resources.getColor(android.R.color.darker_gray))
        val searchbar = dial.getCustomView().searchBar_friends
        searchbar?.setCardViewElevation(0)
        searchbar?.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE)
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY)
            }

            override fun afterTextChanged(s: Editable?) {}

        })
        val recyclerView = dial.getCustomView().shareWith_rv
        val userAdapter = UsersShareWithPagingAdapter(
            UsersPagingAdapter.UserComparator,
            this@CreateTimenote,
            this@CreateTimenote,
            organizers,
            sendTo,
            createGroup,
            false, Utils()
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = userAdapter
        lifecycleScope.launch {
            followViewModel.getUsers(tokenId!!, userInfoDTO.id!!,  0, prefs).collectLatest {
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

    private fun saveTemporary(image: Bitmap, dialog: MaterialDialog){
        val outputDir = requireContext().cacheDir
        val name = "IMG_${System.currentTimeMillis()}"
        val outputFile = File.createTempFile(name, ".jpg", outputDir)
        val outputStream = FileOutputStream(outputFile)
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
        ImageCompressor.compressBitmap(requireContext(), outputFile){
            images?.add(it)
            Glide.with(requireContext()).load(images!![0]).into(picVideoIv)
            pic_cl.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            takeAddPicTv.visibility = View.GONE
            hideChooseBackground()
            dialog.dismiss()
            create_timenote_next_btn.visibility = View.VISIBLE
            done_pb.visibility = View.GONE
        }
    }

    private fun hideChooseBackground() {
        card_line_0.visibility = View.GONE
        choose_color_label.visibility = View.GONE
        create_timenote_first_color.visibility = View.GONE
        create_timenote_second_color.visibility = View.GONE
        create_timenote_third_color.visibility = View.GONE
        create_timenote_fourth_color.visibility = View.GONE
        create_timenote_fifth_color.visibility = View.GONE
    }

    private fun showChooseBackground() {
        card_line_0.visibility = View.VISIBLE
        choose_color_label.visibility = View.VISIBLE
        create_timenote_first_color.visibility = View.VISIBLE
        create_timenote_second_color.visibility = View.VISIBLE
        create_timenote_third_color.visibility = View.VISIBLE
        create_timenote_fourth_color.visibility = View.VISIBLE
        create_timenote_fifth_color.visibility = View.VISIBLE
    }

    private fun colorChoosedUI(fancyButton1: FancyButton, fancyButton2: FancyButton, fancyButton3: FancyButton, fancyButton4: FancyButton, fancyButton5: FancyButton) {
        fancyButton1.setBorderWidth(24)
        fancyButton1.setBorderColor(android.R.color.black)
        fancyButton2.setBorderWidth(24)
        fancyButton2.setBorderColor(android.R.color.black)
        fancyButton3.setBorderWidth(24)
        fancyButton3.setBorderColor(android.R.color.black)
        fancyButton4.setBorderWidth(24)
        fancyButton4.setBorderColor(android.R.color.black)
        fancyButton5.setBorderWidth(0)
    }

    private fun checkFormCompleted(): Boolean {
        formCompleted = true
        val values = creationTimenoteViewModel.getCreateTimeNoteLiveData().value
        if(values?.category?.category.isNullOrEmpty() ||  values?.category?.subcategory.isNullOrEmpty()) creationTimenoteViewModel.setCategory(null)
        if (values?.endingAt.isNullOrBlank() || values?.startingAt.isNullOrBlank()) {
            formCompleted = false
            create_timenote_when_error.visibility = View.VISIBLE
        }
        if (values?.title.isNullOrBlank()) {
            formCompleted = false
            create_timenote_title_error.visibility = View.VISIBLE
        }
        if(images.isNullOrEmpty() && values?.colorHex.isNullOrBlank() && creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.pictures.isNullOrEmpty() && videoUrl.isNullOrBlank()) formCompleted = false
        if(values?.location != null){
            prefs.stringLiveData(offset, "+00:00").observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if(creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.startingAt?.isNotBlank()!!)
                    creationTimenoteViewModel.setStartDateOffset(creationTimenoteViewModel.setOffset(ISO, creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.startingAt!!, it))
                if(creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.endingAt?.isNotBlank()!!)
                    creationTimenoteViewModel.setEndDateOffset(creationTimenoteViewModel.setOffset(ISO, creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.endingAt!!, it))

            })
        }
        if (!formCompleted) Toast.makeText(
            requireContext(),
            getString(R.string.error_message_filling),
            Toast.LENGTH_SHORT
        ).show()
        return formCompleted
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun pushFile(file: File, isVideo : Boolean){
        val transferUtiliy = TransferUtility(am, requireContext())
        val key = "timenote/${UUID.randomUUID().mostSignificantBits}"
        val transferObserver = transferUtiliy.upload(bucket_dayzee_dev_image, key, file, CannedAccessControlList.Private)
        transferObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState?) {
                if (state == TransferState.COMPLETED) {
                    if(!isVideo) imagesUrl.add(am.getResourceUrl(bucket_dayzee_dev_image, key).toString())
                    else creationTimenoteViewModel.setVideo(am.getResourceUrl(bucket_dayzee_dev_image, key).toString())
                    if(video != null && !isVideo) pushFile(video!!, true)
                    else if (images?.size == imagesUrl.size) {
                        creationTimenoteViewModel.setPicUser(imagesUrl)
                        if (args.modify == 0 || args.modify == 1) {
                            createTimenotePic()
                        } else {
                            modifyTimenotePic()
                        }
                    }

                }

            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
            }

            override fun onError(id: Int, ex: java.lang.Exception?) {
            }

        })

    }

    private fun modifyTimenotePic() {
        timenoteViewModel.modifySpecificTimenote(
            tokenId!!,
            args.id!!,
            creationTimenoteViewModel.getCreateTimeNoteLiveData().value!!
        ).observe(viewLifecycleOwner) {
            if (it.code() == 401) {
                authViewModel.refreshToken(prefs)
                    .observe(viewLifecycleOwner) { newAccessToken ->
                        tokenId = newAccessToken
                        modifyTimenotePic()
                    }
            }
            if (it.isSuccessful) clearAndPreview(it.body()!!)
        }
    }

    private fun createTimenotePic() {
        if(dateToAdd.isNullOrEmpty())
        timenoteViewModel.createTimenote(
            tokenId!!,
            creationTimenoteViewModel.getCreateTimeNoteLiveData().value!!
        ).observe(
            viewLifecycleOwner
        ) {
            if (it.code() == 401) {
                authViewModel.refreshToken(prefs)
                    .observe(viewLifecycleOwner) { newAccessToken ->
                        tokenId = newAccessToken
                        createTimenotePic()
                    }
            }
            else if (it.isSuccessful) clearAndPreview(it.body()!!)
        } else {
            val timenotes : MutableList<CreationTimenoteDTO> = mutableListOf()
            dateToAdd.forEach {
                creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.startingAt = it
                creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.endingAt = it
                timenotes.add(creationTimenoteViewModel.getCreateTimeNoteLiveData().value!!.copy())
            }
            timenoteViewModel.bulkTimenote(tokenId!!, timenotes).observe(viewLifecycleOwner){
                if(it.code() == 401){
                    authViewModel.refreshToken(prefs).observe(viewLifecycleOwner){
                            newAccessToken -> tokenId = newAccessToken
                        createTimenoteEmptyPic()
                    }
                } else if(it.isSuccessful) clearAndPreview(it.body()?.first()!!)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onImageSelectedFromWeb(bitmap: String, dialog: MaterialDialog) {
        create_timenote_next_btn.visibility = View.GONE
        done_pb.visibility = View.VISIBLE
        if(webSearchViewModel.getBitmap().value != null) webSearchViewModel.getBitmap().removeObservers(viewLifecycleOwner)
        webSearchViewModel.decodeSampledBitmapFromResource(URL(bitmap), Rect(), 100, 100)
        webSearchViewModel.getBitmap().observe(viewLifecycleOwner) {
            if (it != null) {
                saveTemporary(it, dialog)
                webSearchViewModel.clearBitmap()
                webSearchViewModel.getBitmap().removeObservers(viewLifecycleOwner)
            }
        }
    }

    override fun onMoreImagesClicked(position: Int, query: String) {
        webSearchViewModel.search(query, requireContext(), (position).toLong(), getString(R.string.api_web_key))
    }

    override fun onHashTagClicked(hashTag: String?) {
    }

    override fun onSearchClicked(userInfoDTO: UserInfoDTO, isTagged: Boolean) {
    }

    override fun onUnfollow(id: String) {
    }

    override fun onRemove(id: String) {
    }

    override fun onAdd(userInfoDTO: UserInfoDTO, createGroup: Int?) {
        if(createGroup != null && createGroup == 2){
            organizers.add(userInfoDTO.id!!)
        } else {
            if (creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.sharedWith?.contains(userInfoDTO.id) != null) {
                if (!creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.sharedWith?.contains(userInfoDTO.id)!!) sendTo.add(userInfoDTO.id!!)
            } else sendTo.add(userInfoDTO.id!!)
        }
    }

    override fun onRemove(userInfoDTO: UserInfoDTO, createGroup: Int?) {
        if(createGroup != null && createGroup == 2) organizers.remove(userInfoDTO.id)
        else sendTo.remove(userInfoDTO.id)
    }

    override fun onClose(date: String) {
        dateToAdd.remove(date)
        dateAdapter.notifyDataSetChanged()
        if(dateToAdd.isNullOrEmpty()) {
            datesRv.visibility = View.GONE
            addEndDateTv.visibility = View.VISIBLE
            configureRecurrence.visibility = View.VISIBLE
        }
    }


}