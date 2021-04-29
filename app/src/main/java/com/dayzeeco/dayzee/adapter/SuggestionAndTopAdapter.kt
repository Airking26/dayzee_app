package com.dayzeeco.dayzee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.bytesEqualTo
import com.dayzeeco.dayzee.common.pixelsEqualTo
import com.dayzeeco.dayzee.model.SubCategoryRated
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.viewModel.SearchViewModel
import kotlinx.android.synthetic.main.adapter_suggestion_card.view.*
import kotlinx.android.synthetic.main.item_category.view.*
import kotlinx.android.synthetic.main.item_suggestion.view.*
import kotlinx.coroutines.launch

class SuggestionAdapter(
    private var suggestions: Map<SubCategoryRated, List<UserInfoDTO>?>,
    private val listener: SuggestionAdapter.SuggestionItemListener,
    private val picClicked: SuggestionItemPicListener,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val searchViewModel: SearchViewModel,
    private val tokenId: String?
):
    RecyclerView.Adapter<SuggestionAdapter.CardViewHolder>() {

    interface SuggestionItemListener{
        fun onItemSelected(follow: Boolean, userInfoDTO: UserInfoDTO)
    }

    interface SuggestionItemPicListener{
        fun onPicClicked(userInfoDTO: UserInfoDTO)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder =
        CardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_suggestion_card, parent, false))


    override fun getItemCount(): Int =
        suggestions.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bindSuggestions(suggestions, position, listener, picClicked, lifecycleScope, searchViewModel, tokenId)
    }

    class CardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindSuggestions(
            suggestions: Map<SubCategoryRated, List<UserInfoDTO>?>,
            position: Int,
            listener: SuggestionAdapter.SuggestionItemListener,
            picClicked: SuggestionItemPicListener,
            lifecycleScope: LifecycleCoroutineScope,
            searchViewModel: SearchViewModel,
            tokenId: String?
        ) {

            itemView.pref_sub_category_title_category.text = suggestions.keys.elementAt(position).category.subcategory

            when(suggestions.keys.elementAt(position).category.category.toLowerCase()){
                itemView.context.getString(R.string.sports)
                    .toLowerCase(), itemView.context.getString(
                    R.string.sport
                ).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_sport.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.crypto).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_crypto.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.influencers).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_influencers.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.social_and_meeting).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_social_and_meeting.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.for_kids).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_for_kids.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.esports)
                    .toLowerCase(), itemView.context.getString(
                    R.string.esport
                ).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_esport.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.music).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_music.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.holidays)
                    .toLowerCase(), itemView.context.getString(
                    R.string.holiday
                ).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_holidays.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.shopping).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_shopping.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.films_n_amp_series).replace("\n", "")
                    .toLowerCase(),
                itemView.context.getString(R.string.behind_the_screen).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_film.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.culture_n_amp_loisirs).replace("\n", "")
                    .toLowerCase(),
                itemView.context.getString(R.string.culture).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_culture.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.youtubers), itemView.context.getString(R.string.youtube_channels)
                    .toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_youtuber.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.religions)
                    .toLowerCase(), itemView.context.getString(
                    R.string.religion
                ).toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_religion.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.calendrier_n_interessant).replace("\n", "")
                    .toLowerCase().trim() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_interesting_calendar.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.fair_trade), itemView.context.getString(R.string.events_and_fair_trade)
                    .toLowerCase() ->
                    Glide
                        .with(itemView)
                        .load("https://timenote-dev-images.s3.eu-west-3.amazonaws.com/timenote/category_else.jpg")
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                else -> itemView.suggestions_iv.setImageDrawable(
                    ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.audiance
                    )
                )
            }

            val subadapter = SuggestionItemAdapter(suggestions.values.elementAt(position), listener, picClicked)

            itemView.suggestion_rv.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                isNestedScrollingEnabled = false
                adapter = subadapter
            }


        }

    }

}

class SuggestionItemAdapter(
    private val suggestions: List<UserInfoDTO>?,
    private val listener: SuggestionAdapter.SuggestionItemListener,
    private val picClicked: SuggestionAdapter.SuggestionItemPicListener
):
    RecyclerView.Adapter<SuggestionItemAdapter.ItemViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_suggestion, parent, false))

    override fun getItemCount(): Int = suggestions?.size!!

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItemSuggestion(suggestions, position, listener, picClicked)
    }

    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindItemSuggestion(
            suggestions: List<UserInfoDTO>?,
            position: Int,
            listener: SuggestionAdapter.SuggestionItemListener,
            picClicked: SuggestionAdapter.SuggestionItemPicListener
        ) {
            itemView.suggestion_name_user.text = suggestions?.get(position)?.userName
            Glide
                .with(itemView)
                .load(suggestions?.get(position)?.picture)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .circleCrop()
                .into(itemView.suggestion_imageview)


            itemView.suggestion_imageview.setOnClickListener { picClicked.onPicClicked(suggestions?.get(position)!!) }

            itemView.suggestion_follow_btn.setOnClickListener {
                if(itemView.suggestion_follow_btn.drawable.bytesEqualTo(itemView.resources.getDrawable(R.drawable.ic_add_circle_black_24dp)) && itemView.suggestion_follow_btn.drawable.pixelsEqualTo(itemView.resources.getDrawable(R.drawable.ic_add_circle_black_24dp))){
                    itemView.suggestion_follow_btn.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_baseline_remove_circle_outline_24))
                    listener.onItemSelected(true, suggestions?.get(position)!!)
                } else {
                    itemView.suggestion_follow_btn.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_add_circle_black_24dp))
                    listener.onItemSelected(false, suggestions?.get(position)!!)
                }

            }
        }
    }

}