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
import kotlinx.android.synthetic.main.adapter_search_explore.view.*
import kotlinx.android.synthetic.main.adapter_suggestion_card.view.*
import kotlinx.android.synthetic.main.adapter_suggestion_card.view.suggestions_iv
import kotlinx.android.synthetic.main.footer_loading.view.*
import kotlinx.android.synthetic.main.item_category.view.*
import kotlinx.android.synthetic.main.item_suggestion.view.*
import kotlinx.coroutines.launch

class SuggestionAdapter(
    private var suggestions: Map<SubCategoryRated, List<UserInfoDTO>?>,
    private val listener: SuggestionItemListener,
    private val picClicked: SuggestionItemPicListener):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TYPE_FOOTER = 1
    val TYPE_CARD = 0

    var isStillLoading = false

    fun setLoadingFooter(isStillLoading: Boolean){
        this.isStillLoading = isStillLoading
    }

    interface SuggestionItemListener{
        fun onItemSelected(follow: Boolean, userInfoDTO: UserInfoDTO)
    }

    interface SuggestionItemPicListener{
        fun onPicClicked(userInfoDTO: UserInfoDTO)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_CARD -> CardViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_suggestion_card, parent, false)
            )
            else ->
                FooterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.footer_loading, parent, false))
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (suggestions.isNotEmpty() && position == suggestions.size){
            TYPE_FOOTER
        } else
            TYPE_CARD
    }

    override fun getItemCount(): Int {
        return if(suggestions.isEmpty())
            suggestions.size
        else
            suggestions.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is CardViewHolder) holder.bindSuggestions(suggestions, position, listener, picClicked)
        else if(holder is FooterViewHolder) holder.bindFooter(this.isStillLoading)
    }

    class CardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindSuggestions(
            suggestions: Map<SubCategoryRated, List<UserInfoDTO>?>,
            position: Int,
            listener: SuggestionItemListener,
            picClicked: SuggestionItemPicListener) {

            itemView.pref_sub_category_title_category.text = suggestions.keys.elementAt(position).category.subcategory

            when(suggestions.keys.elementAt(position).category.category.toLowerCase()){
                itemView.context.getString(R.string.sports).toLowerCase(), itemView.context.getString(
                    R.string.sport
                ).toLowerCase() ->
                    //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_sport))
                    Glide
                        .with(itemView)
                        .load(R.drawable.category_sport)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.crypto).toLowerCase() ->
                    //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_crypto))
                    Glide
                        .with(itemView)
                        .load(R.drawable.category_crypto)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.influencers).toLowerCase() ->
                    //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_influencers))
                    Glide
                        .with(itemView)
                        .load(R.drawable.category_influencers)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
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
                        .into(itemView.suggestions_iv)
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
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.music).toLowerCase() ->
                    //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_music))
                    Glide
                        .with(itemView)
                        .load(R.drawable.category_music)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
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
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.shopping).toLowerCase() ->
                    //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_shopping))
                    Glide
                        .with(itemView)
                        .load(R.drawable.category_shopping)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.films_n_amp_series).replace("\n", "")
                    .toLowerCase(),
                itemView.context.getString(R.string.behind_the_screen).toLowerCase() ->
                    //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_film))
                    Glide
                        .with(itemView)
                        .load(R.drawable.category_film)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.culture_n_amp_loisirs).replace("\n", "")
                    .toLowerCase(),
                itemView.context.getString(R.string.culture).toLowerCase() ->
                    //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_culture))
                    Glide
                        .with(itemView)
                        .load(R.drawable.category_culture)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.youtubers), itemView.context.getString(R.string.youtube_channels)
                    .toLowerCase() ->
                    //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_youtuber))
                    Glide
                        .with(itemView)
                        .load(R.drawable.category_youtuber)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
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
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.calendrier_n_interessant).replace("\n", "")
                    .toLowerCase().trim() ->
                    //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_interesting_calendar))
                    Glide
                        .with(itemView)
                        .load(R.drawable.category_interesting_calendar)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                itemView.context.getString(R.string.fair_trade), itemView.context.getString(R.string.events_and_fair_trade)
                    .toLowerCase() ->
                    //itemView.category_iv.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.category_else))
                    Glide
                        .with(itemView)
                        .load(R.drawable.category_else)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
                else ->
                    Glide
                        .with(itemView)
                        .load(R.drawable.category_else)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemView.suggestions_iv)
            }

            val subadapter = SuggestionItemAdapter(suggestions.values.elementAt(position), listener, picClicked)

            itemView.suggestion_rv.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                isNestedScrollingEnabled = false
                adapter = subadapter
            }


        }

    }
    class FooterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindFooter(stillLoading: Boolean) {
            if(!stillLoading) itemView.footer_rv.visibility = View.GONE
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