package com.dayzeeco.dayzee.androidView.instaLike;

import android.util.Log;

import com.dayzeeco.picture_library.engine.ImageEngine;
import com.dayzeeco.picture_library.engine.PictureSelectorEngine;
import com.dayzeeco.picture_library.entity.LocalMedia;
import com.dayzeeco.picture_library.listener.OnResultCallbackListener;

import java.util.List;

public class PictureSelectorEngineImp implements PictureSelectorEngine {
    private static final String TAG = PictureSelectorEngineImp.class.getSimpleName();

    @Override
    public ImageEngine createEngine() {
        return GlideEngine.createGlideEngine();
    }

    @Override
    public OnResultCallbackListener<LocalMedia> getResultCallbackListener() {
        return new OnResultCallbackListener<LocalMedia>() {
            @Override
            public void onResult(List<LocalMedia> result) {
                Log.i(TAG, "onResult:" + result.size());
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "PictureSelector onCancel");
            }
        };
    }
}
