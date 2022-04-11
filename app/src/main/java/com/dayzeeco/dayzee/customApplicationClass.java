package com.dayzeeco.dayzee;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.CameraX;
import androidx.camera.core.CameraXConfig;

import com.appsflyer.AppsFlyerLib;
import com.appsflyer.attribution.AppsFlyerRequestListener;
import com.dayzeeco.dayzee.androidView.instaLike.PictureSelectorEngineImp;
import com.dayzeeco.dayzee.webService.repo.DayzeeRepository;
import com.dayzeeco.dayzee.webService.service.TimenoteService;
import com.dayzeeco.picture_library.app.IApp;
import com.dayzeeco.picture_library.app.PictureAppMaster;
import com.dayzeeco.picture_library.crash.PictureSelectorCrashUtils;
import com.dayzeeco.picture_library.engine.PictureSelectorEngine;
import com.google.firebase.analytics.FirebaseAnalytics;

import io.branch.referral.Branch;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

public class customApplicationClass extends Application implements IApp, CameraXConfig.Provider {

    @Override
    public void onCreate() {
        super.onCreate();
        Branch.enableDebugMode();

        FirebaseAnalytics.getInstance(this);
        //AppsFlyerLib.getInstance().init("KdGKBY4Q3u3ooKjm4KT5am", null, this);
        AppsFlyerLib.getInstance().start(this, "KdGKBY4Q3u3ooKjm4KT5am", new AppsFlyerRequestListener() {
            @Override
            public void onSuccess() {
                Log.d("appsflyer", "Launch sent successfully, got 200 response code from server");
            }

            @Override
            public void onError(int i, @NonNull String s) {
                Log.d("Appsflyer", "Launch failed to be sent:\n" +
                        "Error code: " + i + "\n"
                        + "Error description: " + s);
            }
        });
        AppsFlyerLib.getInstance().setDebugLog(true);
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

}
