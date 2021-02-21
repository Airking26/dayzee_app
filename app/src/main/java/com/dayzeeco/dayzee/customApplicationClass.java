package com.dayzeeco.dayzee;

import android.app.Application;

import io.branch.referral.Branch;

public class customApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Branch logging for debugging
        Branch.enableDebugMode();

        // Branch object initialization
        Branch.getAutoInstance(this);
    }
}
