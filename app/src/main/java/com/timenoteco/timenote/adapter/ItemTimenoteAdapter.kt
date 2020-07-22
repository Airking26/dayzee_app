package com.timenoteco.timenote.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.common.RoundedCornersTransformation
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.Timenote
import com.timenoteco.timenote.model.StatusTimenote
import kotlinx.android.synthetic.main.adapter_timenote_recent.view.*
import kotlinx.android.synthetic.main.item_timenote.view.*
import kotlinx.android.synthetic.main.item_timenote_recent.view.*


class ItemTimenoteAdapter(
    private val timenotes: List<Timenote>,
    private val timenotesToCome: List<Timenote>,
    private val isHeterogeneous: Boolean,
    private val timenoteRecentClicked: TimenoteRecentClicked?,
    private val timenoteListenerListener: TimenoteOptionsListener,
    private val fragment: Fragment
)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    interface TimenoteRecentClicked{
        fun onTimenoteRecentClicked()
    }

    private var itemViewType: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(isHeterogeneous) {
            if(viewType == R.layout.adapter_timenote_recent){
                TimenoteToComeViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.adapter_timenote_recent, parent, false))
            } else {
                TimenoteViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_timenote, parent, false))
            }
        } else {
            TimenoteViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_timenote, parent, false))
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
            return if(position == 0) (holder as TimenoteToComeViewHolder).bindTimenoteTocome(timenotesToCome, timenoteRecentClicked)
            else (holder as TimenoteViewHolder).bindTimenote(timenotes[position],timenoteListenerListener, fragment)
        } else {
            (holder as TimenoteViewHolder).bindTimenote(
                timenotes[position],
                timenoteListenerListener,
                fragment
            )
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if(isHeterogeneous) {
            if(position == 0) R.layout.adapter_timenote_recent else R.layout.item_timenote
        } else {
            super.getItemViewType(position)
        }
    }

    class TimenoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        private lateinit var timenote: Timenote

        fun bindTimenote(timenote: Timenote, timenoteListenerListener: TimenoteOptionsListener, fragment: Fragment) {

            this.timenote = timenote

            Glide
                .with(itemView)
                .load(timenote.pic_user)
                .apply(RequestOptions.circleCropTransform())
                .into(itemView.timenote_pic_user_imageview)

            Glide
                .with(itemView)
                .load(timenote.pic_user)
                .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(itemView.context, 90, 0, "#FFFFFF", 4)))
                .into(itemView.timenote_pic_participant_one)
            Glide
                .with(itemView)
                .load("https://wl-sympa.cf.tsp.li/resize/728x/jpg/f6e/ef6/b5b68253409b796f61f6ecd1f1.jpg")
                .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(itemView.context, 90, 0, "#FFFFFF", 4)))
                .into(itemView.timenote_pic_participant_two)
            Glide
                .with(itemView)
                .load("https://www.fc-photos.com/wp-content/uploads/2016/09/fc-photos-Weynacht-0001.jpg")
                .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(itemView.context, 90, 0, "#FFFFFF", 4)))
                .into(itemView.timenote_pic_participant_three)


            val screenSlideCreationTimenotePagerAdapter =  ScreenSlideTimenotePagerAdapter(fragment, timenote.pic, true){
                if(timenote.status ==StatusTimenote.PAID || (timenote.status ==StatusTimenote.FREE && !timenote.url.isNullOrBlank())) {
                    itemView.timenote_buy.visibility = View.VISIBLE
                    if(timenote.status == StatusTimenote.PAID){
                        itemView.timenote_buy.text = "Buy " + timenote.price.toString() + "$"
                    } else {
                        itemView.timenote_buy.text = timenote.url
                    }
                }
            }
            itemView.timenote_vp.adapter = screenSlideCreationTimenotePagerAdapter
            itemView.timenote_indicator.setViewPager(itemView.timenote_vp)
            if(timenote.pic?.size == 1) itemView.timenote_indicator.visibility = View.GONE
            screenSlideCreationTimenotePagerAdapter.registerAdapterDataObserver(itemView.timenote_indicator.adapterDataObserver)
            itemView.timenote_username.text = timenote.username
            itemView.timenote_place.text = timenote.place
            val test = "Ronny Dahan et des milliers d'autres personnes ont enregistré ce Dayzee"

            val p = Typeface.create("sans-serif-light", Typeface.NORMAL)
            val m = Typeface.create("sans-serif", Typeface.NORMAL)
            val o = ItemTimenoteToComeAdapter.CustomTypefaceSpan(p)
            val k = ItemTimenoteToComeAdapter.CustomTypefaceSpan(m)

            val t = SpannableStringBuilder(test)
            t.setSpan(k, 0, 45, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            t.setSpan(o, 46, test.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

            itemView.timenote_added_by.text = t

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
            }

            itemView.timenote_options.setOnClickListener {
                createOptionsOnTimenote(itemView.context, false, timenoteListenerListener)
            }
            itemView.timenote_pic_user_imageview.setOnClickListener { timenoteListenerListener.onPictureClicked() }
            itemView.timenote_comment.setOnClickListener { timenoteListenerListener.onCommentClicked() }
            itemView.timenote_plus.setOnClickListener { timenoteListenerListener.onPlusClicked() }
            itemView.timenote_see_more.setOnClickListener { timenoteListenerListener.onSeeMoreClicked() }
            itemView.timenote_place.setOnClickListener{timenoteListenerListener.onAddressClicked()}
        }

        private fun createOptionsOnTimenote(context: Context, isMine: Boolean, timenoteListenerListener: TimenoteOptionsListener){
            val listItems: MutableList<String>
            if(isMine) listItems = mutableListOf(context.getString(R.string.duplicate), context.getString(
                            R.string.edit), context.getString(R.string.delete), context.getString(R.string.alarm))
            else listItems = mutableListOf(context.getString(R.string.duplicate), context.getString(R.string.delete), context.getString(R.string.alarm), context.getString(R.string.report))
            MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.posted_false)
                listItems (items = listItems){ dialog, index, text ->
                    when(text.toString()){
                        context.getString(R.string.duplicate) -> timenoteListenerListener.onDuplicateClicked()
                        context.getString(R.string.edit) -> timenoteListenerListener.onEditClicked()
                        context.getString(R.string.report) -> timenoteListenerListener.onReportClicked()
                        context.getString(R.string.alarm) -> timenoteListenerListener.onAlarmClicked()
                        context.getString(R.string.delete) -> timenoteListenerListener.onDeleteClicked()
                        context.getString(R.string.hide_to_others) -> timenoteListenerListener.onHideToOthersClicked()
                    }
                }
            }
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
                .load(timenote.pic!![0])
                .centerCrop()
                .into(itemView.timenote_recent_pic_imageview)

            Glide
                .with(itemView)
                .load(timenote.pic_user)
                .apply(RequestOptions.circleCropTransform())
                .into(itemView.timenote_recent_pic_user_imageview)

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