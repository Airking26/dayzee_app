package com.dayzeeco.dayzee.common

import android.content.Context
import android.graphics.Point
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.androidView.matisse.MimeType
import com.dayzeeco.dayzee.androidView.matisse.filter.Filter
import com.dayzeeco.dayzee.androidView.matisse.internal.entity.IncapableCause
import com.dayzeeco.dayzee.androidView.matisse.internal.entity.Item
import com.dayzeeco.dayzee.androidView.matisse.internal.utils.PhotoMetadataUtils


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
                ""
            )
        } else null
    }
}