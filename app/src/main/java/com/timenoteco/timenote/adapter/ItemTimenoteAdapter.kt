package com.timenoteco.timenote.adapter

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import android.text.style.TypefaceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.Timenote
import kotlinx.android.synthetic.main.adapter_timenote_recent.view.*
import kotlinx.android.synthetic.main.item_timenote.view.*
import kotlinx.android.synthetic.main.item_timenote_recent.view.*

class ItemTimenoteAdapter(
    private val timenotes: List<Timenote>, private val timenotesToCome: List<Timenote>,
    private val isHeterogeneous: Boolean, private val commentListener: CommentListener,
    private val plusListener: PlusListener, private val pictureProfileListener: PictureProfileListener,
    private val timenoteRecentClicked: TimenoteRecentClicked?)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    interface CommentListener{
        fun onCommentClicked()
    }

    interface PlusListener{
        fun onPlusClicked()
    }

    interface PictureProfileListener{
        fun onPictureClicked()
    }


    interface TimenoteRecentClicked{
        fun onTimenoteRecentClicked()
    }

    private var itemViewType: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(isHeterogeneous) {
            when (viewType) {
                0 -> TimenoteToComeViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.adapter_timenote_recent, parent, false)
                )
                else -> TimenoteViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_timenote, parent, false)
                )
            }
        } else {
            TimenoteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_timenote, parent, false))
        }

    }

    override fun getItemCount(): Int{
        return if(isHeterogeneous) {
            when (itemViewType) {
                0 -> timenotesToCome.size
                else -> timenotes.size
            }
        } else {
            timenotes.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(isHeterogeneous){
            when(itemViewType){
                0 -> (holder as TimenoteToComeViewHolder).bindTimenoteTocome(timenotesToCome, timenoteRecentClicked)
                else -> (holder as TimenoteViewHolder).bindTimenote(timenotes[position], commentListener, plusListener, pictureProfileListener)
            }
        } else {
            (holder as TimenoteViewHolder).bindTimenote(
                timenotes[position],
                commentListener,
                plusListener,
                pictureProfileListener
            )
        }

    }


    override fun getItemViewType(position: Int): Int {
        return if(isHeterogeneous) {
            itemViewType = position % 4 * 5
            itemViewType
        } else {
            super.getItemViewType(position)
        }
    }

    class TimenoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {


        fun bindTimenote(
            timenote: Timenote,
            commentListener: CommentListener,
            plusListener: PlusListener,
            pictureProfileListener: PictureProfileListener
        ) {

            Glide
                .with(itemView)
                .load(timenote.pic_user)
                .apply(RequestOptions.circleCropTransform())
                .into(itemView.timenote_pic_user_imageview)

            Glide
                .with(itemView)
                .load(timenote.pic)
                .centerCrop()
                .into(itemView.timenote_pic_imageview)

            itemView.timenote_username.text = timenote.username
            itemView.timenote_place.text = timenote.place
            val p = Typeface.create("sans-serif-light", Typeface.NORMAL)
            val m = Typeface.create("sans-serif", Typeface.NORMAL)
            val o = ItemTimenoteToComeAdapter.CustomTypefaceSpan(p)
            val k = ItemTimenoteToComeAdapter.CustomTypefaceSpan(m)
            val h = SpannableStringBuilder(timenote.desc)
            h.setSpan(k, 0, 17, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            h.setSpan(o, 18, timenote.desc?.length!!, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            itemView.timenote_username_desc.text = h
            itemView.timenote_title.text = timenote.title
            itemView.timenote_year.text = timenote.year
            itemView.timenote_day_month.text = timenote.month
            itemView.timenote_time.text = timenote.date
            itemView.timenote_day_month.setOnClickListener {
                itemView.separator_1.visibility = View.GONE
                itemView.separator_2.visibility = View.GONE
                itemView.timenote_day_month.visibility = View.GONE
                itemView.timenote_time.visibility = View.GONE
                itemView.timenote_year.visibility = View.GONE
                itemView.timerProgramCountdown.visibility = View.VISIBLE
                itemView.timerProgramCountdown.startCountDown(99999999)
            }
            itemView.timenote_pic_user_imageview.setOnClickListener { pictureProfileListener.onPictureClicked() }
            itemView.timenote_comment.setOnClickListener { commentListener.onCommentClicked() }
            itemView.timenote_plus.setOnClickListener { plusListener.onPlusClicked() }

        }

    }

    class TimenoteToComeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bindTimenoteTocome(timenote: List<Timenote>, timenoteRecentClicked: TimenoteRecentClicked?){
            itemView.home_recent_rv.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            itemView.home_recent_rv.adapter = ItemTimenoteToComeAdapter(timenote, timenoteRecentClicked)
        }
    }

}

class ItemTimenoteToComeAdapter(private val timenotesToCome: List<Timenote>, private val timenoteClicked: ItemTimenoteAdapter.TimenoteRecentClicked?): RecyclerView.Adapter<ItemTimenoteToComeAdapter.ItemAdapter>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter {
        return ItemAdapter(LayoutInflater.from(parent.context).inflate(R.layout.item_timenote_recent, parent, false))
    }

    override fun getItemCount(): Int {
        return timenotesToCome.size
    }

    override fun onBindViewHolder(holder: ItemAdapter, position: Int) {
        holder.bindItem(timenotesToCome[position], timenoteClicked)
    }


    class ItemAdapter(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindItem(timenote: Timenote, timenoteClicked: ItemTimenoteAdapter.TimenoteRecentClicked?){
            Glide
                .with(itemView)
                .load(timenote.pic)
                .centerCrop()
                .into(itemView.timenote_recent_pic_imageview)

            Glide
                .with(itemView)
                .load(timenote.pic_user)
                .apply(RequestOptions.circleCropTransform())
                .into(itemView.timenote_recent_pic_user_imageview)

            itemView.timenote_recent_username.text = timenote.username
            itemView.timenote_recent_title.text = timenote.title
            itemView.timenote_recent_date.text = timenote.dateIn
            itemView.setOnClickListener { timenoteClicked?.onTimenoteRecentClicked() }
        }
    }

    class CustomTypefaceSpan(private val typeface: Typeface?) : MetricAffectingSpan() {
        override fun updateDrawState(paint: TextPaint) {
            paint.typeface = typeface
        }

        override fun updateMeasureState(paint: TextPaint) {
            paint.typeface = typeface
        }
    }

}