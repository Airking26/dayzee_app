package com.timenoteco.timenote.adapter


import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.text.*
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
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
import com.timenoteco.timenote.common.*
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.TimenoteInfoDTO
import kotlinx.android.synthetic.main.item_timenote.view.*
import kotlinx.android.synthetic.main.item_timenote_root.view.*
import java.text.SimpleDateFormat
import java.util.*

class ItemTimenoteAdapter(
    private val timenotes: List<TimenoteInfoDTO>,
    private val timenoteListenerListener: TimenoteOptionsListener,
    private val fragment: Fragment,
    private val isFromFuture: Boolean,
    private val utils: Utils, private val createdBy: String?) :
    RecyclerView.Adapter<ItemTimenoteAdapter.TimenoteViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimenoteViewHolder {
            return TimenoteViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_timenote, parent, false))
    }

    override fun getItemCount(): Int = timenotes.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TimenoteViewHolder, position: Int) {
            holder.bindTimenote(
                timenotes[position],
                timenoteListenerListener,
                fragment,
                isFromFuture,
                utils,
            createdBy)

    }

    class TimenoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindTimenote(
            timenote: TimenoteInfoDTO,
            timenoteListenerListener: TimenoteOptionsListener,
            fragment: Fragment,
            isFromFuture: Boolean,
            utils: Utils,
            createdBy: String?
        ) {
            if(isFromFuture) {
                itemView.timenote_plus.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_ajout_cal))
                if(timenote.isParticipating || createdBy == timenote.createdBy.id) itemView.timenote_plus.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_ajout_cal_plein_gradient))
                else itemView.timenote_plus.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_ajout_cal))
            }
            else itemView.timenote_plus.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_like))

            itemView.timenote_title.text = timenote.title

            timenoteListenerListener.onAddMarker(timenote)

            Glide
                .with(itemView)
                .load(timenote.createdBy.picture)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.circle_pic)
                .into(itemView.timenote_pic_user_imageview)

            if(timenote.commentAccount!! > 0){
                itemView.timenote_comment_account.visibility = View.VISIBLE
                if(timenote.commentAccount == 1) itemView.timenote_comment_account.text = "See comment"
                else itemView.timenote_comment_account.text = "See the ${timenote.commentAccount} comments"
            } else {
                itemView.timenote_comment_account.visibility = View.GONE
            }

            var addedBy = ""
            val addedByFormated: SpannableStringBuilder
            val p = Typeface.create("sans-serif-light", Typeface.NORMAL)
            val m = Typeface.create("sans-serif", Typeface.NORMAL)
            val light = ItemTimenoteRecentAdapter.CustomTypefaceSpan(p)
            val bold = ItemTimenoteRecentAdapter.CustomTypefaceSpan(m)

            if(!timenote.joinedBy?.users.isNullOrEmpty()){
                when {
                    timenote.joinedBy?.count == 1 -> addedBy = "Saved by ${timenote.joinedBy.users[0].userName}"
                    timenote.joinedBy?.count in 1..20 -> addedBy = "Saved by ${timenote.joinedBy?.users?.get(0)?.userName} and ${timenote.joinedBy?.count!! - 1} other people"
                    timenote.joinedBy?.count in 21..100 -> addedBy = "Saved by ${timenote.joinedBy?.users?.get(0)?.userName} and tens of other people"
                    timenote.joinedBy?.count in 101..2000 -> addedBy = "Saved by ${timenote.joinedBy?.users?.get(0)?.userName} and hundreds of other people"
                    timenote.joinedBy?.count in 2001..2000000 -> addedBy = "Saved by ${timenote.joinedBy?.users?.get(0)?.userName} and thousands of other people"
                    timenote.joinedBy?.count!! > 2000000 -> addedBy = "Saved by ${timenote.joinedBy?.users?.get(0)?.userName} and millions of other people"
                }

                addedByFormated = SpannableStringBuilder(addedBy)
                addedByFormated.setSpan(light, 0, 8, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                addedByFormated.setSpan(bold, 9, addedBy.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                itemView.timenote_added_by.text = addedByFormated

                when (timenote.joinedBy?.users?.size) {
                    1 -> {
                        Glide
                            .with(itemView)
                            .load(timenote.joinedBy.users[0].picture)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .apply(RequestOptions.circleCropTransform())
                            .into(itemView.timenote_pic_participant_three)
                        itemView.timenote_pic_participant_two_rl.visibility = View.GONE
                        itemView.timenote_pic_participant_one_rl.visibility = View.GONE
                    }
                    2 -> {
                        Glide
                            .with(itemView)
                            .load(timenote.joinedBy.users[0].picture)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .apply(RequestOptions.circleCropTransform())
                            .into(itemView.timenote_pic_participant_two)

                        Glide
                            .with(itemView)
                            .load(timenote.joinedBy.users[1].picture)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .apply(RequestOptions.circleCropTransform())
                            .into(itemView.timenote_pic_participant_three)
                        itemView.timenote_pic_participant_one_rl.visibility = View.GONE
                    }
                    else -> {
                        Glide
                            .with(itemView)
                            .load(timenote.joinedBy?.users?.get(0)?.picture)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .apply(RequestOptions.circleCropTransform())
                            .into(itemView.timenote_pic_participant_one)

                        Glide
                            .with(itemView)
                            .load(timenote.joinedBy?.users?.get(1)?.picture)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .apply(RequestOptions.circleCropTransform())
                            .into(itemView.timenote_pic_participant_two)

                        Glide
                            .with(itemView)
                            .load(timenote.joinedBy?.users?.get(2)?.picture)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .apply(RequestOptions.circleCropTransform())
                            .into(itemView.timenote_pic_participant_three)
                    }
                }
            }
            else {
                if(timenote.joinedBy?.count!! > 0){
                    addedBy = "Saved by ${timenote.joinedBy.count} people"
                    val addedByFormated = SpannableStringBuilder(addedBy)
                    addedByFormated.setSpan(light, 0, 8, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                    addedByFormated.setSpan(bold, 9, addedBy.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                    itemView.timenote_added_by.text = addedByFormated
                    itemView.timenote_pic_participant_two_rl.visibility = View.GONE
                    itemView.timenote_pic_participant_three_rl.visibility = View.GONE
                    itemView.timenote_pic_participant_one_rl.visibility = View.GONE
                } else {
                    itemView.timenote_pic_participant_three_rl.visibility = View.GONE
                    itemView.timenote_pic_participant_two_rl.visibility = View.GONE
                    itemView.timenote_pic_participant_one_rl.visibility = View.GONE
                    itemView.timenote_fl.visibility = View.GONE
                }
            }

            val screenSlideCreationTimenotePagerAdapter =  ScreenSlideTimenotePagerAdapter(fragment, if(timenote.pictures.isNullOrEmpty()) listOf(timenote.colorHex!!) else timenote.pictures, true, timenote.pictures.isNullOrEmpty()){ _ : Int, i1: Int ->
                if(i1 == 0) {
                    if (timenote.price.price >= 0 && !timenote.url.isNullOrBlank()) {
                        itemView.timenote_buy_cl.visibility = View.VISIBLE
                        if (timenote.price .price > 0) itemView.timenote_buy.text = timenote.price.price.toString().plus(timenote.price.currency)
                    }
                } else {
                    if(isFromFuture && createdBy != timenote.createdBy.id) {
                        if(itemView.timenote_plus.drawable.bytesEqualTo(itemView.context.getDrawable(R.drawable.ic_ajout_cal)) && itemView.timenote_plus.drawable.pixelsEqualTo(itemView.context.getDrawable(R.drawable.ic_ajout_cal))){
                            itemView.timenote_plus.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_ajout_cal_plein_gradient))
                            timenoteListenerListener.onPlusClicked(timenote, true)
                        } else {
                            itemView.timenote_plus.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_ajout_cal))
                            timenoteListenerListener.onPlusClicked(timenote, false)
                        }
                    }
                }
            }

            itemView.timenote_buy_cl.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(if(timenote.url?.contains("https://")!!) timenote.url else "https://" + timenote.url)
                itemView.context.startActivity(i)
            }

            itemView.timenote_vp.adapter = screenSlideCreationTimenotePagerAdapter
            itemView.timenote_indicator.setViewPager(itemView.timenote_vp)
            if(timenote.pictures?.size == 1 || timenote.pictures.isNullOrEmpty()) itemView.timenote_indicator.visibility = View.GONE
            screenSlideCreationTimenotePagerAdapter.registerAdapterDataObserver(itemView.timenote_indicator.adapterDataObserver)
            itemView.timenote_username.text = timenote.createdBy.userName
            if(timenote.location != null) itemView.timenote_place.text = timenote.location.address.address.plus(", ").plus(timenote.location.address.city).plus(" ").plus(timenote.location.address.country)

           val hashTagHelper = HashTagHelper.Creator.create(R.color.colorAccent, object : HashTagHelper.OnHashTagClickListener{
                override fun onHashTagClicked(hashTag: String?) {
                    timenoteListenerListener.onHashtagClicked(timenote, hashTag)
                }

            }, null, itemView.resources)
            hashTagHelper.handle(itemView.timenote_username_desc)

            /*if(timenote.hashtags.isNullOrEmpty() && timenote.description.isNullOrBlank()){
                itemView.timenote_username_desc.visibility = View.GONE
            } else if(timenote.hashtags.isNullOrEmpty() && !timenote.description.isNullOrBlank()){
                val desc = SpannableStringBuilder(timenote.description)
                desc.setSpan(light, 0, timenote.description.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                itemView.timenote_username_desc.text = username.append(" ").append(desc)
            } else if(!timenote.hashtags.isNullOrEmpty() && timenote.description.isNullOrBlank()){
                val hashtags = SpannableStringBuilder(timenote.hashtags.joinToString(separator = ""))
                hashtags.setSpan(bold, 0, hashtags.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                itemView.timenote_username_desc.text = username.append(" ").append(hashtags)
            } else {
                val hashtags = SpannableStringBuilder(timenote.hashtags?.joinToString(separator = ""))
                val completeDesc = SpannableStringBuilder(timenote.hashtags?.joinToString(separator = "")).append(" ${timenote.description}")
                completeDesc.setSpan(bold, 0, hashtags.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                completeDesc.setSpan(light, hashtags.length, completeDesc.toString().length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                itemView.timenote_username_desc.text = username.append(" ").append(completeDesc)
            }*/

            if(timenote.description.isNullOrBlank()){
                itemView.timenote_username_desc.visibility = View.GONE
            } else {
                val ellipzise = "..."
                val ellipsizeClickable = SpannableString(ellipzise)
                val clickalbleEllipsize = object : ClickableSpan(){
                    override fun onClick(widget: View) {
                        timenoteListenerListener.onSeeMoreClicked(timenote)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.isUnderlineText  = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            ds.color = itemView.context.getColor(R.color.colorText)
                            ds.typeface = Typeface.DEFAULT_BOLD
                        }
                    }
                }
                ellipsizeClickable.setSpan(clickalbleEllipsize, 0, ellipzise.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                val description = "${timenote.createdBy.userName} ${timenote.description}"
                val descriptionFormatted = SpannableStringBuilder(description)
                descriptionFormatted.setSpan(bold, 0, timenote.createdBy.userName?.length!!, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                descriptionFormatted.setSpan(light, timenote.createdBy.userName?.length!!, description.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                itemView.timenote_username_desc.text = descriptionFormatted

                itemView.timenote_username_desc.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener{
                    override fun onGlobalLayout() {
                        itemView.timenote_username_desc.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        if(itemView.timenote_username_desc.lineCount > 2){
                            val endOfLastLine = itemView.timenote_username_desc.layout.getLineEnd(1)
                            var descriptionWithEllipsize = TextUtils.concat(itemView.timenote_username_desc.text.subSequence(0, endOfLastLine - 4), ellipsizeClickable)
                            itemView.timenote_username_desc.text = descriptionWithEllipsize
                        }
                    }

                })
            }

            itemView.timenote_year.text = utils.setYear(timenote.startingAt)
            itemView.timenote_day_month.text = utils.setFormatedStartDate(timenote.startingAt, timenote.endingAt)
            itemView.timenote_time.text = utils.setFormatedEndDate(timenote.startingAt, timenote.endingAt)

            itemView.timenote_day_month.setOnClickListener { showInTime(isFromFuture, utils, timenote) }
            itemView.timenote_year.setOnClickListener { showInTime(isFromFuture, utils, timenote) }
            itemView.timenote_time.setOnClickListener { showInTime(isFromFuture, utils, timenote) }
            itemView.separator_1.setOnClickListener { showInTime(isFromFuture, utils, timenote) }
            itemView.separator_2.setOnClickListener { showInTime(isFromFuture, utils, timenote) }

            itemView.timenote_in_label.setOnClickListener {
                itemView.separator_1.visibility = View.VISIBLE
                itemView.separator_2.visibility = View.VISIBLE
                itemView.timenote_day_month.visibility = View.VISIBLE
                itemView.timenote_time.visibility = View.VISIBLE
                itemView.timenote_year.visibility = View.VISIBLE
                itemView.timenote_in_label.visibility = View.INVISIBLE
            }

            itemView.timenote_options.setOnClickListener {
                createOptionsOnTimenote(itemView.context,  timenoteListenerListener, timenote, createdBy)
            }

            itemView.timenote_comment_account.setOnClickListener { timenoteListenerListener.onSeeMoreClicked(timenote) }
            itemView.timenote_share.setOnClickListener{timenoteListenerListener.onShareClicked(timenote)}
            itemView.timenote_pic_user_imageview.setOnClickListener { timenoteListenerListener.onPictureClicked(timenote.createdBy) }
            itemView.timenote_comment.setOnClickListener { timenoteListenerListener.onCommentClicked(timenote) }
            itemView.timenote_plus.setOnClickListener {
                if(isFromFuture && createdBy != timenote.createdBy.id) {
                    if(itemView.timenote_plus.drawable.bytesEqualTo(itemView.context.getDrawable(R.drawable.ic_ajout_cal)) && itemView.timenote_plus.drawable.pixelsEqualTo(itemView.context.getDrawable(R.drawable.ic_ajout_cal))){
                        itemView.timenote_plus.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_ajout_cal_plein_gradient))
                        timenoteListenerListener.onPlusClicked(timenote, true)
                    } else {
                        itemView.timenote_plus.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_ajout_cal))
                        timenoteListenerListener.onPlusClicked(timenote, false)
                    }
                }
            }
            itemView.timenote_place.setOnClickListener{timenoteListenerListener.onAddressClicked(timenote)}
            itemView.timenote_fl.setOnClickListener{timenoteListenerListener.onSeeParticipants(timenote)}
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun showInTime(
            isFromFuture: Boolean,
            utils: Utils,
            timenote: TimenoteInfoDTO
        ) {
            itemView.separator_1.visibility = View.INVISIBLE
            itemView.separator_2.visibility = View.INVISIBLE
            itemView.timenote_day_month.visibility = View.INVISIBLE
            itemView.timenote_time.visibility = View.INVISIBLE
            itemView.timenote_year.visibility = View.INVISIBLE
            itemView.timenote_in_label.visibility = View.VISIBLE
            if (isFromFuture) itemView.timenote_in_label.text = utils.inTime(timenote.startingAt)
            else itemView.timenote_in_label.text = utils.sinceTime(timenote.endingAt)
        }

        private fun createOptionsOnTimenote(
            context: Context,
            timenoteListenerListener: TimenoteOptionsListener,
            timenote: TimenoteInfoDTO,
            createdBy: String?
        ){
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val ISO =  "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            val listItems: MutableList<String> = if(createdBy == timenote.createdBy.id) mutableListOf(context.getString(R.string.share_to) ,context.getString(R.string.duplicate), context.getString(R.string.edit), context.getString(R.string.delete)) else mutableListOf(context.getString(R.string.share_to) ,context.getString(R.string.duplicate), context.getString(R.string.report))
            MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(text = "Posted : " + dateFormat.format(SimpleDateFormat(ISO).parse(timenote.createdAt).time))
                listItems (items = listItems){ _, _, text ->
                    when(text.toString()){
                        context.getString(R.string.duplicate) -> timenoteListenerListener.onDuplicateClicked(timenote)
                        context.getString(R.string.report) -> timenoteListenerListener.onReportClicked(timenote)
                        context.getString(R.string.share_to) -> timenoteListenerListener.onAlarmClicked(timenote)
                        context.getString(R.string.edit) -> timenoteListenerListener.onEditClicked(timenote)
                        context.getString(R.string.delete)  -> timenoteListenerListener.onDeleteClicked(timenote)
                    }
                }
            }
        }

    }

}