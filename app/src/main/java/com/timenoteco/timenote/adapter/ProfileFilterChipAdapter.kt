package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.robertlevonyan.views.chip.Chip
import com.timenoteco.timenote.R
import com.timenoteco.timenote.listeners.OnRemoveFilterBarListener
import kotlinx.android.synthetic.main.item_pref_filter_chip.view.*
import kotlinx.android.synthetic.main.item_remove_filter_bar.view.*

class ProfileFilterChipAdapter(private val chips: MutableList<String>, val onRemoveFilterBarListener: OnRemoveFilterBarListener): RecyclerView.Adapter<ProfileFilterChipAdapter.ChipViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        val view = if(viewType == R.layout.item_pref_filter_chip) {
            LayoutInflater.from(parent.context).inflate(R.layout.item_pref_filter_chip, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.item_remove_filter_bar, parent, false)
        }
        return ChipViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chips.size
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        if(position == chips.size -1) holder.bindClose(onRemoveFilterBarListener)
        else holder.bindChip(chips[position])
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == chips.size -1) R.layout.item_remove_filter_bar else R.layout.item_pref_filter_chip
    }

    class ChipViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindChip(name: String) {
            itemView.chip_profile_filter.text = name
            itemView.chip_profile_filter.setOnClickListener {
                if((it as Chip).chipTextColor == itemView.context.resources.getColor(R.color.colorText)) {
                    it.apply {
                        chipBackgroundColor = itemView.context.resources.getColor(R.color.white)
                        chipTextColor = itemView.context.resources.getColor(R.color.colorAccentCustom)
                    }
                } else {
                    it.apply {
                        chipBackgroundColor = itemView.context.resources.getColor(R.color.white)
                        chipTextColor = itemView.context.resources.getColor(R.color.colorText)
                    }
                }

            }
        }

        fun bindClose(onRemoveFilterBarListener: OnRemoveFilterBarListener) {
            itemView.close_filter_bar.setOnClickListener { onRemoveFilterBarListener.onHideFilterBarClicked(null) }
        }
    }
}