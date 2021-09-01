package com.dayzeeco.dayzee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.model.Category
import kotlinx.android.synthetic.main.adapter_suggestion_card.view.*
import kotlinx.android.synthetic.main.item_category.view.*
import java.util.*

class CategoryAdapter(private val categories: List<Category>?, private val categoriesAlreadyChosen : List<Category>?) : BaseAdapter() {

    override fun getCount(): Int = categories?.size ?: 0

    override fun getItem(position: Int): Any = Any()
    override fun getItemId(position: Int): Long = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val category = categories?.get(position)
        val view : View = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.item_category, parent, false)

        categoriesAlreadyChosen?.forEach {
            if(it.category == category?.category)
                view.category_btn.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_checktwo))
        }

        view.category_tv.text = category?.category

        when(category?.category?.toLowerCase()){
            view.context.getString(R.string.sports).toLowerCase(), view.context.getString(
                R.string.sport
            ).toLowerCase() ->
                view.category_iv.setImageDrawable(view.context.resources.getDrawable(R.drawable.category_sport))
                /*Glide
                    .with(view)
                    .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_sport.jpg")
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(view.category_iv)*/
            view.context.getString(R.string.crypto).toLowerCase() ->
                view.category_iv.setImageDrawable(view.context.resources.getDrawable(R.drawable.category_crypto))
            /*Glide
                    .with(view)
                    .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_crypto.jpg")
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(view.category_iv)*/
            view.context.getString(R.string.influencers).toLowerCase() ->
                view.category_iv.setImageDrawable(view.context.resources.getDrawable(R.drawable.category_influencers))
            /*Glide
                    .with(view)
                    .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_influencers.jpg")
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(view.category_iv)*/
            view.context.getString(R.string.social_and_meeting).toLowerCase() ->
                view.category_iv.setImageDrawable(view.context.resources.getDrawable(R.drawable.category_social_and_meeting))
            /*Glide
                .with(view)
                .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_social_and_meeting.jpg")
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view.category_iv)*/
            view.context.getString(R.string.for_kids).toLowerCase() ->
                view.category_iv.setImageDrawable(view.context.resources.getDrawable(R.drawable.category_for_kids))
            /*Glide
                .with(view)
                .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_for_kids.jpg")
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view.category_iv)*/
            view.context.getString(R.string.esports)
                .toLowerCase(), view.context.getString(
                R.string.esport
            ).toLowerCase() ->
                view.category_iv.setImageDrawable(view.context.resources.getDrawable(R.drawable.category_esport))
            /*Glide
                .with(view)
                .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_esport.jpg")
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view.category_iv)*/
            view.context.getString(R.string.music).toLowerCase() ->
                view.category_iv.setImageDrawable(view.context.resources.getDrawable(R.drawable.category_music))
            /*Glide
                .with(view)
                .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_music.jpg")
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view.category_iv)*/
            view.context.getString(R.string.holidays)
                .toLowerCase(), view.context.getString(
                R.string.holiday
            ).toLowerCase() ->
                view.category_iv.setImageDrawable(view.context.resources.getDrawable(R.drawable.category_holidays))
            /*Glide
                .with(view)
                .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_holidays.jpg")
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view.category_iv)*/
            view.context.getString(R.string.shopping).toLowerCase() ->
                view.category_iv.setImageDrawable(view.context.resources.getDrawable(R.drawable.category_shopping))
            /*Glide
                .with(view)
                .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_shopping.jpg")
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view.category_iv)*/
            view.context.getString(R.string.films_n_amp_series).replace("\n", "")
                .toLowerCase(),
            view.context.getString(R.string.behind_the_screen).toLowerCase() ->
                view.category_iv.setImageDrawable(view.context.resources.getDrawable(R.drawable.category_film))
            /*Glide
                .with(view)
                .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_film.jpg")
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view.category_iv)*/
            view.context.getString(R.string.culture_n_amp_loisirs).replace("\n", "")
                .toLowerCase(),
            view.context.getString(R.string.culture).toLowerCase() ->
                view.category_iv.setImageDrawable(view.context.resources.getDrawable(R.drawable.category_culture))
            /* Glide
                 .with(view)
                 .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_culture.jpg")
                 .centerCrop()
                 .diskCacheStrategy(DiskCacheStrategy.ALL)
                 .into(view.category_iv)*/
            view.context.getString(R.string.youtubers), view.context.getString(R.string.youtube_channels)
                .toLowerCase() ->
                view.category_iv.setImageDrawable(view.context.resources.getDrawable(R.drawable.category_youtuber))
            /*Glide
                .with(view)
                .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_youtuber.jpg")
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view.category_iv)*/
            view.context.getString(R.string.religions)
                .toLowerCase(), view.context.getString(
                R.string.religion
            ).toLowerCase() ->
                view.category_iv.setImageDrawable(view.context.resources.getDrawable(R.drawable.category_religion))
            /*Glide
                .with(view)
                .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_religion.jpg")
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view.category_iv)*/
            view.context.getString(R.string.calendrier_n_interessant).replace("\n", "")
                .toLowerCase().trim() ->
                view.category_iv.setImageDrawable(view.context.resources.getDrawable(R.drawable.category_interesting_calendar))
            /*Glide
                .with(view)
                .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_interesting_calendar.jpg")
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view.category_iv)*/
            view.context.getString(R.string.fair_trade), view.context.getString(R.string.events_and_fair_trade)
                .toLowerCase() ->
                view.category_iv.setImageDrawable(view.context.resources.getDrawable(R.drawable.category_else))
            /*Glide
                .with(view)
                .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_else.jpg")
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view.category_iv)*/
            else -> view.category_iv.setImageDrawable(
                ContextCompat.getDrawable(
                    view.context,
                    R.drawable.audiance
                )
            )
        }


        return view
    }
}