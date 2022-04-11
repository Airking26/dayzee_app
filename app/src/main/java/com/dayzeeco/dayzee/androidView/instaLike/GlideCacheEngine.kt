package com.dayzeeco.dayzee.androidView.instaLike

import android.content.Context
import com.dayzeeco.picture_library.engine.CacheResourcesEngine
import java.io.File


class GlideCacheEngine private constructor() : CacheResourcesEngine {
    override fun onCachePath(context: Context?, url: String?): String {
        val cacheFile: File = if (GLIDE_VERSION >= 4) ImageCacheUtils.getCacheFileTo4x(context, url)!!
         else ImageCacheUtils.getCacheFileTo3x(context, url)!!
        return cacheFile.getAbsolutePath()
    }

    companion object {
        /**
         * glide版本号,请根据用户集成为准 这里只是模拟
         */
        private const val GLIDE_VERSION = 4
        private var instance: GlideCacheEngine? = null
        fun createCacheEngine(): GlideCacheEngine? {
            if (null == instance) {
                synchronized(GlideCacheEngine::class.java) {
                    if (null == instance) {
                        instance = GlideCacheEngine()
                    }
                }
            }
            return instance
        }
    }
}