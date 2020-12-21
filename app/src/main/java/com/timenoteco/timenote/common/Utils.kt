package com.timenoteco.timenote.common

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.WebSearchAdapter
import com.timenoteco.timenote.androidView.dialog.input
import com.timenoteco.timenote.androidView.matisse.Matisse
import com.timenoteco.timenote.androidView.matisse.MimeType
import com.timenoteco.timenote.androidView.matisse.engine.impl.GlideEngine
import com.timenoteco.timenote.androidView.matisse.filter.Filter
import com.timenoteco.timenote.androidView.matisse.internal.entity.CaptureStrategy
import com.timenoteco.timenote.model.DetailedPlace
import com.timenoteco.timenote.model.Location
import com.timenoteco.timenote.model.accessToken
import com.timenoteco.timenote.viewModel.WebSearchViewModel
import com.timenoteco.timenote.webService.repo.DayzeeRepository
import kotlinx.android.synthetic.main.web_search_rv.view.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.time.*
import java.util.*
import kotlin.math.abs

class Utils {


    fun picturePickerTimenote(context: Context, resources: Resources, view: View, view1: View, fragment: Fragment, webSearchViewModel: WebSearchViewModel) {
        val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.take_add_a_picture)
            listItems(items = listOf(resources.getString(R.string.add_a_picture), resources.getString(R.string.search_on_web))) { _, index, text ->
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    view.visibility = View.GONE
                    view1.visibility = View.VISIBLE
                    when (text) {
                        resources.getString(R.string.add_a_picture) -> createImagePicker(fragment, context)
                        //resources.getString(R.string.choose_from_gallery) -> createPictureMultipleBS(fragment.childFragmentManager, "multiple")
                        resources.getString(R.string.search_on_web) -> createWebSearchDialog(context, webSearchViewModel, fragment, view, view1)
                    }
                } else fragment.requestPermissions(PERMISSIONS_STORAGE, 2)
            }
            lifecycleOwner(fragment)
        }
    }

    fun createWebSearchDialog(context: Context, webSearchViewModel: WebSearchViewModel, fragment: Fragment, view: View?, view1: View?) {
        var recyclerView : RecyclerView?
        var webSearchAdapter : WebSearchAdapter? = null
        var progressDialog: Dialog = progressDialog(context)
        MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            input { _, charSequence ->
                progressDialog.show()
                webSearchViewModel.search(charSequence.toString(), context, 0)
                webSearchViewModel.getListResults().removeObservers(fragment.viewLifecycleOwner)
                webSearchViewModel.getListResults().observe(fragment.viewLifecycleOwner, Observer {
                    if (!it.isNullOrEmpty()) {
                        view?.visibility = View.GONE
                        view1?.visibility = View.VISIBLE
                        if (it.size <= 10) {
                            val dialog =
                                MaterialDialog(context, BottomSheet(LayoutMode.MATCH_PARENT)).show {
                                    customView(R.layout.web_search_rv, scrollable = true)
                                    lifecycleOwner(fragment.viewLifecycleOwner)
                                }

                            recyclerView = dialog.getCustomView().websearch_rv as RecyclerView
                            recyclerView?.apply {
                                webSearchAdapter = WebSearchAdapter(
                                    it,
                                    fragment as WebSearchAdapter.ImageChoosedListener,
                                    fragment as WebSearchAdapter.MoreImagesClicked,
                                    charSequence.toString(),
                                    dialog
                                )
                                layoutManager = GridLayoutManager(context, 2)
                                (layoutManager as GridLayoutManager).spanSizeLookup
                                adapter = webSearchAdapter
                                webSearchAdapter?.notifyDataSetChanged()
                                progressDialog.hide()
                            }

                            dialog.onDismiss {
                                webSearchAdapter?.clear()
                                webSearchAdapter = null
                                webSearchViewModel.getListResults().removeObservers(fragment.viewLifecycleOwner)
                            }

                        } else {
                            webSearchAdapter?.notifyDataSetChanged()
                        }


                    }
                })
            }
            onDismiss {
                if (webSearchAdapter == null || webSearchAdapter?.images.isNullOrEmpty()) {
                    view?.visibility = View.VISIBLE
                    view1?.visibility = View.GONE
                } else {
                    webSearchAdapter?.clear()
                }
            }
            positiveButton(R.string.search_on_web)
            lifecycleOwner(fragment.viewLifecycleOwner)
        }
    }

    fun createImagePicker(fragment: Fragment, context: Context){
        Matisse.from(fragment)
            .choose(MimeType.ofImage())
            .theme(R.style.Matisse_Dracula)
            .countable(false)
            .capture(true)
            .spanCount(4)
            .captureStrategy(CaptureStrategy(true, "com.timenoteco.timenote.fileprovider", "TIMENOTE"))
            .maxSelectable(3)
            .addFilter(GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
            .gridExpectedSize(context.resources.getDimensionPixelSize(R.dimen.grid))
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            .thumbnailScale(0.85f)
            .imageEngine(GlideEngine())
            .maxOriginalSize(10)
            .autoHideToolbarOnSingleTap(true)
            .forResult(112)
    }

    fun hideStatusBar(activity: Activity){
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_IMMERSIVE
    }

    @SuppressLint("InlinedApi")
    @RequiresApi(Build.VERSION_CODES.M)
    fun showStatusBar(activity: Activity){
        if(activity.resources.getColor(R.color.colorBackground) == activity.resources.getColor(android.R.color.white)){
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    fun createPb(context: Context): CircularProgressDrawable {
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        return circularProgressDrawable
    }

    fun progressDialog(context: Context): Dialog {
        val dialog = Dialog(context)
        val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
        dialog.setContentView(inflate)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    fun formatDate(format: String, timestamp: Long): String {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return if(timestamp == 0L) ""
        else dateFormat.format(timestamp)
    }

    fun setFormatedStartDatePreview(startDate: String, endDate: String): String{
        val DATE_FORMAT_DAY = "d MMM yyyy"
        val DATE_FORMAT_TIME = "hh:mm aaa"
        val DATE_FORMAT_TIME_FORMATED = "d\nMMM"
        val DATE_FORMAT_SAME_DAY_DIFFERENT_TIME = "d MMM.\nhh:mm"
        val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"

        val formatedStartDate: String


        val startingAt = SimpleDateFormat(ISO, Locale.getDefault()).parse(startDate).time
        val endingAt = SimpleDateFormat(ISO, Locale.getDefault()).parse(endDate).time


        if(formatDate(DATE_FORMAT_DAY, startingAt) == formatDate(DATE_FORMAT_DAY, endingAt)){
            if(formatDate(DATE_FORMAT_TIME, startingAt) == formatDate(DATE_FORMAT_TIME, endingAt)){
                formatedStartDate = formatDate(DATE_FORMAT_TIME_FORMATED, startingAt)
            } else {
                formatedStartDate = formatDate(DATE_FORMAT_TIME_FORMATED, startingAt)
            }
        } else {
            formatedStartDate = formatDate(DATE_FORMAT_SAME_DAY_DIFFERENT_TIME, startingAt)
        }

        return formatedStartDate
    }

    fun setFormatedEndDatePreview(startDate: String, endDate: String):String{

        val DATE_FORMAT_DAY = "d MMM yyyy"
        val DATE_FORMAT_TIME = "hh:mm aaa"
        val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"
        val DATE_FORMAT_SAME_DAY_DIFFERENT_TIME = "d MMM\nhh:mm"


        var formatedEndDate: String

        val startingAt = SimpleDateFormat(ISO, Locale.getDefault()).parse(startDate).time
        val endingAt = SimpleDateFormat(ISO, Locale.getDefault()).parse(endDate).time


        formatedEndDate =
            if(formatDate(DATE_FORMAT_DAY, startingAt) == formatDate(DATE_FORMAT_DAY, endingAt)){
                if(formatDate(DATE_FORMAT_TIME, startingAt) == formatDate(DATE_FORMAT_TIME, endingAt)){
                    formatDate(DATE_FORMAT_TIME, startingAt)
                } else {
                    formatDate(DATE_FORMAT_TIME, startingAt) + "\n" + formatDate(DATE_FORMAT_TIME, endingAt)
                }
            } else {
                formatDate(DATE_FORMAT_SAME_DAY_DIFFERENT_TIME, endingAt)
            }

        return formatedEndDate

    }

    fun setFormatedStartDate(startDate: String, endDate: String) : String{
        val DATE_FORMAT_DAY = "d MMM yyyy"
        val DATE_FORMAT_TIME = "hh:mm aaa"
        val DATE_FORMAT_TIME_FORMATED = "d\nMMM"
        val DATE_FORMAT_SAME_DAY_DIFFERENT_TIME = "d MMM.\nhh:mm"
        val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

        val test = "2020-11-13T12:00:00.000-04:00"
        val isoTest = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"
        val formatedStartDateTest: String

        val startingTest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ldt = OffsetDateTime.parse(test).toEpochSecond()
            ldt * 1000
            //Instant.parse(test).epochSecond * 1000
        } else {
            SimpleDateFormat(isoTest, Locale.getDefault()).parse(test).time
        }

        val endingTest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ldt = OffsetDateTime.parse(test).toEpochSecond()
            ldt * 1000
            //Instant.parse(test).epochSecond * 1000
        } else {
            SimpleDateFormat(isoTest, Locale.getDefault()).parse(test).time
        }

        if(formatDate(DATE_FORMAT_DAY, startingTest) == formatDate(DATE_FORMAT_DAY, endingTest)){
            if(formatDate(DATE_FORMAT_TIME, startingTest) == formatDate(DATE_FORMAT_TIME, endingTest)){
                formatedStartDateTest = formatDate(DATE_FORMAT_TIME_FORMATED, startingTest)
            } else {
                formatedStartDateTest = formatDate(DATE_FORMAT_TIME_FORMATED, startingTest)
            }
        } else {
            formatedStartDateTest = formatDate(DATE_FORMAT_SAME_DAY_DIFFERENT_TIME, startingTest)
        }

        val m = formatedStartDateTest

        val formatedStartDate: String

        val starting = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant.parse(startDate).epochSecond * 1000
        } else {
            val o = SimpleDateFormat(ISO)
            o.timeZone = TimeZone.getTimeZone("UTC")
            val m = o.parse(startDate)
            o.timeZone = TimeZone.getDefault()
            val k = o.format(m)
            val c = SimpleDateFormat(ISO, Locale.getDefault()).parse(k).time
            val ds = SimpleDateFormat(ISO, Locale.getDefault()).parse(startDate).time
            SimpleDateFormat(ISO, Locale.getDefault()).parse(k).time
        }

        val ending = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant.parse(endDate).epochSecond * 1000
        } else {
            val o = SimpleDateFormat(ISO)
            o.timeZone = TimeZone.getTimeZone("UTC")
            val m = o.parse(endDate)
            o.timeZone = TimeZone.getDefault()
            val k = o.format(m)
            SimpleDateFormat(ISO, Locale.getDefault()).parse(k).time
        }



        if(formatDate(DATE_FORMAT_DAY, starting) == formatDate(DATE_FORMAT_DAY, ending)){
            if(formatDate(DATE_FORMAT_TIME, starting) == formatDate(DATE_FORMAT_TIME, ending)){
                formatedStartDate = formatDate(DATE_FORMAT_TIME_FORMATED, starting)
            } else {
                formatedStartDate = formatDate(DATE_FORMAT_TIME_FORMATED, starting)
            }
        } else {
            formatedStartDate = formatDate(DATE_FORMAT_SAME_DAY_DIFFERENT_TIME, starting)
        }

        return formatedStartDate
    }

    fun setFormatedEndDate(startDate: String, endDate: String): String{
        val DATE_FORMAT_DAY = "d MMM yyyy"
        val DATE_FORMAT_TIME = "hh:mm aaa"
        val DATE_FORMAT_SAME_DAY_DIFFERENT_TIME = "d MMM\nhh:mm"
        val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

        val test = "2020-11-13T12:00:00.000-04:00"
        val isoTest = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"
        val formatedStartDateTest: String

        val startingTest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ldt = OffsetDateTime.parse(test).toEpochSecond()
            ldt * 1000
            //Instant.parse(test).epochSecond * 1000
        } else {
            SimpleDateFormat(isoTest, Locale.getDefault()).parse(test).time
        }

        val endingTest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ldt = OffsetDateTime.parse(test).toEpochSecond()
            ldt * 1000
            //Instant.parse(test).epochSecond * 1000
        } else {
            SimpleDateFormat(isoTest, Locale.getDefault()).parse(test).time
        }

        formatedStartDateTest = if(formatDate(DATE_FORMAT_DAY, startingTest) == formatDate(DATE_FORMAT_DAY, endingTest)){
            if(formatDate(DATE_FORMAT_TIME, startingTest) == formatDate(DATE_FORMAT_TIME, endingTest)){
                formatDate(DATE_FORMAT_TIME, startingTest)
            } else {
                formatDate(DATE_FORMAT_TIME, startingTest) + "\n" + formatDate(DATE_FORMAT_TIME, endingTest)
            }
        } else {
            formatDate(DATE_FORMAT_SAME_DAY_DIFFERENT_TIME, endingTest)
        }

        val m = formatedStartDateTest

        var formatedEndDate: String

        val starting = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant.parse(startDate).epochSecond * 1000
        } else {
            val o = SimpleDateFormat(ISO)
            o.timeZone = TimeZone.getTimeZone("UTC")
            val m = o.parse(startDate)
            o.timeZone = TimeZone.getDefault()
            val k = o.format(m)
            SimpleDateFormat(ISO, Locale.getDefault()).parse(k).time
            //SimpleDateFormat(ISO, Locale.getDefault()).parse(startDate).time
        }

        val ending = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant.parse(endDate).epochSecond * 1000
        } else {
            val o = SimpleDateFormat(ISO)
            o.timeZone = TimeZone.getTimeZone("UTC")
            val m = o.parse(endDate)
            o.timeZone = TimeZone.getDefault()
            val k = o.format(m)
            SimpleDateFormat(ISO, Locale.getDefault()).parse(k).time
        }
        formatedEndDate =
            if(formatDate(DATE_FORMAT_DAY, starting) == formatDate(DATE_FORMAT_DAY, ending)){
                if(formatDate(DATE_FORMAT_TIME, starting) == formatDate(DATE_FORMAT_TIME, ending)){
                    formatDate(DATE_FORMAT_TIME, starting)
                } else {
                    formatDate(DATE_FORMAT_TIME, starting) + "\n" + formatDate(DATE_FORMAT_TIME, ending)
                }
            } else {
                formatDate(DATE_FORMAT_SAME_DAY_DIFFERENT_TIME, ending)
            }

        return formatedEndDate
    }

    fun setYear(startDate: String): String {
        val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val YEAR = "yyyy"
        val starting = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant.parse(startDate).epochSecond * 1000
        } else {
            val o = SimpleDateFormat(ISO)
            o.timeZone = TimeZone.getTimeZone("UTC")
            val m = o.parse(startDate)
            o.timeZone = TimeZone.getDefault()
            val k = o.format(m)
            SimpleDateFormat(ISO, Locale.getDefault()).parse(k).time
        }
        return formatDate(YEAR, starting)
    }

    fun setYearPreview(startDate: String): String{
        val YEAR = "yyyy"
        val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"

        val startingAt = SimpleDateFormat(ISO).parse(startDate).time
        return formatDate(YEAR, startingAt)
    }

    fun inTime(startDate: String): String {
        val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val nbrYear : Int
        val nbrMonth: Int
        val nbrDay : Int
        val nbrHours : Int
        val nbrMin : Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val period = Period.between(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC).toLocalDate()
                , LocalDateTime.ofInstant(Instant.parse(startDate), ZoneOffset.UTC).toLocalDate())

            nbrYear = period.years
            nbrMonth = period.minusYears(nbrYear.toLong()).months
            nbrDay = period.minusYears(nbrYear.toLong()).minusMonths(nbrMonth.toLong()).days - 1

            val duration = Duration.between(Instant.now(), Instant.parse(startDate))
            nbrHours = duration.minusDays(duration.toDays()).toHours().toInt()
            nbrMin = duration.minusDays(duration.toDays()).minusHours(duration.minusDays(duration.toDays()).toHours()).toMinutes().toInt()

        } else {
            val time = SimpleDateFormat(ISO).parse(startDate).time - System.currentTimeMillis()
            val c: Calendar = Calendar.getInstance(Locale.getDefault())
            c.timeInMillis = time
            nbrYear = c.get(Calendar.YEAR) - 1970
            nbrMonth = c.get(Calendar.MONTH)
            nbrDay = c.get(Calendar.DAY_OF_MONTH) - 1
            nbrHours = c.get(Calendar.HOUR) + 12
            nbrMin = c.get(Calendar.MINUTE)
        }

        return formatInTime(nbrYear, nbrMonth, nbrDay, nbrHours, nbrMin)
    }

    private fun formatInTime(nbrYear: Int, nbrMonth: Int, nbrDay: Int, nbrHour: Int, nbrMin: Int): String {

        val decountTime: String
        if(nbrYear <= 0 && nbrMonth <= 0 && nbrDay <= 0 && nbrHour <= 0 && nbrMin < 0){
            decountTime = "ONGOING"
        }
        else if(nbrYear <= 0){
            if(nbrMonth <= 0){
                if(nbrDay <= 0){
                    decountTime = if(nbrHour > 1){
                        if(nbrMin > 1){
                            "In $nbrHour hours and $nbrMin minutes"
                        } else {
                            "In $nbrHour hours and $nbrMin minute"
                        }
                    } else {
                        if(nbrMin > 1){
                            "In $nbrHour hour and $nbrMin minutes"
                        } else {
                            "In $nbrHour hour and $nbrMin minute"
                        }
                    }
                } else {
                    decountTime = if(nbrDay > 1){
                        if(nbrHour > 1) "In $nbrDay days and $nbrHour hours"
                        else "In $nbrDay days and $nbrHour hour"
                    } else {
                        if(nbrHour > 1) "In $nbrDay day and $nbrHour hours"
                        else "In $nbrDay day and $nbrHour hour"
                    }

                }
            } else {
                decountTime = if(nbrMonth > 1){
                    if(nbrDay > 1){
                        "In $nbrMonth months and $nbrDay days"
                    } else {
                        "In $nbrMonth months and $nbrDay day"
                    }
                } else {
                    if(nbrDay >1){
                        "In $nbrMonth month and $nbrDay days"
                    } else {
                        "In $nbrMonth month and $nbrDay day"
                    }
                }
            }
        } else {
            decountTime = if(nbrYear > 1){
                if(nbrMonth > 1) {
                    "In $nbrYear years and $nbrMonth months"
                } else {
                    "In $nbrYear years and $nbrMonth month"
                }
            } else {
                if(nbrMonth > 1){
                    "In $nbrYear year and $nbrMonth months"
                } else {
                    "In $nbrYear year and $nbrMonth month"
                }
            }
        }

        return decountTime
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sinceTime(startDate: String): String {
        val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val nbrYear : Int
        val nbrMonth: Int
        val nbrDay : Int
        val nbrHours : Int
        val nbrMin : Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val period = Period.between(LocalDateTime.ofInstant(Instant.parse(startDate), ZoneOffset.UTC).toLocalDate(), LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC).toLocalDate())

            nbrYear = period.years
            nbrMonth = period.minusYears(nbrYear.toLong()).months
            nbrDay = period.minusYears(nbrYear.toLong()).minusMonths(nbrMonth.toLong()).days - 1

            val duration = Duration.between(Instant.now(), Instant.parse(startDate))
            nbrHours = duration.minusDays(duration.toDays()).toHours().toInt()
            nbrMin = duration.minusDays(duration.toDays()).minusHours(duration.minusDays(duration.toDays()).toHours()).toMinutes().toInt()

        } else {
            val time = System.currentTimeMillis() - SimpleDateFormat(ISO).parse(startDate).time
            val c: Calendar = Calendar.getInstance(Locale.getDefault())
            c.timeInMillis = time
            nbrYear = c.get(Calendar.YEAR) - 1970
            nbrMonth = c.get(Calendar.MONTH)
            nbrDay = c.get(Calendar.DAY_OF_MONTH) - 1
            nbrHours = c.get(Calendar.HOUR)
            nbrMin = c.get(Calendar.MINUTE)
        }

        return formatSinceTime(abs(nbrYear), abs(nbrMonth), abs(nbrDay), abs(nbrHours), abs(nbrMin)) }

    private fun formatSinceTime(
        nbrYear: Int,
        nbrMonth: Int,
        nbrDay: Int,
        nbrHours: Int,
        nbrMin: Int
    ): String {
        val decountTime: String
        if(nbrYear == 0){
            if(nbrMonth == 0){
                if(nbrDay == 0){
                    decountTime = if(nbrHours > 1){
                        if(nbrMin > 1){
                            "Since $nbrHours hours and $nbrMin minutes"
                        } else {
                            "Since $nbrHours hours and $nbrMin minute"
                        }
                    } else {
                        if(nbrMin > 1){
                            "Since $nbrHours hour and $nbrMin minutes"
                        } else {
                            "Since $nbrHours hour and $nbrMin minute"
                        }
                    }
                } else {
                    decountTime = if(nbrDay > 1){
                        if(nbrHours > 1) "Since $nbrDay days and $nbrHours hours"
                        else "Since $nbrDay days and $nbrHours hour"
                    } else {
                        if(nbrHours > 1) "Since $nbrDay day and $nbrHours hours"
                        else "Since $nbrDay day and $nbrHours hour"
                    }

                }
            } else {
                decountTime = if(nbrMonth > 1){
                    if(nbrDay > 1){
                        "Since $nbrMonth months and $nbrDay days"
                    } else {
                        "Since $nbrMonth months and $nbrDay day"
                    }
                } else {
                    if(nbrDay >1){
                        "Since $nbrMonth month and $nbrDay days"
                    } else {
                        "Since $nbrMonth month and $nbrDay day"
                    }
                }
            }
        } else {
            decountTime = if(nbrYear > 1){
                if(nbrMonth > 1) {
                    "Since $nbrYear years and $nbrMonth months"
                } else {
                    "Since $nbrYear years and $nbrMonth month"
                }
            } else {
                if(nbrMonth > 1){
                    "Since $nbrYear year and $nbrMonth months"
                } else {
                    "Since $nbrYear year and $nbrMonth month"
                }
            }
        }
        return decountTime


    }

    fun setLocation(detailedPlace: DetailedPlace, isInCreation: Boolean, sharedPreferences: SharedPreferences?): Location {
        var zipcode = ""
        var city = ""
        var country = ""
        if(isInCreation) formatOffset(detailedPlace.result.utc_offset / 60, detailedPlace.result.utc_offset % 60, sharedPreferences!!)
        for(n in detailedPlace.result.address_components){
            if(n.types.contains("locality")) city = n.long_name
            if(n.types.contains("postal_code")) zipcode = n.short_name
            if(n.types.contains("country")) country = n.long_name
        }
        return Location( detailedPlace.result.geometry.location.lng, detailedPlace.result.geometry.location.lat,
            com.timenoteco.timenote.model.Address(detailedPlace.result.name, zipcode, city, country))
    }

    private fun formatOffset(hours: Int, minutes: Int, sharedPreferences: SharedPreferences){
        val offSetToSave = when (hours) {
                in 0 downTo -9 -> {
                    if(minutes == 0) "-0${abs(hours)}:00"
                    else "-0$hours:$minutes"
                }
                in 0..9 -> {
                    if(minutes == 0) "+0$hours:00"
                    else "+0$hours:$minutes"
                }
                in 10..13 -> {
                    if(minutes == 0) "+$hours:00"
                    else  "+$hours:$minutes"
                }
                in -10 downTo -13 -> {
                    if(minutes == 0)  "-0${abs(hours)}:00"
                    else  "-$hours:$minutes"
                }
                else -> "+00:00"
            }

        sharedPreferences.edit().putString("offset", offSetToSave).apply()
    }

    suspend fun refreshToken(sharedPreferences: SharedPreferences): String? {
        val authService = DayzeeRepository().getAuthService()
        val newAccessToken = authService.refreshAccessToken(sharedPreferences.getString(com.timenoteco.timenote.model.refreshToken, null)!!)
        sharedPreferences.edit().putString(accessToken, newAccessToken.body()?.accessToken).apply()
        return newAccessToken.body()?.accessToken
    }
}


