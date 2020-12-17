package com.timenoteco.timenote.view.createTimenoteFlow

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ScreenSlideCreationTimenotePagerAdapter
import com.timenoteco.timenote.adapter.UsersPagingAdapter
import com.timenoteco.timenote.adapter.UsersShareWithPagingAdapter
import com.timenoteco.timenote.adapter.WebSearchAdapter
import com.timenoteco.timenote.androidView.input
import com.timenoteco.timenote.common.*
import com.timenoteco.timenote.listeners.BackToHomeListener
import com.timenoteco.timenote.listeners.ExitCreationTimenote
import com.timenoteco.timenote.listeners.GoToProfile
import com.timenoteco.timenote.listeners.TimenoteCreationPicListeners
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.viewModel.*
import com.yalantis.ucrop.UCrop
import com.zhihu.matisse.Matisse
import kotlinx.android.synthetic.main.fragment_create_timenote.*
import kotlinx.android.synthetic.main.friends_search_cl.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mehdi.sakout.fancybuttons.FancyButton
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Integer.min
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Type
import java.net.URL
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class CreateTimenote : Fragment(), View.OnClickListener,
    TimenoteCreationPicListeners, WebSearchAdapter.ImageChoosedListener, WebSearchAdapter.MoreImagesClicked,
    HashTagHelper.OnHashTagClickListener, UsersPagingAdapter.SearchPeopleListener,
    UsersShareWithPagingAdapter.SearchPeopleListener, UsersShareWithPagingAdapter.AddToSend {

    private lateinit var goToProfileLisner : GoToProfile
    private var accountType: Int = -1
    private var sendTo: MutableList<String> = mutableListOf()
    private var organizers: MutableList<String> = mutableListOf()
    private var indexGroupChosen: MutableList<Int> = mutableListOf()
    private lateinit var handler: Handler
    private val TRIGGER_AUTO_COMPLETE = 200
    private val AUTO_COMPLETE_DELAY: Long = 200
    private var visibilityTimenote: Int = -1
    private var startDateDisplayed: String? = null
    private var endDateDisplayed: String? = null
    val amazonClient = AmazonS3Client(
        BasicAWSCredentials(
            "AKIA5JWTNYVYJQIE5GWS",
            "pVf9Wxd/rK4r81FsOsNDaaOJIKE5AGbq96Lh4RB9"
        )
    )
    private val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"
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
    private lateinit var vp: ViewPager2
    private lateinit var screenSlideCreationTimenotePagerAdapter: ScreenSlideCreationTimenotePagerAdapter
    private var images: MutableList<File>? = mutableListOf()
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
    private val DATE_FORMAT_DAY_AND_TIME = "EEE, d MMM yyyy hh:mm aaa"
    private lateinit var dateFormatDateAndTime: SimpleDateFormat
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
        loginViewModel.getAuthenticationState().observe(
            requireActivity(),
            androidx.lifecycle.Observer {
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
            })
        Places.initialize(requireContext(), "AIzaSyBhM9HQo1fzDlwkIVqobfmrRmEMCWTU1CA")
        placesClient = Places.createClient(requireContext())
    }

    override fun onResume() {
        super.onResume()
        when(loginViewModel.getAuthenticationState().value){
            LoginViewModel.AuthenticationState.GUEST -> loginViewModel.markAsUnauthenticated()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        goToProfileLisner = context as GoToProfile
        backToHomeListener = context as BackToHomeListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_create_timenote, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            args = navArgs<CreateTimenoteArgs>().value
        } catch (i : InvocationTargetException){
            val k = i.message
            val m = i.targetException
        }

        if(!tokenId.isNullOrBlank()) {
            setUp()

            val type: Type = object : TypeToken<UserInfoDTO?>() {}.type
            userInfoDTO = Gson().fromJson<UserInfoDTO>(prefs.getString("UserInfoDTO", ""), type)
            creationTimenoteViewModel.setCreatedBy(userInfoDTO.id!!)
            accountType = userInfoDTO.status

            if (args.modify == 0) creationTimenoteViewModel.getCreateTimeNoteLiveData().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    populateModel(it)
                }) else {
                creationTimenoteViewModel.setDuplicateOrEdit(args.timenoteBody!!)
                creationTimenoteViewModel.setCreatedBy(userInfoDTO.id!!)
                populateModel(args.timenoteBody!!)
            }

            prefs.stringLiveData("offset", "0").observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if(creationTimenoteViewModel.getCreateTimeNoteLiveData().value != null && creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.startingAt?.isNotBlank()!!)
                    creationTimenoteViewModel.setStartDateOffset(creationTimenoteViewModel.setOffset(ISO, creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.startingAt!!, it))
                if(creationTimenoteViewModel.getCreateTimeNoteLiveData().value != null && creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.endingAt?.isNotBlank()!!)
                    creationTimenoteViewModel.setEndDateOffset(creationTimenoteViewModel.setOffset(ISO, creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.endingAt!!, it))
            })

            visibilityTimenote = prefs.getInt("default_settings_at_creation_time", 1)
            when (visibilityTimenote) {
                0 -> {
                    shareWithTv.text = getString(R.string.only_me)
                    creationTimenoteViewModel.setSharedWith(listOf(userInfoDTO.id!!))
                }
                1 -> {
                    shareWithTv.text = if(accountType == 1) "Followers" else "Everyone"
                    creationTimenoteViewModel.setSharedWith(listOf())
                }
            }
        }
    }

    private fun populateModel(it: CreationTimenoteDTO) {
        when (it.price.price) {
            0 -> {
                noAnswer.text = getString(R.string.free)
                    paidLabelTv.setTextColor(resources.getColor(R.color.colorText))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        paidLabelTv.compoundDrawableTintList = ColorStateList.valueOf(resources.getColor(R.color.colorText))
                    }
            }
            in 1..Int.MAX_VALUE -> {
                noAnswer.text = it.price.price.toString() + it.price.currency
                paidLabelTv.setTextColor(resources.getColor(R.color.colorText))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    paidLabelTv.compoundDrawableTintList = ColorStateList.valueOf(resources.getColor(R.color.colorText))
                }
            }
            /*else -> {
                noAnswer.text = ""
                paidLabelTv.setTextColor(resources.getColor(android.R.color.darker_gray))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    paidLabelTv.compoundDrawableTintList = ColorStateList.valueOf(resources.getColor(android.R.color.darker_gray))
                }
            }*/
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
            }
        }

        if (it.title.isBlank()) titleTv.text = getString(R.string.title) else titleTv.text = it.title
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
            fromTv.text = startDateDisplayed
            toTv.text = endDateDisplayed
        } else if(it.startingAt.isNotBlank() && it.endingAt.isNotBlank() && it.startingAt == it.endingAt) {
            fixedDate.visibility = View.VISIBLE
            toLabel.visibility = View.GONE
            fromLabel.visibility = View.GONE
            toTv.visibility = View.GONE
            fromTv.visibility = View.GONE
            fixedDate.text = startDateDisplayed
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
            if(it.organizers?.size == 1) create_timenote_organizers_btn.text =
                "${it.organizers?.size.toString()} Organizer"
            else create_timenote_organizers_btn.text =
                "${it.organizers?.size.toString()} Organizers"
        }
        when {
            it.sharedWith.isNullOrEmpty() -> create_timenote_share_with.text = if(accountType == 1) "Followers" else "Everyone"
            else -> {
                if(it.sharedWith?.size == 1 && it.sharedWith?.get(0) == it.createdBy) create_timenote_share_with.text = "Only Me"
                else create_timenote_share_with.text = "Shared"
            }
        }

        if(it.title.isNotEmpty() && it.title.isNotBlank()){
            descCv.visibility = View.VISIBLE
            descTv.text = it.description
        } else {
            descCv.visibility = View.GONE
        }


    }

    private fun setUp() {
        utils = Utils()
        //progressDialog = utils.progressDialog(requireContext())
        create_timenote_clear.paintFlags = create_timenote_clear.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        dateFormatDateAndTime = SimpleDateFormat(DATE_FORMAT_DAY_AND_TIME, Locale.getDefault())
        subcategoryCv = sub_category_cardview
        addEndDateTv = profile_add_end_date
        fromLabel = from_label
        toLabel = to_label
        paidTimenote = paid_timenote_cardview
        fixedDate = create_timenote_fixed_date
        fromTv = create_timenote_from
        toTv = create_timenote_to
        categoryTv = create_timenote_category
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
        vp = vp_pic
        if(args.modify == 0) {
            screenSlideCreationTimenotePagerAdapter = ScreenSlideCreationTimenotePagerAdapter(
                this@CreateTimenote,
                images,
                hideIcons = false,
                fromDuplicateOrEdit = false,
                pictures = listOf()
            )
            vp_pic.apply {
                adapter = screenSlideCreationTimenotePagerAdapter
            }
        } else {
            screenSlideCreationTimenotePagerAdapter = ScreenSlideCreationTimenotePagerAdapter(
                this@CreateTimenote,
                images,
                hideIcons = true,
                fromDuplicateOrEdit = true,
                pictures = args.timenoteBody?.pictures
            )
            vp_pic.apply {
                adapter = screenSlideCreationTimenotePagerAdapter
            }
        }

        indicator.setViewPager(vp_pic)
        screenSlideCreationTimenotePagerAdapter.registerAdapterDataObserver(indicator.adapterDataObserver)

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
        desc_cardview.setOnClickListener(this)
        when_cardview.setOnClickListener(this)
        addEndDateTv.setOnClickListener(this)
        paidTimenote.setOnClickListener(this)
        noAnswer.setOnClickListener(this)
        subcategoryCv.setOnClickListener(this)
        create_timenote_btn_back.setOnClickListener(this)
        url_cardview.setOnClickListener(this)
        organizers_cv.setOnClickListener(this)
        create_timenote_clear.setOnClickListener(this)
        url_title_cardview.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        creationTimenoteViewModel.fetchLocation(place.id!!).observe(
                            viewLifecycleOwner,
                            androidx.lifecycle.Observer {
                                creationTimenoteViewModel.setLocation(utils.setLocation(it.body()!!, true, prefs))
                            })
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
        } else if(requestCode == 112 && resultCode == Activity.RESULT_OK){
            val r = Matisse.obtainResult(data)
            for(image in r!!){
                ImageCompressor.compressBitmap(requireContext(), File(getPath(image)!!)){
                    images?.add(it)
                    //images?.add("file://${it.absolutePath}")
                }
            }
            screenSlideCreationTimenotePagerAdapter.images = images
            screenSlideCreationTimenotePagerAdapter.notifyDataSetChanged()
            indicator.setViewPager(vp_pic)
            pic_cl.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            takeAddPicTv.visibility = View.GONE
            hideChooseBackground()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                utils.picturePickerTimenote(
                    requireContext(),
                    resources,
                    takeAddPicTv,
                    progressBar,
                    this,
                    webSearchViewModel
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
        when (v) {
            when_cardview -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
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
                    creationTimenoteViewModel.setEndDate(datetime.time.time)
                    addEndDateTv.visibility = View.VISIBLE
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
                    creationTimenoteViewModel.setEndDate(datetime.time.time)
                    startDateDisplayed = dateFormatDateAndTime.format(startDate)
                    endDateDisplayed = dateFormatDateAndTime.format(datetime.time.time)
                }
            }
            from_label -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                dateTimePicker { _, datetime ->
                    startDate = datetime.time.time
                    startDateDisplayed = dateFormatDateAndTime.format(startDate)
                    creationTimenoteViewModel.setStartDate(startDate!!, ISO)
                }
                lifecycleOwner(this@CreateTimenote)
            }
            to_label -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                dateTimePicker { dialog, datetime ->
                    endDate = datetime.time.time
                    endDateDisplayed = dateFormatDateAndTime.format(endDate)
                    creationTimenoteViewModel.setEndDate(endDate!!)
                }
                lifecycleOwner(this@CreateTimenote)
            }
            where_cardview -> startActivityForResult(
                Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY,
                    placesList
                ).build(requireContext()), AUTOCOMPLETE_REQUEST_CODE
            )
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
                title(R.string.title)
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
                    if (!titleInput.isBlank()) {
                        descCv.visibility = View.VISIBLE
                    }
                }
                lifecycleOwner(this@CreateTimenote)
            }
            descCv -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.description)
                input(inputType = InputType.TYPE_CLASS_TEXT, prefill = creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.description, allowEmpty = true) { _, charSequence ->
                    descTv.text = charSequence.toString()
                    val hashTagHelper = HashTagHelper.Creator.create(R.color.colorText, this@CreateTimenote, null, resources)
                    hashTagHelper.handle(descTv)
                    val hashtagList = hashTagHelper.getAllHashTags(true)
                    var descWithoutHashtag = descTv.text.toString()
                    //for (hashtag in hashtagList) { descWithoutHashtag = descWithoutHashtag.replace(hashtag, "") }
                    //val descWithoutHashtagFormated = descWithoutHashtag.replace("\\s+".toRegex(), " ").trim().capitalize()
                    if(!descWithoutHashtag.trim().startsWith("#")) descWithoutHashtag = descWithoutHashtag.capitalize()
                    creationTimenoteViewModel.setHashtags(hashtagList)
                    creationTimenoteViewModel.setDescription(descWithoutHashtag)
                }
            }
            create_timenote_fifth_color -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
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
                utils.picturePickerTimenote(
                    requireContext(),
                    resources,
                    takeAddPicTv,
                    progressBar,
                    this,
                    webSearchViewModel
                )
            }
            paid_timenote_cardview -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.paid_timenote)
                listItems(
                    items = listOf(
                        getString(R.string.free), getString(R.string.paid)
                    )
                ) { _, index, text ->
                    when (index) {
                        0 -> {
                            noAnswer.text = text.toString()
                            creationTimenoteViewModel.setPrice(Price(0, ""))
                        }
                        1 -> {
                            noAnswer.text = text.toString()
                            MaterialDialog(
                                requireContext(),
                                BottomSheet(LayoutMode.WRAP_CONTENT)
                            ).show {
                                onCancel { creationTimenoteViewModel.setPrice(Price(0, "")) }
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
                                                    price.toString().toInt(),
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
                        //progressDialog.show()
                        create_timenote_next_btn.visibility = View.GONE
                        done_pb.visibility = View.VISIBLE
                        for (image in images!!) {
                            /*var file: File = if(image.contains("content://")) {
                                File(getPath(Uri.parse(image))!!)
                            } else File(image)*/

                            pushPic(image)
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

    private fun modifyTimenote() {
        timenoteViewModel.modifySpecificTimenote(tokenId!!, args.id!!, creationTimenoteViewModel.getCreateTimeNoteLiveData().value!!).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it.code() == 401) {
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, androidx.lifecycle.Observer {newAccessToken ->
                    tokenId = newAccessToken
                    modifyTimenote()
                })
            }
            if(it.isSuccessful) clearAndPreview(it)
        })
    }

    private fun createTimenoteEmptyPic() {
        timenoteViewModel.createTimenote(tokenId!!, creationTimenoteViewModel.getCreateTimeNoteLiveData().value!!
        ).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it.code() == 401) {
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, androidx.lifecycle.Observer {newAccessToken ->
                    tokenId = newAccessToken
                    createTimenoteEmptyPic()
                })
            }
            if(it.isSuccessful) clearAndPreview(it)
        })
    }

    private fun clearAndPreview(it: Response<TimenoteInfoDTO>) {
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
        switchToPreviewDetailedTimenoteViewModel.setTimenoteInfoDTO(it.body()!!)
        switchToPreviewDetailedTimenoteViewModel.switchToPreviewDetailedTimenoteViewModel(true)
    }

    private fun shareWith() {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.share_with)
            val all = if(accountType == 1) "Followers" else "Everyone"
            listItems(items = listOf(all, "Only me", "Groups", "Friends", "Create a new group")) { _, index, text ->
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
        profileViewModel.getAllGroups(tokenId!!).observe(viewLifecycleOwner, androidx.lifecycle.Observer { response ->
                val listGroups: MutableList<String> = mutableListOf()
                if(response.code() == 401) {
                    authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, androidx.lifecycle.Observer {newAccessToken ->
                        tokenId = newAccessToken
                        getAllGroups()
                    })
                }
                if (response.isSuccessful) response.body()!!.forEach { group -> listGroups.add(group.name) }
                listItemsMultiChoice(items = listGroups, initialSelection = indexGroupChosen.toIntArray(), allowEmptySelection = true) { _, index, text ->
                    index.forEach { indexes ->
                        response.body()?.get(indexes)?.users?.forEach { user ->
                            if (creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.sharedWith != null && !creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.sharedWith?.contains(user.id)!!) {
                                sendTo.add(user.id!!)
                            } else if (!sendTo.contains(user.id)) sendTo.add(user.id!!)
                        }
                    }

                    val removedList = indexGroupChosen.filterNot { i -> index.toMutableList().any { i == it } }
                    if(removedList.isNotEmpty()){
                        removedList.forEach {indexes ->
                            response.body()?.get(indexes)?.users?.forEach {user ->
                                sendTo.remove(user.id)
                            }
                        }
                    }

                    indexGroupChosen = index.toMutableList()
                    creationTimenoteViewModel.setSharedWith(sendTo)
                }
            })
    }

    private fun createFriendsBottomSheet(createGroup: Int, groupName: String?) {
        val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
            customView(R.layout.friends_search_cl)
            lifecycleOwner(this@CreateTimenote)
            positiveButton(R.string.confirm) {
                when (createGroup) {
                    0 -> creationTimenoteViewModel.setSharedWith(sendTo)
                    1 -> {
                        profileViewModel.createGroup(tokenId!!, CreateGroupDTO(groupName!!, sendTo)).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                            if(it.isSuccessful) creationTimenoteViewModel.setSharedWith(sendTo)
                        })
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
            createGroup
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

    private fun cropView(uri: Uri?) {
        val u = Uri.parse(File(getPath(uri)!!).absolutePath)
        val a = Uri.parse(File(getPath(uri)!!).canonicalPath)
        UCrop.of(Uri.parse(uri?.toString()), u)
            .withAspectRatio(16F, 9F)
            .start(requireContext(), this)

        /*var cropView: CropImageView?
        val dialog = MaterialDialog(requireContext()).show {
            customView(R.layout.cropview)
            title(R.string.resize)
            positiveButton(R.string.done) {
                progressBar.visibility = View.GONE
                takeAddPicTv.visibility = View.GONE
                picCl.visibility = View.VISIBLE

                //MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri) = cropView?.croppedImage!!
                saveImage(MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri), uri , null)
                screenSlideCreationTimenotePagerAdapter.notifyDataSetChanged()
                vp.adapter = screenSlideCreationTimenotePagerAdapter
            }
            lifecycleOwner(this@CreateTimenote)
        }

        cropView = dialog.getCustomView().crop_view as CropImageView
        cropView.setImageBitmap(MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri))
        if(uri != null){
            hideChooseBackground()
        }*/
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
            //images?.add("file://${it.absolutePath}")
            screenSlideCreationTimenotePagerAdapter.images = images
            screenSlideCreationTimenotePagerAdapter.notifyDataSetChanged()
            indicator.setViewPager(vp_pic)
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
        if(images.isNullOrEmpty() && values?.colorHex.isNullOrBlank() && creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.pictures.isNullOrEmpty()) formCompleted = false
        if(values?.location != null){
            prefs.stringLiveData("offset", "0").observe(viewLifecycleOwner, androidx.lifecycle.Observer {
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
    fun pushPic(file: File){
        amazonClient.setRegion(Region.getRegion(Regions.EU_WEST_3))
        val transferUtiliy = TransferUtility(amazonClient, requireContext())
        val key = "timenote/${UUID.randomUUID().mostSignificantBits}"
        val transferObserver = transferUtiliy.upload("timenote-dev-images", key, file, CannedAccessControlList.Private)
        transferObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState?) {
                if (state == TransferState.COMPLETED) {
                    imagesUrl.add(
                        amazonClient.getResourceUrl("timenote-dev-images", key).toString()
                    )
                    if (images?.size == imagesUrl.size) {
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
        ).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it.code() == 401) {
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, androidx.lifecycle.Observer {newAccessToken ->
                    tokenId = newAccessToken
                    modifyTimenotePic()
                })
            }
            if(it.isSuccessful) clearAndPreview(it)
        })
    }

    private fun createTimenotePic() {
        timenoteViewModel.createTimenote(
            tokenId!!,
            creationTimenoteViewModel.getCreateTimeNoteLiveData().value!!
        ).observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                if(it.code() == 401) {
                    authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, androidx.lifecycle.Observer {newAccessToken ->
                        tokenId = newAccessToken
                        createTimenotePic()
                    })
                }
                if(it.isSuccessful) clearAndPreview(it)
            })
    }

    fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = requireActivity().managedQuery(uri, projection, null, null, null)
        requireActivity().startManagingCursor(cursor)
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    override fun onCropPicClicked(uri: Uri?) {
        cropView(uri)
    }

    override fun onAddClicked() {
        utils.picturePickerTimenote(
            requireContext(),
            resources,
            takeAddPicTv,
            progressBar,
            this,
            webSearchViewModel
        )
    }

    override fun onDeleteClicked(uri: Uri?) {
        //images?.remove(uri.toString())
        vp_pic.apply {
            screenSlideCreationTimenotePagerAdapter = ScreenSlideCreationTimenotePagerAdapter(
                this@CreateTimenote,
                images,
                false,
                false,
                listOf()
            )
            adapter = screenSlideCreationTimenotePagerAdapter
        }
        indicator.setViewPager(vp_pic)
        if(images?.size == 0){
            picCl.visibility = View.GONE
            takeAddPicTv.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            card_line_0.visibility = View.VISIBLE
            choose_color_label.visibility = View.VISIBLE
            create_timenote_first_color.visibility = View.VISIBLE
            create_timenote_second_color.visibility = View.VISIBLE
            create_timenote_third_color.visibility = View.VISIBLE
            create_timenote_fourth_color.visibility = View.VISIBLE
            create_timenote_fifth_color.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onImageSelectedFromWeb(bitmap: String, dialog: MaterialDialog) {
        create_timenote_next_btn.visibility = View.GONE
        done_pb.visibility = View.VISIBLE
        webSearchViewModel.getBitmap().removeObservers(viewLifecycleOwner)
        webSearchViewModel.decodeSampledBitmapFromResource(URL(bitmap), Rect(), 100, 100)
        webSearchViewModel.getBitmap().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                saveTemporary(it, dialog)
                webSearchViewModel.clearBitmap()
                webSearchViewModel.getBitmap().removeObservers(viewLifecycleOwner)
            }
        })
    }

    override fun onMoreImagesClicked(position: Int, query: String) {
        webSearchViewModel.search(query, requireContext(), (position).toLong())
    }

    override fun onHashTagClicked(hashTag: String?) {
    }

    override fun onSearchClicked(userInfoDTO: UserInfoDTO) {
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

}