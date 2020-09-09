package com.timenoteco.timenote.webService

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.model.*
import java.lang.reflect.Type

class NearbyFilterData(context: Context) {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val type: Type = object : TypeToken<NearbyRequestBody?>() {}.type

    private var nearbyFilterModel: NearbyRequestBody? = Gson().fromJson<NearbyRequestBody>(
        prefs.getString("nearby",
            Gson().toJson(
                NearbyRequestBody(Location(0.0, 0.0, Address("","", "","")),
                10, Categories("", ""), "", 0, ""))), type)

    private fun notifyNearbyFilterChanged(){
        prefs.edit().putString("nearby", Gson().toJson(nearbyFilterModel)).apply()
    }

    fun loadNearbyFilter(): NearbyRequestBody? {
        return nearbyFilterModel
    }

    fun setCategories(categories: Categories){
        nearbyFilterModel?.categories = categories
        notifyNearbyFilterChanged()
    }

    fun setFrom(from: String){
        nearbyFilterModel?.type = from
        notifyNearbyFilterChanged()
    }

    fun setPaidTimenote(paidTimenote: Int){
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

    fun clear(){}


}