package com.timenoteco.timenote.webService.service

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.model.AlarmCreationDTO
import com.timenoteco.timenote.model.AlarmInfoDTO
import com.timenoteco.timenote.model.NearbyRequestBody
import java.lang.reflect.Type

class AlarmData(context: Context) {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val type: Type = object : TypeToken<MutableList<AlarmInfoDTO?>>() {}.type
    private val listEmpty : MutableList<AlarmInfoDTO> = mutableListOf()

    private var listOfAlarms: MutableList<AlarmInfoDTO> = Gson().fromJson<MutableList<AlarmInfoDTO>>(prefs.getString("alarms", Gson().toJson(listEmpty)), type)

    fun getAlarms(): MutableList<AlarmInfoDTO>{
        return listOfAlarms
    }

    fun deleteAlarm(id: String): MutableList<AlarmInfoDTO>{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            listOfAlarms.removeIf{ t -> t.id == id}
        } else {
            listOfAlarms.forEach { alarm -> if(alarm.id == id) listOfAlarms.remove(alarm) }
        }
        notifyAlarmDataChanged()
        return listOfAlarms
    }

    fun addAlarm(alarmInfoDTO: AlarmInfoDTO): MutableList<AlarmInfoDTO>{
        listOfAlarms.add(alarmInfoDTO)
        notifyAlarmDataChanged()
        return listOfAlarms
    }

    fun updateAlarm(id: String, alarmInfoDTO: AlarmInfoDTO): MutableList<AlarmInfoDTO>{
        listOfAlarms.forEachIndexed { index, aid ->
            if(alarmInfoDTO.id == aid.id) listOfAlarms[index] = alarmInfoDTO
        }
        notifyAlarmDataChanged()
        return listOfAlarms
    }

    private fun notifyAlarmDataChanged(){
        prefs.edit().putString("alarms", Gson().toJson(listOfAlarms)).apply()
    }

}