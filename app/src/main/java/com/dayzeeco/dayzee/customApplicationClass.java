package com.dayzeeco.dayzee;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.CameraX;
import androidx.camera.core.CameraXConfig;
import androidx.work.Configuration;
import androidx.work.DelegatingWorkerFactory;
import androidx.work.WorkerFactory;

import com.dayzeeco.dayzee.androidView.instaLike.PictureSelectorEngineImp;
import com.dayzeeco.dayzee.webService.repo.DayzeeRepository;
import com.dayzeeco.dayzee.webService.service.TimenoteService;
import com.dayzeeco.dayzee.worker.MyWorkerFactory;
import com.dayzeeco.picture_library.app.IApp;
import com.dayzeeco.picture_library.app.PictureAppMaster;
import com.dayzeeco.picture_library.crash.PictureSelectorCrashUtils;
import com.dayzeeco.picture_library.engine.PictureSelectorEngine;

import io.branch.referral.Branch;

public class customApplicationClass extends Application implements IApp, CameraXConfig.Provider, Configuration.Provider {

    @Override
    public void onCreate() {
        super.onCreate();
        // Branch logging for debugging
        Branch.enableDebugMode();

        // Branch object initialization
        Branch.getAutoInstance(this);
        PictureAppMaster.getInstance().setApp(this);
        PictureSelectorCrashUtils.init((t, e) -> {});
    }

    @Override
    public Context getAppContext() {
        return this;
    }

    @Override
    public PictureSelectorEngine getPictureSelectorEngine() {
        return new PictureSelectorEngineImp();
    }

    @NonNull
    @Override
    public CameraXConfig getCameraXConfig() {
        return Camera2Config.defaultConfig();
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        DelegatingWorkerFactory dwf = new DelegatingWorkerFactory();
        dwf.addFactory(new MyWorkerFactory(new DayzeeRepository().getTimenoteService(), new DayzeeRepository().getAuthService()));
        return new Configuration.Builder()
                .setMinimumLoggingLevel(Log.DEBUG)
                .setWorkerFactory(dwf)
                .build();
    }
}
