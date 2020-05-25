package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.SubCategoryCardAdapter.SubCategorySeekBarListener
import com.timenoteco.timenote.model.SubCategory
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.android.synthetic.main.adapter_pref_sub_category_card.view.*
import kotlinx.android.synthetic.main.item_pref_sub_category.view.*

class SubCategoryCardAdapter(private var categories: Map<String, List<SubCategory>>, private val listener: SubCategorySeekBarListener): RecyclerView.Adapter<SubCategoryCardAdapter.CardViewHolder>(){

    interface SubCategorySeekBarListener{
        fun onSeekBarModified(likedLevel: Int, categoryName: String, subCategoryPosition: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val viewItem = LayoutInflater.from(parent.context).inflate(R.layout.adapter_pref_sub_category_card, parent, false)
        return CardViewHolder(viewItem)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holderCard: CardViewHolder, position: Int) {
        holderCard.bindSubCategories(categories, listener, position)
    }

    class CardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindSubCategories(categories: Map<String, List<SubCategory>>, listener: SubCategorySeekBarListener, position: Int) {
            itemView.pref_sub_category_rv.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                isNestedScrollingEnabled = false
            }

            itemView.pref_sub_category_title_category.text = categories.keys.elementAt(position)
            itemView.pref_sub_category_rv.adapter = SubCategoryItemAdapter(categories.values.elementAt(position), listener, categories.keys.elementAt(position))

        }

    }

}

class SubCategoryItemAdapter(private val categories: List<SubCategory>, private val listener: SubCategorySeekBarListener, private val categoryName: String)
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

        fun bindItem(categories: List<SubCategory>, subCategoryPosition: Int, listener: SubCategorySeekBarListener, categoryName: String) {
            itemView.pref_sub_category_title_sub_category.text = categories[subCategoryPosition].name
            itemView.pref_sub_category_seekbar.setProgress(categories[subCategoryPosition].appreciated.toFloat())
            itemView.pref_sub_category_seekbar.onSeekChangeListener = object: OnSeekChangeListener{
                override fun onSeeking(seekParams: SeekParams?) {
                }
                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {}
                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                    listener.onSeekBarModified(seekBar!!.progress, categoryName, subCategoryPosition)
                }

            }
        }

    }


}
