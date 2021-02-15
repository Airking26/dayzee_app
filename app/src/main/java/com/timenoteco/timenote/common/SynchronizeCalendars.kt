package com.timenoteco.timenote.common

import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.text.format.DateUtils
import androidx.preference.PreferenceManager
import com.timenoteco.timenote.model.CalendarEvent
import java.util.*

class SynchronizeCalendars{

    private val IS_EMAIL_LINKED = "is_email_linked"
    private val GMAIL = "gmail"
    var mEventsList = ArrayList<CalendarEvent>()
    private val EVENT_PROJECTION = arrayOf(
        CalendarContract.Calendars._ID,
        CalendarContract.Calendars.ACCOUNT_NAME,
        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
        CalendarContract.Calendars.OWNER_ACCOUNT
    )

    fun readCalendar(context: Context, days: Int, hours: Int) {
        val sharedPref =
            PreferenceManager.getDefaultSharedPreferences(context)
        if (sharedPref.getBoolean(IS_EMAIL_LINKED, false)) {
            val selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?))"
            val selectionArgs =
                arrayOf(sharedPref.getString(GMAIL, null))
            val contentResolver = context.contentResolver
            val uri = CalendarContract.Calendars.CONTENT_URI
            @SuppressLint("MissingPermission") val cursor =
                contentResolver.query(uri, EVENT_PROJECTION, selection, selectionArgs, null)
            val calendarIds = getCalenderIds(cursor)
            val eventMap = HashMap<String, List<CalendarEvent>>()
            val idCal = getGoogleCalendarId(context)
            for (id in calendarIds) {
                if (id.equals(idCal.toString(), ignoreCase = true)) {
                    val builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon()
                    val now = System.currentTimeMillis()
                    ContentUris.appendId(builder, now)
                    ContentUris.appendId(builder, Long.MAX_VALUE)
                    val eventCursor = contentResolver.query(
                        builder.build(), arrayOf(
                            CalendarContract.Events.TITLE,
                            CalendarContract.Instances.BEGIN,
                            CalendarContract.Instances.END,
                            CalendarContract.Events.ALL_DAY,
                            CalendarContract.Instances.EVENT_ID,
                            CalendarContract.Events.EVENT_LOCATION,
                            CalendarContract.Events.SELF_ATTENDEE_STATUS
                        ),
                        CalendarContract.Events.CALENDAR_ID + "=" + id,
                        null,
                        "startDay ASC, startMinute ASC"
                    )
                    println("eventCursor count=" + (Objects.requireNonNull(eventCursor)?.count ?: ""))
                    if (eventCursor!!.count > 0) {
                        eventCursor.moveToNext()
                        var ce = loadEvent(eventCursor)
                        mEventsList.add(ce!!)

                        while (eventCursor.moveToNext()) {
                            ce = loadEvent(eventCursor)
                            mEventsList.add(ce!!)
                        }

                        mEventsList.sort()
                        eventMap[id] = mEventsList
                    }
                }
            }
        }
    }


    private fun getGoogleCalendarId(context: Context): Long {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT
        )
        @SuppressLint("MissingPermission") val calCursor =
            Objects.requireNonNull(
                context.contentResolver.query(
                    CalendarContract.Calendars.CONTENT_URI,
                    projection,
                    null,
                    null,
                    CalendarContract.Calendars._ID + " ASC"
                )
            )
        if (calCursor?.moveToFirst()!!) {
            do {
                val id = calCursor.getLong(0)
                val displayName = calCursor.getString(1)
                val email = PreferenceManager.getDefaultSharedPreferences(context).getString(GMAIL, null)
                if (displayName.contains(email.toString())) {
                    return id
                }
            } while (calCursor.moveToNext())
        }
        calCursor.close()
        return -1
    }

    fun syncCalendars(context: Context) {
        val sharedPref =
            PreferenceManager.getDefaultSharedPreferences(context)
        val accountManager = AccountManager.get(context)
        val accounts = accountManager.getAccountsByType(null)
        if (accounts.size != 0 && !sharedPref.getBoolean(IS_EMAIL_LINKED, false)) {
            determineCalendar(context, sharedPref.getString(GMAIL, null))
        }
        val authority = CalendarContract.Calendars.CONTENT_URI.authority
        for (account in accounts) {
            if (account.name == sharedPref.getString(GMAIL, null)) {
                val extras = Bundle()
                extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
                extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
                ContentResolver.requestSync(account, authority, extras)
            }
        }
    }

    private fun loadEvent(csr: Cursor): CalendarEvent? {
        return CalendarEvent(
            csr.getString(0),
            Date(csr.getLong(1)),
            Date(csr.getLong(2)),
            csr.getString(3) != "0",
            csr.getLong(4),
            csr.getString(5),
            csr.getInt(6)
        )
    }

    private fun getCalenderIds(cursor: Cursor?): HashSet<String> {
        val calendarIds = HashSet<String>()
        try {
            if (cursor!!.count > 0) {
                while (cursor.moveToNext()) {
                    val _id = cursor.getString(0)
                    val displayName = cursor.getString(1)
                    val selected = cursor.getString(2) != "0"
                    println("Id: $_id Display Name: $displayName Selected: $selected")
                    calendarIds.add(_id)
                }
            }
        } catch (ex: AssertionError) {
            ex.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return calendarIds
    }

    @SuppressLint("MissingPermission")
    private fun determineCalendar(
        context: Context,
        accountName: String?
    ) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        val selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?))"
        val selectionArgs = arrayOf(accountName)
        val contentResolver = context.contentResolver
        val uri = CalendarContract.Calendars.CONTENT_URI
        val cursor =
            contentResolver.query(uri, EVENT_PROJECTION, selection, selectionArgs, null)
        if (cursor != null) {
            editor.putString(GMAIL, accountName)
            editor.putBoolean(IS_EMAIL_LINKED, true)
            editor.apply()
            cursor.close()
            return
        }
        editor.putBoolean(IS_EMAIL_LINKED, false)
        editor.apply()
    }

}