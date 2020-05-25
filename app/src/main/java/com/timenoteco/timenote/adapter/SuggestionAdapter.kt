package com.timenoteco.timenote.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SuggestionAdapter(): RecyclerView.Adapter<SuggestionAdapter.CardViewHolder>() {

    interface SuggestionItemListener{
        fun onItemSelected()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    class CardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

}