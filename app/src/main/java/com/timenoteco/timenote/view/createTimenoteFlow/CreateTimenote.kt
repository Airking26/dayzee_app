package com.timenoteco.timenote.view.createTimenoteFlow

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.location.Address
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
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
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.color.ColorPalette
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.asksira.bsimagepicker.BSImagePicker
import com.bumptech.glide.Glide
import com.theartofdev.edmodo.cropper.CropImageView
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ScreenSlideCreationTimenotePagerAdapter
import com.timenoteco.timenote.adapter.WebSearchAdapter
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.listeners.PlacePickerListener
import com.timenoteco.timenote.listeners.TimenoteCreationPicListeners
import com.timenoteco.timenote.model.statusTimenote
import com.timenoteco.timenote.viewModel.CreationTimenoteViewModel
import com.timenoteco.timenote.viewModel.WebSearchViewModel
import kotlinx.android.synthetic.main.cropview.view.*
import kotlinx.android.synthetic.main.fragment_create_timenote.*
import kotlinx.android.synthetic.main.friends_search.view.*
import mehdi.sakout.fancybuttons.FancyButton
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URL
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class CreateTimenote : Fragment(), View.OnClickListener, PlacePickerListener, BSImagePicker.OnSingleImageSelectedListener,
    BSImagePicker.OnMultiImageSelectedListener, BSImagePicker.ImageLoaderDelegate, BSImagePicker.OnSelectImageCancelledListener,
    TimenoteCreationPicListeners, WebSearchAdapter.ImageChoosedListener, WebSearchAdapter.MoreImagesClicked {

    private lateinit var progressDialog: Dialog
    private lateinit var utils: Utils
    private lateinit var dateFormatDate: SimpleDateFormat
    private lateinit var fromLabel: TextView
    private lateinit var toLabel : TextView
    private lateinit var addEndDateTv: TextView
    private lateinit var fixedDate : TextView
    private lateinit var url: TextView
    private lateinit var paidTimenote : CardView
    private lateinit var picCl: ConstraintLayout
    private lateinit var noAnswer: TextView
    private lateinit var vp: ViewPager2
    private lateinit var screenSlideCreationTimenotePagerAdapter: ScreenSlideCreationTimenotePagerAdapter
    private var images: MutableList<Bitmap>? = mutableListOf()
    private lateinit var titleInput: String
    private var endDate: Long? = null
    private var formCompleted: Boolean = true
    private var startDate: Long? = null
    private val creationTimenoteViewModel: CreationTimenoteViewModel by activityViewModels()
    private val webSearchViewModel : WebSearchViewModel by activityViewModels()
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
    private var listSharedWith: MutableList<String> = mutableListOf()
    private val DATE_FORMAT_DAY_AND_TIME = "EEE, d MMM yyyy hh:mm aaa"
    private val DATE_FORMAT_ONLY_DAY = "EEE, d MMM yyyy"
    private lateinit var dateFormatDateAndTime: SimpleDateFormat

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_create_timenote, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUp()
        creationTimenoteViewModel.getCreateTimeNoteLiveData()
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                when (it.status) {
                    statusTimenote.FREE -> {
                        if(it.url.isNullOrBlank()){
                        noAnswer.text = getString(R.string.free)
                        noAnswer.setTextColor(resources.getColor(android.R.color.black))
                        url.visibility = View.GONE } else {
                            url.visibility = View.VISIBLE
                            url.text = it.url
                        }
                    }
                    statusTimenote.PAID -> {
                        noAnswer.text = it.price.toString() + "$"
                        noAnswer.setTextColor(resources.getColor(R.color.colorText))
                        url.visibility = View.VISIBLE
                        url.text = it.url
                    }
                    else -> {
                        noAnswer.setTextColor(resources.getColor(android.R.color.black))
                        noAnswer.text = getString(R.string.no_answer)
                        url.visibility = View.GONE
                    }
                }
                if (it.category.isNullOrBlank()) create_timenote_category.text = getString(R.string.none) else create_timenote_category.text = it.category
                if (it.pic == null) {
                    takeAddPicTv.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    picCl.visibility = View.GONE
                } else {
                    hideChooseBackground()
                    takeAddPicTv.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    picCl.visibility = View.VISIBLE
                }
                if (it.title.isNullOrBlank()) titleTv.text = getString(R.string.title) else titleTv.text = it.title
                if (it.place.isNullOrBlank()) create_timenote_where_btn.text = getString(R.string.where) else create_timenote_where_btn.text = it.place
                if (!it.startDate.isNullOrBlank() && !it.endDate.isNullOrBlank()){
                    fixedDate.visibility = View.GONE
                    toLabel.visibility = View.VISIBLE
                    fromLabel.visibility = View.VISIBLE
                    toTv.visibility = View.VISIBLE
                    fromTv.visibility = View.VISIBLE
                    fromTv.text = it.startDate
                    toTv.text = it.endDate
                } else {
                    fixedDate.visibility = View.VISIBLE
                    toLabel.visibility = View.GONE
                    fromLabel.visibility = View.GONE
                    toTv.visibility = View.GONE
                    fromTv.visibility = View.GONE
                    fixedDate.text = it.startDate
                }
                if (it.endDate.isNullOrBlank()) toTv.text = "" else toTv.text = it.endDate
                if (!it.color.isNullOrBlank()) {
                    when (it.color) {
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
                            moreColorTv.setBackgroundColor(Color.parseColor(it.color))
                        }

                    }
                }
            })
    }

    private fun setUp() {

        utils = Utils()
        progressDialog = utils.progressDialog(requireContext())
        dateFormatDateAndTime = SimpleDateFormat(DATE_FORMAT_DAY_AND_TIME, Locale.getDefault())
        dateFormatDate = SimpleDateFormat(DATE_FORMAT_ONLY_DAY, Locale.getDefault())
        addEndDateTv = profile_add_end_date
        fromLabel = from_label
        toLabel = to_label
        paidTimenote = paid_timenote_cardview
        fixedDate = create_timenote_fixed_date
        fromTv = create_timenote_from
        url = create_timenote_paid_timenote_status_price
        toTv = create_timenote_to
        categoryTv = create_timenote_category
        titleTv = create_timenote_title_btn
        noAnswer = create_timenote_paid_timenote_status_no_answer
        shareWithTv = create_timenote_share_with_btn
        moreColorTv = create_timenote_fifth_color
        firstColorTv = create_timenote_first_color
        secondColorTv = create_timenote_second_color
        thirdColorTv = create_timenote_third_color
        fourthColorTv = create_timenote_fourth_color
        takeAddPicTv = create_timenote_take_add_pic
        progressBar = create_timenote_pb
        descTv = create_timenote_desc_btn
        descCv = desc_cardview
        picCl = pic_cl
        vp = vp_pic
        vp_pic.apply {
            screenSlideCreationTimenotePagerAdapter = ScreenSlideCreationTimenotePagerAdapter(this@CreateTimenote, images, false)
            adapter = screenSlideCreationTimenotePagerAdapter
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
        url.setOnClickListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                utils.picturePicker(requireContext(), resources, takeAddPicTv, progressBar, this, webSearchViewModel)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            when_cardview -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                dateTimePicker { _, datetime ->
                    fixedDate.visibility = View.VISIBLE
                    toLabel.visibility = View.GONE
                    fromLabel.visibility = View.GONE
                    toTv.visibility = View.GONE
                    fromTv.visibility = View.GONE
                    fixedDate.text = dateFormatDateAndTime.format(datetime.time.time)
                    creationTimenoteViewModel.setYear(datetime.time.time)
                    creationTimenoteViewModel.setStartDate(
                        datetime.time.time,
                        DATE_FORMAT_DAY_AND_TIME
                    )
                    startDate = datetime.time.time
                    creationTimenoteViewModel.setEndDate(0L)
                    creationTimenoteViewModel.setFormatedStartDate(
                        datetime.time.time,
                        datetime.time.time
                    )
                    addEndDateTv.visibility = View.VISIBLE
                }
            }
            addEndDateTv -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                dateTimePicker { _, datetime ->
                    fixedDate.visibility = View.GONE
                    addEndDateTv.visibility = View.GONE
                    toLabel.visibility = View.VISIBLE
                    fromLabel.visibility = View.VISIBLE
                    toTv.visibility = View.VISIBLE
                    fromTv.visibility = View.VISIBLE
                    fixedDate.text = dateFormatDateAndTime.format(datetime.time.time)
                    creationTimenoteViewModel.setYear(datetime.time.time)
                    creationTimenoteViewModel.setEndDate(datetime.time.time)
                    creationTimenoteViewModel.setFormatedStartDate(startDate!!, datetime.time.time)
                    fromTv.text = dateFormatDateAndTime.format(startDate)
                    toTv.text = dateFormatDateAndTime.format(datetime.time.time)
                }
            }
            create_timenote_next_btn -> {
                //if(checkFormCompleted())
                findNavController().navigate(CreateTimenoteDirections.actionCreateTimenoteToPreviewTimenoteCreated())
            }
            from_label -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show{
                dateTimePicker { _, datetime ->
                    startDate = datetime.time.time
                    fromTv.text = dateFormatDateAndTime.format(startDate)
                    creationTimenoteViewModel.setYear(startDate!!)
                    creationTimenoteViewModel.setStartDate(startDate!!, DATE_FORMAT_DAY_AND_TIME)
                    if (endDate != null) creationTimenoteViewModel.setFormatedStartDate(
                        startDate!!,
                        endDate!!
                    )
                }
                lifecycleOwner(this@CreateTimenote)
            }
            to_label -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                dateTimePicker { dialog, datetime ->
                    endDate = datetime.time.time
                    toTv.text = dateFormatDateAndTime.format(endDate)
                    creationTimenoteViewModel.setEndDate(endDate!!)
                    if (startDate != null) creationTimenoteViewModel.setFormatedStartDate(
                        startDate!!,
                        endDate!!
                    )
                }
                lifecycleOwner(this@CreateTimenote)
            }
            where_cardview -> utils.placePicker(requireContext(), this@CreateTimenote, create_timenote_where_btn, this, false, requireActivity())
            share_with_cardview -> shareWith()
            category_cardview -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
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
                    creationTimenoteViewModel.setCategory(text.toString())
                }
                lifecycleOwner(this@CreateTimenote)
            }
            title_cardview -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
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
                lifecycleOwner(this@CreateTimenote)
            }
            descCv -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.description)
                input(
                    inputType = InputType.TYPE_CLASS_TEXT,
                    maxLength = 100,
                    prefill = creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.desc
                ) { materialDialog, charSequence ->
                    descTv.text = charSequence.toString()
                    creationTimenoteViewModel.setDescription(charSequence.toString())
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
                utils.picturePicker(requireContext(), resources, takeAddPicTv, progressBar, this, webSearchViewModel)
            }
            paid_timenote_cardview -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.paid_timenote)
                listItems(items = listOf(getString(R.string.free), getString(R.string.paid),getString(R.string.no_answer))){
                    _, index, text ->
                    when(index){
                        0 -> {
                            noAnswer.text = text.toString()
                            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                                title(R.string.link)
                                input(inputType = InputType.TYPE_TEXT_VARIATION_URI) { _, charSequence ->
                                    creationTimenoteViewModel.setUrl(charSequence.toString())
                                    creationTimenoteViewModel.setStatus(statusTimenote.FREE)
                                }
                                negativeButton()
                                lifecycleOwner(this@CreateTimenote)
                            }
                        }
                        1 -> {
                            noAnswer.text = text.toString()
                            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                                title(R.string.price)
                                input(inputType = InputType.TYPE_CLASS_NUMBER) { _, charSequence ->
                                    creationTimenoteViewModel.setPrice(charSequence.toString().toLong())
                                    MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                                        title(R.string.link)
                                        input(inputType = InputType.TYPE_TEXT_VARIATION_URI) { _, charSequence ->
                                            creationTimenoteViewModel.setUrl(charSequence.toString())
                                            creationTimenoteViewModel.setStatus(statusTimenote.PAID)
                                        }
                                        lifecycleOwner(this@CreateTimenote)
                                    }
                                    lifecycleOwner(this@CreateTimenote)
                                }
                            }
                        }
                        2 -> {
                            noAnswer.text = text.toString()
                            creationTimenoteViewModel.setStatus(statusTimenote.NOANSWER)
                        }
                    }
                }
            }
            url -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.link)
                input(inputType = InputType.TYPE_TEXT_VARIATION_URI, prefill = creationTimenoteViewModel.getCreateTimeNoteLiveData().value?.url) { _, charSequence ->
                    creationTimenoteViewModel.setUrl(charSequence.toString())
                }
                lifecycleOwner(this@CreateTimenote)
            }

        }
    }

    private fun shareWith() {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.share_with)
            listItems(
                items = listOf("All", "Only me", "Groups", "Friends", "Create a new group" )) { _, _, text ->
                when (text) {
                    "Groups" -> MaterialDialog(
                        requireContext(),
                        BottomSheet(LayoutMode.WRAP_CONTENT)
                    ).show {
                        title(R.string.share_with)
                        listItemsMultiChoice(
                            items = listOf(
                                "LeFramboisier",
                                "La Famille"
                            )
                        ) { _, index, text ->
                            listSharedWith.add(text.joinToString())
                            shareWithTv.text = listSharedWith.joinToString()
                        }
                        positiveButton(R.string.done)
                        lifecycleOwner(this@CreateTimenote)
                    }
                    "Friends" -> {
                        val dial = MaterialDialog(
                            requireContext(),
                            BottomSheet(LayoutMode.WRAP_CONTENT)
                        ).show {
                            customView(R.layout.friends_search)
                            lifecycleOwner(this@CreateTimenote)
                        }
                        val searchbar = dial.getCustomView().searchBar_friends
                        searchbar.setCardViewElevation(0)
                        positiveButton(R.string.done)
                        lifecycleOwner(this@CreateTimenote)
                    }
                    "Create a new group" ->
                        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                            title(R.string.name_group)
                            input(inputType = InputType.TYPE_CLASS_TEXT, maxLength = 20) { materialDialog, charSequence ->
                                val dial = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                                    customView(R.layout.friends_search)
                                    lifecycleOwner(this@CreateTimenote)
                                }
                                val searchbar = dial.getCustomView().searchBar_friends
                                searchbar.setCardViewElevation(0)
                                positiveButton(R.string.done)
                            }
                            positiveButton(R.string.done)
                            lifecycleOwner(this@CreateTimenote)
                        }

                }
            }
        }
    }

    private fun cropView(bitmap: Bitmap?, position: Int?) {
        var cropView: CropImageView? = null
        val dialog = MaterialDialog(requireContext()).show {
            customView(R.layout.cropview)
            title(R.string.resize)
            positiveButton(R.string.done) {
                progressBar.visibility = View.GONE
                takeAddPicTv.visibility = View.GONE
                picCl.visibility = View.VISIBLE
                if(position == null)images?.add(cropView?.croppedImage!!)
                else images?.set(position, cropView?.croppedImage!!)
                screenSlideCreationTimenotePagerAdapter.notifyDataSetChanged()
                vp.adapter = screenSlideCreationTimenotePagerAdapter
                creationTimenoteViewModel.setPicUser(images!!)
            }
            lifecycleOwner(this@CreateTimenote)
        }

        cropView = dialog.getCustomView().crop_view as CropImageView
        cropView.setImageBitmap(bitmap)
        if(bitmap != null){
            hideChooseBackground()
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

    private fun saveImage(image: Bitmap, dialog: MaterialDialog): String? {
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
            savedImagePath = imageFile.getAbsolutePath()
            try {
                val fOut: OutputStream = FileOutputStream(imageFile)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            galleryAddPic(savedImagePath, dialog)
        }
        return savedImagePath
    }

    private fun galleryAddPic(imagePath: String?, dialog: MaterialDialog) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(imagePath!!)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        requireActivity().sendBroadcast(mediaScanIntent)
        dialog.dismiss()
        progressDialog.hide()
        utils.createPictureMultipleBS(childFragmentManager, "multiple")
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

    override fun onPlacePicked(address: Address) {
        creationTimenoteViewModel.setLocation(address.getAddressLine(0))
    }

    fun checkFormCompleted(): Boolean {
        val values = creationTimenoteViewModel.getCreateTimeNoteLiveData().value
        if (values?.pic == null) formCompleted = false
        if (values?.endDate.isNullOrBlank()) formCompleted = false
        if (values?.startDate.isNullOrBlank()) formCompleted = false
        if (values?.place.isNullOrBlank()) formCompleted = false
        if (values?.title.isNullOrBlank()) formCompleted = false
        if (values?.category.isNullOrBlank()) formCompleted = false
        if (!formCompleted) Toast.makeText(
            requireContext(),
            getString(R.string.error_message_filling),
            Toast.LENGTH_SHORT
        ).show()
        return formCompleted
    }

    override fun onSingleImageSelected(uri: Uri?, tag: String?) {
        if(tag != "single") {
            utils.showPicSelected(MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri), tag?.toInt(),  this::cropView)
        } else {
            utils.showPicSelected(MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri), null, this::cropView)
        }
    }

    override fun onMultiImageSelected(uriList: MutableList<Uri>?, tag: String?) {
        for(image in uriList!!){
            images?.add(MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, image))
        }
        creationTimenoteViewModel.setPicUser(images!!)
        screenSlideCreationTimenotePagerAdapter.notifyDataSetChanged()
        pic_cl.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        takeAddPicTv.visibility = View.GONE
        hideChooseBackground()
    }

    override fun loadImage(imageUri: Uri?, ivImage: ImageView?) {
        Glide.with(this).load(imageUri).into(ivImage!!)
    }

    override fun onCancelled(isMultiSelecting: Boolean, tag: String?) {
        if(images?.isNullOrEmpty()!!){
        progressBar.visibility = View.GONE
        takeAddPicTv.visibility = View.VISIBLE
        picCl.visibility = View.GONE }
        else {
            progressBar.visibility = View.GONE
        }
    }

    override fun onChangePicClicked(position: Int) {
        utils.createPictureSingleBS(childFragmentManager, position.toString())
    }

    override fun onCropPicClicked(bitmap: Bitmap, position: Int) {
        cropView(bitmap, position)
    }

    override fun onAddClicked() {
        utils.picturePicker(requireContext(), resources, takeAddPicTv, progressBar, this, webSearchViewModel)
    }

    override fun onDeleteClicked(position: Int) {
        images?.removeAt(position)
        screenSlideCreationTimenotePagerAdapter.notifyDataSetChanged()
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
        progressDialog.show()
        webSearchViewModel.getBitmap().removeObservers(viewLifecycleOwner)
        webSearchViewModel.decodeSampledBitmapFromResource(URL(bitmap), Rect(), 100, 100)
        webSearchViewModel.getBitmap().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it != null) {
                saveImage(it, dialog)
                webSearchViewModel.clearBitmap()
                webSearchViewModel.getBitmap().removeObservers(viewLifecycleOwner)
            }
        })
    }

    override fun onMoreImagesClicked(position: Int, query: String) {
        webSearchViewModel.search(query, requireContext(), (position).toLong())
    }

}
