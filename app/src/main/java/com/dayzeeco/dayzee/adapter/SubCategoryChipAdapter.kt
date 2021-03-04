package com.dayzeeco.dayzee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.robertlevonyan.views.chip.OnCloseClickListener
import com.dayzeeco.dayzee.R
import kotlinx.android.synthetic.main.adapter_pref_sub_category_card.view.*
import kotlinx.android.synthetic.main.item_category.view.*
import kotlinx.android.synthetic.main.item_pref_sub_category_chip.view.*
import kotlinx.android.synthetic.main.item_subcategory_category_removal.view.*

class SubCategoryChipAdapter(private val chips: MutableList<String>, private val listener: SubCategoryChipListener): RecyclerView.Adapter<SubCategoryChipAdapter.ChipViewHolder>() {

    interface SubCategoryChipListener{
        fun onCloseChip(index: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subcategory_category_removal, parent, false)
        return ChipViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chips.size
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        holder.bindChip(chips[position], listener, position)
    }

    class ChipViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindChip(name: String, listener: SubCategoryChipListener, position: Int) {
            itemView.subcategory_category_tv.text = name
            when(name){
                itemView.context.getString(R.string.sports), itemView.context.getString(R.string.sport) -> itemView.subcategory_category_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_sport))
                itemView.context.getString(R.string.esports), itemView.context.getString(R.string.esport) -> itemView.subcategory_category_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_esport))
                itemView.context.getString(R.string.holidays), itemView.context.getString(R.string.holiday) -> itemView.subcategory_category_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_holidays))
                itemView.context.getString(R.string.shopping) -> itemView.subcategory_category_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_shopping))
                itemView.context.getString(R.string.films_n_amp_series) -> itemView.subcategory_category_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_film))
                itemView.context.getString(R.string.culture_n_amp_loisirs) -> itemView.subcategory_category_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_culture))
                itemView.context.getString(R.string.youtubers) -> itemView.subcategory_category_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_youtuber))
                itemView.context.getString(R.string.religions) -> itemView.subcategory_category_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_religion))
                itemView.context.getString(R.string.calendrier_n_interessant) -> itemView.subcategory_category_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_interesting_calendar))
                itemView.context.getString(R.string.fair_trade) -> itemView.subcategory_category_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_else))
                else -> itemView.subcategory_category_iv.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.audiance))
            }

            itemView.subcategory_category_btn.setOnClickListener {
                listener.onCloseChip(position)
            }
        }
    }
}