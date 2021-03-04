package com.dayzeeco.dayzee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.SubCategoryCardAdapter.SubCategorySeekBarListener
import com.dayzeeco.dayzee.model.SubCategoryRated
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.android.synthetic.main.adapter_pref_sub_category_card.view.*
import kotlinx.android.synthetic.main.adapter_pref_sub_category_card.view.pref_sub_category_title_category
import kotlinx.android.synthetic.main.adapter_suggestion_card.view.*
import kotlinx.android.synthetic.main.item_category.view.*
import kotlinx.android.synthetic.main.item_pref_sub_category.view.*

class SubCategoryCardAdapter(private var categories: MutableMap<String, MutableList<SubCategoryRated>>, private val listener: SubCategorySeekBarListener): RecyclerView.Adapter<SubCategoryCardAdapter.CardViewHolder>(){

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

        fun bindSubCategories(categories: MutableMap<String, MutableList<SubCategoryRated>>, listener: SubCategorySeekBarListener, position: Int) {
            itemView.pref_sub_category_rv.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                isNestedScrollingEnabled = false
            }

            itemView.pref_sub_category_title_category.text = categories.keys.elementAt(position)
            itemView.pref_sub_category_rv.adapter = SubCategoryItemAdapter(categories.values.elementAt(position), listener, categories.keys.elementAt(position))
            when(categories.keys.elementAt(position)){
                itemView.context.getString(R.string.sports), itemView.context.getString(R.string.sport) -> itemView.subcategory_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_sport))
                itemView.context.getString(R.string.esports), itemView.context.getString(R.string.esport) -> itemView.subcategory_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_esport))
                itemView.context.getString(R.string.holidays), itemView.context.getString(R.string.holiday) -> itemView.subcategory_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_holidays))
                itemView.context.getString(R.string.shopping) -> itemView.subcategory_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_shopping))
                itemView.context.getString(R.string.films_n_amp_series) -> itemView.subcategory_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_film))
                itemView.context.getString(R.string.culture_n_amp_loisirs) -> itemView.subcategory_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_culture))
                itemView.context.getString(R.string.youtubers) -> itemView.subcategory_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_youtuber))
                itemView.context.getString(R.string.religions) -> itemView.subcategory_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_religion))
                itemView.context.getString(R.string.calendrier_n_interessant) -> itemView.subcategory_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_interesting_calendar))
                itemView.context.getString(R.string.fair_trade) -> itemView.subcategory_iv.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.category_else))
                else -> itemView.subcategory_iv.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.audiance))
            }

        }

    }

}

class SubCategoryItemAdapter(private val categories: MutableList<SubCategoryRated>, private val listener: SubCategorySeekBarListener, private val categoryName: String)
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

        fun bindItem(categories: MutableList<SubCategoryRated>, subCategoryPosition: Int, listener: SubCategorySeekBarListener, categoryName: String) {
            itemView.pref_sub_category_title_sub_category.text = categories[subCategoryPosition].category.subcategory
            itemView.pref_sub_category_seekbar.setProgress(categories[subCategoryPosition].rating.toFloat())
            itemView.pref_sub_category_seekbar.onSeekChangeListener = object: OnSeekChangeListener{
                override fun onSeeking(seekParams: SeekParams?) {}
                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {}
                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                    listener.onSeekBarModified(seekBar!!.progress, categories[subCategoryPosition].category.subcategory)
                }

            }
        }

    }


}