fun <T : Drawable> T.bytesEqualTo(t: T?) = toBitmap().bytesEqualTo(t?.toBitmap(), true)

fun <T : Drawable> T.pixelsEqualTo(t: T?) = toBitmap().pixelsEqualTo(t?.toBitmap(), true)

fun Bitmap.bytesEqualTo(otherBitmap: Bitmap?, shouldRecycle: Boolean = false) = otherBitmap?.let { other ->
    if (width == other.width && height == other.height) {
        val res = toBytes().contentEquals(other.toBytes())
        if (shouldRecycle) {
            doRecycle().also { otherBitmap.doRecycle() }
        }
        res
    } else false
} ?: kotlin.run { false }

fun Bitmap.pixelsEqualTo(otherBitmap: Bitmap?, shouldRecycle: Boolean = false) = otherBitmap?.let { other ->
    if (width == other.width && height == other.height) {
        val res = Arrays.equals(toPixels(), other.toPixels())
        if (shouldRecycle) {
            doRecycle().also { otherBitmap.doRecycle() }
        }
        res
    } else false
} ?: kotlin.run { false }

fun Bitmap.doRecycle() {
    if (!isRecycled) recycle()
}

fun <T : Drawable> T.toBitmap(): Bitmap {
    if (this is BitmapDrawable) return bitmap

    val drawable: Drawable = this
    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

fun Bitmap.toBytes(): ByteArray = ByteArrayOutputStream().use { stream ->
    compress(Bitmap.CompressFormat.JPEG, 100, stream)
    stream.toByteArray()
}

fun Bitmap.toPixels() = IntArray(width * height).apply { getPixels(this, 0, width, 0, 0, width, height) }