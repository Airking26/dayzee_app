package com.dayzeeco.dayzee.webService

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.dayzeeco.dayzee.common.nearby
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.dayzeeco.dayzee.model.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class NearbyFilterData(context: Context) {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val type: Type = object : TypeToken<NearbyRequestBody?>() {}.type
    val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    private var nearbyFilterModel: NearbyRequestBody? =
        Gson().fromJson<NearbyRequestBody>(prefs.getString(nearby, Gson().toJson(NearbyRequestBody(Location(0.0, 0.0, Address("","", "","")), 250, null, SimpleDateFormat(ISO, Locale.getDefault()).format(System.currentTimeMillis()), Price(0.0, ""), 2))), type)

    private fun notifyNearbyFilterChanged(){
        prefs.edit().putString(nearby, Gson().toJson(nearbyFilterModel)).apply()
    }

    fun loadNearbyFilter(): NearbyRequestBody? {
        return nearbyFilterModel
    }

    fun setCategories(categories: Categories){
        nearbyFilterModel?.categories = categories
        notifyNearbyFilterChanged()
    }

    fun setFrom(from: Int){
        nearbyFilterModel?.type = from
        notifyNearbyFilterChanged()
    }

    fun setPaidTimenote(paidTimenote: Price){
        nearbyFilterModel?.price = paidTimenote
        notifyNearbyFilterChanged()
    }

    fun setDistance(distance: Int){
        nearbyFilterModel?.maxDistance = distance
        notifyNearbyFilterChanged()
    }

    fun setWhen(whenn: String){
        nearbyFilterModel?.date = whenn
        notifyNearbyFilterChanged()
    }

    fun setWhere(where: Location){
        nearbyFilterModel?.location = where
        notifyNearbyFilterChanged()
    }

    fun setID(id: String){
        nearbyFilterModel?.userID = id
        notifyNearbyFilterChanged()
    }

    fun clearData(nearbyRequestBody: NearbyRequestBody){
        nearbyFilterModel = NearbyRequestBody(nearbyRequestBody.location,10, null, SimpleDateFormat(ISO, Locale.getDefault()).format(System.currentTimeMillis()), Price(0.0, ""), 2)
        notifyNearbyFilterChanged()
    }

}