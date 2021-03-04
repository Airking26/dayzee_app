package com.dayzeeco.dayzee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.bytesEqualTo
import com.dayzeeco.dayzee.common.pixelsEqualTo
import com.dayzeeco.dayzee.model.SubCategoryRated
import com.dayzeeco.dayzee.model.UserInfoDTO
import kotlinx.android.synthetic.main.adapter_suggestion_card.view.*
import kotlinx.android.synthetic.main.item_subcategory_category_removal.view.*
import kotlinx.android.synthetic.main.item_suggestion.view.*

class SuggestionAdapter(private var suggestions: Map<SubCategoryRated, List<UserInfoDTO>>,
                        private val listener: SuggestionItemListener,
                        private val picClicked: SuggestionItemPicListener
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


    override fun getItemCount(): Int = suggestions.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bindSuggestions(suggestions, position, listener, picClicked)
    }

    class CardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindSuggestions(
            suggestions: Map<SubCategoryRated, List<UserInfoDTO>>,
            position: Int,
            listener: SuggestionItemListener,
            picClicked: SuggestionItemPicListener
        ) {
            itemView.pref_sub_category_title_category.text = suggestions.keys.elementAt(position).category.subcategory

            when(suggestions.keys.elementAt(position).category.category){
                itemView.context.getString(R.string.sports), itemView.context.getString(R.string.sport) -> itemView.suggestions_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_sport))
                itemView.context.getString(R.string.esports), itemView.context.getString(R.string.esport) -> itemView.suggestions_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_esport))
                itemView.context.getString(R.string.holidays), itemView.context.getString(R.string.holiday) -> itemView.suggestions_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_holidays))
                itemView.context.getString(R.string.shopping) -> itemView.suggestions_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_shopping))
                itemView.context.getString(R.string.films_n_amp_series) -> itemView.suggestions_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_film))
                itemView.context.getString(R.string.culture_n_amp_loisirs) -> itemView.suggestions_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_culture))
                itemView.context.getString(R.string.youtubers) -> itemView.suggestions_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_youtuber))
                itemView.context.getString(R.string.religions) -> itemView.suggestions_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_religion))
                itemView.context.getString(R.string.calendrier_n_interessant) -> itemView.suggestions_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_interesting_calendar))
                itemView.context.getString(R.string.fair_trade) -> itemView.suggestions_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_else))
                else -> itemView.suggestions_iv.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.audiance))
            }

            itemView.suggestion_rv.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                isNestedScrollingEnabled = false
                adapter = SuggestionItemAdapter(suggestions.values.elementAt(position), listener, picClicked)
            }
        }

    }

}

class SuggestionItemAdapter(
    private val suggestions: List<UserInfoDTO>,
    private val listener: SuggestionAdapter.SuggestionItemListener,
    private val picClicked: SuggestionAdapter.SuggestionItemPicListener
):
    RecyclerView.Adapter<SuggestionItemAdapter.ItemViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_suggestion, parent, false))

    override fun getItemCount(): Int = suggestions.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItemSuggestion(suggestions, position, listener, picClicked)
    }

    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindItemSuggestion(
            suggestions: List<UserInfoDTO>,
            position: Int,
            listener: SuggestionAdapter.SuggestionItemListener,
            picClicked: SuggestionAdapter.SuggestionItemPicListener
        ) {
            itemView.suggestion_name_user.text = suggestions[position].userName
            Glide
                .with(itemView)
                .load(suggestions[position].picture)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .circleCrop()
                .into(itemView.suggestion_imageview)


            itemView.suggestion_imageview.setOnClickListener { picClicked.onPicClicked(suggestions[position]) }

            itemView.suggestion_follow_btn.setOnClickListener {
                if(itemView.suggestion_follow_btn.drawable.bytesEqualTo(itemView.resources.getDrawable(R.drawable.ic_add_circle_black_24dp)) && itemView.suggestion_follow_btn.drawable.pixelsEqualTo(itemView.resources.getDrawable(R.drawable.ic_add_circle_black_24dp))){
                    itemView.suggestion_follow_btn.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_baseline_remove_circle_outline_24))
                    listener.onItemSelected(true, suggestions[position])
                } else {
                    itemView.suggestion_follow_btn.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_add_circle_black_24dp))
                    listener.onItemSelected(false, suggestions[position])
                }

            }
        }
    }

}