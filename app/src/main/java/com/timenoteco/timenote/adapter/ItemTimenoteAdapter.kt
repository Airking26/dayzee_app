package com.timenoteco.timenote.adapter


import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.common.RoundedCornersTransformation
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.TimenoteInfoDTO
import kotlinx.android.synthetic.main.item_timenote.view.*
import kotlinx.android.synthetic.main.item_timenote_root.view.*
import java.text.SimpleDateFormat

class ItemTimenoteAdapter(
    private val timenotes: List<TimenoteInfoDTO>,
    private val timenoteListenerListener: TimenoteOptionsListener,
    private val fragment: Fragment,
    private val isFromFuture: Boolean,
    private val utils: Utils) :
    RecyclerView.Adapter<ItemTimenoteAdapter.TimenoteViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimenoteViewHolder {
            return TimenoteViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_timenote, parent, false))
    }

    override fun getItemCount(): Int = timenotes.size

    override fun onBindViewHolder(holder: TimenoteViewHolder, position: Int) {
            holder.bindTimenote(
                timenotes[position],
                timenoteListenerListener,
                fragment,
                isFromFuture,
                utils
            )

    }

    class TimenoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindTimenote(
            timenote: TimenoteInfoDTO,
            timenoteListenerListener: TimenoteOptionsListener,
            fragment: Fragment,
            isFromFuture: Boolean,
            utils: Utils
        ) {
            if(isFromFuture) itemView.timenote_plus.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_ajout_cal))
            else itemView.timenote_plus.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_like))

            itemView.timenote_title.text = timenote.title

            Glide
                .with(itemView)
                .load(timenote.createdBy.picture)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.circle_pic)
                .into(itemView.timenote_pic_user_imageview)

            var addedBy = ""
            val addedByFormated: SpannableStringBuilder
            val p = Typeface.create("sans-serif-light", Typeface.NORMAL)
            val m = Typeface.create("sans-serif", Typeface.NORMAL)
            val light = ItemTimenoteRecentAdapter.CustomTypefaceSpan(p)
            val bold = ItemTimenoteRecentAdapter.CustomTypefaceSpan(m)

            if(!timenote.joinedBy.users.isNullOrEmpty()){
                when {
                    timenote.joinedBy.count == 1 -> addedBy = "Saved by ${timenote.joinedBy.users[0].userName}"
                    timenote.joinedBy.count in 1..20 -> addedBy = "Saved by ${timenote.joinedBy.users[0].userName} and ${timenote.joinedBy.count - 1} other people"
                    timenote.joinedBy.count in 21..100 -> addedBy = "Saved by ${timenote.joinedBy.users[0].userName} and tens of other people"
                    timenote.joinedBy.count in 101..2000 -> addedBy = "Saved by ${timenote.joinedBy.users[0].userName} and hundreds of other people"
                    timenote.joinedBy.count in 2001..2000000 -> addedBy = "Saved by ${timenote.joinedBy.users[0].userName} and thousands of other people"
                    timenote.joinedBy.count > 2000000 -> addedBy = "Saved by ${timenote.joinedBy.users[0].userName} and millions of other people"
                }

                addedByFormated = SpannableStringBuilder(addedBy)
                addedByFormated.setSpan(light, 0, 8, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                addedByFormated.setSpan(bold, 9, addedBy.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                itemView.timenote_added_by.text = addedByFormated

                when (timenote.joinedBy.users.size) {
                    1 -> {
                        Glide
                            .with(itemView)
                            .load(timenote.joinedBy.users[0].picture)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .apply(RequestOptions.circleCropTransform())
                            .into(itemView.timenote_pic_participant_three)
                        itemView.timenote_pic_participant_two.visibility = View.GONE
                        itemView.timenote_pic_participant_one.visibility = View.GONE
                    }
                    2 -> {
                        Glide
                            .with(itemView)
                            .load(timenote.joinedBy.users[0].picture)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(itemView.context, 90, 0, itemView.context.resources.getString(0 + R.color.colorBackground), 4)))
                            .into(itemView.timenote_pic_participant_two)

                        Glide
                            .with(itemView)
                            .load(timenote.joinedBy.users[1].picture)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(itemView.context, 90, 0, itemView.context.resources.getString(0 + R.color.colorBackground), 4)))
                            .into(itemView.timenote_pic_participant_three)
                        itemView.timenote_pic_participant_one.visibility = View.GONE
                    }
                    else -> {
                        Glide
                            .with(itemView)
                            .load(timenote.joinedBy.users[0].picture)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(itemView.context, 90, 0, itemView.context.resources.getString(0 + R.color.colorBackground), 4)))
                            .into(itemView.timenote_pic_participant_one)

                        Glide
                            .with(itemView)
                            .load(timenote.joinedBy.users[1].picture)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(itemView.context, 90, 0, itemView.context.resources.getString(0 + R.color.colorBackground), 4)))
                            .into(itemView.timenote_pic_participant_two)

                        Glide
                            .with(itemView)
                            .load(timenote.joinedBy.users[2].picture)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(itemView.context, 90, 0, itemView.context.resources.getString(0 + R.color.colorBackground), 4)))
                            .into(itemView.timenote_pic_participant_three)
                    }
                }
            } else {
                itemView.timenote_pic_participant_one.visibility = View.GONE
                itemView.timenote_pic_participant_two.visibility = View.GONE
                itemView.timenote_pic_participant_three.visibility = View.GONE
                itemView.timenote_fl.visibility = View.GONE
            }

            val screenSlideCreationTimenotePagerAdapter =  ScreenSlideTimenotePagerAdapter(fragment, timenote.pictures, true){ _ : Int, i1: Int ->
                if(i1 == 0) {
                    if (timenote.price.price >= 0 || (timenote.price.price >= 0 && !timenote.url.isBlank())) {
                        itemView.timenote_buy.visibility = View.VISIBLE
                        if (timenote.price .price> 0) itemView.timenote_buy.text = timenote.price.price.toString().plus(timenote.price.currency)
                    }
                } else {
                    timenoteListenerListener.onDoubleClick()
                }
            }

            itemView.timenote_vp.adapter = screenSlideCreationTimenotePagerAdapter
            itemView.timenote_indicator.setViewPager(itemView.timenote_vp)
            if(timenote.pictures.size == 1) itemView.timenote_indicator.visibility = View.GONE
            screenSlideCreationTimenotePagerAdapter.registerAdapterDataObserver(itemView.timenote_indicator.adapterDataObserver)
            itemView.timenote_username.text = timenote.createdBy.userName
            if(timenote.location != null) itemView.timenote_place.text = timenote.location.address.address.plus(", ").plus(timenote.location.address.city).plus(" ").plus(timenote.location.address.country)


            if(timenote.hashtags.isNullOrEmpty() && timenote.description.isBlank()){
                itemView.timenote_username_desc.visibility = View.GONE
                if(timenote.joinedBy.count == 0){
                    itemView.timenote_see_more.visibility = View.GONE
                }
            } else if(timenote.hashtags.isNullOrEmpty() && !timenote.description.isBlank()){
                itemView.timenote_username_desc.text = timenote.description
            } else if(!timenote.hashtags.isNullOrEmpty() && timenote.description.isBlank()){
                val hashtags = SpannableStringBuilder(timenote.hashtags.joinToString(separator = ""))
                hashtags.setSpan(bold, 0, hashtags.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                itemView.timenote_username_desc.text = hashtags
            } else {
                val hashtags = SpannableStringBuilder(timenote.hashtags.joinToString(separator = ""))
                val completeDesc = SpannableStringBuilder(timenote.hashtags.joinToString(separator = "")).append(" ${timenote.description}")
                completeDesc.setSpan(bold, 0, hashtags.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                completeDesc.setSpan(light, hashtags.length, completeDesc.toString().length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                itemView.timenote_username_desc.text = completeDesc
            }
            itemView.timenote_year.text = utils.setYear(timenote.startingAt)
            itemView.timenote_day_month.text = utils.setFormatedStartDate(timenote.startingAt, timenote.endingAt)
            itemView.timenote_time.text = utils.setFormatedEndDate(timenote.startingAt, timenote.endingAt)

            itemView.timenote_day_month.setOnClickListener {
                itemView.separator_1.visibility = View.INVISIBLE
                itemView.separator_2.visibility = View.INVISIBLE
                itemView.timenote_day_month.visibility = View.INVISIBLE
                itemView.timenote_time.visibility = View.INVISIBLE
                itemView.timenote_year.visibility = View.INVISIBLE
                itemView.timenote_in_label.visibility = View.VISIBLE
                itemView.timenote_in_label.text = utils.calculateDecountTime(timenote.startingAt)
            }

            itemView.timenote_in_label.setOnClickListener {
                itemView.separator_1.visibility = View.VISIBLE
                itemView.separator_2.visibility = View.VISIBLE
                itemView.timenote_day_month.visibility = View.VISIBLE
                itemView.timenote_time.visibility = View.VISIBLE
                itemView.timenote_year.visibility = View.VISIBLE
                itemView.timenote_in_label.visibility = View.INVISIBLE
            }

            itemView.timenote_options.setOnClickListener {
                createOptionsOnTimenote(itemView.context,  timenoteListenerListener, timenote)
            }

            itemView.timenote_share.setOnClickListener{timenoteListenerListener.onShareClicked(timenote)}
            itemView.timenote_pic_user_imageview.setOnClickListener { timenoteListenerListener.onPictureClicked(timenote) }
            itemView.timenote_comment.setOnClickListener { timenoteListenerListener.onCommentClicked(timenote) }
            itemView.timenote_plus.setOnClickListener {
                if(false){
                    itemView.timenote_plus.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_ajout_cal))
                } else {
                    itemView.timenote_plus.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_ajoute_cal_grad_ok))
                }
                timenoteListenerListener.onPlusClicked(timenote)
            }
            itemView.timenote_see_more.setOnClickListener { timenoteListenerListener.onSeeMoreClicked(timenote) }
            itemView.timenote_place.setOnClickListener{timenoteListenerListener.onAddressClicked(timenote)}
            itemView.timenote_fl.setOnClickListener{timenoteListenerListener.onSeeParticipants(timenote)}
        }

        private fun createOptionsOnTimenote(
            context: Context,
            timenoteListenerListener: TimenoteOptionsListener,
            timenote: TimenoteInfoDTO
        ){
            val dateFormat = SimpleDateFormat("dd.MM.yyyy")
            val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            val listItems: MutableList<String> = mutableListOf(context.getString(R.string.duplicate), context.getString(R.string.alarm), context.getString(R.string.report))
            MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(text = "Posted : " + dateFormat.format(SimpleDateFormat(ISO).parse(timenote.createdAt).time))
                listItems (items = listItems){ _, _, text ->
                    when(text.toString()){
                        context.getString(R.string.duplicate) -> timenoteListenerListener.onDuplicateClicked(timenote)
                        context.getString(R.string.report) -> timenoteListenerListener.onReportClicked()
                        context.getString(R.string.alarm) -> timenoteListenerListener.onAlarmClicked(timenote)
                    }
                }
            }
        }

    }

}