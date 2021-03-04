package com.dayzeeco.dayzee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.model.Category
import kotlinx.android.synthetic.main.adapter_suggestion_card.view.*
import kotlinx.android.synthetic.main.item_category.view.*

class CategoryAdapter(private val categories: List<Category>?, private val categoriesAlreadyChosen : List<Category>?) : BaseAdapter() {

    override fun getCount(): Int = categories?.size ?: 0

    override fun getItem(position: Int): Any = Any()
    override fun getItemId(position: Int): Long = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val category = categories?.get(position)
        val view : View = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.item_category, parent, false)

        categoriesAlreadyChosen?.forEach {
            if(it.category == category?.category)
                view.category_btn.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_checktwo))
        }

        view.category_tv.text = category?.category
        when(category?.category){
            view.context.getString(R.string.sports), view.context.getString(R.string.sport) -> view.category_iv.setImageDrawable(
                ContextCompat.getDrawable(view.context, R.drawable.category_sport))
            view.context.getString(R.string.esports), view.context.getString(R.string.esport) -> view.category_iv.setImageDrawable(
                ContextCompat.getDrawable(view.context, R.drawable.category_esport))
            view.context.getString(R.string.holidays), view.context.getString(R.string.holiday) -> view.category_iv.setImageDrawable(
                ContextCompat.getDrawable(view.context, R.drawable.category_holidays))
            view.context.getString(R.string.shopping) -> view.category_iv.setImageDrawable(
                ContextCompat.getDrawable(view.context, R.drawable.category_shopping))
            view.context.getString(R.string.films_n_amp_series) -> view.category_iv.setImageDrawable(
                ContextCompat.getDrawable(view.context, R.drawable.category_film))
            view.context.getString(R.string.culture_n_amp_loisirs) -> view.category_iv.setImageDrawable(
                ContextCompat.getDrawable(view.context, R.drawable.category_culture))
            view.context.getString(R.string.youtubers) -> view.category_iv.setImageDrawable(
                ContextCompat.getDrawable(view.context, R.drawable.category_youtuber))
            view.context.getString(R.string.religions) -> view.category_iv.setImageDrawable(
                ContextCompat.getDrawable(view.context, R.drawable.category_religion))
            view.context.getString(R.string.calendrier_n_interessant) -> view.category_iv.setImageDrawable(
                ContextCompat.getDrawable(view.context, R.drawable.category_interesting_calendar))
            view.context.getString(R.string.fair_trade) -> view.category_iv.setImageDrawable(
                ContextCompat.getDrawable(view.context, R.drawable.category_else))
            else -> view.category_iv.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.audiance))
        }

        return view
    }
}