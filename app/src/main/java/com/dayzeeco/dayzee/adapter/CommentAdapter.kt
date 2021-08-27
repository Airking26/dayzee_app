package com.dayzeeco.dayzee.adapter

import android.graphics.Typeface
import android.media.Image
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.MentionHelper
import com.dayzeeco.dayzee.common.MentionHelperComment
import com.dayzeeco.dayzee.model.CommentInfoDTO
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.android.synthetic.main.item_comment_pic.view.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.NoSuchElementException

class CommentAdapter(
    private val comments: List<CommentInfoDTO>,
    private val commentPicUserListener: CommentPicUserListener,
    private val commentMoreListener: CommentMoreListener,
    private val userTaggedListener: UserTaggedListener
) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    interface CommentPicUserListener{
        fun onPicUserCommentClicked(userInfoDTO: UserInfoDTO)
    }

    interface CommentMoreListener{
        fun onCommentMoreClicked(createdBy: String?, commentId: String?)
    }

    interface UserTaggedListener{
        fun onUserTaggedClicked(userInfoDTO: UserInfoDTO?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder =
        CommentViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_comment_pic,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bindComment(comments[position], commentPicUserListener, commentMoreListener, userTaggedListener)
    }

    class CommentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindComment(
            commentModel: CommentInfoDTO,
            commentPicUserListener: CommentPicUserListener,
            commentMoreListener: CommentMoreListener,
            userTaggedListener: UserTaggedListener
        ) {
            Glide
                .with(itemView)
                .load(commentModel.createdBy.picture)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.circle_pic)
                .into(itemView.comment_user_pic_imageview)

            if(commentModel.picture.isNullOrBlank()) itemView.comment_pic?.visibility = View.GONE
            else Glide
                .with(itemView)
                .load(commentModel.picture)
                .into(itemView.comment_pic)


            itemView.comment_pic.setOnClickListener {
                StfalconImageViewer.Builder(itemView.context, listOf(commentModel)){ view, image ->
                    Glide
                        .with(itemView)
                        .load(image.picture)
                        .into(view)
                }.withBackgroundColor(itemView.resources.getColor(R.color.places_text_black_alpha_87))
                    .show()
            }


            val nameAndComment =
                commentModel.createdBy.userName + " " + commentModel.description

            val i = SpannableStringBuilder(nameAndComment)


                        val m = Typeface.create("sans-serif", Typeface.NORMAL)
                        val p = Typeface.create("sans-serif-light", Typeface.NORMAL)
                        val o = ItemTimenoteRecentAdapter.CustomTypefaceSpan(p)
                        val k = ItemTimenoteRecentAdapter.CustomTypefaceSpan(m)

                        val sizeName = commentModel.createdBy.userName?.length

                        i.setSpan(k, 0, sizeName!!, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                        i.setSpan(
                            o,
                            sizeName,
                            nameAndComment.length,
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                        itemView.comment_username_comment.text = i

            val mentionHelper = MentionHelperComment.Creator.create(
                R.color.colorAccent,
                object : MentionHelperComment.OnMentionClickListener {
                    override fun onMentionClicked(mention: String?) {
                        val user: UserInfoDTO? = try {
                            commentModel.tagged?.single { userInfoDTO -> userInfoDTO.userName?.replace("\\s".toRegex(), "")?.replace("[^A-Za-z0-9 ]".toRegex(), "") == mention }
                        } catch (ex: NoSuchElementException){
                            null
                        }
                        userTaggedListener.onUserTaggedClicked(user)
                    } },
                null,
                itemView.resources,
                commentModel.tagged
            )
            mentionHelper.handle(itemView.comment_username_comment)

            itemView.comment_username_comment.text = i


            itemView.comment_time.text = calculateTimeSinceComment(commentModel.createdAt)

            itemView.comment_more.setOnClickListener{commentMoreListener.onCommentMoreClicked(
                commentModel.createdBy.id,
                commentModel.id
            )}
            itemView.comment_user_pic_imageview.setOnClickListener { commentPicUserListener.onPicUserCommentClicked(
                commentModel.createdBy
            ) }
        }

        private fun calculateTimeSinceComment(createdAt: String): String{
            val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

            val d = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Date.from(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(createdAt))).time
            } else {
                val sdf = SimpleDateFormat(ISO, Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("GMT")
                sdf.parse(createdAt).time
            }
            val c: Calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            c.timeInMillis = System.currentTimeMillis() + 10000 - d
            val mYear: Int = c.get(Calendar.YEAR) - 1970
            val mMonth: Int = c.get(Calendar.MONTH)
            val mDay: Int = c.get(Calendar.DAY_OF_MONTH) - 1
            val mHours: Int = c.get(Calendar.HOUR)
            val mMin : Int = c.get(Calendar.MINUTE)

            val timeSince: String
            if(mYear <= 0){
                if(mMonth <= 0){
                    if(mDay <=0){
                        timeSince = if(mHours <= 0){
                            if(mMin <= 0){
                                itemView.context.getString(R.string.few_sec_ago)
                            } else {
                                if(mMin > 1) itemView.context.resources.getQuantityString(
                                    R.plurals.time_minutes_comment,
                                    mMin,
                                    mMin
                                )
                                else itemView.context.resources.getQuantityString(
                                    R.plurals.time_minutes_comment,
                                    mMin,
                                    mMin
                                )
                            }
                        } else {
                            if(mHours > 1) itemView.context.resources.getQuantityString(
                                R.plurals.time_hours_comment,
                                mHours,
                                mHours
                            )
                            else itemView.context.resources.getQuantityString(
                                R.plurals.time_hours_comment,
                                mHours,
                                mHours
                            )
                        }
                    } else {
                        timeSince = if(mDay > 1) itemView.context.resources.getQuantityString(
                            R.plurals.time_days_comment,
                            mDay,
                            mDay
                        )
                        else itemView.context.resources.getQuantityString(
                            R.plurals.time_days_comment,
                            mDay,
                            mDay
                        )
                    }
                } else {
                    timeSince = if(mMonth > 1) itemView.context.resources.getQuantityString(
                        R.plurals.time_months_comment,
                        mMonth,
                        mMonth
                    )
                    else itemView.context.resources.getQuantityString(
                        R.plurals.time_months_comment,
                        mMonth,
                        mMonth
                    )
                }
            } else {
                timeSince = if(mYear > 1) itemView.context.resources.getQuantityString(
                    R.plurals.time_years_comment,
                    mYear,
                    mYear
                )
                else  itemView.context.resources.getQuantityString(
                    R.plurals.time_years_comment,
                    mYear,
                    mYear
                )
            }

            return timeSince
        }

    }
}