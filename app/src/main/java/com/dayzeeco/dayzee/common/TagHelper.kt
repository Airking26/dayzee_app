package com.dayzeeco.dayzee.common

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
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.ItemTimenoteRecentAdapter
import com.dayzeeco.dayzee.model.UserInfoDTO
import java.util.*


class TagHelper private constructor(
    private val mMentionWordColor: Int,
    private val listener: OnMentionClickListener,
    private val additionalMentionCharacters: List<Char>?,
    private val resources: Resources
) : ClickableForegroundColorSpanMention.OnMentionClickListener{

    private val mAdditionalMentionChars: MutableList<Char>
    private var mTextView: TextView? = null
    private val mOnMentionClickListener: OnMentionClickListener?

    object Creator {
        fun create(color: Int, listener: OnMentionClickListener, resources: Resources): TagHelper {
            return TagHelper(color, listener, null, resources)
        }

        fun create(color: Int, listener: OnMentionClickListener, additionalMentionChars: List<Char>?,  resources: Resources): TagHelper {
            return TagHelper(color, listener, additionalMentionChars, resources)
        }
    }

    interface OnMentionClickListener {
        fun onMentionClicked(mention: String?)
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
            if (mOnMentionClickListener != null) {
                // we need to set this in order to get onClick event
                mTextView!!.movementMethod = LinkMovementMethod.getInstance()

                // after onClick clicked text become highlighted
                mTextView!!.highlightColor = Color.TRANSPARENT
            } else {
                // hash tags are not clickable, no need to change these parameters
            }
            setColorsToAllMentions(mTextView!!.text)
        } else {
            throw RuntimeException("TextView is not null. You need to create a unique Mention Helper for every TextView")
        }
    }

    private fun eraseAndColorizeAllText(text: CharSequence) {
        val spannable = mTextView!!.text as Spannable
        val spans = spannable.getSpans(
            0, text.length,
            CharacterStyle::class.java
        )
        for (span in spans) {
            spannable.removeSpan(span)
        }
        setColorsToAllMentions(text)
    }

    private fun setColorsToAllMentions(text: CharSequence) {
        var startIndexOfNextHashSign: Int
        var index = 0
        while (index < text.length - 1) {
            val sign = text[index]
            val beforeSignNonValid = if(index > 0) text[index-1].isLetterOrDigit() && sign == '@' else false
            var nextNotLetterDigitCharIndex = index + 1 // we assume it is next. if if was not changed by findNextValidMention // Char then index will be incremented by 1
            if (sign == '@' && !beforeSignNonValid) {
                    startIndexOfNextHashSign = index
                    nextNotLetterDigitCharIndex = findNextValidMentionChar(text, startIndexOfNextHashSign)
                    setColorForMentionToTheEnd(
                        startIndexOfNextHashSign,
                        nextNotLetterDigitCharIndex
                    )
            }
            index = nextNotLetterDigitCharIndex
        }
    }

    private fun findNextValidMentionChar(text: CharSequence, start: Int): Int {
        var nonLetterDigitCharIndex = -1 // skip first sign '#"
        for (index in start + 1 until text.length) {
            val sign = text[index]
            val isValidSign =
                Character.isLetterOrDigit(sign) || mAdditionalMentionChars.contains(sign)
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

    private fun setColorForMentionToTheEnd(startIndex: Int, nextNotLetterDigitCharIndex: Int) {
        val s = mTextView!!.text as Spannable
        val span: CharacterStyle
        if (mOnMentionClickListener != null) {
            span = ClickableForegroundColorSpanMention(resources.getColor(R.color.colorText), this)
        } else {
            // no need for clickable span because it is messing with selection when click
            span = ForegroundColorSpan(resources.getColor(R.color.colorText))
        }
        s.setSpan(span, startIndex, nextNotLetterDigitCharIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun setFontForMentionToTheEnd(startIndex: Int, nextNotLetterDigitCharIndex: Int) {
        val m = Typeface.create("sans-serif", Typeface.BOLD_ITALIC)
        val bold = ItemTimenoteRecentAdapter.CustomTypefaceSpan(m)
        val span = SpannableStringBuilder(mTextView?.text)
        span.setSpan(bold, startIndex, nextNotLetterDigitCharIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    fun getAllMentions(withHashes: Boolean): List<String> {
        val text = mTextView!!.text.toString()
        val spannable = mTextView!!.text as Spannable

        // use set to exclude duplicates
        val Mentions: MutableSet<String> =
            LinkedHashSet()
        for (span in spannable.getSpans(
            0, text.length,
            CharacterStyle::class.java
        )) {
            Mentions.add(
                text.substring(
                    if (!withHashes) spannable.getSpanStart(span) + 1 /*skip "#" sign*/ else spannable.getSpanStart(
                        span
                    ),
                    spannable.getSpanEnd(span)
                )
            )
        }
        return ArrayList(Mentions)
    }

    val allMentions: List<String> get() = getAllMentions(false)

    override fun onMentionClicked(mention: String?) {
        mOnMentionClickListener!!.onMentionClicked(mention)
    }

    init {
        mOnMentionClickListener = listener
        mAdditionalMentionChars = ArrayList()
        if (additionalMentionCharacters != null) {
            for (additionalChar in additionalMentionCharacters) {
                mAdditionalMentionChars.add(additionalChar)
            }
        }
    }

}