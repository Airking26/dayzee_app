package com.dayzeeco.dayzee.common

import android.graphics.Typeface
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import com.dayzeeco.dayzee.model.UserInfoDTO


class ClickableForegroundColorSpanMention(@param:ColorInt private val mColor: Int, private val mOnMentionClickListener: OnMentionClickListener?
) :
    ClickableSpan() {

    interface OnMentionClickListener {
        fun onMentionClicked(mention: String?)
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.color = mColor
        ds.typeface = Typeface.DEFAULT
    }

    override fun onClick(widget: View) {
        val text = (widget as TextView).text
        val s = text as Spanned
        val start = s.getSpanStart(this)
        val end = s.getSpanEnd(this)
        mOnMentionClickListener!!.onMentionClicked(text.subSequence(start + 1 /*skip "#" sign*/, end).toString())
    }

    init {
        if (mOnMentionClickListener == null) {
            throw RuntimeException("constructor, click listener not specified. Are you sure you need to use this class?")
        }
    }
}