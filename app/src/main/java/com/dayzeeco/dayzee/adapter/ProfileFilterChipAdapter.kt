package com.dayzeeco.dayzee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.listeners.OnRemoveFilterBarListener
import kotlinx.android.synthetic.main.item_pref_filter_chip.view.*
import kotlinx.android.synthetic.main.item_remove_filter_bar.view.*

class ProfileFilterChipAdapter(private val chips: List<String>, val onRemoveFilterBarListener: OnRemoveFilterBarListener): RecyclerView.Adapter<ProfileFilterChipAdapter.ChipViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        return ChipViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_pref_filter_chip, parent, false))
    }

    override fun getItemCount(): Int {
        return chips.size
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
            holder.bindChip(chips[position])
    }

    override fun getItemViewType(position: Int): Int {
            return R.layout.item_pref_filter_chip
    }

    class ChipViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindChip(name: String) {
            itemView.chip_profile_filter.text = name
        }

        fun bindClose(onRemoveFilterBarListener: OnRemoveFilterBarListener) {
            itemView.close_filter_bar.setOnClickListener {
                onRemoveFilterBarListener.onHideFilterBarClicked(null)
            }
        }
    }
}