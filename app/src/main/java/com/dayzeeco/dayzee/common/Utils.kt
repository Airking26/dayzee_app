package com.dayzeeco.dayzee.common

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.WebSearchAdapter
import com.dayzeeco.dayzee.androidView.dialog.input
import com.dayzeeco.dayzee.model.Address
import com.dayzeeco.dayzee.model.DetailedPlace
import com.dayzeeco.dayzee.model.Location
import com.dayzeeco.dayzee.viewModel.WebSearchViewModel
import com.dayzeeco.dayzee.webService.repo.DayzeeRepository
import kotlinx.android.synthetic.main.web_search_rv.view.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.*
import java.util.*
import kotlin.math.abs
import kotlin.time.ExperimentalTime


class Utils {

    fun createWebSearchDialog(
        context: Context,
        webSearchViewModel: WebSearchViewModel,
        fragment: Fragment,
        view: View?,
        view1: View?
    ) {
        var recyclerView : RecyclerView?
        var webSearchAdapter : WebSearchAdapter? = null
        //var progressDialog: Dialog = progressDialog(context)
        MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            input { _, charSequence ->
                //progressDialog.show()
                webSearchViewModel.search(
                    charSequence.toString(),
                    context,
                    0,
                    context.getString(R.string.api_web_key)
                )
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
                                //progressDialog.hide()
                            }

                            dialog.onDismiss {
                                webSearchAdapter?.clear()
                                webSearchAdapter = null
                                webSearchViewModel.getListResults()
                                    .removeObservers(fragment.viewLifecycleOwner)
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

    fun hideStatusBar(activity: Activity){
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_IMMERSIVE
    }

    @SuppressLint("InlinedApi")
    @RequiresApi(Build.VERSION_CODES.M)
    fun showStatusBar(activity: Activity){
        if(activity.resources.getColor(R.color.colorBackground) == activity.resources.getColor(
                android.R.color.white
            )){
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

    fun checkIf24HFormat(context: Context): Boolean {
        return android.text.format.DateFormat.is24HourFormat(context)
    }

    fun formatDate(format: String, timestamp: Long): String {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return if(timestamp == 0L) ""
        else dateFormat.format(timestamp)
    }

    fun formatDateToShare(date: String): String{
        val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val dateFormatted = "d MMMM yyyy"
        val dateFormat = SimpleDateFormat(ISO, Locale.getDefault()).parse(date)!!.time
        val o = SimpleDateFormat(dateFormatted, Locale.getDefault()).format(dateFormat)
        return SimpleDateFormat(dateFormatted, Locale.getDefault()).format(dateFormat)
    }

    fun formatHourToShare(date: String): String{
        val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val dateFormatted = "HH:mm"
        val dateFormat = SimpleDateFormat(ISO, Locale.getDefault()).parse(date)!!.time
        val o = SimpleDateFormat(dateFormatted, Locale.getDefault()).format(dateFormat)
        return SimpleDateFormat(dateFormatted, Locale.getDefault()).format(dateFormat)
    }

    fun setFormatedStartDate(startDate: String, endDate: String, context: Context) : String{
        val DATE_FORMAT_DAY = "d MMM yyyy"
        val DATE_FORMAT_TIME = if(checkIf24HFormat(context)) "HH:mm" else "hh:mm aaa"
        val DATE_FORMAT_TIME_FORMATED = "d\nMMM"
        val DATE_FORMAT_SAME_DAY_DIFFERENT_TIME = if(checkIf24HFormat(context)) "d MMM\nHH:mm" else "d MMM\nhh:mm"
        val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

        val formatedStartDate: String

        val starting = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant.parse(startDate).epochSecond * 1000
        } else {
            val o = SimpleDateFormat(ISO, Locale.getDefault())
            o.timeZone = TimeZone.getTimeZone("UTC")
            val m = o.parse(startDate)
            o.timeZone = TimeZone.getDefault()
            val k = o.format(m)
            SimpleDateFormat(ISO, Locale.getDefault()).parse(k)!!.time
        }

        val ending = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant.parse(endDate).epochSecond * 1000
        } else {
            val o = SimpleDateFormat(ISO, Locale.getDefault())
            o.timeZone = TimeZone.getTimeZone("UTC")
            val m = o.parse(endDate)
            o.timeZone = TimeZone.getDefault()
            val k = o.format(m)
            SimpleDateFormat(ISO, Locale.getDefault()).parse(k)!!.time
        }



        formatedStartDate =
            if(formatDate(DATE_FORMAT_DAY, starting) == formatDate(DATE_FORMAT_DAY, ending)){
                if(formatDate(DATE_FORMAT_TIME, starting) == formatDate(DATE_FORMAT_TIME, ending)){
                    formatDate(DATE_FORMAT_TIME_FORMATED, starting)
                } else {
                    formatDate(DATE_FORMAT_TIME_FORMATED, starting)
                }
            } else {
                formatDate(DATE_FORMAT_SAME_DAY_DIFFERENT_TIME, starting)
            }

        return formatedStartDate
    }

    fun setFormatedEndDate(startDate: String, endDate: String, context: Context): String{
        val DATE_FORMAT_DAY = "d MMM yyyy"
        val DATE_FORMAT_TIME = if(checkIf24HFormat(context)) "HH:mm" else "hh:mm aaa"
        val DATE_FORMAT_SAME_DAY_DIFFERENT_TIME = if(checkIf24HFormat(context)) "d MMM\nHH:mm" else "d MMM\nhh:mm"
        val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

        var formatedEndDate: String

        val starting = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant.parse(startDate).epochSecond * 1000
        } else {
            val o = SimpleDateFormat(ISO, Locale.getDefault())
            o.timeZone = TimeZone.getTimeZone("UTC")
            val m = o.parse(startDate)
            o.timeZone = TimeZone.getDefault()
            val k = o.format(m)
            SimpleDateFormat(ISO, Locale.getDefault()).parse(k)!!.time
            //SimpleDateFormat(ISO, Locale.getDefault()).parse(startDate).time
        }

        val ending = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant.parse(endDate).epochSecond * 1000
        } else {
            val o = SimpleDateFormat(ISO, Locale.getDefault())
            o.timeZone = TimeZone.getTimeZone("UTC")
            val m = o.parse(endDate)
            o.timeZone = TimeZone.getDefault()
            val k = o.format(m)
            SimpleDateFormat(ISO, Locale.getDefault()).parse(k)!!.time
        }
        formatedEndDate =
            if(formatDate(DATE_FORMAT_DAY, starting) == formatDate(DATE_FORMAT_DAY, ending)){
                if(formatDate(DATE_FORMAT_TIME, starting) == formatDate(DATE_FORMAT_TIME, ending)){
                    formatDate(DATE_FORMAT_TIME, starting)
                } else {
                    formatDate(DATE_FORMAT_TIME, starting) + "\n" + formatDate(
                        DATE_FORMAT_TIME,
                        ending
                    )
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
            val o = SimpleDateFormat(ISO, Locale.getDefault())
            o.timeZone = TimeZone.getTimeZone("UTC")
            val m = o.parse(startDate)
            o.timeZone = TimeZone.getDefault()
            val k = o.format(m)
            SimpleDateFormat(ISO, Locale.getDefault()).parse(k)!!.time
        }
        return formatDate(YEAR, starting)
    }

    @ExperimentalTime
    fun inTime(startDate: String, context: Context): String {
        val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val nbrYear : Int
        val nbrMonth: Int
        val nbrDay : Int
        val nbrHours : Int
        val nbrMin : Int
        val nbrSec : Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val period = Period.between(
                LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC).toLocalDate(),
                LocalDateTime.ofInstant(Instant.parse(startDate), ZoneOffset.UTC).toLocalDate()
            )

            nbrYear = period.years
            nbrMonth = period.minusYears(nbrYear.toLong()).months
            val nD = period.minusYears(nbrYear.toLong()).minusMonths(nbrMonth.toLong()).days

            val duration = Duration.between(Instant.now(), Instant.parse(startDate))
            nbrDay = if(duration.toDays().toInt() < nD) duration.toDays().toInt() else nD
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
            nbrSec = c.get(Calendar.SECOND) * 1000
        }

