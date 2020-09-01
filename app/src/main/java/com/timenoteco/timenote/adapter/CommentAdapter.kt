package com.timenoteco.timenote.adapter

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.common.RoundedCornersTransformation
import com.timenoteco.timenote.model.CommentModel
import com.timenoteco.timenote.view.searchFlow.DetailedTimenoteSearch
import kotlinx.android.synthetic.main.item_comment.view.*

class CommentAdapter(val comments: List<CommentModel>, val commentPicUserListener: CommentPicUserListener) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    interface CommentPicUserListener{
        fun onPicUserCommentClicked()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder =
        CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false))

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bindComment(comments[position], commentPicUserListener)
    }

    class CommentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindComment(
            commentModel: CommentModel,
            commentPicUserListener: CommentPicUserListener
        ) {
            Glide
                .with(itemView)
                .load("https://wl-sympa.cf.tsp.li/resize/728x/jpg/f6e/ef6/b5b68253409b796f61f6ecd1f1.jpg")
                .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(itemView.context, 90, 0, itemView.context.resources.getString(0 + R.color.colorBackground), 4)))
                .into(itemView.comment_user_pic_imageview)

            val m = Typeface.create("sans-serif", Typeface.NORMAL)
            val p = Typeface.create("sans-serif-light", Typeface.NORMAL)
            val o = ItemTimenoteToComeAdapter.CustomTypefaceSpan(p)
            val k = ItemTimenoteToComeAdapter.CustomTypefaceSpan(m)

            val sizeName = commentModel.name.length
            val nameAndComment = commentModel.name + " " + commentModel.comment

            val i = SpannableStringBuilder(nameAndComment)
            i.setSpan(k, 0, sizeName, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            i.setSpan(o, sizeName,  nameAndComment.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

            itemView.comment_username_comment.text = i
            itemView.comment_time.text = commentModel.time

            itemView.comment_user_pic_imageview.setOnClickListener { commentPicUserListener.onPicUserCommentClicked() }
        }

    }
}