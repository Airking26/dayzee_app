package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.UserSuggested
import kotlinx.android.synthetic.main.adapter_suggestion_card.view.*
import kotlinx.android.synthetic.main.item_suggestion.view.*

class SuggestionAdapter(private var suggestions: Map<String, List<UserSuggested>>, private val listener: SuggestionItemListener): RecyclerView.Adapter<SuggestionAdapter.CardViewHolder>() {

    interface SuggestionItemListener{
        fun onItemSelected()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder =
        CardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_suggestion_card, parent, false))


    override fun getItemCount(): Int = suggestions.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bindSuggestions(suggestions, position, listener)
    }

    class CardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindSuggestions(suggestions: Map<String, List<UserSuggested>>, position: Int, listener: SuggestionItemListener) {
            itemView.suggestion_rv.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                isNestedScrollingEnabled = false
            }
            itemView.suggestion_title_category.text = suggestions.keys.elementAt(position)
            itemView.suggestion_rv.adapter = SuggestionItemAdapter(suggestions.values.elementAt(position), listener)
        }

    }

}

class SuggestionItemAdapter(private val suggestions: List<UserSuggested>, private val listener: SuggestionAdapter.SuggestionItemListener):
    RecyclerView.Adapter<SuggestionItemAdapter.ItemViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionItemAdapter.ItemViewHolder =
        ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_suggestion, parent, false))

    override fun getItemCount(): Int = suggestions.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItemSuggestion(suggestions, position, listener)
    }

    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindItemSuggestion(suggestions: List<UserSuggested>, position: Int, listener: SuggestionAdapter.SuggestionItemListener) {
            itemView.suggestion_name_user.text = suggestions[position].name
            Glide
                .with(itemView)
                .load(suggestions[position].picUrl)
                .circleCrop()
                .into(itemView.suggestion_imageview)

            itemView.suggestion_follow_btn.setOnClickListener {
                listener.onItemSelected()
            }
        }
    }

}