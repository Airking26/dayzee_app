package com.dayzeeco.dayzee.webService.repo

import com.dayzeeco.dayzee.webService.service.*
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DayzeeRepository {

    companion object{
        private const val BASE_URL = "http://timenote-env.eba-2htqeacb.us-east-1.elasticbeanstalk.com/"
    }

    private val service = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getAuthService(): AuthService {
        return service.create(AuthService::class.java)
    }

    fun getTimenoteService(): TimenoteService{
        return service.create(TimenoteService::class.java)
    }

    fun getProfileService(): ProfileService{
        return service.create(ProfileService::class.java)
    }

    fun getFollowService(): FollowService{
        return service.create(FollowService::class.java)
    }

    fun getPreferencesService(): PreferencesService{
        return service.create(PreferencesService::class.java)
    }

    fun getMeService(): MeService{
        return service.create(MeService::class.java)
    }

    fun getSearchService(): SearchService{
        return service.create(SearchService::class.java)
    }

    fun getCommentService(): CommentService{
        return service.create(CommentService::class.java)
    }

    fun getAlarmService(): AlarmService{
        return service.create(AlarmService::class.java)
    }

    fun getNearbyService(): NearbyService{
        return service.create(NearbyService::class.java)
    }
}