package com.timenoteco.timenote.view.createTimenoteFlow

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.AutoSuggestAdapter
import kotlinx.android.synthetic.main.autocomplete_search_address.view.*
import kotlinx.android.synthetic.main.fragment_create_timenote.*
import mehdi.sakout.fancybuttons.FancyButton
import java.text.SimpleDateFormat
import java.util.*

class CreateTimenote : Fragment(), View.OnClickListener{

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
    private var listSharedWith: MutableList<String> = mutableListOf()
    private var places : MutableList<String> = mutableListOf()
    private val TRIGGER_AUTO_COMPLETE = 100
    private val AUTO_COMPLETE_DELAY: Long = 300
    private lateinit var handler: Handler
    private val DATE_FORMAT = "EEE, d MMM yyyy hh:mm aaa"
    private lateinit var dateFormat : SimpleDateFormat
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_create_timenote, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        fromTv = create_timenote_from
        toTv = create_timenote_to
        categoryTv = create_timenote_category
        titleTv = create_timenote_title_btn
        shareWithTv = create_timenote_share_with_btn
        moreColorTv = create_timenote_fifth_color
        firstColorTv = create_timenote_first_color
        secondColorTv = create_timenote_second_color
        thirdColorTv = create_timenote_third_color
        fourthColorTv = create_timenote_fourth_color

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

    }

    private fun checkIfPermissionGranted(): Boolean {
        if (checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            requestPermissions(PERMISSIONS_STORAGE, 2)
            return false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == 2){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                checkIfPermissionGranted()
            }
        }
    }

    override fun onClick(v: View?) {
        when(v){
            create_timenote_next_btn -> findNavController().navigate(CreateTimenoteDirections.actionCreateTimenoteToPreviewTimenoteCreated())
            from_label -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                dateTimePicker { _, datetime -> fromTv.text = dateFormat.format(datetime.time.time) }
                lifecycleOwner(this@CreateTimenote)
            }
            to_label -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                dateTimePicker { dialog, datetime -> toTv.text = dateFormat.format(datetime.time.time)}
                lifecycleOwner(this@CreateTimenote)
            }
            where_cardview -> placePicker()
            share_with_cardview -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.share_with)
                listItemsMultiChoice(items = listOf("LeFramboisier", "La Famille")){_, index, text ->
                    listSharedWith.add(text.toString())
                }
                positiveButton(R.string.done){ shareWithTv.text = listSharedWith.toString() }
                negativeButton(R.string.contacts){
                    MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(R.string.contacts)
                        listItemsMultiChoice(items = listOf("Pierre", "Paul")){_, index, text ->
                            listSharedWith.add(text.toString())
                        }
                        positiveButton(R.string.done){
                            shareWithTv.text = listSharedWith.toString()
                        }
                        negativeButton(R.string.create_new_group){
                            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                                title(R.string.name_group)
                                input(inputType = InputType.TYPE_CLASS_TEXT){ _, text ->

                                }
                                positiveButton(R.string.done){
                                    shareWithTv.text = listSharedWith.toString()
                                }
                                lifecycleOwner(this@CreateTimenote)
                            }
                        }
                        lifecycleOwner(this@CreateTimenote)
                    }
                }
                lifecycleOwner(this@CreateTimenote)
            }
            category_cardview -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.category)
                listItems(items = listOf("Judaisme", "Bouddhisme", "Techno", "Pop", "Football", "Tennis",
                    "Judaisme", "Bouddhisme", "Techno", "Pop", "Football", "Tennis", "Judaisme", "Bouddhisme",
                    "Techno", "Pop", "Football", "Tennis")){_, index, text ->
                    categoryTv.text = text
                }
                lifecycleOwner(this@CreateTimenote)
            }
            title_cardview -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.title)
                input(inputType = InputType.TYPE_CLASS_TEXT, maxLength = 100){ _, text ->
                    titleTv.text = text
                }
                positiveButton(R.string.done)
                lifecycleOwner(this@CreateTimenote)
            }
            create_timenote_fifth_color ->  MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.colors)
                colorChooser(
                    colors = ColorPalette.Primary,
                    subColors = ColorPalette.PrimarySub,
                    allowCustomArgb = true,
                    showAlphaSelector = true) { _, color ->
                    moreColorTv.setBackgroundColor(color)
                    colorChoosed(firstColorTv, secondColorTv, thirdColorTv, fourthColorTv, moreColorTv)
                }
                positiveButton(R.string.done)
                negativeButton(android.R.string.cancel)
                lifecycleOwner(this@CreateTimenote)
            }
            create_timenote_first_color -> colorChoosed(secondColorTv, thirdColorTv, fourthColorTv, moreColorTv, firstColorTv)
            create_timenote_second_color -> colorChoosed(thirdColorTv, fourthColorTv, moreColorTv, firstColorTv, secondColorTv)
            create_timenote_third_color -> colorChoosed(fourthColorTv, moreColorTv, firstColorTv, secondColorTv, thirdColorTv)
            create_timenote_fourth_color -> colorChoosed(thirdColorTv, moreColorTv, firstColorTv, secondColorTv, fourthColorTv)
            create_timenote_take_add_pic -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.take_add_a_picture)
                listItems(items = listOf(getString(R.string.take_a_photo), getString(R.string.choose_from_gallery))){ _, index, text ->
                    if(checkIfPermissionGranted()) {
                        when (text) {
                            getString(R.string.take_a_photo) -> startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), 0)
                            getString(R.string.choose_from_gallery) -> startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1)
                        }
                    }
                }
                lifecycleOwner(this@CreateTimenote)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            0 -> {
                if(resultCode == RESULT_OK && data != null){
                    val selectedImage: Bitmap = data.extras?.get("data") as Bitmap
                }
            }
            1 -> {
                if(resultCode == RESULT_OK && data != null){
                    val selectedImage : Uri? = data.data
                    val filePathColumn: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
                    if(selectedImage != null){
                        val cursor: Cursor? = requireActivity().contentResolver.query(selectedImage, filePathColumn, null, null, null)
                        if(cursor != null){
                            cursor.moveToFirst()
                            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                            val picturePath: String = cursor.getString(columnIndex)
                            val bitmap = BitmapFactory.decodeFile(picturePath)
                            cursor.close()
                        }
                    }
                }
            }
        }
    }

    private fun placePicker(){
        val dialog = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.where)
            customView(R.layout.autocomplete_search_address, scrollable = true, horizontalPadding = true)
            positiveButton(R.string.done)
            lifecycleOwner(this@CreateTimenote)
        }
        val customView = dialog.getCustomView()
        val autoCompleteTextView = customView.autocompleteTextview_Address as AutoCompleteTextView
        val geocoder = Geocoder(requireContext())
        val autocompleteAdapter = AutoSuggestAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line)
        autoCompleteTextView.threshold = 3
        autoCompleteTextView.setAdapter(autocompleteAdapter)
        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            dialog.dismiss()
            create_timenote_where_btn.text = autocompleteAdapter.getObject(position)
        }
        handler = Handler(Handler.Callback { msg ->
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(autoCompleteTextView.text)) {
                    places.clear()
                    val i = geocoder.getFromLocationName(autoCompleteTextView.text.toString(), 3)
                    if(!i.isNullOrEmpty()){
                        for(y in i){
                            val city: String = y.locality ?: ""
                            val country: String = y.countryName ?: ""
                            val address: String = y.getAddressLine(0) ?: ""
                            places.add("$address, $city, $country")
                        }
                        autocompleteAdapter.setData(places)
                        autocompleteAdapter.notifyDataSetChanged()
                    }
                }
            }
            false;
        })
        autoCompleteTextView.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
            }

        })
    }

    private fun colorChoosed(fancyButton1: FancyButton, fancyButton2: FancyButton, fancyButton3: FancyButton, fancyButton4: FancyButton, fancyButton5: FancyButton){
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
}
