package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.robertlevonyan.views.chip.Chip
import com.robertlevonyan.views.chip.OnCloseClickListener
import com.robertlevonyan.views.chip.OnSelectClickListener
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.item_pref_filter_chip.view.*
import kotlinx.android.synthetic.main.item_pref_sub_category_chip.view.*

class ProfileFilterChipAdapter(private val chips: MutableList<String>): RecyclerView.Adapter<ProfileFilterChipAdapter.ChipViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pref_filter_chip, parent, false)
        return ChipViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chips.size
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        holder.bindChip(chips[position])
    }

    class ChipViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindChip(name: String) {
            itemView.chip_profile_filter.text = name
            itemView.chip_profile_filter.setOnClickListener {
                if((it as Chip).chipBackgroundColor == itemView.context.resources.getColor(R.color.white)) {
                    it.apply {
                        chipBackgroundColor = itemView.context.resources.getColor(R.color.colorPrimary)
                        chipTextColor = itemView.context.resources.getColor(R.color.white)
                    }
                } else {
                    it.apply {
                        chipBackgroundColor = itemView.context.resources.getColor(R.color.white)
                        chipTextColor = itemView.context.resources.getColor(R.color.colorPrimary)
                    }
                }

            }
        }
    }
}