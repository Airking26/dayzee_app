package com.timenoteco.timenote.common

import android.content.Context
import android.graphics.Point
import com.timenoteco.timenote.R
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.filter.Filter
import com.zhihu.matisse.internal.entity.IncapableCause
import com.zhihu.matisse.internal.entity.Item
import com.zhihu.matisse.internal.utils.PhotoMetadataUtils


internal class GifSizeFilter(
    private val mMinWidth: Int,
    private val mMinHeight: Int,
    private val mMaxSize: Int
) :
    Filter() {
    override fun constraintTypes(): HashSet<MimeType?> {
        return object : HashSet<MimeType?>() {
            init {
                add(MimeType.GIF)
            }
        }
    }

    override fun filter(context: Context, item: Item): IncapableCause? {
        if (!needFiltering(context, item)) return null
        val size: Point =
            PhotoMetadataUtils.getBitmapBound(context.getContentResolver(), item.getContentUri())
        return if (size.x < mMinWidth || size.y < mMinHeight || item.size > mMaxSize) {
            IncapableCause(
                IncapableCause.DIALOG,
                context.getString(
                    R.string.error_file_type,
                    mMinWidth,
                    PhotoMetadataUtils.getSizeInMB(mMaxSize.toLong()).toString()
                )
            )
        } else null
    }
}