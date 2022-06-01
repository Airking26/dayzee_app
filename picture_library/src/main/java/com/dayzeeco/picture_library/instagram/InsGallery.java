package com.dayzeeco.picture_library.instagram;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;

import com.dayzeeco.picture_library.PictureSelectionModel;
import com.dayzeeco.picture_library.PictureSelector;
import com.dayzeeco.picture_library.R;
import com.dayzeeco.picture_library.config.PictureConfig;
import com.dayzeeco.picture_library.config.PictureMimeType;
import com.dayzeeco.picture_library.engine.CacheResourcesEngine;
import com.dayzeeco.picture_library.engine.ImageEngine;
import com.dayzeeco.picture_library.entity.LocalMedia;
import com.dayzeeco.picture_library.listener.OnResultCallbackListener;
import com.dayzeeco.picture_library.style.PictureCropParameterStyle;
import com.dayzeeco.picture_library.style.PictureParameterStyle;
import com.dayzeeco.picture_library.style.PictureWindowAnimationStyle;

import java.util.List;

import androidx.core.content.ContextCompat;

public final class InsGallery {
    public static final int THEME_STYLE_DEFAULT = 0;
    public static final int THEME_STYLE_DARK = 1;
    public static final int THEME_STYLE_DARK_BLUE = 2;
    public static int currentTheme = THEME_STYLE_DEFAULT;
    public static int maxPhoto = 4;

    private InsGallery() {
        throw new IllegalStateException("you can't instantiate me!");
    }

    public static void openGallery(Activity activity, ImageEngine engine, OnResultCallbackListener listener, int maxPhotoInt) {
        maxPhoto = maxPhotoInt;
        openGallery(activity, engine, null, null, listener);
    }

    public static void openGallery(Activity activity, ImageEngine engine, CacheResourcesEngine cacheResourcesEngine, OnResultCallbackListener listener) {
        openGallery(activity, engine, cacheResourcesEngine, null, listener);
    }

