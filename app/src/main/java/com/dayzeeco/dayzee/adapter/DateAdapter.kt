package com.dayzeeco.dayzee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dayzeeco.dayzee.R
import com.robertlevonyan.views.chip.OnCloseClickListener
import kotlinx.android.synthetic.main.item_pref_sub_category_chip.view.*
import java.text.SimpleDateFormat
import java.util.*

class DateAdapter(private val dates: MutableList<String>, private val dateCloseListener: DateCloseListener): RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    interface DateCloseListener{
        fun onClose(date: String)
    }

    class DateViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"
        private val DATE_FORMAT_DAY_AND_TIME = "EEE, d MMM yyyy hh:mm aaa"

        fun bindDate(date: String, listener: DateCloseListener) {
            itemView.chip.text = SimpleDateFormat(DATE_FORMAT_DAY_AND_TIME, Locale.getDefault()).format(SimpleDateFormat(ISO).parse(date).time)
            itemView.chip.onCloseClickListener = OnCloseClickListener {
                listener.onClose(date)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder = DateViewHolder(LayoutInflater.from(parent.context).inflate(
        R.layout.item_pref_sub_category_chip, parent, false))

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bindDate(dates[position], dateCloseListener)
    }

    override fun getItemCount(): Int = dates.size
}