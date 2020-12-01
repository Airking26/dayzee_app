package com.timenoteco.timenote.view.searchFlow

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.timenoteco.timenote.common.HashTagHelper
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.common.stringLiveData
import com.timenoteco.timenote.listeners.BackToHomeListener
import com.timenoteco.timenote.listeners.TimenoteCreationPicListeners
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.view.createTimenoteFlow.CreateTimenoteDirections
import com.timenoteco.timenote.viewModel.*
import com.zhihu.matisse.Matisse
import kotlinx.android.synthetic.main.fragment_create_timenote.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.friends_search.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mehdi.sakout.fancybuttons.FancyButton
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.reflect.Type
import java.net.URL
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class CreateTimenoteSearch : Fragment(), View.OnClickListener,
    TimenoteCreationPicListeners, WebSearchAdapter.ImageChoosedListener, WebSearchAdapter.MoreImagesClicked,
    HashTagHelper.OnHashTagClickListener, UsersPagingAdapter.SearchPeopleListener,
    UsersShareWithPagingAdapter.SearchPeopleListener, UsersShareWithPagingAdapter.AddToSend {

    private var accountType: Int = -1
    private var sendTo: MutableList<String> = mutableListOf()
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
    private val args : CreateTimenoteSearchArgs by navArgs()
    private val timenoteViewModel: TimenoteViewModel by activityViewModels()
    private val AUTOCOMPLETE_REQUEST_CODE: Int = 11
    private lateinit var progressDialog: Dialog
    private lateinit var utils: Utils
    private lateinit var fromLabel: TextView
    private lateinit var toLabel : TextView
    private lateinit var addEndDateTv: TextView
    private lateinit var fixedDate : TextView
    private lateinit var paidTimenote : CardView
    private lateinit var picCl: ConstraintLayout
    private lateinit var noAnswer: TextView
    private lateinit var vp: ViewPager2
    private lateinit var screenSlideCreationTimenotePagerAdapter: ScreenSlideCreationTimenotePagerAdapter
    private var images: MutableList<String>? = mutableListOf()
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
    private lateinit var titleError : ImageView
    private lateinit var whenError: ImageView
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
        backToHomeListener = context as BackToHomeListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_create_timenote, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(!tokenId.isNullOrBlank()) {
            setUp()

            val type: Type = object : TypeToken<UserInfoDTO?>() {}.type
            userInfoDTO = Gson().fromJson<UserInfoDTO>(prefs.getString("UserInfoDTO", ""), type)
            creationTimenoteViewModel.setCreatedBy(userInfoDTO.id!!)
            accountType = userInfoDTO.status

            //creationTimenoteViewModel.clear()
            if (args.modify == 0) creationTimenoteViewModel.getCreateTimeNoteLiveData().observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer {
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

    private fun populateModel(it: CreationTimenoteDTO) {
        when (it.price.price) {
            0 -> {
                if (it.url.isNullOrBlank()) {
                    noAnswer.text = getString(R.string.free)
                } else {
                    noAnswer.text = getString(R.string.no_answer)
                }
            }
            in 1..Int.MAX_VALUE -> {
                noAnswer.text = it.price.price.toString() + it.price.currency
            }
            else -> noAnswer.text = getString(R.string.no_answer)
        }
        if(it.url.isNullOrBlank()) create_timenote_url_btn.hint = getString(R.string.add_an_url) else create_timenote_url_btn.text = it.url
        if (it.category == null) create_timenote_category.text = getString(R.string.none) else create_timenote_category.text = it.category!!.subcategory
        if(args.modify == 0){
            if (images?.isNullOrEmpty()!!) {
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
        } else if(!it.startingAt.isBlank() && !it.endingAt.isBlank() && it.startingAt == it.endingAt) {
            fixedDate.visibility = View.VISIBLE
            toLabel.visibility = View.GONE
            fromLabel.visibility = View.GONE
            toTv.visibility = View.GONE
            fromTv.visibility = View.GONE
            fixedDate.text = startDateDisplayed
        }
        if (!it.colorHex.isNullOrBlank() && it.pictures.isNullOrEmpty()) {
            when (it.colorHex) {
                "#ffff8800" -> colorChoosedUI(
                    secondColorTv,
                    thirdColorTv,
                    fourthColorTv,
                    moreColorTv,
                    firstColorTv
                )
                "#ffcc0000" -> colorChoosedUI(
                    thirdColorTv,
                    fourthColorTv,
                    moreColorTv,
                    firstColorTv,
                    secondColorTv
                )
                "#ff0099cc" -> colorChoosedUI(
                    fourthColorTv,
                    moreColorTv,
                    firstColorTv,
                    secondColorTv,
                    thirdColorTv
                )
                "#ffaa66cc" -> colorChoosedUI(
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
                    moreColorTv.setBackgroundColor(Color.parseColor(if(it.colorHex?.contains("#")!!) it.colorHex else  "#${it.colorHex}"))
                }

            }
        }
        if(it.organizers.isNullOrEmpty()) create_timenote_organizers_btn.hint = getString(R.string.organizers) else create_timenote_organizers_btn.hint =
            "${it.organizers?.size.toString()} Organizers"
        when {
            it.sharedWith != null && !it.sharedWith?.isNullOrEmpty()!! -> create_timenote_share_with.text = if(accountType == 1) "Followers" else "Everyone"
            else -> {
                if(it.sharedWith?.size == 1 && it.sharedWith?.get(0) == it.createdBy) create_timenote_share_with.text = "Only Me"
                else create_timenote_share_with.text = "Shared"
            }
        }
    }

    private fun setUp() {
        utils = Utils()
        //progressDialog = utils.progressDialog(requireContext())
        dateFormatDateAndTime = SimpleDateFormat(DATE_FORMAT_DAY_AND_TIME, Locale.getDefault())
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
        picCl = pic_cl
        vp = vp_pic
        if(args.modify == 0) {
            screenSlideCreationTimenotePagerAdapter = ScreenSlideCreationTimenotePagerAdapter(
                this,
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
                this,
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
        create_timenote_btn_back.setOnClickListener(this)
        url_cardview.setOnClickListener(this)
        create_timenote_organizers_btn.setOnClickListener(this)
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
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(TAG, status.statusMessage!!)
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
                images?.add(image.toString())
                //images?.add(AWSFile(image, MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, image)))
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

    override fun onClick(v: View?) {
        when (v) {
            when_cardview -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                whenError.visibility = View.GONE
                dateTimePicker { _, datetime ->
                    fixedDate.visibility = View.VISIBLE
                    toLabel.visibility = View.GONE
                    fromLabel.visibility = View.GONE
                    toTv.visibility = View.GONE
                    fromTv.visibility = View.GONE
                    startDateDisplayed = dateFormatDateAndTime.format(datetime.time.time)
                    fixedDate.text = startDateDisplayed
                    creationTimenoteViewModel.setStartDate(datetime.time.time, ISO)
                    startDate = datetime.time.time
                    creationTimenoteViewModel.setEndDate(datetime.time.time)
                    addEndDateTv.visibility = View.VISIBLE
                }
            }
            addEndDateTv -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
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
                    fromTv.text = startDateDisplayed
                    toTv.text = endDateDisplayed
                }
            }
            create_timenote_next_btn -> {
                if (checkFormCompleted()) {
                    if (!images?.isNullOrEmpty()!!) {
                        //progressDialog.show()
                        create_timenote_next_btn.visibility = View.GONE
                        done_pb.visibility = View.VISIBLE
                        for (image in images!!) {
                            var file: File = if(image.contains("content://")) {
                                File(getPath(Uri.parse(image))!!)
                            } else File(image)

                            pushPic(
                                file,
                                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, Uri.parse(image))
                            )
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
            from_label -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                dateTimePicker { _, datetime ->
                    startDate = datetime.time.time
                    startDateDisplayed = dateFormatDateAndTime.format(startDate)
                    fromTv.text = startDateDisplayed
                    creationTimenoteViewModel.setStartDate(startDate!!, ISO)
                }
                lifecycleOwner(this@CreateTimenoteSearch)
            }
            to_label -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                dateTimePicker { dialog, datetime ->
                    endDate = datetime.time.time
                    endDateDisplayed = dateFormatDateAndTime.format(endDate)
                    toTv.text = endDateDisplayed
                    creationTimenoteViewModel.setEndDate(endDate!!)
                }
                lifecycleOwner(this@CreateTimenoteSearch)
            }
            where_cardview -> startActivityForResult(
                Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY,
                    placesList
                ).build(requireContext()), AUTOCOMPLETE_REQUEST_CODE
            )
            share_with_cardview -> shareWith()
            category_cardview -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.category)
                listItems(
                    items = listOf(
                        "Judaisme",
                        "Bouddhisme",
                        "Techno",
                        "Pop",
                        "Football",
                        "Tennis",
                        "Judaisme",
                        "Bouddhisme",
                        "Techno",
                        "Pop",
                        "Football",
                        "Tennis",
                        "Judaisme",
                        "Bouddhisme",
                        "Techno",
                        "Pop",
                        "Football",
                        "Tennis"
                    )
                ) { _, index, text ->
                    categoryTv.text = text
                    creationTimenoteViewModel.setCategory(Category("", text.toString()))
                }
                lifecycleOwner(this@CreateTimenoteSearch)
            }
            title_cardview -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
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
                    if (!titleInput.isNullOrBlank()) {
                        descCv.visibility = View.VISIBLE
                    }
                }
                lifecycleOwner(this@CreateTimenoteSearch)
            }
            descCv -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.description)
                input(
                    inputType = InputType.TYPE_CLASS_TEXT,
                    prefill = creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.description
                ) { materialDialog, charSequence ->
                    descTv.text = charSequence.toString()
                    val hashTagHelper = HashTagHelper.Creator.create(
                        R.color.colorText,
                        this@CreateTimenoteSearch,
                        null,
                        resources
                    )
                    hashTagHelper.handle(descTv)
                    val hashtagList = hashTagHelper.getAllHashTags(true)
                    var descWithoutHashtag = descTv.text.toString()
                    for (hashtag in hashtagList) {
                        descWithoutHashtag = descWithoutHashtag.replace(
                            hashtag,
                            ""
                        )
                    }
                    var descWithoutHashtagFormated = descWithoutHashtag.replace(
                        "\\s+".toRegex(),
                        " "
                    ).trim().capitalize()
                    creationTimenoteViewModel.setHashtags(hashtagList)
                    creationTimenoteViewModel.setDescription(descWithoutHashtagFormated)
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
                    val colorHex = '#' + Integer.toHexString(color)
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

                lifecycleOwner(this@CreateTimenoteSearch)
            }
            create_timenote_first_color -> {
                colorChoosedUI(
                    secondColorTv,
                    thirdColorTv,
                    fourthColorTv,
                    moreColorTv,
                    firstColorTv
                )
                creationTimenoteViewModel.setColor("#ffff8800")
            }
            create_timenote_second_color -> {
                colorChoosedUI(
                    thirdColorTv,
                    fourthColorTv,
                    moreColorTv,
                    firstColorTv,
                    secondColorTv
                )
                creationTimenoteViewModel.setColor("#ffcc0000")
            }
            create_timenote_third_color -> {
                colorChoosedUI(
                    fourthColorTv,
                    moreColorTv,
                    firstColorTv,
                    secondColorTv,
                    thirdColorTv
                )
                creationTimenoteViewModel.setColor("#ff0099cc")
            }
            create_timenote_fourth_color -> {
                colorChoosedUI(
                    thirdColorTv,
                    moreColorTv,
                    firstColorTv,
                    secondColorTv,
                    fourthColorTv
                )
                creationTimenoteViewModel.setColor("#ffaa66cc")
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
                        getString(R.string.free), getString(R.string.paid), getString(
                            R.string.no_answer
                        )
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
                                    lifecycleOwner(this@CreateTimenoteSearch)
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
                                            lifecycleOwner(this@CreateTimenoteSearch)
                                        }
                                    }
                                }
                            }
                        }
                        2 -> {
                            noAnswer.text = text.toString()
                        }
                    }
                }
            }
            url_cardview -> MaterialDialog(
                requireContext(),
                BottomSheet(LayoutMode.WRAP_CONTENT)
            ).show {
                title(R.string.link)
                input(
                    inputType = InputType.TYPE_TEXT_VARIATION_URI,
                    prefill = creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.url
                ) { _, charSequence ->
                    creationTimenoteViewModel.setUrl(charSequence.toString())
                }
                lifecycleOwner(this@CreateTimenoteSearch)
            }

            create_timenote_organizers_btn -> createFriendsBottomSheet(2, null)

            create_timenote_btn_back -> {
                if (args.from == 0) backToHomeListener.onBackHome()
                else findNavController().popBackStack()
            }

        }
    }

    private fun modifyTimenote() {
        timenoteViewModel.modifySpecificTimenote(
            tokenId!!,
            args.id!!,
            creationTimenoteViewModel.getCreateTimeNoteLiveData().value!!
        ).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it.code() == 401) {
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, androidx.lifecycle.Observer {newAccessToken ->
                    tokenId = newAccessToken
                    modifyTimenote()
                })
            }
            if (it.isSuccessful) {
                startDateDisplayed = null
                endDateDisplayed = null
                create_timenote_next_btn.visibility = View.VISIBLE
                done_pb.visibility = View.GONE
                findNavController().navigate(
                    CreateTimenoteDirections.actionCreateTimenoteToPreviewTimenoteCreated(
                        args.from,
                        it.body(), args.modify
                    )
                )
                imagesUrl = mutableListOf()
                images = mutableListOf()
            }
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
            if (it.isSuccessful) {
                startDateDisplayed = null
                endDateDisplayed = null
                create_timenote_next_btn.visibility = View.VISIBLE
                done_pb.visibility = View.GONE
                findNavController().navigate(
                    CreateTimenoteDirections.actionCreateTimenoteToPreviewTimenoteCreated(
                        args.from,
                        it.body(), args.modify
                    )
                )
                imagesUrl = mutableListOf()
                images = mutableListOf()
            }
        })
    }

    private fun MaterialDialog.getAllGroups() {
        profileViewModel.getAllGroups(tokenId!!)
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { response ->
                val listGroups: MutableList<String> = mutableListOf()
                if(response.code() == 401) {
                    authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, androidx.lifecycle.Observer {newAccessToken ->
                        tokenId = newAccessToken
                        getAllGroups()
                    })
                }
                if (response.isSuccessful) response.body()!!
                    .forEach { group -> listGroups.add(group.name) }
                listItemsMultiChoice(items = listGroups) { _, index, text ->
                    index.forEach { indexes ->
                        response.body()?.get(indexes)?.users?.forEach { user ->
                            if (creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.sharedWith != null &&
                                !creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.sharedWith?.contains(
                                    user.id
                                )!!
                            ) {
                                sendTo.add(user.id!!)
                            } else if (!sendTo.contains(user.id))
                                sendTo.add(user.id!!)
                        }
                    }


                    if (creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.sharedWith.isNullOrEmpty() && sendTo.size > 0)
                        creationTimenoteViewModel.setSharedWith(sendTo)
                    else if (sendTo.size > 0)
                        creationTimenoteViewModel.setSharedWith(
                            creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.sharedWith?.plus(
                                sendTo
                            )!!
                        )
                    sendTo.clear()
                }
            })
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
                        lifecycleOwner(this@CreateTimenoteSearch)
                    }
                    3 -> createFriendsBottomSheet(0, null)
                    4 -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(R.string.name_group)
                        input(inputType = InputType.TYPE_CLASS_TEXT, maxLength = 20) { _, charSequence ->
                            createFriendsBottomSheet(1, charSequence.toString())
                        }
                        positiveButton(R.string.confirm)
                        lifecycleOwner(this@CreateTimenoteSearch)
                    }
                    else -> shareWithTv.text = text.toString()
                }
            }
        }
    }

    private fun createFriendsBottomSheet(createGroup: Int, groupName: String?) {
        sendTo = mutableListOf()
        val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
            customView(R.layout.friends_search)
            lifecycleOwner(this@CreateTimenoteSearch)
            positiveButton(R.string.confirm) {
                when (createGroup) {
                    0 -> {
                        creationTimenoteViewModel.setSharedWith(if(creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.sharedWith.isNullOrEmpty()) sendTo
                        else creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.sharedWith?.plus(sendTo)!!)

                    }
                    1 -> {
                        profileViewModel.createGroup(tokenId!!, CreateGroupDTO(groupName!!, sendTo)).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                            if(it.isSuccessful)
                                creationTimenoteViewModel.setSharedWith(if(creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.sharedWith.isNullOrEmpty()) sendTo
                                else creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.sharedWith?.plus(sendTo)!!)
                        })
                    }
                    2 -> {
                        creationTimenoteViewModel.setOrganizers(if(creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.organizers.isNullOrEmpty()) sendTo
                        else creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.organizers?.plus(sendTo)!!)
                    }
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
            this@CreateTimenoteSearch,
            this@CreateTimenoteSearch
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = userAdapter
        lifecycleScope.launch {
            followViewModel.getUsers(tokenId!!, userInfoDTO.id!! ,0, prefs).collectLatest {
                userAdapter.submitData(it)
            }
        }
    }

    private fun saveImage(image: Bitmap, uri: Uri?, dialog: MaterialDialog?): String? {
        var savedImagePath: String? = null
        val imageFileName = "JPEG_${Timestamp(System.currentTimeMillis())}.jpg"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + "/TIMENOTE_PICTURES"
        )
        var success = true
        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }
        if (success) {
            val imageFile = File(storageDir, imageFileName)
            savedImagePath = imageFile.absolutePath
            compressFile(imageFile, image)
            val u1 = Uri.parse("file://$savedImagePath")
            val u3 = Uri.parse("file://$imageFile")
            val u10 = File(u1.path!!)
            val u12= File(u3.path!!)
            val u13= File(u1.path!!).absolutePath
            val u14= File(u1.path!!).canonicalPath
            val u17= File(u3.path!!).canonicalPath
            val u18= File(u3.path!!).absolutePath

            if(dialog != null) galleryAddPic(savedImagePath, dialog)
            else{
                images!![images?.indexOf(uri.toString())!!] = u3.toString()
            }
        }


        return savedImagePath
    }

    private fun compressFile(imageFile: File, image: Bitmap) {
        try {
            val fOut: OutputStream = FileOutputStream(imageFile)
            image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            fOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
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

    private fun galleryAddPic(imagePath: String?, dialog: MaterialDialog) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(imagePath!!)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        requireActivity().sendBroadcast(mediaScanIntent)
        dialog.dismiss()
        create_timenote_next_btn.visibility = View.VISIBLE
        done_pb.visibility = View.GONE
        //progressDialog.hide()
        utils.createImagePicker(this, requireContext())
    }

    private fun colorChoosedUI(
        fancyButton1: FancyButton,
        fancyButton2: FancyButton,
        fancyButton3: FancyButton,
        fancyButton4: FancyButton,
        fancyButton5: FancyButton
    ) {
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


    fun pushPic(file: File, bitmap: Bitmap){
        amazonClient.setRegion(Region.getRegion(Regions.EU_WEST_3))
        val transferUtiliy = TransferUtility(amazonClient, requireContext())
        compressFile(file, bitmap)
        val key = "timenote/${UUID.randomUUID().mostSignificantBits}"
        val transferObserver = transferUtiliy.upload(
            "timenote-dev-images",
            key,
            file,
            CannedAccessControlList.Private
        )
        transferObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState?) {
                Log.d(TAG, "onStateChanged: ${state?.name}")
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
                Log.d(TAG, "onProgressChanged: ")
            }

            override fun onError(id: Int, ex: java.lang.Exception?) {
                Log.d(TAG, "onError: ${ex?.message}")
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
            if (it.isSuccessful) {
                startDateDisplayed = null
                endDateDisplayed = null
                create_timenote_next_btn.visibility = View.VISIBLE
                done_pb.visibility = View.GONE
                images = mutableListOf()
                imagesUrl = mutableListOf()
                findNavController().navigate(
                    CreateTimenoteDirections.actionCreateTimenoteToPreviewTimenoteCreated(
                        args.from,
                        it.body(), args.modify
                    )
                )
            }
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
                if (it.isSuccessful) {
                    startDateDisplayed = null
                    endDateDisplayed = null
                    create_timenote_next_btn.visibility = View.VISIBLE
                    done_pb.visibility = View.GONE
                    images = mutableListOf()
                    imagesUrl = mutableListOf()
                    findNavController().navigate(
                        CreateTimenoteDirections.actionCreateTimenoteToPreviewTimenoteCreated(
                            args.from,
                            it.body(), args.modify
                        )
                    )
                }
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
        images?.remove(uri.toString())
        vp_pic.apply {
            screenSlideCreationTimenotePagerAdapter = ScreenSlideCreationTimenotePagerAdapter(
                this@CreateTimenoteSearch,
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

    override fun onImageSelectedFromWeb(bitmap: String, dialog: MaterialDialog) {
        //progressDialog.show()
        create_timenote_next_btn.visibility = View.GONE
        done_pb.visibility = View.VISIBLE
        webSearchViewModel.getBitmap().removeObservers(viewLifecycleOwner)
        webSearchViewModel.decodeSampledBitmapFromResource(URL(bitmap), Rect(), 100, 100)
        webSearchViewModel.getBitmap().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                saveImage(it, null,  dialog)
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

    override fun onAdd(userInfoDTO: UserInfoDTO) {
        if(creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.sharedWith?.contains(userInfoDTO.id) != null) {
            if (!creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.sharedWith?.contains(userInfoDTO.id)!!) sendTo.add(userInfoDTO.id!!)
        } else sendTo.add(userInfoDTO.id!!)
    }

    override fun onRemove(userInfoDTO: UserInfoDTO) {
        sendTo.remove(userInfoDTO.id)
    }


}