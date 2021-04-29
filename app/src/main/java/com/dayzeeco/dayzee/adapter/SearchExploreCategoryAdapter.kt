package com.dayzeeco.dayzee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.model.Category
import kotlinx.android.synthetic.main.adapter_search_explore.view.*
import kotlinx.android.synthetic.main.item_search_explore.view.*

class SearchExploreCategoryAdapter(
    val explores: Map<String, List<Category>?>?,
    val subCategoryListener: SearchSubCategoryListener
) : RecyclerView.Adapter<SearchExploreCategoryAdapter.SearchExploreHolder>() {

    interface SearchSubCategoryListener {
        fun onSubCategorySelected(category: Category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchExploreHolder =
        SearchExploreHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_search_explore,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = explores?.size!!

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: SearchExploreHolder, position: Int) {
        holder.bindCategory(explores, position, subCategoryListener)
    }

    class SearchExploreHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var expanded : Boolean = false

        fun bindCategory(
            list: Map<String, List<Category>?>?,
            position: Int,
            subCategoryListener: SearchSubCategoryListener
        ) {
            itemView.search_explore_category.text = list?.keys?.elementAt(position)

            val options = RequestOptions()
            options.centerCrop()


            when(list?.keys?.elementAt(position)?.toLowerCase()){
                itemView.context.getString(R.string.sports)
                    .toLowerCase(), itemView.context.getString(
                    R.string.sport
                ).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_sport.jpg")
                        .apply(options)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.search_explore_category_iv)
                itemView.context.getString(R.string.crypto).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_crypto.jpg")
                        .apply(options)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.search_explore_category_iv)
                itemView.context.getString(R.string.influencers).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_influencers.jpg")
                        .apply(options)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.search_explore_category_iv)
                itemView.context.getString(R.string.social_and_meeting).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_social_and_meeting.jpg")
                        .apply(options)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.search_explore_category_iv)
                itemView.context.getString(R.string.for_kids).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_for_kids.jpg")
                        .apply(options)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.search_explore_category_iv)
                itemView.context.getString(R.string.esports)
                    .toLowerCase(), itemView.context.getString(
                    R.string.esport
                ).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_esport.jpg")
                        .apply(options)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.search_explore_category_iv)
                itemView.context.getString(R.string.music).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_music.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.search_explore_category_iv)
                itemView.context.getString(R.string.holidays)
                    .toLowerCase(), itemView.context.getString(
                    R.string.holiday
                ).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_holidays.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.search_explore_category_iv)
                itemView.context.getString(R.string.shopping).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_shopping.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.search_explore_category_iv)
                itemView.context.getString(R.string.films_n_amp_series).replace("\n", "")
                    .toLowerCase(),
                itemView.context.getString(R.string.behind_the_screen).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_film.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.search_explore_category_iv)
                itemView.context.getString(R.string.culture_n_amp_loisirs).replace("\n", "")
                    .toLowerCase(),
                itemView.context.getString(R.string.culture).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_culture.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.search_explore_category_iv)
                itemView.context.getString(R.string.youtubers), itemView.context.getString(R.string.youtube_channels)
                    .toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_youtuber.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.search_explore_category_iv)
                itemView.context.getString(R.string.religions)
                    .toLowerCase(), itemView.context.getString(
                    R.string.religion
                ).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_religion.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.search_explore_category_iv)
                itemView.context.getString(R.string.calendrier_n_interessant).replace("\n", "")
                    .toLowerCase().trim() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_interesting_calendar.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.search_explore_category_iv)
                itemView.context.getString(R.string.fair_trade), itemView.context.getString(R.string.events_and_fair_trade)
                    .toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_else.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.search_explore_category_iv)
                else -> itemView.search_explore_category_iv.setImageDrawable(
                    ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.audiance
                    )
                )
            }

            itemView.setOnClickListener {
                if(!expanded) {
                    itemView.search_explore_rv.apply {
                        visibility = View.VISIBLE
                        layoutManager = LinearLayoutManager(itemView.context)
                        isNestedScrollingEnabled = false
                        adapter = SearchExploreSubCategoryAdapter(
                            list?.values?.elementAt(position),
                            subCategoryListener
                        )
                    }
                    expanded = !expanded
                } else {
                    itemView.search_explore_rv.visibility = View.GONE
                    expanded = !expanded
                }
            }
            if(list?.values?.elementAt(position).isNullOrEmpty()) {
                itemView.search_explore_rv.visibility = View.GONE
            }

        }

    }
}

class SearchExploreSubCategoryAdapter(
    private val subCategory: List<Category>?,
    private val subCategoryListener: SearchExploreCategoryAdapter.SearchSubCategoryListener

): RecyclerView.Adapter<SearchExploreSubCategoryAdapter.ItemSearchExploreHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSearchExploreHolder =
        ItemSearchExploreHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_search_explore,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = subCategory?.size!!

    override fun onBindViewHolder(holder: ItemSearchExploreHolder, position: Int) {
        holder.setIsRecyclable(false)
        when (position) {
            subCategory?.lastIndex -> {
                val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
                params.bottomMargin = 50
                holder.itemView.layoutParams = params
            }
            0 -> {
                val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
                params.topMargin = 25
                holder.itemView.layoutParams = params
            }
            else -> {
                val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
                params.bottomMargin = 0
                holder.itemView.layoutParams = params
            }
        }
        holder.bindSubCategory(subCategory?.get(position)!!, subCategoryListener)
    }


    class ItemSearchExploreHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindSubCategory(
            subCategory: Category,
            subCategoryListener: SearchExploreCategoryAdapter.SearchSubCategoryListener,
        ) {
            itemView.search_explore_subcategory.text = subCategory.subcategory
            itemView.search_explore_subcategory.setOnClickListener { subCategoryListener.onSubCategorySelected(
                Category(
                    subCategory.category,
                    subCategory.subcategory
                )
            )
            }
        }


    }
}