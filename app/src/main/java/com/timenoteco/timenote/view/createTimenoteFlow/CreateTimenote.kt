package com.timenoteco.timenote.view.createTimenoteFlow

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.takusemba.cropme.CropLayout
import com.takusemba.cropme.OnCropListener
import com.timenoteco.timenote.listeners.PlacePickerListener
import com.timenoteco.timenote.R
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.viewModel.CreationTimenoteViewModel
import kotlinx.android.synthetic.main.cropview.view.*
import kotlinx.android.synthetic.main.fragment_create_timenote.*
import mehdi.sakout.fancybuttons.FancyButton
import java.text.SimpleDateFormat
import java.util.*

class CreateTimenote : Fragment(), View.OnClickListener, OnCropListener,
    PlacePickerListener {

    private val creationTimenoteViewModel: CreationTimenoteViewModel by activityViewModels()
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
    private var listSharedWith: MutableList<String> = mutableListOf()
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
        takeAddPicTv = create_timenote_take_add_pic
        progressBar = create_timenote_pb

        titleTv.text = creationTimenoteViewModel.getDescription() ?: getString(R.string.title)

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
            where_cardview -> Utils().placePicker(requireContext(), this@CreateTimenote, create_timenote_where_btn, this)
            share_with_cardview -> shareWith()
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
                input(inputType = InputType.TYPE_CLASS_TEXT, maxLength = 100, prefill = creationTimenoteViewModel.getDescription()){ _, text ->
                    titleTv.text = text
                    creationTimenoteViewModel.setDescription(text.toString())
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
                        takeAddPicTv.visibility = View.GONE
                        progressBar.visibility = View.VISIBLE
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

    private fun shareWith() {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.share_with)
            listItems(
                items = listOf(
                    "Groups",
                    "Friends",
                    "Create a new group"
                )
            ) { dialog, index, text ->
                when (text) {
                    "Groups" -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(R.string.share_with)
                        listItemsMultiChoice(items = listOf("LeFramboisier", "La Famille")) { _, index, text ->
                            listSharedWith.add(text.joinToString())
                            shareWithTv.text = listSharedWith.joinToString()
                        }
                        positiveButton(R.string.done)
                        lifecycleOwner(this@CreateTimenote)
                    }
                    "Friends" -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(R.string.contacts)
                        listItemsMultiChoice(items = listOf("Pierre", "Paul")) { _, index, text ->
                            listSharedWith.add(text.joinToString())
                            shareWithTv.text = listSharedWith.joinToString()
                        }
                        positiveButton(R.string.done)
                        lifecycleOwner(this@CreateTimenote)
                    }
                    "Create a new group" -> MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        title(R.string.contacts)
                        listItemsMultiChoice(items = listOf("Pierre", "Paul")) { _, index, text ->
                            listSharedWith.add(text.toString())
                            shareWithTv.text = listSharedWith.toString()
                            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                                title(R.string.name_group)
                                input(inputType = InputType.TYPE_CLASS_TEXT, maxLength = 20) { materialDialog, charSequence ->
                                }
                                positiveButton(R.string.done)
                                lifecycleOwner(this@CreateTimenote)
                            }
                        }
                        positiveButton(R.string.name_group)
                        lifecycleOwner(this@CreateTimenote)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            0 -> {
                if(resultCode == RESULT_OK && data != null){
                    val selectedImage: Bitmap = data.extras?.get("data") as Bitmap
                    cropImage(selectedImage)
                } else {
                    progressBar.visibility =View.GONE
                    takeAddPicTv.visibility = View.VISIBLE
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
                            cropImage(bitmap)
                        }
                    }
                } else {
                    progressBar.visibility =View.GONE
                    takeAddPicTv.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun cropImage(bitmap: Bitmap) {
        var cropView: CropLayout? = null
        val dialog = MaterialDialog(requireContext()).show {
            customView(R.layout.cropview)
            title(R.string.resize)
            positiveButton(R.string.done) {
                val p = cropView?.crop()
                cropView?.addOnCropListener(this@CreateTimenote)
            }
            lifecycleOwner(this@CreateTimenote)

        }

        cropView = dialog.getCustomView().crop_view as CropLayout
        cropView.setBitmap(bitmap)
        cropView.isOffFrame()
        cropView.crop()
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

    override fun onFailure(e: Exception) {
        e.toString()
    }

    override fun onSuccess(bitmap: Bitmap) {
        create_timenote_take_add_pic.visibility = View.GONE
        create_timenote_pb.visibility = View.GONE
        create_timenote_pic.visibility = View.VISIBLE
        create_timenote_pic.setImageBitmap(bitmap)
    }

    override fun onPlacePicked(address: String) {
        Toast.makeText(requireContext(), address, Toast.LENGTH_SHORT).show()
    }
}
