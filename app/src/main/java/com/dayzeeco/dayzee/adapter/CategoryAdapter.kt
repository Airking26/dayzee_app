package com.dayzeeco.dayzee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.model.Category
import kotlinx.android.synthetic.main.item_category.view.*

class CategoryAdapter(private val categories: List<Category>?, private val categoriesAlreadyChosen : List<Category>?) : BaseAdapter() {

    override fun getCount(): Int = categories?.size ?: 0

    override fun getItem(position: Int): Any = Any()
    override fun getItemId(position: Int): Long = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val category = categories?.get(position)
        val view : View = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.item_category, parent, false)

        categoriesAlreadyChosen?.forEach {
            if(it.category == view.category_tv.text) view.category_btn.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_checktwo))
        }

        view.category_tv.text = category?.category
        when(category?.category){
            view.context.getString(R.string.sports), view.context.getString(R.string.sport) -> view.category_iv.setImageDrawable(
                ContextCompat.getDrawable(view.context, R.drawable.category_sport))
            view.context.getString(R.string.esports), view.context.getString(R.string.esport) -> view.category_iv.setImageDrawable(
                ContextCompat.getDrawable(view.context, R.drawable.category_esport))
            else -> view.category_iv.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.category_else))
        }

        return view
    }
}