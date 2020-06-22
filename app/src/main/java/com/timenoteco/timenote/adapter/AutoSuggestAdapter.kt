package com.timenoteco.timenote.adapter

import android.content.Context
import android.location.Address
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.item_search_place.view.*
import java.util.*

class AutoSuggestAdapter(context: Context, resource: Int) : ArrayAdapter<Address>(context, resource), Filterable {
    private val mlistData: MutableList<Address> = mutableListOf()

    fun setData(list: List<Address>?) {
        mlistData.clear()
        mlistData.addAll(list!!)
    }

    override fun getCount(): Int {
        return mlistData.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val place = mlistData[position].getAddressLine(0)
        val view = LayoutInflater.from(context).inflate(R.layout.item_search_place, null)
        val tv = view.item_search_place_tv
        tv.text = place
        return  view
    }

    override fun getItem(position: Int): Address {
        return mlistData[position]
    }

    /**
     * Used to Return the full object directly from adapter.
     *
     * @param position
     * @return
     */
    fun getObject(position: Int): Address {
        return mlistData[position]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                filterResults.values = mlistData
                filterResults.count = mlistData.size
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                if (results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }

}