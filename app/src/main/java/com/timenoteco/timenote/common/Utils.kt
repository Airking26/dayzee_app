package com.timenoteco.timenote.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.google.android.gms.maps.model.LatLng
import com.theartofdev.edmodo.cropper.CropImageView
import com.timenoteco.timenote.listeners.PlacePickerListener
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.AutoSuggestAdapter
import kotlinx.android.synthetic.main.autocomplete_search_address.view.*
import kotlinx.android.synthetic.main.cropview.view.*

class Utils {

    fun placePicker(context: Context, lifecycleOwner: LifecycleOwner, textView: TextView, placePickerListener: PlacePickerListener){
        val places : MutableList<Address> = mutableListOf()
        val TRIGGER_AUTO_COMPLETE = 250
        val AUTO_COMPLETE_DELAY: Long = 250
        lateinit var handler: Handler

        val dialog = MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.where)
            customView(R.layout.autocomplete_search_address, scrollable = true, horizontalPadding = true)
            positiveButton(R.string.done)
            lifecycleOwner(lifecycleOwner)
        }
        val customView = dialog.getCustomView()
        val autoCompleteTextView = customView.autocompleteTextview_Address as AutoCompleteTextView
        val geocoder = Geocoder(context)
        val autocompleteAdapter = AutoSuggestAdapter(context, android.R.layout.simple_dropdown_item_1line)
        autoCompleteTextView.threshold = 3
        autoCompleteTextView.setAdapter(autocompleteAdapter)
        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            dialog.dismiss()
            placePickerListener.onPlacePicked(autocompleteAdapter.getObject(position))
            textView.text = autocompleteAdapter.getObject(position).getAddressLine(0)
        }
        handler = Handler(Handler.Callback { msg ->
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(autoCompleteTextView.text)) {
                    places.clear()
                    val i = geocoder.getFromLocationName(autoCompleteTextView.text.toString(), 3)
                    if(!i.isNullOrEmpty()){
                        for(y in i){
                            //val city: String = y.locality ?: ""
                            //val country: String = y.countryName ?: ""
                            val address: String = y.getAddressLine(0) ?: ""
                            val latLong: LatLng = LatLng(y.latitude, y.longitude)
                            places.add(y)
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

    fun picturePicker(context: Context, resources: Resources, view: View, view1: View, fragment: Fragment) {
        val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.take_add_a_picture)
            listItems(
                items = listOf(
                    resources.getString(R.string.take_a_photo),
                    resources.getString(R.string.choose_from_gallery)
                )
            ) { _, index, text ->
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    view.visibility = View.GONE
                    view1.visibility = View.VISIBLE
                    when (text) {
                        resources.getString(R.string.take_a_photo) -> fragment.startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), 0)
                        resources.getString(R.string.choose_from_gallery) -> fragment.startActivityForResult(Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            ), 1
                        )
                    }
                } else fragment.requestPermissions(PERMISSIONS_STORAGE, 2)
            }
            lifecycleOwner(fragment)
        }
    }

    fun picturePickerResult(requestCode: Int, resultCode: Int, data: Intent?, view: View, view1: View, view2: View?, activity: Activity, croper: (Bitmap) -> Unit) {
        when (requestCode) {
            0 -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage: Bitmap = data.extras?.get("data") as Bitmap
                    croper(selectedImage)
                } else {
                    view.visibility = View.GONE
                    view1.visibility = View.VISIBLE
                    view2?.visibility = View.GONE
                }
            }
            1 -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage: Uri? = data.data
                    val filePathColumn: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
                    if (selectedImage != null) {
                        val cursor: Cursor? = activity.contentResolver.query(
                            selectedImage,
                            filePathColumn,
                            null,
                            null,
                            null
                        )
                        if (cursor != null) {
                            cursor.moveToFirst()
                            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                            val picturePath: String = cursor.getString(columnIndex)
                            val bitmap = BitmapFactory.decodeFile(picturePath)
                            cursor.close()
                            croper(bitmap)
                        }
                    }
                } else {
                    view2?.visibility = View.GONE
                    view.visibility = View.GONE
                    view1.visibility = View.VISIBLE
                }
            }
        }
    }

}