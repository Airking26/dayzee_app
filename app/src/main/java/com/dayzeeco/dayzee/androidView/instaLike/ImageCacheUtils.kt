package com.dayzeeco.dayzee.androidView.instaLike

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import java.io.File


object ImageCacheUtils {
    /**
     * 根据url获取图片缓存
     * Glide 4.x请调用此方法
     * 注意：此方法必须在子线程中进行
     *
     * @param context
     * @param url
     * @return
     */
    fun getCacheFileTo4x(context: Context?, url: String?): File? {
        return try {
            Glide.with(context!!).downloadOnly().load(url).submit().get()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 根据url获取图片缓存
     * Glide 3.x请调用此方法
     * 注意：此方法必须在子线程中进行
     *
     * @param context
     * @param url
     * @return
     */
    fun getCacheFileTo3x(context: Context?, url: String?): File? {
        return try {
            Glide.with(context!!).load(url).downloadOnly(1, 1)
                .get()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}