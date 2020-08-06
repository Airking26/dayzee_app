package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.Category
import com.timenoteco.timenote.model.Preferences
import com.timenoteco.timenote.model.SubCategoryRated
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH

interface PreferencesService {

    @GET("preference/me")
    suspend fun getMyPreferences(@Header("Authorization") token: String) : Response<MutableList<SubCategoryRated>>

    @PATCH("preference/me")
    suspend fun modifyPreferences(@Header("Authorization") token: String, @Body preferences: Preferences) : Response<List<SubCategoryRated>>

    @GET("category/all")
    suspend fun getAllCategories() : Response<List<Category>>

}