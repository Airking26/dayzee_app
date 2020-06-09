package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.adapter_search_explore.view.*
import kotlinx.android.synthetic.main.item_search_explore.view.*

class SearchExploreCategoryAdapter(val explores: Map<String, List<String>>, val categoryListener: SearchCategoryListener, val subCategoryListener: SearchSubCategoryListener) : RecyclerView.Adapter<SearchExploreCategoryAdapter.SearchExploreHolder>() {

    interface SearchCategoryListener {
        fun onCategorySelected()
    }

    interface SearchSubCategoryListener {
        fun onSubCategorySelected()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchExploreHolder =
        SearchExploreHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_search_explore, parent, false))

    override fun getItemCount(): Int = explores.size

    override fun onBindViewHolder(holder: SearchExploreHolder, position: Int) {
        holder.bindCategory(explores, position,categoryListener, subCategoryListener)
    }

    class SearchExploreHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var expanded : Boolean = false

        fun bindCategory(list: Map<String, List<String>>, position: Int, categoryListener: SearchCategoryListener, subCategoryListener: SearchSubCategoryListener) {
            itemView.search_explore_category.text = list.keys.elementAt(position)
            itemView.search_explore_category.setOnClickListener { categoryListener.onCategorySelected() }
            if(list.values.elementAt(position).isNullOrEmpty()) {
                itemView.search_explore_see_subcategory.visibility = View.GONE
                itemView.search_explore_rv.visibility = View.GONE
            }
            else {
                itemView.search_explore_see_subcategory.setOnClickListener {
                    if(!expanded) {
                        itemView.search_explore_rv.apply {
                            visibility = View.VISIBLE
                            layoutManager = LinearLayoutManager(itemView.context)
                            isNestedScrollingEnabled = false
                            adapter = SearchExploreSubCategoryAdapter(list.values.elementAt(position), subCategoryListener)
                        }
                        itemView.search_explore_see_subcategory.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp))
                        expanded = !expanded
                    } else {
                        itemView.search_explore_rv.visibility = View.GONE
                        itemView.search_explore_see_subcategory.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp))
                        expanded = !expanded
                    }
                }
            }

        }

    }
}

class SearchExploreSubCategoryAdapter(

    private val subCategory: List<String>,
    private val subCategoryListener: SearchExploreCategoryAdapter.SearchSubCategoryListener

): RecyclerView.Adapter<SearchExploreSubCategoryAdapter.ItemSearchExploreHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSearchExploreHolder =
        ItemSearchExploreHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_search_explore, parent, false))

    override fun getItemCount(): Int = subCategory.size

    override fun onBindViewHolder(holder: ItemSearchExploreHolder, position: Int) {
        holder.bindSubCategory(subCategory[position], subCategoryListener)
    }


    class ItemSearchExploreHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindSubCategory(
            subCategory: String,
            subCategoryListener: SearchExploreCategoryAdapter.SearchSubCategoryListener
        ) {
            itemView.search_explore_subcategory.text = subCategory
            itemView.search_explore_subcategory.setOnClickListener { subCategoryListener.onSubCategorySelected() }
        }

    }
}