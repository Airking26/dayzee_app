package com.timenoteco.timenote.webService

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.model.Category
import com.timenoteco.timenote.model.Location
import com.timenoteco.timenote.model.NearbyFilterModel
import java.lang.reflect.Type

class NearbyFilterData(context: Context) {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val type: Type = object : TypeToken<NearbyFilterModel?>() {}.type

    private var nearbyFilterModel: NearbyFilterModel? = Gson().fromJson<NearbyFilterModel>(
        prefs.getString("nearby",
            Gson().toJson(NearbyFilterModel(null, null, null, null, null, null))), type)

    private fun notifyNearbyFilterChanged(){
        prefs.edit().putString("nearby", Gson().toJson(nearbyFilterModel)).apply()
    }

    fun loadNearbyFilter(): NearbyFilterModel? {
        return nearbyFilterModel
    }

    fun setCategories(categories: List<Category>){
        nearbyFilterModel?.categories = categories
        notifyNearbyFilterChanged()
    }

    fun setFrom(from: Int){
        nearbyFilterModel?.from = from
        notifyNearbyFilterChanged()
    }

    fun setPaidTimenote(paidTimenote: Int){
        nearbyFilterModel?.paidTimenote = paidTimenote
        notifyNearbyFilterChanged()
    }

    fun setDistance(distance: Int){
        nearbyFilterModel?.distance = distance
        notifyNearbyFilterChanged()
    }

    fun setWhen(whenn: String){
        nearbyFilterModel?.whenn = whenn
        notifyNearbyFilterChanged()
    }

    fun setWhere(where: Location){
        nearbyFilterModel?.where = where
        notifyNearbyFilterChanged()
    }

    fun clear(){}


}