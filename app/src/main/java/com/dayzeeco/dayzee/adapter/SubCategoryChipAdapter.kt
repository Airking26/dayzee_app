package com.dayzeeco.dayzee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.robertlevonyan.views.chip.OnCloseClickListener
import com.dayzeeco.dayzee.R
import kotlinx.android.synthetic.main.item_pref_sub_category_chip.view.*

class SubCategoryChipAdapter(private val chips: MutableList<String>, private val listener: SubCategoryChipListener): RecyclerView.Adapter<SubCategoryChipAdapter.ChipViewHolder>() {

    interface SubCategoryChipListener{
        fun onCloseChip(index: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pref_sub_category_chip, parent, false)
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
            itemView.chip.text = name
            itemView.chip.onCloseClickListener = OnCloseClickListener {
                listener.onCloseChip(position)
            }
        }
    }
}