        //return formatInTime(nbrYear, nbrMonth, nbrDay, nbrHours, nbrMin, nbrSec, context)
        return ""
    }

    fun formatInTime(
        nbrYear: Long,
        nbrMonth: Long,
        nbrDay: Long,
        nbrHour: Long,
        nbrMin: Long,
        nbrSec: Long,
        context: Context
    ): String {

        val decountTime: String
        if(nbrYear <= 0 && nbrMonth <= 0 && nbrDay <= 0 && nbrHour <= 0 && nbrMin < 0 && nbrSec <= 0){
            decountTime = context.getString(R.string.live)
        }
        else
        if(nbrYear <= 0){
            if(nbrMonth <= 0){
                if(nbrDay <= 0) {
                    if (nbrHour <= 0) {
                        decountTime =
                            if(nbrMin > 1){
                                context.resources.getQuantityString(R.plurals.minutes_plural_seconds_vary, nbrMin.toInt(), nbrMin, nbrSec)
                            } else {
                                context.resources.getQuantityString(R.plurals.minutes_plural_seconds_vary, nbrMin.toInt(), nbrMin, nbrSec)
                            }
                    }
                    else {
                        decountTime =
                            if (nbrHour > 1) {
                                if (nbrMin > 1) {
                                    context.resources.getQuantityString(
                                        R.plurals.hours_plural_minutes_vary,
                                        nbrMin.toInt(),
                                        nbrHour,
                                        nbrMin
                                    )
                                } else {
                                    context.resources.getQuantityString(
                                        R.plurals.hours_plural_minutes_vary,
                                        nbrMin.toInt(),
                                        nbrHour,
                                        nbrMin
                                    )
                                }
                            }
                            else {
                                if (nbrMin > 1) {
                                    context.resources.getQuantityString(
                                        R.plurals.hours_singular_minutes_vary,
                                        nbrMin.toInt(),
                                        nbrHour,
                                        nbrMin
                                    )
                                } else {
                                    context.resources.getQuantityString(
                                        R.plurals.hours_singular_minutes_vary,
                                        nbrMin.toInt(),
                                        nbrHour,
                                        nbrMin
                                    )
                                }
                            }
                    }
                }
                else {
                    decountTime = if(nbrDay > 1){
                        if(nbrHour > 1) context.resources.getQuantityString(
                            R.plurals.days_plural_hours_vary,
                            nbrHour.toInt(),
                            nbrDay,
                            nbrHour
                        )
                        else context.resources.getQuantityString(
                            R.plurals.days_plural_hours_vary,
                            nbrHour.toInt(),
                            nbrDay,
                            nbrHour
                        )
                    } else {
                        if(nbrHour > 1) context.resources.getQuantityString(
                            R.plurals.days_singular_hours_vary,
                            nbrHour.toInt(),
                            nbrDay,
                            nbrHour
                        )
                        else context.resources.getQuantityString(
                            R.plurals.days_singular_hours_vary,
                            nbrHour.toInt(),
                            nbrHour,
                            nbrHour
                        )
                    }

                }
            }
            else {
                decountTime = if(nbrMonth > 1){
                    if(nbrDay > 1){
                        context.resources.getQuantityString(
                            R.plurals.months_plural_days_vary,
                            nbrDay.toInt(),
                            nbrMonth,
                            nbrDay
                        )
                    } else {
                        context.resources.getQuantityString(
                            R.plurals.months_plural_days_vary,
                            nbrDay.toInt(),
                            nbrMonth,
                            nbrDay
                        )
                    }
                } else {
                    if(nbrDay >1){
                        context.resources.getQuantityString(
                            R.plurals.months_singular_days_vary,
                            nbrDay.toInt(),
                            nbrMonth,
                            nbrDay
                        )
                    } else {
                        context.resources.getQuantityString(
                            R.plurals.months_singular_days_vary,
                            nbrDay.toInt(),
                            nbrMonth,
                            nbrDay
                        )
                    }
                }
            }
        } else {
            decountTime = if(nbrYear > 1){
                if(nbrMonth > 1) {
                    context.resources.getQuantityString(
                        R.plurals.years_plural_months_vary,
                        nbrMonth.toInt(),
                        nbrYear,
                        nbrMonth
                    )
                } else {
                    context.resources.getQuantityString(
                        R.plurals.years_plural_months_vary,
                        nbrMonth.toInt(),
                        nbrYear,
                        nbrMonth
                    )
                }
            } else {
                if(nbrMonth > 1){
                    context.resources.getQuantityString(
                        R.plurals.years_singular_month_vary,
                        nbrMonth.toInt(),
                        nbrYear,
                        nbrMonth
                    )
                } else {
                    context.resources.getQuantityString(
                        R.plurals.years_singular_month_vary,
                        nbrMonth.toInt(),
                        nbrYear,
                        nbrMonth
                    )
                }
            }
        }

        return decountTime
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sinceTime(startDate: String, context: Context): String {
        val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val nbrYear : Int
        val nbrMonth: Int
        val nbrDay : Int
        val nbrHours : Int
        val nbrMin : Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val period = Period.between(
                LocalDateTime.ofInstant(
                    Instant.parse(startDate),
                    ZoneOffset.UTC
                ).toLocalDate(),
                LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC).toLocalDate()
            )

            nbrYear = period.years
            nbrMonth = period.minusYears(nbrYear.toLong()).months
            val nD = period.minusYears(nbrYear.toLong()).minusMonths(nbrMonth.toLong()).days


            val duration = Duration.between(Instant.now(), Instant.parse(startDate))
            nbrDay = if(abs(duration.toDays().toInt()) < nD) duration.toDays().toInt() else nD
            nbrHours = duration.minusDays(duration.toDays()).toHours().toInt()
            nbrMin = duration.minusDays(duration.toDays()).minusHours(
                duration.minusDays(duration.toDays()).toHours()
            ).toMinutes().toInt()

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

        return formatSinceTime(
            abs(nbrYear),
            abs(nbrMonth),
            abs(nbrDay),
            abs(nbrHours),
            abs(nbrMin),
            context
        )
    }

    private fun formatSinceTime(
        nbrYear: Int,
        nbrMonth: Int,
        nbrDay: Int,
        nbrHours: Int,
        nbrMin: Int,
        context: Context
    ): String {
        val decountTime: String
        if(nbrYear == 0){
            if(nbrMonth == 0){
                if(nbrDay == 0){
                    decountTime = if(nbrHours > 1){
                        if(nbrMin > 1){
                            context.resources.getQuantityString(
                                R.plurals.since_hours_plural_minutes_vary,
                                nbrMin,
                                nbrHours,
                                nbrMin
                            )
                        } else {
                            context.resources.getQuantityString(
                                R.plurals.since_hours_plural_minutes_vary,
                                nbrMin,
                                nbrHours,
                                nbrMin
                            )
                        }
                    } else {
                        if(nbrMin > 1){
                            context.resources.getQuantityString(
                                R.plurals.since_hours_singular_minutes_vary,
                                nbrMin,
                                nbrHours,
                                nbrMin
                            )
                        } else {
                            context.resources.getQuantityString(
                                R.plurals.since_hours_singular_minutes_vary,
                                nbrMin,
                                nbrHours,
                                nbrMin
                            )
                        }
                    }
                } else {
                    decountTime = if(nbrDay > 1){
                        if(nbrHours > 1) context.resources.getQuantityString(
                            R.plurals.since_days_plural_hours_vary,
                            nbrHours,
                            nbrDay,
                            nbrHours
                        )
                        else context.resources.getQuantityString(
                            R.plurals.since_days_plural_hours_vary,
                            nbrHours,
                            nbrDay,
                            nbrHours
                        )
                    } else {
                        if(nbrHours > 1) context.resources.getQuantityString(
                            R.plurals.since_days_singular_hours_vary,
                            nbrHours,
                            nbrDay,
                            nbrHours
                        )
                        else context.resources.getQuantityString(
                            R.plurals.since_days_singular_hours_vary,
                            nbrHours,
                            nbrDay,
                            nbrHours
                        )
                    }

                }
            } else {
                decountTime = if(nbrMonth > 1){
                    if(nbrDay > 1){
                        context.resources.getQuantityString(
                            R.plurals.since_months_plural_days_vary,
                            nbrDay,
                            nbrMonth,
                            nbrDay
                        )
                    } else {
                        context.resources.getQuantityString(
                            R.plurals.since_months_plural_days_vary,
                            nbrDay,
                            nbrMonth,
                            nbrDay
                        )
                    }
                } else {
                    if(nbrDay >1){
                        context.resources.getQuantityString(
                            R.plurals.since_months_singular_days_vary,
                            nbrDay,
                            nbrMonth,
                            nbrDay
                        )
                    } else {
                        context.resources.getQuantityString(
                            R.plurals.since_months_singular_days_vary,
                            nbrDay,
                            nbrMonth,
                            nbrDay
                        )
                    }
                }
            }
        } else {
            decountTime = if(nbrYear > 1){
                if(nbrMonth > 1) {
                    context.resources.getQuantityString(
                        R.plurals.since_years_plural_months_vary,
                        nbrMonth,
                        nbrYear,
                        nbrMonth
                    )
                } else {
                    context.resources.getQuantityString(
                        R.plurals.since_years_plural_months_vary,
                        nbrMonth,
                        nbrYear,
                        nbrMonth
                    )
                }
            } else {
                if(nbrMonth > 1){
                    context.resources.getQuantityString(
                        R.plurals.since_years_singular_month_vary,
                        nbrMonth,
                        nbrYear,
                        nbrMonth
                    )
                } else {
                    context.resources.getQuantityString(
                        R.plurals.since_years_singular_month_vary,
                        nbrMonth,
                        nbrYear,
                        nbrMonth
                    )
                }
            }
        }
        return decountTime


    }

    fun setLocation(
        detailedPlace: DetailedPlace,
        isInCreation: Boolean,
        sharedPreferences: SharedPreferences?
    ): Location {
        var zipcode = ""
        var city = ""
        var country = ""
        if(isInCreation) formatOffset(
            detailedPlace.result.utc_offset / 60,
            detailedPlace.result.utc_offset % 60,
            sharedPreferences!!
        )
        for(n in detailedPlace.result.address_components){
            if(n.types.contains("locality")) city = n.long_name
            if(n.types.contains("postal_code")) zipcode = n.short_name
            if(n.types.contains("country")) country = n.long_name
        }
        return Location(
            detailedPlace.result.geometry.location.lng, detailedPlace.result.geometry.location.lat,
            Address(detailedPlace.result.name, zipcode, city, country)
        )
    }

    private fun formatOffset(hours: Int, minutes: Int, sharedPreferences: SharedPreferences){
        val offSetToSave = when (hours) {
            in 0 downTo -9 -> {
                if (minutes == 0) "-0${abs(hours)}:00"
                else "-0$hours:$minutes"
            }
            in 0..9 -> {
                if (minutes == 0) "+0$hours:00"
                else "+0$hours:$minutes"
            }
            in 10..13 -> {
                if (minutes == 0) "+$hours:00"
                else "+$hours:$minutes"
            }
            in -10 downTo -13 -> {
                if (minutes == 0) "-0${abs(hours)}:00"
                else "-$hours:$minutes"
            }
                else -> "+00:00"
            }

        sharedPreferences.edit().putString(offset, offSetToSave).apply()
    }

    suspend fun refreshToken(sharedPreferences: SharedPreferences): String? {
        val authService = DayzeeRepository().getAuthService()
        val newAccessToken = authService.refreshAccessToken(
            sharedPreferences.getString(
                refreshToken,
                null
            )!!
        )
        sharedPreferences.edit().putString(accessToken, newAccessToken.body()?.accessToken).apply()
        return newAccessToken.body()?.accessToken
    }

    fun getLocaFromAddress(context: Context, address: String): Location? {
        var loca: Location? = null
        if(isNetworkAvailable(context)) {
            val geo = Geocoder(context)
            try {
                val g = if (!address.isBlank()) geo.getFromLocationName(address, 5) else listOf()
                loca = if (g.isNotEmpty())
                    Location(g[0].longitude, g[0].latitude, Address(address, "", "", ""))
                else null
            } catch (e: IOException) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        } else Toast.makeText(
            context,
            context.getString(R.string.error_network),
            Toast.LENGTH_SHORT
        ).show()
        return loca
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
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
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

fun Bitmap.toBytes(): ByteArray = ByteArrayOutputStream().use { stream ->
    compress(Bitmap.CompressFormat.JPEG, 100, stream)
    stream.toByteArray()
}

fun Bitmap.toPixels() = IntArray(width * height).apply { getPixels(
    this,
    0,
    width,
    0,
    0,
    width,
    height
) }

fun getSafeSubstring(s: String, maxLength: Int): String? {
    if (!TextUtils.isEmpty(s)) {
        if (s.length >= maxLength) {
            return s.substring(0, maxLength).plus("...")
        }
    }
    return s
}