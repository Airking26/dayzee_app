package com.dayzeeco.dayzee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.model.Category
import kotlinx.android.synthetic.main.adapter_search_explore.view.*
import kotlinx.android.synthetic.main.item_search_explore.view.*

class SearchExploreCategoryAdapter(val explores: Map<String, List<String>>, val subCategoryListener: SearchSubCategoryListener) : RecyclerView.Adapter<SearchExploreCategoryAdapter.SearchExploreHolder>() {

    interface SearchSubCategoryListener {
        fun onSubCategorySelected(category: Category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchExploreHolder =
        SearchExploreHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_search_explore, parent, false))

    override fun getItemCount(): Int = explores.size

    override fun onBindViewHolder(holder: SearchExploreHolder, position: Int) {
        holder.bindCategory(explores, position, subCategoryListener)
    }

    class SearchExploreHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var expanded : Boolean = false

        fun bindCategory(list: Map<String, List<String>>, position: Int,  subCategoryListener: SearchSubCategoryListener) {
            itemView.search_explore_category.text = list.keys.elementAt(position)

            when(list.keys.elementAt(position)){
                itemView.context.getString(R.string.sports), itemView.context.getString(R.string.sport) -> itemView.search_explore_category_iv.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.category_sport))
                itemView.context.getString(R.string.esports), itemView.context.getString(R.string.esport) -> itemView.search_explore_category_iv.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.category_esport))
                else -> itemView.search_explore_category_iv.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.category_else))
            }

            itemView.setOnClickListener {
                if(!expanded) {
                    itemView.search_explore_rv.apply {
                        visibility = View.VISIBLE
                        layoutManager = LinearLayoutManager(itemView.context)
                        isNestedScrollingEnabled = false
                        adapter = SearchExploreSubCategoryAdapter(list.keys.elementAt(position), list.values.elementAt(position), subCategoryListener)
                    }
                    //itemView.search_explore_see_subcategory.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp))
                    expanded = !expanded
                } else {
                    itemView.search_explore_rv.visibility = View.GONE
                    //itemView.search_explore_see_subcategory.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp))
                    expanded = !expanded
                }
            }
            if(list.values.elementAt(position).isNullOrEmpty()) {
                //itemView.search_explore_see_subcategory.visibility = View.GONE
                itemView.search_explore_rv.visibility = View.GONE
            }

        }

    }
}

class SearchExploreSubCategoryAdapter(
    private val category: String,
    private val subCategory: List<String>,
    private val subCategoryListener: SearchExploreCategoryAdapter.SearchSubCategoryListener

): RecyclerView.Adapter<SearchExploreSubCategoryAdapter.ItemSearchExploreHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSearchExploreHolder =
        ItemSearchExploreHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_search_explore, parent, false))

    override fun getItemCount(): Int = subCategory.size

    override fun onBindViewHolder(holder: ItemSearchExploreHolder, position: Int) {
        if (position == subCategory.lastIndex){
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 50
            holder.itemView.layoutParams = params
        } else if(position == 0){
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.topMargin = 25
            holder.itemView.layoutParams = params
        }
        else{
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 0
            holder.itemView.layoutParams = params
        }
        holder.bindSubCategory(category, subCategory[position], subCategoryListener, position)
    }


    class ItemSearchExploreHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindSubCategory(
            category: String,
            subCategory: String,
            subCategoryListener: SearchExploreCategoryAdapter.SearchSubCategoryListener,
            position: Int
        ) {
            itemView.search_explore_subcategory.text = subCategory
            itemView.search_explore_subcategory.setOnClickListener { subCategoryListener.onSubCategorySelected(Category(category, subCategory))
            }
        }


    }
}