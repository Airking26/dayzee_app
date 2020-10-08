package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.UserSuggested
import kotlinx.android.synthetic.main.adapter_suggestion_card.view.*
import kotlinx.android.synthetic.main.item_suggestion.view.*

class SuggestionAdapter(private var suggestions: Map<String, List<UserSuggested>>,
                        private val listener: SuggestionItemListener,
                        private val picClicked: SuggestionItemPicListener):
    RecyclerView.Adapter<SuggestionAdapter.CardViewHolder>() {

    interface SuggestionItemListener{
        fun onItemSelected(follow: Boolean)
    }

    interface SuggestionItemPicListener{
        fun onPicClicked()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder =
        CardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_suggestion_card, parent, false))


    override fun getItemCount(): Int = suggestions.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bindSuggestions(suggestions, position, listener, picClicked)
    }

    class CardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindSuggestions(
            suggestions: Map<String, List<UserSuggested>>,
            position: Int,
            listener: SuggestionItemListener,
            picClicked: SuggestionItemPicListener) {
            itemView.suggestion_title_category.text = suggestions.keys.elementAt(position)

            itemView.suggestion_rv.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                isNestedScrollingEnabled = false
                adapter = SuggestionItemAdapter(suggestions.values.elementAt(position), listener, picClicked)
            }
        }

    }

}

class SuggestionItemAdapter(
    private val suggestions: List<UserSuggested>,
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
            suggestions: List<UserSuggested>,
            position: Int,
            listener: SuggestionAdapter.SuggestionItemListener,
            picClicked: SuggestionAdapter.SuggestionItemPicListener) {
            itemView.suggestion_name_user.text = suggestions[position].name
            Glide
                .with(itemView)
                .load(suggestions[position].picUrl)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .circleCrop()
                .into(itemView.suggestion_imageview)

            itemView.suggestion_imageview.setOnClickListener { picClicked.onPicClicked() }

            itemView.suggestion_follow_btn.setOnClickListener {
                if(itemView.suggestion_follow_btn.drawable.constantState == itemView.resources.getDrawable(R.drawable.ic_add_circle_black_24dp).constantState){
                    itemView.suggestion_follow_btn.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_baseline_remove_circle_outline_24))
                    listener.onItemSelected(true)
                } else {
                    itemView.suggestion_follow_btn.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_add_circle_black_24dp))
                    listener.onItemSelected(false)
                }

            }
        }
    }

}