package com.timenoteco.timenote.common

import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ItemTimenoteRecentAdapter
import java.util.*


class HashTagHelper private constructor(
    private val mHashTagWordColor: Int,
    private val listener: OnHashTagClickListener,
    private val additionalHashTagCharacters: List<Char>?,
    private val resources: Resources
) : ClickableForegroundColorSpan.OnHashTagClickListener {

    private val mAdditionalHashTagChars: MutableList<Char>
    private var mTextView: TextView? = null
    private val mOnHashTagClickListener: OnHashTagClickListener?

    object Creator {
        fun create(color: Int, listener: OnHashTagClickListener, resources: Resources): HashTagHelper {
            return HashTagHelper(color, listener, null, resources)
        }

        fun create(color: Int, listener: OnHashTagClickListener, additionalHashTagChars: List<Char>?,  resources: Resources): HashTagHelper {
            return HashTagHelper(color, listener, additionalHashTagChars, resources)
        }
    }

    interface OnHashTagClickListener {
        fun onHashTagClicked(hashTag: String?)
    }

    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            text: CharSequence,
            start: Int,
            before: Int,
            count: Int
        ) {
            if (text.isNotEmpty()) eraseAndColorizeAllText(text)
        }

        override fun afterTextChanged(s: Editable) {}
    }

    fun handle(textView: TextView?) {
        if (mTextView == null) {
            mTextView = textView
            mTextView!!.addTextChangedListener(mTextWatcher)

            // in order to use spannable we have to set buffer type
            mTextView!!.setText(mTextView!!.text, TextView.BufferType.SPANNABLE)
            if (mOnHashTagClickListener != null) {
                // we need to set this in order to get onClick event
                mTextView!!.movementMethod = LinkMovementMethod.getInstance()

                // after onClick clicked text become highlighted
                mTextView!!.highlightColor = Color.TRANSPARENT
            } else {
                // hash tags are not clickable, no need to change these parameters
            }
            setColorsToAllHashTags(mTextView!!.text)
        } else {
            throw RuntimeException("TextView is not null. You need to create a unique HashTagHelper for every TextView")
        }
    }

    private fun eraseAndColorizeAllText(text: CharSequence) {
        val spannable = mTextView!!.text as Spannable
        val spans = spannable.getSpans(
            0, text.length,
            CharacterStyle::class.java
        )
        for (span in spans) {
            //spannable.removeSpan(span)
        }
        setColorsToAllHashTags(text)
    }

    private fun setColorsToAllHashTags(text: CharSequence) {
        var startIndexOfNextHashSign: Int
        var index = 0
        while (index < text.length - 1) {
            val sign = text[index]
            var nextNotLetterDigitCharIndex =
                index + 1 // we assume it is next. if if was not changed by findNextValidHashTagChar then index will be incremented by 1
            if (sign == '#') {
                startIndexOfNextHashSign = index
                nextNotLetterDigitCharIndex =
                    findNextValidHashTagChar(text, startIndexOfNextHashSign)
                setColorForHashTagToTheEnd(startIndexOfNextHashSign, nextNotLetterDigitCharIndex)
            }
            index = nextNotLetterDigitCharIndex
        }
    }

    private fun findNextValidHashTagChar(text: CharSequence, start: Int): Int {
        var nonLetterDigitCharIndex = -1 // skip first sign '#"
        for (index in start + 1 until text.length) {
            val sign = text[index]
            val isValidSign =
                Character.isLetterOrDigit(sign) || mAdditionalHashTagChars.contains(sign)
            if (!isValidSign) {
                nonLetterDigitCharIndex = index
                break
            }
        }
        if (nonLetterDigitCharIndex == -1) {
            // we didn't find non-letter. We are at the end of text
            nonLetterDigitCharIndex = text.length
        }
        return nonLetterDigitCharIndex
    }

    private fun setColorForHashTagToTheEnd(
        startIndex: Int,
        nextNotLetterDigitCharIndex: Int
    ) {
        val s = mTextView!!.text as Spannable
        val span: CharacterStyle
        if (mOnHashTagClickListener != null) {
            span = ClickableForegroundColorSpan(resources.getColor(R.color.colorText), this)
        } else {
            // no need for clickable span because it is messing with selection when click
            span = ForegroundColorSpan(resources.getColor(R.color.colorText))
        }
        s.setSpan(span, startIndex, nextNotLetterDigitCharIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }


    private fun setFontForHashTagToTheEnd(
        startIndex: Int,
        nextNotLetterDigitCharIndex: Int
    ) {
        val m = Typeface.create("sans-serif", Typeface.BOLD_ITALIC)
        val bold = ItemTimenoteRecentAdapter.CustomTypefaceSpan(m)
        val span = SpannableStringBuilder(mTextView?.text)
        span.setSpan(bold, startIndex, nextNotLetterDigitCharIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    fun getAllHashTags(withHashes: Boolean): List<String> {
        val text = mTextView!!.text.toString()
        val spannable = mTextView!!.text as Spannable

        // use set to exclude duplicates
        val hashTags: MutableSet<String> =
            LinkedHashSet()
        for (span in spannable.getSpans(
            0, text.length,
            CharacterStyle::class.java
        )) {
            hashTags.add(
                text.substring(
                    if (!withHashes) spannable.getSpanStart(span) + 1 /*skip "#" sign*/ else spannable.getSpanStart(
                        span
                    ),
                    spannable.getSpanEnd(span)
                )
            )
        }
        return ArrayList(hashTags)
    }

    val allHashTags: List<String>
        get() = getAllHashTags(false)

    override fun onHashTagClicked(hashTag: String?) {
        mOnHashTagClickListener!!.onHashTagClicked(hashTag)
    }

    init {
        mOnHashTagClickListener = listener
        mAdditionalHashTagChars = ArrayList()
        if (additionalHashTagCharacters != null) {
            for (additionalChar in additionalHashTagCharacters) {
                mAdditionalHashTagChars.add(additionalChar)
            }
        }
    }
}