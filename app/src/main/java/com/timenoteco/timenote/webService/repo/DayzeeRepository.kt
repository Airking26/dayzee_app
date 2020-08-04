package com.timenoteco.timenote.webService.repo

import com.timenoteco.timenote.webService.service.AuthService
import com.timenoteco.timenote.webService.service.ProfileService
import com.timenoteco.timenote.webService.service.TimenoteService
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

}