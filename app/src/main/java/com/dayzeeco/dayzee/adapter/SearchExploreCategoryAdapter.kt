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
import kotlinx.android.synthetic.main.item_category.view.*
import kotlinx.android.synthetic.main.item_search_explore.view.*

private val allCategoriesSelected : MutableList<Int> = mutableListOf()

class SearchExploreCategoryAdapter(
    val explores: Map<String, List<Category>?>?,
    val subCategoryListener: SearchSubCategoryListener
) : RecyclerView.Adapter<SearchExploreCategoryAdapter.SearchExploreHolder>() {

    interface SearchSubCategoryListener {
        fun onSubCategorySelected(category: Category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, itemViewType: Int): SearchExploreHolder =
        SearchExploreHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_search_explore,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = explores?.size!! - 1

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: SearchExploreHolder, position: Int) {
        if(explores?.keys?.elementAt(position)?.toLowerCase() != holder.itemView.context.getString(R.string.common).toLowerCase())
        holder.bindCategory(explores, position, subCategoryListener)
    }

    class SearchExploreHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindCategory(
            list: Map<String, List<Category>?>?,
            position: Int,
            subCategoryListener: SearchSubCategoryListener
        ) {
                itemView.search_explore_category.text = list?.keys?.elementAt(position)
                when (list?.keys?.elementAt(position)?.toLowerCase()) {
                    itemView.context.getString(R.string.sports)
                        .toLowerCase(), itemView.context.getString(
                        R.string.sport
                    ).toLowerCase() ->
                        //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_sport))
                        Glide
                            .with(itemView)
                            .load(R.drawable.category_sport)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(itemView.search_explore_category_iv)
                    itemView.context.getString(R.string.crypto).toLowerCase() ->
                        //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_crypto))
                        Glide
                            .with(itemView)
                            .load(R.drawable.category_crypto)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(itemView.search_explore_category_iv)
                    itemView.context.getString(R.string.influencers).toLowerCase() ->
                        //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_influencers))
                        Glide
                            .with(itemView)
                            .load(R.drawable.category_influencers)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(itemView.search_explore_category_iv)
                    itemView.context.getString(R.string.social_and_meeting).toLowerCase() ->
                        //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_social_and_meeting))
                        Glide
                            .with(itemView)
                            .load(R.drawable.category_social_and_meeting)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(itemView.search_explore_category_iv)
                    itemView.context.getString(R.string.for_kids).toLowerCase() ->
                        //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_for_kids))
                        Glide
                            .with(itemView)
                            .load(R.drawable.category_for_kids)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(itemView.search_explore_category_iv)
                    itemView.context.getString(R.string.esports)
                        .toLowerCase(), itemView.context.getString(
                        R.string.esport
                    ).toLowerCase() ->
                        //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_esport))
                        Glide
                            .with(itemView)
                            .load(R.drawable.category_esport)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(itemView.search_explore_category_iv)
                    itemView.context.getString(R.string.music).toLowerCase() ->
                        //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_music))
                        Glide
                            .with(itemView)
                            .load(R.drawable.category_music)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(itemView.search_explore_category_iv)
                    itemView.context.getString(R.string.holidays)
                        .toLowerCase(), itemView.context.getString(
                        R.string.holiday
                    ).toLowerCase() ->
                        //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_holidays)) *
                        Glide
                            .with(itemView)
                            .load(R.drawable.category_holidays)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(itemView.search_explore_category_iv)
                    itemView.context.getString(R.string.shopping).toLowerCase() ->
                        //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_shopping))
                        Glide
                            .with(itemView)
                            .load(R.drawable.category_shopping)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(itemView.search_explore_category_iv)
                    itemView.context.getString(R.string.films_n_amp_series).replace("\n", "")
                        .toLowerCase(),
                    itemView.context.getString(R.string.behind_the_screen).toLowerCase() ->
                        //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_film))
                        Glide
                            .with(itemView)
                            .load(R.drawable.category_film)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(itemView.search_explore_category_iv)
                    itemView.context.getString(R.string.culture_n_amp_loisirs).replace("\n", "")
                        .toLowerCase(),
                    itemView.context.getString(R.string.culture).toLowerCase() ->
                        //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_culture))
                        Glide
                            .with(itemView)
                            .load(R.drawable.category_culture)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(itemView.search_explore_category_iv)
                    itemView.context.getString(R.string.youtubers), itemView.context.getString(R.string.youtube_channels)
                        .toLowerCase() ->
                        //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_youtuber))
                        Glide
                            .with(itemView)
                            .load(R.drawable.category_youtuber)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(itemView.search_explore_category_iv)
                    itemView.context.getString(R.string.religions)
                        .toLowerCase(), itemView.context.getString(
                        R.string.religion
                    ).toLowerCase() ->
                        //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_religion))
                        Glide
                            .with(itemView)
                            .load(R.drawable.category_religion)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(itemView.search_explore_category_iv)
                    itemView.context.getString(R.string.calendrier_n_interessant).replace("\n", "")
                        .toLowerCase().trim() ->
                        //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_interesting_calendar))
                        Glide
                            .with(itemView)
                            .load(R.drawable.category_interesting_calendar)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(itemView.search_explore_category_iv)
                    itemView.context.getString(R.string.fair_trade), itemView.context.getString(R.string.events_and_fair_trade)
                        .toLowerCase() ->
                        //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_else))
                        Glide
                            .with(itemView)
                            .load(R.drawable.category_else)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(itemView.search_explore_category_iv)
                    else ->
                        Glide
                            .with(itemView)
                            .load(R.drawable.category_else)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(itemView.search_explore_category_iv)
                }

                if (allCategoriesSelected.contains(absoluteAdapterPosition)) {
                    itemView.search_explore_rv.apply {
                        visibility = View.VISIBLE
                        layoutManager = LinearLayoutManager(itemView.context)
                        isNestedScrollingEnabled = false
                        adapter = SearchExploreSubCategoryAdapter(
                            list?.values?.elementAt(position),
                            subCategoryListener
                        )
                    }
                } else {
                    itemView.search_explore_rv.visibility = View.GONE
                }

                itemView.setOnClickListener {
                    if (!allCategoriesSelected.contains(absoluteAdapterPosition)) {
                        allCategoriesSelected.add(absoluteAdapterPosition)
                        itemView.search_explore_rv.apply {
                            visibility = View.VISIBLE
                            layoutManager = LinearLayoutManager(itemView.context)
                            isNestedScrollingEnabled = false
                            adapter = SearchExploreSubCategoryAdapter(
                                list?.values?.elementAt(position),
                                subCategoryListener
                            )
                        }
                    } else {
                        allCategoriesSelected.remove(absoluteAdapterPosition)
                        itemView.search_explore_rv.visibility = View.GONE
                    }
                }
                if (list?.values?.elementAt(position).isNullOrEmpty()) {
                    itemView.search_explore_rv.visibility = View.GONE
                }

        }

    }
}

class SearchExploreSubCategoryAdapter(
    private val subCategory: List<Category>?,
    private val subCategoryListener: SearchExploreCategoryAdapter.SearchSubCategoryListener

): RecyclerView.Adapter<SearchExploreSubCategoryAdapter.ItemSearchExploreHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, itemViewType: Int): ItemSearchExploreHolder =
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