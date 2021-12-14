package com.dayzeeco.dayzee.worker

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.common.gmail
import com.dayzeeco.dayzee.common.map_event_id_to_timenote
import com.dayzeeco.dayzee.common.refreshToken
import com.dayzeeco.dayzee.model.CreationTimenoteDTO
import com.dayzeeco.dayzee.model.Price
import com.dayzeeco.dayzee.webService.service.AuthService
import com.dayzeeco.dayzee.webService.service.TimenoteService
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

const val user_id = "userId"
const val token_id = "tokenID"

class SynchronizeGoogleCalendarWorker(val context: Context, parameters: WorkerParameters, private val timenoteService: TimenoteService, private val loginService: AuthService) : CoroutineWorker(context, parameters) {

    private val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"
    private val transport = AndroidHttp.newCompatibleTransport()
    private val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()
    private var idUser : String? = null
    private var tokenId : String? = null
    private lateinit var map : MutableMap<String, String>
    private val utils = Utils()

    override suspend fun doWork(): Result {
        idUser = inputData.getString(user_id)
        tokenId = inputData.getString(token_id)
        val credential = GoogleAccountCredential.usingOAuth2(context, listOf(CalendarScopes.CALENDAR_READONLY))
            .setBackOff(ExponentialBackOff())
            .setSelectedAccountName(PreferenceManager.getDefaultSharedPreferences(context).getString(
                gmail, null))

        val service = Calendar.Builder(
            transport, jsonFactory, credential)
            .setApplicationName("TestGoogleCalendar")
            .build()

        map = Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(context).getString(
            map_event_id_to_timenote, null), object : TypeToken<MutableMap<String, String>>() {}.type) ?: mutableMapOf()

        val li = service.events()?.list("primary")?.setMaxResults(10)?.setTimeMin(DateTime( System.currentTimeMillis()))?.setOrderBy("startTime")?.setSingleEvents(true)?.execute()
        val end = if(li?.items!= null && li.items.isNotEmpty()) loopThroughCalendar(li.items.iterator()) else false

        return if(end) Result.success()
        else Result.failure()
    }

    private suspend fun loopThroughCalendar(mEventsList: Iterator<Event>): Boolean {
        if (mEventsList.hasNext()) {
            val item = mEventsList.next()
            if(!map.keys.contains(item.id)) {
                val creationTimenoteDTO = CreationTimenoteDTO(
                    idUser!!,
                    listOf(),
                    item.summary,
                    item.description,
                    listOf("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/toDL.jpg"),
                    null,
                    if(item.location != null) utils.getLocaFromAddress(context, item.location) else null,
                    null,
                    SimpleDateFormat(ISO, Locale.getDefault()).format(if(item.start.date == null) item.start.dateTime.value else item.start.date.value),
                    SimpleDateFormat(ISO, Locale.getDefault()).format(if(item.end.date == null) item.end.dateTime.value else item.end.date.value),
                    listOf(),
                    null,
                    Price(0.0, ""),
                    listOf(idUser!!),
                    null
                )

                var currentTimenote = this.timenoteService.createTimenote("Bearer $tokenId", creationTimenoteDTO)
                if(currentTimenote.isSuccessful) {
                    loopThroughCalendar(mEventsList.iterator())
                    map[item.id] = currentTimenote.body()?.id!!
                }
                else if(currentTimenote.code() == 401){
                    val refreshToken = this.loginService.refreshAccessToken(PreferenceManager.getDefaultSharedPreferences(context).getString(refreshToken, null)!!)
                    tokenId = refreshToken.body()?.accessToken
                    currentTimenote = this.timenoteService.createTimenote("Bearer $tokenId", creationTimenoteDTO)
                    if(currentTimenote.isSuccessful) {
                        loopThroughCalendar(mEventsList.iterator())
                        map[item.id] = currentTimenote.body()?.id!!
                    }
                }

            } else {
                loopThroughCalendar(mEventsList.iterator())
            }
        } else {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(
                map_event_id_to_timenote, Gson().toJson(map)).apply()
            return true
        }

        return false
    }

}