    public static void openGallery(Activity activity, ImageEngine engine, CacheResourcesEngine cacheResourcesEngine, List<LocalMedia> selectionMedia, OnResultCallbackListener listener) {
        applyInstagramOptions(activity.getApplicationContext(), PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofAll()))
                .imageEngine(engine)
                .loadCacheResourcesCallback(cacheResourcesEngine)
                .selectionData(selectionMedia)
                .forResult(listener);
    }

    public static void openGallery(Activity activity, ImageEngine engine, CacheResourcesEngine cacheResourcesEngine, List<LocalMedia> selectionMedia, InstagramSelectionConfig instagramConfig, OnResultCallbackListener listener) {
        applyInstagramOptions(activity.getApplicationContext(), instagramConfig, PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofAll()))// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(engine)// 外部传入图片加载引擎，必传项
                .loadCacheResourcesCallback(cacheResourcesEngine)// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                .selectionData(selectionMedia)// 是否传入已选图片
                .forResult(listener);
    }

    public static void openGallery(Activity activity, ImageEngine engine, CacheResourcesEngine cacheResourcesEngine, List<LocalMedia> selectionMedia) {
        applyInstagramOptions(activity.getApplicationContext(), PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofAll()))// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(engine)// 外部传入图片加载引擎，必传项
                .loadCacheResourcesCallback(cacheResourcesEngine)// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                .selectionData(selectionMedia)// 是否传入已选图片
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    public static void openGallery(Activity activity, ImageEngine engine, CacheResourcesEngine cacheResourcesEngine, List<LocalMedia> selectionMedia, InstagramSelectionConfig instagramConfig) {
        applyInstagramOptions(activity.getApplicationContext(), instagramConfig, PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofAll()))// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(engine)// 外部传入图片加载引擎，必传项
                .loadCacheResourcesCallback(cacheResourcesEngine)// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                .selectionData(selectionMedia)// 是否传入已选图片
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    public static void openGallery(Activity activity, ImageEngine engine, CacheResourcesEngine cacheResourcesEngine, List<LocalMedia> selectionMedia, int requestCode) {
        applyInstagramOptions(activity.getApplicationContext(), PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofAll()))// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(engine)// 外部传入图片加载引擎，必传项
                .loadCacheResourcesCallback(cacheResourcesEngine)// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                .selectionData(selectionMedia)// 是否传入已选图片
                .forResult(requestCode);//结果回调onActivityResult code
    }


    public static PictureSelectionModel applyInstagramOptions(Context context, PictureSelectionModel selectionModel) {
        return applyInstagramOptions(context, InstagramSelectionConfig.createConfig().setCurrentTheme(currentTheme), selectionModel);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public static PictureSelectionModel applyInstagramOptions(Context context, InstagramSelectionConfig instagramConfig, PictureSelectionModel selectionModel) {
        return selectionModel
                .setInstagramConfig(instagramConfig)
                .setPictureStyle(createInstagramStyle(context))
                .setPictureCropStyle(createInstagramCropStyle(context))
                .setPictureWindowAnimationStyle(new PictureWindowAnimationStyle())
                .isWithVideoImage(false)
                .maxSelectNum(maxPhoto)
                .minSelectNum(1)
                .maxVideoSelectNum(1)
                //.minVideoSelectNum(1)
                .imageSpanCount(4)
                .isReturnEmpty(false)
                //.isAndroidQTransform(false)
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .selectionMode(PictureConfig.MULTIPLE)
                .isSingleDirectReturn(false)
                .isPreviewImage(true)
                .isPreviewVideo(true)
                //.querySpecifiedFormatSuffix(PictureMimeType.ofJPEG())
                .enablePreviewAudio(false)
                .isCamera(false)
                //.isMultipleSkipCrop(false)
                //.isMultipleRecyclerAnimation(false)
                .isZoomAnim(true)
                //.imageFormat(PictureMimeType.PNG)x
                .isEnableCrop(true)
                //.basicUCropConfig()
                .isCompress(false)
                //.compressQuality(50)
                .synOrAsy(true)
                //.queryMaxFileSize(10)
                //.compressSavePath("/storage/emulated/0/Movies/ins.mp4")
                .withAspectRatio(1, 1)
                .showCropFrame(true)
                .showCropGrid(true)
                .isOpenClickSound(false)
                //.isDragFrame(false)
                //.cropImageWideHigh(300, 400)
                .videoMaxSecond(31)
                .videoMinSecond(3)
                .recordVideoSecond(60)
                .recordVideoMinSecond(3)
                //.cutOutQuality(80)
                //.minimumCompressSize(80)
                //.rotateEnabled(false)
                .scaleEnabled(true)
//                .videoQuality(1)
                ;
    }

    public static PictureParameterStyle createInstagramStyle(Context context) {
        PictureParameterStyle mPictureParameterStyle = new PictureParameterStyle();
        if (currentTheme == THEME_STYLE_DARK || currentTheme == THEME_STYLE_DARK_BLUE) {
            mPictureParameterStyle.isChangeStatusBarFontColor = false;
        } else {
            mPictureParameterStyle.isChangeStatusBarFontColor = true;
        }
        mPictureParameterStyle.isOpenCompletedNumStyle = false;
        mPictureParameterStyle.isOpenCheckNumStyle = true;
        if (currentTheme == THEME_STYLE_DARK) {
            mPictureParameterStyle.pictureStatusBarColor = Color.parseColor("#1C1C1E");
        } else if (currentTheme == THEME_STYLE_DARK_BLUE) {
            mPictureParameterStyle.pictureStatusBarColor = Color.parseColor("#213040");
        } else {
            mPictureParameterStyle.pictureStatusBarColor = Color.parseColor("#FFFFFF");
        }
        if (currentTheme == THEME_STYLE_DARK) {
            mPictureParameterStyle.pictureTitleBarBackgroundColor = Color.parseColor("#1C1C1E");
        } else if (currentTheme == THEME_STYLE_DARK_BLUE) {
            mPictureParameterStyle.pictureTitleBarBackgroundColor = Color.parseColor("#213040");
        } else {
            mPictureParameterStyle.pictureTitleBarBackgroundColor = Color.parseColor("#FFFFFF");
        }
        mPictureParameterStyle.pictureTitleUpResId = R.drawable.picture_arrow_up;
        mPictureParameterStyle.pictureTitleDownResId = R.drawable.picture_arrow_down;
        mPictureParameterStyle.pictureFolderCheckedDotStyle = R.drawable.picture_orange_oval;
        mPictureParameterStyle.pictureLeftBackIcon = R.drawable.picture_close;
        if (currentTheme == THEME_STYLE_DARK) {
            mPictureParameterStyle.pictureTitleTextColor = ContextCompat.getColor(context, R.color.picture_color_white);
        } else if (currentTheme == THEME_STYLE_DARK_BLUE) {
            mPictureParameterStyle.pictureTitleTextColor = ContextCompat.getColor(context, R.color.picture_color_white);
        } else {
            mPictureParameterStyle.pictureTitleTextColor = ContextCompat.getColor(context, R.color.picture_color_black);
        }
        if (currentTheme == THEME_STYLE_DARK) {
            mPictureParameterStyle.pictureRightDefaultTextColor = ContextCompat.getColor(context, R.color.picture_color_1766FF);
        } else if (currentTheme == THEME_STYLE_DARK_BLUE) {
            mPictureParameterStyle.pictureRightDefaultTextColor = Color.parseColor("#2FA6FF");
        } else {
            mPictureParameterStyle.pictureRightDefaultTextColor = ContextCompat.getColor(context, R.color.picture_color_1766FF);
        }
        if (currentTheme == THEME_STYLE_DARK) {
            mPictureParameterStyle.pictureContainerBackgroundColor = ContextCompat.getColor(context, R.color.picture_color_black);
        } else if (currentTheme == THEME_STYLE_DARK_BLUE) {
            mPictureParameterStyle.pictureContainerBackgroundColor = Color.parseColor("#18222D");
        } else {
            mPictureParameterStyle.pictureContainerBackgroundColor = ContextCompat.getColor(context, R.color.picture_color_white);
        }
        mPictureParameterStyle.pictureCheckedStyle = R.drawable.picture_instagram_num_selector;
        mPictureParameterStyle.pictureBottomBgColor = ContextCompat.getColor(context, R.color.picture_color_fa);
        mPictureParameterStyle.pictureCheckNumBgStyle = R.drawable.picture_num_oval;
        mPictureParameterStyle.picturePreviewTextColor = ContextCompat.getColor(context, R.color.picture_color_fa632d);
        mPictureParameterStyle.pictureUnPreviewTextColor = ContextCompat.getColor(context, R.color.picture_color_9b);
        mPictureParameterStyle.pictureCompleteTextColor = ContextCompat.getColor(context, R.color.picture_color_fa632d);
        mPictureParameterStyle.pictureUnCompleteTextColor = ContextCompat.getColor(context, R.color.picture_color_9b);
        mPictureParameterStyle.pictureExternalPreviewDeleteStyle = R.drawable.picture_icon_black_delete;
        mPictureParameterStyle.pictureExternalPreviewGonePreviewDelete = true;
        mPictureParameterStyle.pictureRightDefaultText = context.getString(R.string.next);
        return mPictureParameterStyle;
    }

    public static PictureCropParameterStyle createInstagramCropStyle(Context context) {
        if (currentTheme == THEME_STYLE_DARK) {
            return new PictureCropParameterStyle(
                    Color.parseColor("#1C1C1E"),
                    Color.parseColor("#1C1C1E"),
                    Color.parseColor("#1C1C1E"),
                    ContextCompat.getColor(context, R.color.picture_color_white),
                    false);
        } else if (currentTheme == THEME_STYLE_DARK_BLUE) {
            return new PictureCropParameterStyle(
                    Color.parseColor("#213040"),
                    Color.parseColor("#213040"),
                    Color.parseColor("#213040"),
                    ContextCompat.getColor(context, R.color.picture_color_white),
                    false);
        }
        return new PictureCropParameterStyle(
                ContextCompat.getColor(context, R.color.picture_color_white),
                ContextCompat.getColor(context, R.color.picture_color_white),
                ContextCompat.getColor(context, R.color.picture_color_white),
                ContextCompat.getColor(context, R.color.picture_color_black),
                true);
    }

    public static void setCurrentTheme(int currentTheme) {
        InsGallery.currentTheme = currentTheme;
    }
}
