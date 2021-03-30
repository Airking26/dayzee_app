package com.dayzeeco.dayzee.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.dayzeeco.dayzee.webService.service.AuthService
import com.dayzeeco.dayzee.webService.service.TimenoteService

class MyWorkerFactory(private val timenoteService: TimenoteService, private val loginService: AuthService) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when(workerClassName) {
            SynchronizeGoogleCalendarWorker::class.java.name ->
                SynchronizeGoogleCalendarWorker(appContext, workerParameters, timenoteService, loginService)
            else ->
                null
        }

    }
}