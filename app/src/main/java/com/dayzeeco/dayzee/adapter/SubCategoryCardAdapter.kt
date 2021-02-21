package com.dayzeeco.dayzee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.SubCategoryCardAdapter.SubCategorySeekBarListener
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.android.synthetic.main.adapter_pref_sub_category_card.view.*
import kotlinx.android.synthetic.main.item_pref_sub_category.view.*

class SubCategoryCardAdapter(private var categories: MutableMap<String, MutableList<String>>, private val listener: SubCategorySeekBarListener): RecyclerView.Adapter<SubCategoryCardAdapter.CardViewHolder>(){

    interface SubCategorySeekBarListener{
        fun onSeekBarModified(likedLevel: Int, subCategoryName: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder =
        CardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_pref_sub_category_card, parent, false))

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holderCard: CardViewHolder, position: Int) {
        holderCard.bindSubCategories(categories, listener, position)
    }

    class CardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindSubCategories(categories: MutableMap<String, MutableList<String>>, listener: SubCategorySeekBarListener, position: Int) {
            itemView.pref_sub_category_rv.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                isNestedScrollingEnabled = false
            }

            itemView.pref_sub_category_title_category.text = categories.keys.elementAt(position)
            itemView.pref_sub_category_rv.adapter = SubCategoryItemAdapter(categories.values.elementAt(position), listener, categories.keys.elementAt(position))

        }

    }

}

class SubCategoryItemAdapter(private val categories: MutableList<String>, private val listener: SubCategorySeekBarListener, private val categoryName: String)
    : RecyclerView.Adapter<SubCategoryItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val viewItem = LayoutInflater.from(parent.context).inflate(R.layout.item_pref_sub_category, parent, false)
        return ItemViewHolder(viewItem)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holderItem: ItemViewHolder, subCategoryPosition: Int) {
        holderItem.bindItem(categories, subCategoryPosition, listener, categoryName)
    }


    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindItem(categories: MutableList<String>, subCategoryPosition: Int, listener: SubCategorySeekBarListener, categoryName: String) {
            itemView.pref_sub_category_title_sub_category.text = categories[subCategoryPosition]
            itemView.pref_sub_category_seekbar.onSeekChangeListener = object: OnSeekChangeListener{
                override fun onSeeking(seekParams: SeekParams?) {}
                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {}
                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                    listener.onSeekBarModified(seekBar!!.progress, categories[subCategoryPosition])
                }

            }
        }

    }


}
